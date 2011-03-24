package org.micropsi.comp.console.worldconsole.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.common.xml.XMLFile;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.console.worldconsole.OverlayRendererDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author David
 *
 * A ImageLibrary contains bitmap images for world objects and
 * information when and how to use them
 */
public class ImageLibrary {

	private ImageRegistry imageRegistry = null;	// contains the actual images
	private HashMap<String,LibraryClassInfoEntry> classInfoMap = null; 
	private LibraryIconInfoEntry defaultIcon = null;	// default image for unknown classes
	
	private String groundmapImageDir = null;
	private String objectImageDir = null;
	private Image groundmapImage = null;
	private List<OverlayRendererDescriptor> overlayRenderDescriptors;
	
	private ConsoleFacadeIF console = null;
	

	public ImageLibrary(String configFileURL, ConsoleFacadeIF console) {

		classInfoMap = new HashMap<String,LibraryClassInfoEntry>();
		imageRegistry = new ImageRegistry();

		this.console = console;
		
		try {
			initFromConfigFile(configFileURL);
		} catch (MicropsiException e) {
			console.getExproc().handleException(e);
		}
	}


	/**
	 * init images from xml config file
	 */
	protected void initFromConfigFile(String configFileURL) throws MicropsiException {
		String defaultFilePrefix = configFileURL.substring(0, configFileURL.lastIndexOf("/") + 1);
		XMLFile configFile = null;
		try {
			File f = new File(configFileURL);
			FileInputStream configInput = new FileInputStream(f); 
			configFile = new XMLFile(configInput);
		} catch (FileNotFoundException e) {
			throw new MicropsiException(10, "World console plugin: config file not found: " + configFileURL, e);
		} catch (IOException e) {
			throw new MicropsiException(10, "World console plugin: error reading config file", e);
		} catch (SAXException e) {
			throw new MicropsiException(10, "World console plugin: xml error reading config file", e);
		} catch (ParserConfigurationException e) {
			throw new MicropsiException(10, "World console plugin: xml error reading config file", e);
		} catch (FactoryConfigurationError e) {
			throw new MicropsiException(10, "World console plugin: xml error reading config file", e);
		}
		Element rootElement = configFile.getDocumentElement();

		try {
			objectImageDir = XMLElementHelper.getElementValueByTagName(rootElement, "objectimagedir");
		} catch (XMLElementNotFoundException e) {
			console.getLogger().error("World console plugin: no groundmap image directory specified.");
		}

		objectImageDir = defaultFilePrefix+"/"+objectImageDir;
		
		Element el;
		overlayRenderDescriptors = new ArrayList<OverlayRendererDescriptor>(5);
		try {
			el = XMLElementHelper.getElementByTagName(rootElement, "overlays");
			NodeList nodes = el.getElementsByTagName("renderer");
			for (int i = 0; i < nodes.getLength(); i++) {
				OverlayRendererDescriptor renderer = new OverlayRendererDescriptor((Element) nodes.item(i), console);
				registerOverlayRenderer(renderer);
			}
		} catch (XMLElementNotFoundException e2) {
			// element is optional
		}

		NodeList classInfoNodes = rootElement.getElementsByTagName("classinfo");
		for (int i = 0; i < classInfoNodes.getLength(); i++) {
			Element classInfoElement = (Element) classInfoNodes.item(i);
			String className = classInfoElement.getAttribute("class");
			if (className == null || className.equals("")) {
				throw new MicropsiException(10, "World console plugin: config file: 'classinfo' node MUST have 'class' attribute.");
			}
			classInfoMap.put(className, new LibraryClassInfoEntry(className, classInfoElement, imageRegistry, console, objectImageDir));
		}

		defaultIcon = null;
		try {
			defaultIcon = new LibraryIconInfoEntry(XMLElementHelper.getElementByTagName(rootElement, "defaulticon"), imageRegistry, console, objectImageDir);
		} catch (XMLElementNotFoundException e1) {
			console.getLogger().warn("World console plugin: no default icon specified, unknown objects will not be visible. Expect NullPointer exceptions.");
		}

		groundmapImage = null;
		try {
			groundmapImageDir = XMLElementHelper.getElementValueByTagName(rootElement, "groundmapimagedir");
		} catch (XMLElementNotFoundException e) {
			console.getLogger().warn("World console plugin: no groundmap image directory specified.");
		}

		groundmapImageDir = defaultFilePrefix+"/"+groundmapImageDir;
	}

	
	/**
	 * @param node
	 */
	public void setGroundmapImageFile(String imageFilename) {
		String sourceURL = null;
		if (groundmapImage != null) {
			groundmapImage.dispose();
		}
		if (imageFilename == null || imageFilename.equals("")) {
			groundmapImage = null;
			return;
		}
		sourceURL = groundmapImageDir+"/"+imageFilename;
		if (sourceURL == null) {
			groundmapImage = null;
		} else {
			groundmapImage = new Image(Display.getDefault(), sourceURL);
		}
	}


