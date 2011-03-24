/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/EntityTransferData.java,v 1.3 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.util.ArrayList;
import java.util.List;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.LocalNetRegistry;
import org.micropsi.nodenet.NetEntity;


public class EntityTransferData {

	public LocalNetFacade net;
	public NetEntity entity;
	public int x;
	public int y;
	
	public static String listToText(ArrayList list) {
		StringBuffer buf = new StringBuffer(list.size()*30);
		for(int i=0;i<list.size();i++) {
			EntityTransferData entry = (EntityTransferData)list.get(i);
			buf.append(entry.net.getNetKey());
			buf.append(";");
			buf.append(entry.entity.getID());
			buf.append(";");
			buf.append(entry.x);
			buf.append(";");
			buf.append(entry.y);
			buf.append("\n");
		}
		return buf.toString();
	}
	
	public static List<EntityTransferData> textToList(String text) throws MicropsiException {
		ArrayList<EntityTransferData> toReturn = new ArrayList<EntityTransferData>();	
		String[] lines = text.split("\n");
		for(int i=0;i<lines.length;i++) {
			String[] parts = lines[i].split(";");
			
			EntityTransferData entry = new EntityTransferData();
			entry.net = LocalNetRegistry.getInstance().getNet(parts[0]);
			entry.entity = entry.net.getEntity(parts[1]);
			entry.x = Integer.parseInt(parts[2]);
			entry.y = Integer.parseInt(parts[3]);
			
			toReturn.add(entry);
		}
		return toReturn;
	}

}
