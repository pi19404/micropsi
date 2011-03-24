/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/autoalign/ForceAlignment.java,v 1.2 2004/11/20 23:53:30 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.autoalign;

import java.util.Iterator;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;

public class ForceAlignment implements IAutoAlignment {

	private static final String name = "force";
	private static final String desc = "Relax selected";
	
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return desc;
	}

	public void align(IAutoAligner toAlign) throws MicropsiException {
		
	// force-field alignment (relaxation)
	// idea: Links have preferred angles and distances. Entities have minimal distances.
	// in each step, we perform a small correction to satisfy these preferences.
	
	// actually, this is just a cheap hack, we dont do vectors but simple nudging.
		
	int ITERATIONS = 100; // we repeat the relaxation several times. yes! it is slow!
	int LINK_LENGTH_H = 120; // this is the preferred horizontal length of links
	int LINK_LENGTH_V = 160; // this is the preferred vertical length of links
	int MIN_ENTITY_DIST = 80; // entities should be distant from each other by this
	int NUDGE_BY = 4; // entities will be moved by x units per iteration and correction
	
	for (int i=0; i<ITERATIONS; i++) {	
		Iterator iter = toAlign.getElements().iterator();
		while (iter.hasNext()) {
			EntityModel e = (EntityModel)iter.next();
			if (toAlign.isSelected(e.getEntity().getID())) {
			int x = e.getX();
			int y = e.getY();
			
			// we do the links for concept nodes only
			if (e.getEntity().getEntityType() == NetEntityTypesIF.ET_NODE) {
				Node n = (Node)e.getEntity();
				if (n.getType() == NodeFunctionalTypesIF.NT_CONCEPT) {
				// check for vertical links
			
			Iterator sublinks = n.getGate(GateTypesIF.GT_SUB).getLinks();
			while(sublinks.hasNext()) {
				Link sublink = (Link)sublinks.next();
				Node m = (Node)sublink.getLinkedEntity();
				if (!n.equals(m) && n.getParentID().equals(m.getParentID()) && toAlign.isSelected(sublink.getLinkedEntityID())) {
					// get the coordinates of the linked entity. ugly.
					Iterator jter = toAlign.getElements().iterator();
					boolean foundLinkedNode=false;
					EntityModel j=null;
					while (jter.hasNext() && !foundLinkedNode) {
						j = (EntityModel)jter.next();
						if (j.getEntity().equals(m)) foundLinkedNode = true;
					}
					if (foundLinkedNode) {
						int xoffset = x-j.getX();
						int ylength = y-j.getY();
						if (ylength > -LINK_LENGTH_V) {
							y-=NUDGE_BY;
							if (xoffset > 0) x+=NUDGE_BY;
							if (xoffset < 0) x-=NUDGE_BY;
						}
						else {
							if (ylength < -LINK_LENGTH_V) y+=NUDGE_BY;
							if (xoffset > 0) x-=NUDGE_BY;
							if (xoffset < 0) x+=NUDGE_BY;
						}
					}
				}
			}
			Iterator surlinks = n.getGate(GateTypesIF.GT_SUR).getLinks();
			while(surlinks.hasNext()) {
				Link surlink = (Link)surlinks.next();
				Node m = (Node)surlink.getLinkedEntity();
				if (!n.equals(m) && n.getParentID().equals(m.getParentID())) {
					// get the coordinates of the linked entity. ugly.
					Iterator jter = toAlign.getElements().iterator();
					boolean foundLinkedNode=false;
					EntityModel j=null;
					while (jter.hasNext() && !foundLinkedNode) {
						j = (EntityModel)jter.next();
						if (j.getEntity().equals(m)) foundLinkedNode = true;
					}
					if (foundLinkedNode) {
						int xoffset = x-j.getX();
						int ylength = y-j.getY();
						if (ylength < LINK_LENGTH_V) {
							y+=NUDGE_BY;
							if (xoffset > 0) x+=NUDGE_BY;
							if (xoffset < 0) x-=NUDGE_BY;
						}
						else {
							if (ylength > LINK_LENGTH_V) y-=NUDGE_BY;
							if (xoffset > 0) x-=NUDGE_BY;
							if (xoffset < 0) x+=NUDGE_BY;
						}
					}
				}
			}
			// check for horizontal links
			Iterator porlinks = n.getGate(GateTypesIF.GT_POR).getLinks();
			while(porlinks.hasNext()) {
				Link porlink = (Link)porlinks.next();
				Node m = (Node)porlink.getLinkedEntity();
				if (!n.equals(m) && n.getParentID().equals(m.getParentID())) {
					// get the coordinates of the linked entity. ugly.
					Iterator jter = toAlign.getElements().iterator();
					boolean foundLinkedNode=false;
					EntityModel j=null;
					while (jter.hasNext() && !foundLinkedNode) {
						j = (EntityModel)jter.next();
						if (j.getEntity().equals(m)) foundLinkedNode = true;
					}
					if (foundLinkedNode) {
						int yoffset = -y+j.getY();
						int xlength = x-j.getX();
						if (xlength > -LINK_LENGTH_H) {
							x-=NUDGE_BY;
						}
						else {
							if (xlength < -LINK_LENGTH_H) x+=NUDGE_BY;
							if (yoffset > 0) y-=NUDGE_BY;
							if (yoffset < 0) y+=NUDGE_BY;
						}
					}
				}
			}
			Iterator retlinks = n.getGate(GateTypesIF.GT_RET).getLinks();
			while(retlinks.hasNext()) {
				Link retlink = (Link)retlinks.next();
				Node m = (Node)retlink.getLinkedEntity();
				if (!n.equals(m) && n.getParentID().equals(m.getParentID())) {
					// get the coordinates of the linked entity. ugly.
					Iterator jter = toAlign.getElements().iterator();
					boolean foundLinkedNode=false;
					EntityModel j=null;
					while (jter.hasNext() && !foundLinkedNode) {
						j = (EntityModel)jter.next();
						if (j.getEntity().equals(m)) foundLinkedNode = true;
					}
					if (foundLinkedNode) {
						int yoffset = -y+j.getY();
						int xlength = x-j.getX();
						if (xlength < LINK_LENGTH_H) {
							x+=NUDGE_BY;
						}
						else {
							if (xlength > LINK_LENGTH_H) x-=NUDGE_BY;
							if (yoffset > 0) y-=NUDGE_BY;
							if (yoffset < 0) y+=NUDGE_BY;
						}
					}
				}
			}
			// check for diagonal links
			// someday we will do this...
			}
			}
				// see if we collide with other entities, correct 
				Iterator kter = toAlign.getElements().iterator();
				while (kter.hasNext()) {
					EntityModel k = (EntityModel)kter.next();
			
					if (!k.equals(e)) {
					int xdist = x-k.getX();
					int ydist = y-k.getY();
					
					// check if entities overlap
					
					if (Math.abs(xdist) < MIN_ENTITY_DIST && Math.abs(ydist) < MIN_ENTITY_DIST ) {
						// first: special case: same coordinates. only small initial correction
						if (xdist == 0 && ydist == 0) {
							x -= NUDGE_BY;
							y -= NUDGE_BY;
						}			
						else {
							// horizontal alignment, only vertical correction
							if (xdist == 0) {
								if (ydist > 0) {
									y = k.getY() + MIN_ENTITY_DIST;
								}
								else { // ydist < 0
									y = k.getY() - MIN_ENTITY_DIST;
								}
							}
							// vertical alignment, only horizontal correction 
							if (ydist == 0) {
								if (xdist > 0) {
									x = k.getX() + MIN_ENTITY_DIST;
								}
								else { // xdist < 0
									x = k.getX() - MIN_ENTITY_DIST;
								}
							} // correct both directions a little
							else { 
								if (ydist > 0) y = k.getY()+MIN_ENTITY_DIST; else y = k.getY()- MIN_ENTITY_DIST;
								if (xdist > 0) x = k.getX()+MIN_ENTITY_DIST; else x = k.getX()- MIN_ENTITY_DIST;
							}
						}
					}
				}
			}
			e.setX(x);
			e.setY(y);
			toAlign.moveElement(e);
			}
		}	
	}
		
//		
//		int i = 0;
//		Iterator iter = toAlign.getElements().iterator();
//		while(iter.hasNext()) {
//			i++;
//			EntityModel e = (EntityModel)iter.next();
//			e.setX(i*5);
//			e.setY(i*5);
//			toAlign.moveElement(e);
//		}
	}

}
