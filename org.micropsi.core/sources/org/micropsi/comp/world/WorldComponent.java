/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/WorldComponent.java,v 1.15 2006/01/22 10:57:03 fuessel Exp $
 */
package org.micropsi.comp.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Area2D;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.FormatterFactory;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.common.xml.XMLFile;
import org.micropsi.common.xml.XMLWriter;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.WorldTypesIF;
import org.micropsi.comp.world.conserv.QTypeChangeObjectProperties;
import org.micropsi.comp.world.conserv.QTypeCreateObject;
import org.micropsi.comp.world.conserv.QTypeCreateWorldMessage;
import org.micropsi.comp.world.conserv.QTypeGetAgentList;
import org.micropsi.comp.world.conserv.QTypeGetAgentObjectId;
import org.micropsi.comp.world.conserv.QTypeGetAgentStatistics;
import org.micropsi.comp.world.conserv.QTypeGetExistingWorldFiles;
import org.micropsi.comp.world.conserv.QTypeGetFileName;
import org.micropsi.comp.world.conserv.QTypeGetGlobalData;
import org.micropsi.comp.world.conserv.QTypeGetObjectChangeList;
import org.micropsi.comp.world.conserv.QTypeGetObjectInfoString;
import org.micropsi.comp.world.conserv.QTypeGetObjectList;
import org.micropsi.comp.world.conserv.QTypeGetObjectListString;
import org.micropsi.comp.world.conserv.QTypeGetObjectProperties;
import org.micropsi.comp.world.conserv.QTypeGetObjectTypes;
import org.micropsi.comp.world.conserv.QTypeGetStatusString;
import org.micropsi.comp.world.conserv.QTypeGetWorldType;
import org.micropsi.comp.world.conserv.QTypeLoadWorld;
import org.micropsi.comp.world.conserv.QTypeRemoveObject;
import org.micropsi.comp.world.conserv.QTypeResetWorld;
import org.micropsi.comp.world.conserv.QTypeSaveWorld;
import org.micropsi.comp.world.conserv.QTypeSetDynamicLevel;
import org.micropsi.comp.world.objects.AbstractAgentObject;
import org.micropsi.comp.world.objects.AbstractCommonObject;
import org.micropsi.comp.world.objects.AbstractObject;
import org.micropsi.comp.world.objects.AgentObjectIF;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WorldComponent extends AbstractComponent {
	
	public static Logger logger = null;

	private World world;
	private HashMap<String,AgentObjectIF> agents = new HashMap<String,AgentObjectIF>();
	private String worldFileDir = null;
	private String groundmapImageDir = null;
	private WorldObjectTypes objectTypes = null;
	private ServerRequestTickHandler serverRequestTickHandler = new ServerRequestTickHandler(this);
	
	public static String convertPath(String path) {
		return path.replace('/', File.separatorChar).replace('\\', File.separatorChar);
	}

	/**
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static String getFullFileName(String path, String fileName) {
		return new File(path, getFileNameWithoutPath(fileName)).getAbsolutePath();
	}

	/**
	 * @param fileName
	 * @return
	 */
	public static String getFileNameWithoutPath(String fileName) {
		File file = new File (fileName);
		return file.getName();
	}

	public long getSimStep() {
		return world.getSimStep();
	}

	protected void performInitialisation() throws MicropsiException {
		logger = getLogger();
		world = new World(this);

		String worldFileName = getFileNameWithoutPath(config.getConfigValue(prefixKey + ".worldfile"));
		worldFileDir = convertPath(config.getConfigValue(prefixKey + ".worldfiledir"));
		groundmapImageDir = convertPath(config.getConfigValue(prefixKey + ".groundmapimagedir"));
		objectTypes = new WorldObjectTypes(config.getConfigValue(prefixKey + ".objecttypesconfigfile"));
		servers.registerRequestHandler("worldserverserver", serverRequestTickHandler);

		servers.registerRequestHandler(
			"worldserverserver",
			new ServerRequestPerceptionHandler(this));

//		consoleService.registerQuestionType(new QTypeGetComponentDescriptor(this));
		consoleService.registerQuestionType(new QTypeGetStatusString(this));
		consoleService.registerQuestionType(new QTypeGetAgentList(this));
		consoleService.registerQuestionType(new QTypeGetAgentStatistics(this));
		consoleService.registerQuestionType(new QTypeGetAgentObjectId(this));
		consoleService.registerQuestionType(new QTypeGetObjectListString(this));
		consoleService.registerQuestionType(new QTypeGetObjectList(this));
		consoleService.registerQuestionType(new QTypeGetObjectChangeList(this));
		consoleService.registerQuestionType(new QTypeCreateWorldMessage(this));
		consoleService.registerQuestionType(new QTypeGetObjectInfoString(this));
		consoleService.registerQuestionType(new QTypeGetObjectProperties(this));
		consoleService.registerQuestionType(new QTypeSaveWorld(this));
		consoleService.registerQuestionType(new QTypeResetWorld(this));
		consoleService.registerQuestionType(new QTypeRemoveObject(this));
		consoleService.registerQuestionType(new QTypeChangeObjectProperties(this));
		consoleService.registerQuestionType(new QTypeGetObjectTypes(this));
		consoleService.registerQuestionType(new QTypeCreateObject(this));
		consoleService.registerQuestionType(new QTypeGetGlobalData(this));
		consoleService.registerQuestionType(new QTypeSetDynamicLevel(this));
		consoleService.registerQuestionType(new QTypeLoadWorld(this));
		consoleService.registerQuestionType(new QTypeGetFileName(this));
		consoleService.registerQuestionType(new QTypeGetWorldType(this));
		consoleService.registerQuestionType(new QTypeGetExistingWorldFiles(this));
		
		LocationBasedMessageDistributor.setWorld(world);

		readWorldData(worldFileName);

	}

	protected void readWorldData(String fileName) throws MicropsiException {
		world.setFileName(getFileNameWithoutPath(fileName));
		fileName = getFullFileName(worldFileDir, fileName);
		XMLFile worldFile = null;
		try {
			worldFile = new XMLFile(fileName);
		} catch (FileNotFoundException e) {
			throw new MicropsiException(10, "World setup file not found: " + fileName, e);
		} catch (IOException e) {
			throw new MicropsiException(10, "Error reading world setup file", e);
		} catch (SAXException e) {
			throw new MicropsiException(10, "Error reading world setup file", e);
		} catch (ParserConfigurationException e) {
			throw new MicropsiException(10, "Error reading world setup file", e);
		} catch (FactoryConfigurationError e) {
			throw new MicropsiException(10, "Error reading world setup file", e);
		}

		Element rootElement = worldFile.getDocumentElement();

		try {
			world.setWorldType(XMLElementHelper.getElementValueByTagName(rootElement, "worldtype"));
		} catch (XMLElementNotFoundException e1) {
			throw new MicropsiException(10, "World setup file has no 'worldtype' tag.");
		}
		try {
			GroundMap groundMap = new GroundMap(XMLElementHelper.getElementByTagName(rootElement, "groundmap"), groundmapImageDir, getLogger());
			world.setGroundMap(groundMap);
		} catch (XMLElementNotFoundException e1) {
			throw new MicropsiException(10, "Error reading world config file: no groundmap tag found.");
		}
		try {
			Element areaElement = XMLElementHelper.getElementByTagName(rootElement, "visiblearea");
			Area2D visibleArea = new Area2D(areaElement, world.getGroundMap().getLowestCoords(), world.getGroundMap().getHighestCoords());
			world.setVisibleArea(visibleArea);
		} catch (XMLElementNotFoundException e1) {
			// ignore: Parameters optional
			world.setVisibleArea(new Area2D(world.getGroundMap().getLowestCoords(), world.getGroundMap().getHighestCoords()));
		}

		
		readObjects(rootElement);
		try {
			Element spawnLocations = XMLElementHelper.getElementByTagName(rootElement, "spawnLocations");
			readSpawnLocations(spawnLocations);
		} catch (XMLElementNotFoundException e) {
			world.addSpawnLocation(new Position(15, 15, 0));
		}
		if (world.getChangeLog() != null) {
			world.getChangeLog().reset(getWorld().getSimStep() + 1);
		}
		world.globalDataChanged();
	}

	private void readObjects(Element rootElement) throws MicropsiException {
		Class[] parameters = new Class[2];
		try {
			parameters[0] = Class.forName("org.w3c.dom.Element"); // configData
			// object data node
			parameters[1] = Class.forName("org.apache.log4j.Logger"); // Logger
		} catch (ClassNotFoundException e) {
			throw new MicropsiException(10, "Error creating world objects.");
		}
		Object[] parameterValues = new Object[2];
		parameterValues[1] = getLogger();

		NodeList objectNodes = rootElement.getElementsByTagName("object");
		for (int i = 0; i < objectNodes.getLength(); i++) {
			Element currentElement = (Element) objectNodes.item(i);
			try {
				parameterValues[0] = XMLElementHelper.getElementByTagName(currentElement, "data");
			} catch (XMLElementNotFoundException e) {
			}
			String classname;
			try {
				classname = XMLElementHelper.getElementValueByTagName(currentElement, "class");
			} catch (XMLElementNotFoundException e) {
				throw new MicropsiException(
					10,
					"Error reading world setup file: no class specified for Object",
					e);
			}
			AbstractObject newThing;
			try {
				Constructor myConstr = Class.forName(classname).getConstructor(parameters);
				newThing = (AbstractObject) myConstr.newInstance(parameterValues);
			} catch (ClassNotFoundException e) {
				throw new MicropsiException(
					10,
					"Error reading world setup file: class not found: " + classname,
					e);
			} catch (NoSuchMethodException e) {
				throw new MicropsiException(
					10,
					"Error reading world setup file: class "
						+ classname
						+ " has no vaild constructor",
					e);
			} catch (InstantiationException e) {
				throw new MicropsiException(
					10,
					"Error reading world setup file: Error setting up class " + classname,
					e);
			} catch (IllegalAccessException e) {
				throw new MicropsiException(
					10,
					"Error reading world setup file: Error setting up class " + classname,
					e);
			} catch (java.lang.reflect.InvocationTargetException e) {
				Exception f = (Exception) e.getCause();
				throw new MicropsiException(
					10,
					"Error reading world setup file: Exception (see below) in constructor for class "
						+ classname,
					f == null ? e : f);
			}
			world.addObject(newThing);

		}
	}
	
	private void readSpawnLocations(Element rootElement) {
		NodeList spawnLoacations = rootElement.getElementsByTagName("position");
		for (int i = 0; i < spawnLoacations.getLength(); i++) {
			Element el = (Element) spawnLoacations.item(i);
			world.addSpawnLocation(new Position(el.getTextContent().trim()));
		}
	}

	/** Replaces all persistent objects by new instances from world setup file
	 * 
	 */
	public void resetWorld() throws MicropsiException {
		List<AbstractObject> toRemove = new ArrayList<AbstractObject>(getWorld().getNumberOfObjects());
		Iterator<AbstractObject> it = getWorld().getObjects().iterator();
		while (it.hasNext()) {
			AbstractCommonObject obj = (AbstractCommonObject) it.next();
			if (obj.isPersistent()) {
				toRemove.add(obj);
			}
		}
		it = toRemove.iterator();
		while (it.hasNext()) {
			AbstractCommonObject obj = (AbstractCommonObject) it.next();
			world.removeObject(obj);
		}
		readWorldData(world.getFileName());
	}
	
	public void replaceWorld(String fileName) throws MicropsiException {
		fileName = getFileNameWithoutPath(fileName);
		XMLFile worldFile = null;
		try {
			worldFile = new XMLFile(getFullFileName(worldFileDir, fileName));
		} catch (FileNotFoundException e) {
			throw new MicropsiException(10, "World setup file not found: " + getFullFileName(worldFileDir, fileName), e);
		} catch (IOException e) {
			throw new MicropsiException(10, "Error reading world setup file", e);
		} catch (SAXException e) {
			throw new MicropsiException(10, "Error reading world setup file", e);
		} catch (ParserConfigurationException e) {
			throw new MicropsiException(10, "Error reading world setup file", e);
		} catch (FactoryConfigurationError e) {
			throw new MicropsiException(10, "Error reading world setup file", e);
		}
		Element rootElement = worldFile.getDocumentElement();
		String newType = "";
		try {
			newType = XMLElementHelper.getElementValueByTagName(rootElement, "worldtype");
		} catch (XMLElementNotFoundException e1) {
			throw new MicropsiException(10, "World setup file has no 'worldtype' tag.");
		}
		if (!newType.equals(world.getWorldType())) {
			throw new MicropsiException(10, "Cannot replace world by world of a different type.");
		}
		world.setFileName(fileName);
		resetWorld();
	}
	
	public void saveWorld() throws MicropsiException {
		saveWorld(world.getFileName());
	}
	
	public void saveWorld(String fileName) throws MicropsiException {
		world.setFileName(getFileNameWithoutPath(fileName));
		fileName = getFullFileName(worldFileDir, fileName);
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (Exception e) {
			throw new MicropsiException(10,"Error saving world data: got no document builder",e);
		}
		
		// create the document element
		Document document = builder.newDocument();
		Element rootElement = document.createElement("world");
		document.appendChild(rootElement);
		XMLElementHelper.appendTextElement(document, rootElement, "worldtype", world.getWorldType());
		rootElement.appendChild(world.getGroundMap().toXMLElement(document));
		rootElement.appendChild(world.getVisibleArea().toXMLElement(document, "visiblearea"));
		
		Iterator it = getWorld().getObjects().iterator();
		while (it.hasNext()) {
			AbstractCommonObject obj = (AbstractCommonObject) it.next();
			if (obj.isPersistent()) {
				rootElement.appendChild(obj.toXMLElement(document));
			}
		}
		
		Element spawnLocationElement = document.createElement("spawnLocations");
		rootElement.appendChild(spawnLocationElement);
		for (Position pos : world.getSpawnLocations()) {
			Element posElement = document.createElement("position");
			spawnLocationElement.appendChild(posElement);
			posElement.setTextContent(pos.toString());
		}
		
		try {
			File file = new File(fileName);
			if(file.exists()) {
				String backupName;
				if (fileName.lastIndexOf(".") >= 0) {
					backupName = fileName.substring(0, fileName.lastIndexOf(".")) + ".bak";
				} else {
					backupName = fileName + ".bak";
				}
				File backupFile = new File(backupName);
				if (backupFile.exists()) {
					backupFile.delete();
				}
				file.renameTo(backupFile);
				file = new File(fileName);
			} 
			file.createNewFile();
			FileOutputStream fout = new FileOutputStream(file);
			XMLWriter.save(document, fout, FormatterFactory.getFormatter(FormatterFactory.FORMATTER_HUMAN_READABLE));
			fout.close();
		} catch (FileNotFoundException e1) {
			throw new MicropsiException(10, "Error saving world data", e1);
		} catch (IOException e1) {
			throw new MicropsiException(10, "IO error saving world data", e1);
		} catch (MicropsiException e1) {
			throw new MicropsiException(10, "Error saving world data", e1);
		}
	}
	
	public String[] getWorldFilenames() {
		File dir = new File(worldFileDir);
		return dir.list();
	}

	protected void tick(long simStep) {
		world.tick(simStep);
	};

	public int getInnerType() {
		return WorldTypesIF.WT_SIMULATED;
	}

	/**
	 * @deprecated Use World methods instead!
	 */
	public int getNumberOfObjects() {
		return world.getNumberOfObjects();
	}
	
	public World getWorld() {
		return world;
	}

	/**
	 * Method getAgent.
	 * @param agentID
	 * @return AgentObjectIF
	 */
	public AbstractAgentObject getAgent(String agentID) {
		return (AbstractAgentObject) agents.get(agentID);
	}

	/**
	 * Returns the agents.
	 * @return HashMap
	 */
	public Map<String,AgentObjectIF> getAgents() {
		return agents;
	}

	/**
	 * @return WorldObjectTypes
	 */
	public WorldObjectTypes getObjectTypes() {
		return objectTypes;
	}

	public void shutdown() {
		
		//TODO Matthias: Bitte hier etwaige Welt-Threads sauber beenden
		
	}

}