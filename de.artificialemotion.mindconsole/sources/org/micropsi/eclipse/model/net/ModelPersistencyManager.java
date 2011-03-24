/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/model/net/ModelPersistencyManager.java,v 1.3 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.model.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.common.xml.FormatterFactory;
import org.micropsi.common.xml.XMLFile;
import org.micropsi.common.xml.XMLWriter;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.nodenet.NetEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ModelPersistencyManager {
	
	protected static void saveModels(NetModel m, OutputStream out) throws MicropsiException,IOException {
		
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (Exception e) {
			throw new MicropsiException(10,e.getMessage(),e);
		}
		
		// create the document element
		Document document = builder.newDocument();
		document.appendChild(document.createElement("MicroPsiMetadata"));
		
		Element main = document.getDocumentElement();

		Iterator iter = m.getNet().getAllEntities();
	
		while(iter.hasNext()) {
			NetEntity e = (NetEntity)iter.next();
			if(!m.models.containsKey(e.getID())) continue;
			
			EntityModel model = m.models.get(e.getID());
			
			Element modelElement = document.createElement("model");
			modelElement.setAttribute("entity", model.getEntity().getID());
			modelElement.setAttribute("x", Integer.toString(model.getX()));
			modelElement.setAttribute("y", Integer.toString(model.getY()));
			modelElement.setAttribute("z", Integer.toString(model.getZ()));
			
			String c = model.getComment();
			c.replace('<', '_');
			c.replace('>', '_');
			c.replace('&', '_');
			modelElement.setAttribute("txt", c);
			main.appendChild(modelElement);
		}
		
		XMLWriter.save(
			document, 
			out,
			FormatterFactory.getFormatter(FormatterFactory.FORMATTER_HUMAN_READABLE)
		);
		
		out.flush();
		out.close();		
	}
	
	protected static Map<String,EntityModel> loadModels(InputStream metadata, NetModel net, ProgressMonitorIF progress) {
		
		if(progress != null) progress.beginTask("Positioning items...");
		
		HashMap<String,EntityModel> toReturn = new HashMap<String,EntityModel>();
		
		try {
			XMLFile f = new XMLFile(metadata);
			
			NodeList modelnodes = f.getDocumentElement().getChildNodes();
			for(int i=0;i<modelnodes.getLength();i++) {				
				Node n = modelnodes.item(i);
				if(!n.getNodeName().equals("model")) continue;
				NamedNodeMap attr = n.getAttributes(); 
				String id = attr.getNamedItem("entity").getNodeValue();
				if(progress != null) progress.reportProgress(modelnodes.getLength()*2+i, modelnodes.getLength()*3, "positioning "+id);			
				try {
					EntityModel tmp = new EntityModel(
						net,
						net.getNet().getEntity(id)
					);
					tmp.setX(Integer.parseInt(attr.getNamedItem("x").getNodeValue()));
					tmp.setY(Integer.parseInt(attr.getNamedItem("y").getNodeValue()));
					tmp.setZ(Integer.parseInt(attr.getNamedItem("z").getNodeValue()));
					tmp.setComment(attr.getNamedItem("txt").getNodeValue());
					toReturn.put(id, tmp);
				} catch (MicropsiException e) {
					MindPlugin.getDefault().getLogger().warn("Entity not found when loading positions: "+id);
					// that's allright, the entity was deleted, so it's ok to just
					// do nothing 
				}
			}
		} catch (Exception e) {
			MindPlugin.getDefault().handleException(e);
		}
		
		if(progress != null) progress.endTask();
		
		return toReturn;
	}

}
