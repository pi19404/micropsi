/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/autoalign/SchemaAlignment.java,v 1.2 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.autoalign;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;


public class SchemaAlignment implements IAutoAlignment {

	private static final String name = "schema";
	private static final String desc = "As schema";
	
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return desc;
	}
	
	public void align(IAutoAligner toAlign) throws MicropsiException {
		List<EntityModel> elements = toAlign.getElements();
		
		// first step: find head nodes and calc depth
		
		ArrayList<EntityModel> headNodes = new ArrayList<EntityModel>();
		for(int i=0;i<elements.size();i++) {
			NetEntity e = elements.get(i).getEntity();
			if(e.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Node n = (Node)e;
			if(	n.getType() != NodeFunctionalTypesIF.NT_CHUNK &&
				n.getType() != NodeFunctionalTypesIF.NT_CONCEPT) {
				
				continue;
			}
			
			if(!n.getGate(GateTypesIF.GT_SUR).hasLinks()) {
				headNodes.add(elements.get(i));
			}
		}
		
		// second step: recursively add levels to each head node
		
		for(int i=0;i<headNodes.size();i++) {
			EntityModel model = headNodes.get(i);
			
			addLevels(model,0,0,0,toAlign);
		}
		
	}

	/**
	 * @param model
	 * @throws MicropsiException
	 */
	private void addLevels(EntityModel model, int level, int xoffset, int yoffset, IAutoAligner toAlign) throws MicropsiException {
			
		ArrayList<EntityModel> isolanis = new ArrayList<EntityModel>();
//		ArrayList chainstarts = new ArrayList();
//		ArrayList other = new ArrayList();
		
/*		Iterator links = model.getEntity().getGate(GateTypesIF.GT_SUB).getLinks();
		while(links.hasNext()) {
			NetEntity e = ((Link)links.next()).getLinkedEntity();
			if(e.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Node n = (Node)e;
			
			EntityModel nModel = netmodel.getModel(n.getID());
			
			if(	n.getType() != NodeFunctionalTypesIF.NT_CHUNK &&
				n.getType() != NodeFunctionalTypesIF.NT_CONCEPT) {
				
				continue;
			}
			
			if(	!n.getGate(GateTypesIF.GT_POR).hasLinks() &&
				!n.getGate(GateTypesIF.GT_RET).hasLinks()) {
				
				isolanis.add(nModel);
			} else if(	n.getGate(GateTypesIF.GT_POR).hasLinks() &&
						!n.getGate(GateTypesIF.GT_RET).hasLinks()) {
				
				chainstarts.add(nModel);
			} else {
				
				other.add(nModel);
			}		
		}*/
	
		isolanis = getInterestingModels(model);
		int x = xoffset;
		
		// isolanis
		for(int i=0;i<isolanis.size();i++) {
			EntityModel isolani = isolanis.get(i);

			move(isolani,x,yoffset,toAlign);
			
			addLevels(isolani, level+1, x, yoffset+1, toAlign);

			int n = getNecessaryWidth(isolani,0);
			if(i>n) n = i;
						
			x += n;
			
			
		}
		

		
	}


	/**
	 * @param isolani
	 * @param i
	 * @param j
	 * @param toAlign
	 */
	private void move(EntityModel model, int x, int y, IAutoAligner toAlign) {
		model.setX(200+(x*80));
		model.setY(200+(y*60));
		toAlign.moveElement(model);
		
	}
	
	// Ermittelt die maximale Zahl von [Children-Links auf einer Ebene]
	public int getNecessaryWidth(EntityModel m, int max) throws MicropsiException {
		
		ArrayList<EntityModel> children = getInterestingModels(m);
		if(children.size() > max) max = children.size();
		
		for(int i=0;i<children.size();i++) {
			int x = getNecessaryWidth(children.get(i),max);
			if(x > max) max = x;
		}
		
		return max;
	}
	
	public ArrayList<EntityModel> getInterestingModels(EntityModel model) throws MicropsiException {

		ArrayList<EntityModel> interestingChildren = new ArrayList<EntityModel>();
		
		NetModel netmodel = model.getUnderlyingNetModel();

		
		Iterator links = model.getEntity().getGate(GateTypesIF.GT_SUB).getLinks();
		while(links.hasNext()) {
			NetEntity e = ((Link)links.next()).getLinkedEntity();
			if(e.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Node n = (Node)e;
			
			EntityModel nModel = netmodel.getModel(n.getID());
			
			if(	n.getType() != NodeFunctionalTypesIF.NT_CHUNK &&
				n.getType() != NodeFunctionalTypesIF.NT_CONCEPT) {
				
				continue;
			}
			
			interestingChildren.add(nModel);
		}
		
		return interestingChildren;
	}
}
