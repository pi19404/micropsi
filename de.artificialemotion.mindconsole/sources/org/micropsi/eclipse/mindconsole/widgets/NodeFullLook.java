/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/NodeFullLook.java,v 1.4 2006/08/03 15:42:13 rvuine Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import org.micropsi.eclipse.mindconsole.ILinkCombinedTypes;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;

public class NodeFullLook extends AbstractEntityLook {
	
	private Color darkgrey;
	private Color ggreen;
	private Color lightgrey;
	private Color blue;
	private Color black;
	private Color red;
	
	public NodeFullLook() {
		super(new Color(null,255,255,255),null);
		darkgrey = new Color(null,0x66,0x66,0x66);
		lightgrey = new Color(null,0xDD,0xDD,0xDD);
		ggreen = new Color(null, 0xAA,0xCC,0xAA);
		blue = new Color(null,0x66,0x66,0xAA);
		black = new Color(null,0,0,0);
		red = new Color(null,0xBB,0x33,0x33);			
	}
	
	public NodeFullLook(Color bgColor) {
		super(bgColor,null);
		darkgrey = new Color(null,0x66,0x66,0x66);
		lightgrey = new Color(null,0xDD,0xDD,0xDD);
		ggreen = new Color(null, 0xAA,0xCC,0xAA);
		blue = new Color(null,0x66,0x66,0xAA);
		black = new Color(null,0,0,0);
		red = new Color(null,0xBB,0x33,0x33);			
	}

	public void dispose() {
		super.dispose();
		darkgrey.dispose();
		lightgrey.dispose();
		ggreen.dispose();
		blue.dispose();
		black.dispose();
		red.dispose();
	}
	
	public void eraseLink(EntityWidget from, int x, int y, GC gc, int linkType) {
		int fromx = from.getLocation().x + from.getBounds().width;
		int fromy = from.getLocation().y + from.getLook().getGateLinkAnchor(from, linkType);
		gc.setForeground(super.bgColor);
		gc.setLineWidth(2);
		gc.drawLine(fromx+15,fromy,x-15,y);
		gc.drawLine(fromx+15,fromy,fromx,fromy);
		gc.drawLine(x,y,x-15,y);	
	}
		
	public void drawLink(EntityWidget from, int x, int y, GC gc, int linkType) {
		int fromx = from.getLocation().x + from.getBounds().width;
		int fromy = from.getLocation().y + from.getLook().getGateLinkAnchor(from,linkType);
		gc.setForeground(black);
		gc.setLineWidth(2);
		gc.drawLine(fromx+15,fromy,x-15,y);
		gc.drawLine(fromx+15,fromy,fromx,fromy);
		gc.drawLine(x,y,x-15,y);
	}
	
	public Point getNeededSize(EntityWidget nw) {
		Point p = new Point(0,0);
		p.x = 100;
		p.y = nw.getEntity().getNumberOfGates() * 20 + 40;
		return p;
	}

	public void paintToGC(GC gc, EntityWidget nw, boolean selected, boolean absolute) {
		Point neededSize = getNeededSize(nw);

		// outer box
		Color c = selected ? blue : lightgrey;
		gc.setBackground(c);
		gc.setForeground(black);
		gc.fillRectangle(0,0,neededSize.x-1,neededSize.y-1);
		gc.drawRectangle(0,0,neededSize.x-1,neededSize.y-1);
				
		// inner structure
		gc.drawLine(40,40,40,neededSize.y);
		gc.drawLine(60,40,60,neededSize.y);
		gc.drawLine(40,40,60,40);
		
		// gates
		Iterator iter = nw.getEntity().getGates();
		int i=0;
		while(iter.hasNext()) {
			Gate g = (Gate)iter.next();
			gc.drawLine(60,40+(i*20),neededSize.x,40+(i*20));
			if(g.isActive()) {
				gc.setBackground(red);
				gc.fillRectangle(21,41+(i*20),58,19);
			}
			gc.drawText(
				TypeStrings.gateType(g.getType()),
				65,
				42+(i*20)
			);
			gc.setBackground(c);
			i++;
		}

		// slot
//		Slot s = (Slot)nw.getNode().getSlot(SlotTypesIF.STYPE_GEN);
		gc.drawLine(1,40+(0*20),39,40+(0*20));
		gc.drawText(
			TypeStrings.slotType(SlotTypesIF.ST_GEN),
			5,
			42+(0*20)
		);
		gc.setBackground(c);
		gc.drawLine(1,40+(1*20),39,40+(1*20));

		// the title bar
		gc.setBackground(nw.getEntity().isActive() ? red : darkgrey);
		gc.setForeground(ggreen);
		gc.fillRectangle(1,1,neededSize.x-2,19);
		gc.drawText(nw.getEntity().getID(),2,2);
		gc.setForeground(black);
		gc.setBackground(c);
		gc.drawText("NODE: "+TypeStrings.nodeType(((Node)nw.getEntity()).getType()),2,22);
	}
	
