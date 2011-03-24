/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/config/XMLConfigurationReader.java,v 1.4 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.micropsi.common.exception.MicropsiException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XMLConfigurationReader implements ConfigurationReaderIF {

	protected Document document;
	
	protected HashMap variables;
	
	private String getStringKey(Element element) {
		Node walker = element;
		String toReturn = "";
		String nodename = "";
		String SEP = "";
		do {	
			nodename = walker.getNodeName();
			NamedNodeMap map = walker.getAttributes();
			for(int i=0;i<map.getLength();i++) {
				nodename = nodename + ":" + 
						   map.item(i).getNodeName() + "=" + 
						   map.item(i).getNodeValue().replace('.','_');
			}
			
			toReturn = nodename+SEP+toReturn;
			SEP = ".";
			walker = walker.getParentNode();
		} while (walker.getNodeType() != Node.DOCUMENT_NODE);
		
		return toReturn;
	}
	
	private Element getElementAt(String key) throws MicropsiConfigException {
		
		key = replaceVariables(key);
		
		Element current = document.getDocumentElement();
		
		StringTokenizer tokener = new StringTokenizer(key,".");
		
		if(!tokener.nextToken().equals(current.getNodeName())) 
			throw new MicropsiConfigException(110,"root node of "+key);
		
		String next;
		while(tokener.hasMoreTokens()) {
			next = tokener.nextToken();
						
			NodeList list = current.getChildNodes();
			int length = list.getLength();
			boolean found = false;
			for(int i=0;i<length;i++) {
				if(next.startsWith(list.item(i).getNodeName())) {

					String nodename = list.item(i).getNodeName();
					NamedNodeMap map = list.item(i).getAttributes();
					for(int j=0;j<map.getLength();j++) {
						nodename = nodename + ":" + 
								   map.item(j).getNodeName() + "=" + 
								   map.item(j).getNodeValue().replace('.','_');
					}
					
					if(replaceVariables(nodename).equals(next)) {
						current = (Element)list.item(i);
						found = true;
						break;
					}
				}				
			}
			if(!found) throw new MicropsiConfigException(110,next+" at "+getStringKey(current));
		}
		return current;
	}
	
	public XMLConfigurationReader(InputStream configInput) throws MicropsiException {
		initialize(configInput);
	}

	public XMLConfigurationReader(String configRoot, HashMap variables) throws MicropsiException {
		
		this.variables = variables;
		
		try {
			initialize(new FileInputStream(new File(configRoot)));			
		} catch (FileNotFoundException e) {
			throw new MicropsiException(100,configRoot);
		}
	}
	
	private void initialize(InputStream configInput) throws MicropsiException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource inp = new InputSource(configInput);
			document = builder.parse(inp);			
		} catch (SAXException e) {
			throw new MicropsiException(101,e.getMessage(),e);
		} catch (IOException e) {
			throw new MicropsiException(102,e.getMessage(),e);
		} catch (ParserConfigurationException e) {
			throw new MicropsiException(101,e.getMessage(),e);
		} catch (FactoryConfigurationError e) {
			throw new MicropsiException(101,e.getMessage(),e);
		}
	}
	
	public NamedNodeMap accessAttributes(String key) throws MicropsiConfigException {
		Element element = getElementAt(key);
		return element.getAttributes();
	}
	
	public Element accessElement(String key) throws MicropsiConfigException {
		if(key == null) return document.getDocumentElement();
		return getElementAt(key);
	}
	
	public String getConfigValue(String key) throws MicropsiConfigException {
		String toReturn = "";
		Element element = getElementAt(key);
		NodeList list = element.getChildNodes();
		int length = list.getLength();
		String SEP = "";
		for(int i=0;i<length;i++) {
			if(list.item(i).getNodeType() == Node.TEXT_NODE) {
				toReturn+= SEP+list.item(i).getNodeValue().trim();
				SEP = System.getProperty("line.separator");
			}
		}
		return replaceVariables(toReturn);
	}

	public int getIntConfigValue(String key) throws MicropsiConfigException {
		int toReturn = 0;
		try {
			String value = getConfigValue(key);		
			toReturn = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new MicropsiConfigException(111,key);
		}
		return toReturn;
	}

	public double getDoubleConfigValue(String key) throws MicropsiConfigException {
		double toReturn = 0;
		try {
			String value = getConfigValue(key);		
			toReturn = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new MicropsiConfigException(111,key);
		}
		return toReturn;
	}
	
	public boolean getBoolConfigValue(String key) throws MicropsiConfigException {
		boolean toReturn = false;
		try {
			String value = getConfigValue(key);		
			if(value.equalsIgnoreCase("true") || value.equals("1")) toReturn = true;
				else toReturn = false;
		} catch (NumberFormatException e) {
			throw new MicropsiConfigException(111,key);
		}
		return toReturn;
	}

	public List<String> getConfigurationValues(String key) throws MicropsiConfigException {
		ArrayList<String> toReturn = new ArrayList<String>();
		Element element = getElementAt(key);
		NodeList list = element.getChildNodes();
		int length = list.getLength();
		for(int i=0;i<length;i++) {
			if(list.item(i).getNodeType() == Node.TEXT_NODE &&
			   list.item(i).getNodeValue().trim().length() > 0) {
				
				toReturn = new ArrayList<String>();
				String text = list.item(i).getNodeValue().trim();
				StringTokenizer tokener = new StringTokenizer(text,",");
				while(tokener.hasMoreTokens()) 
					toReturn.add(
						replaceVariables(tokener.nextToken())
					);
				break;
			} else if(list.item(i).getNodeType() == Node.ELEMENT_NODE) {
				String nodename = list.item(i).getNodeName();
				NamedNodeMap map = list.item(i).getAttributes();
				for(int j=0;j<map.getLength();j++) {
					nodename = nodename + ":" + 
							   map.item(j).getNodeName() + "=" + 
							   map.item(j).getNodeValue().replace('.','_');
				}
				toReturn.add(replaceVariables(nodename));
			}
		}
		return toReturn;	
	}
	
	protected String replaceVariables(String value) {
		if(variables == null) return value;
		if(value.indexOf("$") < 0) return value;

		String toReturn = value;
		Iterator iter = variables.keySet().iterator();
		while(iter.hasNext()) {
			String key = (String)iter.next();
			if(toReturn.indexOf("$"+key) >= 0) {
				String pre = toReturn.substring(0,toReturn.indexOf("$"+key));
				String post = toReturn.substring(toReturn.indexOf("$"+key)+key.length()+1);
				toReturn = pre + (String)variables.get(key) + post;
			}
		}
		return toReturn;
	}
}
