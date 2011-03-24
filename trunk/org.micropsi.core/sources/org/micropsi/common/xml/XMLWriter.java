/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/xml/XMLWriter.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.xml;

import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.micropsi.common.exception.MicropsiException;

public class XMLWriter {
			
			
	public static void save(Document doc, OutputStream outp, XMLFormatterIF format) throws MicropsiException {
		int indent = 0;
		try {
			saveNode(doc.getDocumentElement(),outp,format,indent);
		} catch (IOException e) {
			throw new MicropsiException(15,e.getMessage(),e);
		}
	}
	
	private static void saveNode(Node node, OutputStream outp, XMLFormatterIF format, int indent) throws IOException {
		int newIndent = indent+1;
		switch(node.getNodeType()) {
			case Node.DOCUMENT_NODE:
				//outp.write("".getBytes());
				saveNode(((Document)node).getDocumentElement(),outp,format,indent);
				break;
			case Node.ELEMENT_NODE:
				if(format.breakTags()) {
					outp.write(format.getBreak().getBytes());
					createIndent(outp,format,indent);
				}
				outp.write('<');
				outp.write(node.getNodeName().getBytes());
				NamedNodeMap atts = node.getAttributes();	
 				for(int i=0;i<atts.getLength();i++) 
					saveNode(atts.item(i),outp,format,newIndent);
				if(!node.hasChildNodes()) {			
					outp.write("/>".getBytes());
				} else {
					outp.write('>');					
					NodeList list = node.getChildNodes();
					if(list.getLength() == 1 && list.item(0).getNodeType() == Node.TEXT_NODE) {
						if(format.breakValues()) {
							outp.write(format.getBreak().getBytes());
							createIndent(outp,format,newIndent+1);
						}
						saveNode(list.item(0),outp,format,newIndent);
						if(format.breakValues()) outp.write(format.getBreak().getBytes());
					} else {
						for(int i=0;i<list.getLength();i++) { 
							saveNode(list.item(i),outp,format,newIndent);
						}
					}
					if(format.breakTags()) {
						outp.write(format.getBreak().getBytes());
						createIndent(outp,format,indent);
					}				
					outp.write("</".getBytes());
					outp.write(node.getNodeName().getBytes());
					outp.write(">".getBytes());
				}
				break;
			case Node.ATTRIBUTE_NODE:
				outp.write(' ');
				outp.write(node.getNodeName().getBytes());
				outp.write("=\"".getBytes());
				outp.write(node.getNodeValue().getBytes());
				outp.write("\"".getBytes());
				break;
			case Node.TEXT_NODE:
				if(format.surroundTextWithSpaces()) outp.write(' ');
				outp.write(node.getNodeValue().getBytes());
				if(format.surroundTextWithSpaces()) outp.write(' ');
				break;
			case Node.COMMENT_NODE:
				createIndent(outp,format,newIndent);
				outp.write("<!--".getBytes());
				outp.write(node.getNodeValue().getBytes());
				outp.write("-->".getBytes());
				break;
		}
	}

	
	private static void createIndent(OutputStream outp, XMLFormatterIF format, int indent) throws IOException {
		for(int i=0;i<indent;i++) {
			for(int j=0;j<format.getIndentation();j++) {
				outp.write(format.getTab().getBytes());
			}
		}
	}
		
}