	public int getSlotLinkAnchor(EntityWidget w, int linkType) {
		return 70;
	}

	public int getGateLinkAnchor(EntityWidget w, int type) {
		switch(type) {
			case GateTypesIF.GT_GEN: return 50;
			case GateTypesIF.GT_POR: return 70;		
			case GateTypesIF.GT_RET: return 90;
			case GateTypesIF.GT_SUR: return 110;
			case GateTypesIF.GT_SUB: return 130;			
			case GateTypesIF.GT_CAT: return 150;			
			case GateTypesIF.GT_EXP: return 170;
			case GateTypesIF.GT_SYM: return 190;
			case GateTypesIF.GT_REF: return 210;						
		}
		return 45;
	}
	
	public int[] getAcceptableLinkTypes(EntityWidget nw) {
		int[] toReturn = {
			SlotTypesIF.ST_GEN
		};
		return toReturn;		
	}
	
	public int[] getCreatableLinkTypes(EntityWidget nw) {
		if(((Node)nw.getEntity()).getType() == NodeFunctionalTypesIF.NT_CONCEPT || ((Node)nw.getEntity()).getType() == NodeFunctionalTypesIF.NT_TOPO) {
			int[] toReturn = {
				ILinkCombinedTypes.GT_GEN,
				ILinkCombinedTypes.GT_POR,
				ILinkCombinedTypes.GT_RET,
				ILinkCombinedTypes.GT_SUB,
				ILinkCombinedTypes.GT_SUR,
				ILinkCombinedTypes.GT_CAT,
				ILinkCombinedTypes.GT_EXP,
				ILinkCombinedTypes.GT_SYM,
				ILinkCombinedTypes.GT_REF		
			};
			return toReturn;
		} else if (((Node)nw.getEntity()).getType() == NodeFunctionalTypesIF.NT_ASSOCIATOR) {
			int[] toReturn = {
			    ILinkCombinedTypes.GT_GEN,
				ILinkCombinedTypes.GT_ASSOCIATION
			};
			return toReturn;
		} else if (((Node)nw.getEntity()).getType() == NodeFunctionalTypesIF.NT_DISSOCIATOR) {
			int[] toReturn = {
			    ILinkCombinedTypes.GT_GEN,
				ILinkCombinedTypes.GT_DISSOCIATION
			};
			return toReturn;
		} else {
			int[] toReturn = {
				ILinkCombinedTypes.GT_GEN
			};
			return toReturn;
		}		 	
	}
	
	public int getClickedSlot(EntityWidget ew, int x, int y) {
		if(x > 40) return -1;
		if(y < 40) return -1;
		
		int ny = (y-40) / 20;
		
		Iterator iter = ew.getEntity().getSlots();
		int i=0;
		while(iter.hasNext()) {
			Slot s = (Slot)iter.next();
			if(i == ny) return s.getType();
			i++; 
		}
		
		return -1;		
	}
	
	public int getClickedGate(EntityWidget ew, int x, int y) {
		return -1;
	}


}
