/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ComponentDescriptor.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComponentDescriptor {
	
	private String componentClass;
	private String componentID;
	private int innerType;
	//private ArrayList servers;
	private ArrayList<String> clients;
	private ArrayList<String> questiontypes; 
	
	public ComponentDescriptor(AbstractComponent comp) {
		this.componentClass = comp.getClass().getName();
		this.componentID = comp.getComponentID();
		this.innerType = comp.getInnerType();
		String[] qtypes = comp.getConsoleService().getKnownQuestionTypes();
		if(qtypes != null) {
			questiontypes = new ArrayList<String>();
			for(int i=0;i<qtypes.length;i++) questiontypes.add(qtypes[i]);
		}		
		
		Iterator iter = comp.clients.clientmap.values().iterator();
		if(iter.hasNext()) clients = new ArrayList<String>();
		while(iter.hasNext()) 
			clients.add(iter.next().getClass().getName());

/*		iter = comp.servers.get.values().iterator();
		if(iter.hasNext()) servers = new ArrayList();
		while(iter.hasNext()) 
			servers.add(iter.next().getClass().getName());*/
	}

	/**
	 * Returns the clients.
	 * @return ArrayList
	 */
	public List<String> getClients() {
		return clients;
	}

	/**
	 * Returns the componentClass.
	 * @return String
	 */
	public String getComponentClass() {
		return componentClass;
	}

	/**
	 * Returns the componentID.
	 * @return String
	 */
	public String getComponentID() {
		return componentID;
	}

	/**
	 * Returns the innerType.
	 * @return int
	 */
	public int getInnerType() {
		return innerType;
	}

	/**
	 * Returns the questiontypes.
	 * @return ArrayList
	 */
	public List<String> getQuestiontypes() {
		return questiontypes;
	}

	/**
	 * Returns the servers.
	 * @return ArrayList
	 */
/*	public ArrayList getServers() {
		return servers;
	}*/
	
}