	/**
	 * 	get	icon for class name  
	 *  will  return a valid ObjectVisualInfo
	 */
	public ObjectVisualInfo getVisualInfo(String className, ObjectStates states, int orientation) {
		ObjectVisualInfo visualInfo = new ObjectVisualInfo();
		IIconKeyRetriever iconRetriever = null;
		LibraryClassInfoEntry classInfo = classInfoMap.get(className);
		if(classInfo != null) {
			iconRetriever = classInfo.getIconRetriever(states);
			visualInfo.imageKey = iconRetriever.getIconKey(orientation);
			visualInfo.image = imageRegistry.get(visualInfo.imageKey);
			if (visualInfo.image != null) {
				visualInfo.imageCenter = iconRetriever.getImageCenter();
			}
		}
		if (visualInfo.image == null) {
			visualInfo.stateDescription = "Class: " + className;
			if (defaultIcon != null) {
				visualInfo.image = imageRegistry.get(defaultIcon.getIconRetriever().getIconKey(orientation));
				visualInfo.imageCenter = defaultIcon.getIconRetriever().getImageCenter();
			}
			
		}
		if (visualInfo.image != null) {
			if (visualInfo.imageCenter == null) {
				visualInfo.imageCenter = new Point (visualInfo.image.getBounds().width / 2, visualInfo.image.getBounds().height / 2);
			}
		}
		if (visualInfo.imageCenter == null) {
			visualInfo.imageCenter = new Point(0, 0);
		}
		String s = getStateDescription(states);
		if (s != null && s != "") {
			visualInfo.stateDescription += "\n" + s;
		}
		return visualInfo;
	}
	

	/**
	 * @return
	 */
	public Image getGroundmapImage() {
		return groundmapImage;
	}

	/**
	 * @param stateNode
	 * @return
	 */
	private String getStateDescription(ObjectStates states) {
		String ret = "";
		if (states == null) {
			return ret;
		}
		Iterator it = states.getStatesMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry state = (Entry) it.next();
			if (ret.length() == 0) {
				ret = state.getKey() + (state.getValue() != null ? ": " + state.getValue() : "");
			} else {
				ret += "\n" + state.getKey() + (state.getValue() != null ? ": " + state.getValue() : ""); 
			}
		}
		return ret;
	}


	/**
	 * @return Returns the overlayRenderDescriptors.
	 */
	public List getOverlayRenderDescriptors() {
		return overlayRenderDescriptors;
	}

	/**
	 * Registers an overlay renderer with the system.
	 * 
	 * @param descriptor
	 */
	public void registerOverlayRenderer(OverlayRendererDescriptor descriptor) {
		
		if (descriptor != null) {
			ListIterator<OverlayRendererDescriptor> it = overlayRenderDescriptors.listIterator();
			while (it.hasNext() && it.next().getZOrder() > descriptor.getZOrder()) {
				// well, just move it to next renderer, that's all...
			}
			it.add(descriptor);
		}
	}
	
}

