/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/autoalign/ProtocolChainAlignment.java,v 1.3 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.autoalign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;


public class ProtocolChainAlignment implements IAutoAlignment {

	private static final String name = "protocol";
	private static final String desc = "As protocol chain";
	
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return desc;
	}

	public void align(IAutoAligner toAlign) throws MicropsiException {
		List<EntityModel> elements = toAlign.getElements();
		HashMap<String,EntityModel> chainElements = new HashMap<String,EntityModel>();
				
		// first step: sort out everything that isn't protocol and find the first
		// node
		for(int i=elements.size()-1;i>=0;i--) {
			
			// remove modules
			EntityModel e = elements.get(i);
			if(e.getEntity().getEntityType() != NetEntityTypesIF.ET_NODE) {
				elements.remove(i);
				continue;
			}
			
			// remove non-concept nodes
			Node n = (Node)e.getEntity();
			if(n.getType() != NodeFunctionalTypesIF.NT_CONCEPT) {
				elements.remove(i);
				continue;
			}
			
			// remove USTM
			if(n.getEntityName().equals("USTM")) {			
				elements.remove(i);
				continue;
			}
							
			// finally: put the good ones into the hash for quick access
			chainElements.put(n.getID(), e);
		}
		
		ArrayList<Node> retmost = new ArrayList<Node>();
		
		// second step: select the retmost nodes
		for(int i=elements.size()-1;i>=0;i--) {
			Node n = (Node)elements.get(i).getEntity();
			
			Iterator retlinks = n.getGate(GateTypesIF.GT_RET).getLinks();
			boolean hasLinksHere = false;
			while(retlinks.hasNext()) {
				Link retlink = (Link)retlinks.next();
				if(chainElements.containsKey(retlink.getLinkedEntityID())) {
					hasLinksHere = true;
					break;
				}
			}
			if(!hasLinksHere) retmost.add(n);
		}

		if(retmost.size() == 0) return;
		
		// third step: starting at the retmost, align the things
		int row = -1;
		int column = 0;

		for(int i=0;i<retmost.size();i++) {
			row++;
			column = 0;
			boolean stop = false;
			
//			System.err.println("next -----------> row: "+row+" column: "+column);
			
			Node work = retmost.get(i);
			
			while(!stop) {
			
//				System.err.print("inner: "+row+" column: "+column);
			
				EntityModel model = chainElements.get(work.getID());
//				System.err.println("changing: "+model.getEntity().getID());
				
				model.setX(20 + (column * 80));
				model.setY(20 + (row * 50));
				toAlign.moveElement(model);
			
				column++;
			
				if(column == 20) {
					row++;
					column = 0;
				}
			
				Iterator l = work.getGate(GateTypesIF.GT_POR).getLinks();
					
				Node next = null;
				while(l.hasNext()) {
					Node linked = (Node)((Link)l.next()).getLinkedEntity();
					if(chainElements.containsKey(linked.getID())) next = linked;
				}
			
				if(next == null)
					stop = true;
				else
					work = next;
			}
						
		}
		
	}

}
