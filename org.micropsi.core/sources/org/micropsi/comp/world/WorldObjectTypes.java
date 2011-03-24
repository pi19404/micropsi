/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 29.03.2003
 *
 */
package org.micropsi.comp.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLFile;

/**
 * @author matthias
 *
 */
public class WorldObjectTypes {
	
	String fileName;
	Map<String,WorldObjectType> worldObjectTypes;

	public WorldObjectTypes(String fileName) throws MicropsiException {
		this.fileName = fileName;
		rereadFile();
	}
	
	public void rereadFile() throws MicropsiException {
		XMLFile myFile = null;
		try {
			myFile = new XMLFile(fileName);
		} catch (FileNotFoundException e) {
			throw new MicropsiException(10, "Object type setup file not found: " + fileName, e);
		} catch (IOException e) {
			throw new MicropsiException(10, "Error reading object types setup file", e);
		} catch (SAXException e) {
			throw new MicropsiException(10, "Error reading object types setup file", e);
		} catch (ParserConfigurationException e) {
			throw new MicropsiException(10, "Error reading object types setup file", e);		} catch (FactoryConfigurationError e) {
		}
		
		worldObjectTypes = new HashMap<String,WorldObjectType>(20);
		Element rootElement = myFile.getDocumentElement();

		NodeList typeNodes = rootElement.getElementsByTagName("type");
		for (int i = 0; i < typeNodes.getLength(); i++) {
			addType(new WorldObjectType((Element)typeNodes.item(i)));
		}
	}

	private void addType(WorldObjectType type) {
		worldObjectTypes.put(type.getTypeName(), type);
	}
	
	public Collection<WorldObjectType> getTypes() {
		return worldObjectTypes.values();
	}
	
	public WorldObjectType getType(String name) {
		return worldObjectTypes.get(name);
	}

}
