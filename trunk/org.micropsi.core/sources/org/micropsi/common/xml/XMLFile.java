/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/xml/XMLFile.java,v 1.3 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.micropsi.common.exception.MicropsiException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * /home/cvsroot/aep/sources/de/artificialemotion/common/xml/XMLFile.java,v 1.1 2002/11/05 14:12:39 vuine Exp
 * 
 */
public class XMLFile {
	
	private Document doc;
	private File file;
	
	public XMLFile(String filename) throws FileNotFoundException,IOException,SAXException, ParserConfigurationException, FactoryConfigurationError {
		File file = new File(filename);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = builder.parse(file);		
	}

	public XMLFile(InputStream stream) throws FileNotFoundException,IOException,SAXException, ParserConfigurationException, FactoryConfigurationError {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = builder.parse(new InputSource(stream));		
	}

	
	public XMLFile(File f) throws FileNotFoundException,IOException,SAXException, ParserConfigurationException, FactoryConfigurationError {
		file = f;
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = builder.parse(file);		
	}
	
	public void save() throws MicropsiException,IOException,FileNotFoundException {
		if(file.exists()) file.delete();
		file.createNewFile();
		FileOutputStream fout = new FileOutputStream(file);
		XMLWriter.save(doc, fout, FormatterFactory.getFormatter(FormatterFactory.FORMATTER_HUMAN_READABLE));
	}

	public void save(int formatterType) throws MicropsiException,IOException,FileNotFoundException {
		if(file.exists()) file.delete();
		file.createNewFile();
		FileOutputStream fout = new FileOutputStream(file);
		XMLWriter.save(doc, fout, FormatterFactory.getFormatter(formatterType));
	}
	
	public Element getDocumentElement() {
		return doc.getDocumentElement();
	}
	
	public static Node getFirstNamedChild(Node node, String name) {
		NodeList list = node.getChildNodes();
		for(int i=0;i<list.getLength();i++)
			if(list.item(i).getNodeName().equals(name)) return list.item(i);
		return null;
	}
		
}
