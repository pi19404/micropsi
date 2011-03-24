/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/ActionGatherer.java,v 1.4 2005/09/30 15:22:37 vuine Exp $
 */
package org.micropsi.comp.server;

import java.util.ArrayList;
import java.util.List;

import org.micropsi.comp.messages.MAction;


public class ActionGatherer {

	private ArrayList<MAction> list = new ArrayList<MAction>();
	private ArrayList<MAction> dummy = new ArrayList<MAction>();
	
	public void addAction(MAction action) {
		if(action.getActionType().equals(MAction.NOOP)) return;
		synchronized(list) {
			list.add(action);
		}
	}
	
	public void nextStep() {
	}
	
	public List<MAction> retreiveAndClearStorage() {
		synchronized(list) {
			if(list.isEmpty()) return dummy;
			
			ArrayList<MAction> toReturn = new ArrayList<MAction>();
			toReturn.addAll(list);
			list.clear();
			return toReturn;
		}
	}
	
	public int getNumberOfActionsInCache() {
		return list.size();
	}
	
/*	private ArrayList first = new ArrayList();
	private ArrayList second = new ArrayList();
	
	private ArrayList current = first;
		
	public void addAction(Object action) {
		if(((MAction)action).getActionType().equals(MAction.NOOP)) return;
		current.add(action);
	}
	
	public void nextStep() {
		if(current == first) current = second; else current = first;
	}
	
	public ArrayList retreiveAndClearStorage() {
		ArrayList toReturn = new ArrayList();
		if(current == first) {
			toReturn.addAll(second);
			second.clear();
		} 	else {
			toReturn.addAll(first);
			first.clear();
		}
		return toReturn;
	}
	
	public int getNumberOfActionsInCache() {
		return current.size();
	}*/

}
