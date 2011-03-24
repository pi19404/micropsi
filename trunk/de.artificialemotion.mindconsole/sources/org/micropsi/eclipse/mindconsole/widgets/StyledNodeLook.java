/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/StyledNodeLook.java,v 1.6 2006/08/03 15:42:13 rvuine Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
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

public class StyledNodeLook extends AbstractStyledEntityLook {

	public StyledNodeLook(Color bgColor, Font parentFont) {
		super(bgColor,parentFont);			
	}

	public void dispose() {
		super.dispose();
	}
	
	public Point getNeededSize(EntityWidget nw) {
		if (showCompactNodes) {
			return new Point(compactnodewidth,2*rowheight+lineWidth);
		}
		else {
			return new Point(nodewidth,rowheight*(2+nw.getEntity().getNumberOfGates())+lineWidth);
		}
	}

	public void paintToGC(GC gc, EntityWidget nw, boolean selected, boolean absolute) {
		Point neededSize = getNeededSize(nw);
		int offx = 0;
		int offy = 0;
		if(absolute) {
			offx = nw.getLocation().x;
			offy = nw.getLocation().y;
			gc.setClipping(offx,offy,neededSize.x,neededSize.y);
		}		
		Color c;
		int lineWidth2=lineWidth/2;
		
		gc.setFont(nodeFont);

		if (showCompactNodes) {	
			// body		
			if (showActivation) {	
				c = nw.getEntity().isActive() ? calcActivationColor(((Node)nw.getEntity()).getGenActivation()) : lightgrey;
			}
			else c = lightgrey;
			gc.setBackground(c);
			gc.setForeground(black);
			gc.setLineWidth(lineWidth);
			gc.fillRectangle(offx,offy,neededSize.x-lineWidth,neededSize.y-lineWidth);
			gc.drawRectangle(offx+lineWidth2,offy+lineWidth2,neededSize.x-lineWidth,neededSize.y-lineWidth);
						
			// the title bar		
			gc.setBackground(selected ? blue : darkgrey);
			gc.setForeground(selected ? lightgrey : ggreen);
			gc.fillRectangle(offx+lineWidth,offy+lineWidth,neededSize.x-2*lineWidth,rowheight-lineWidth);
			gc.setClipping(offx+lineWidth,offy+lineWidth,neededSize.x-2*lineWidth-padding,rowheight);
			gc.drawText(nw.getEntity().getEntityName(),offx+lineWidth+padding,offy+padding);
			gc.setForeground(selected ? blue : black);
			gc.setBackground(c);
			gc.setClipping(offx+lineWidth,offy+lineWidth+rowheight,neededSize.x-2*lineWidth-padding,rowheight);
			gc.drawText(TypeStrings.nodeType(((Node)nw.getEntity()).getType()),offx+lineWidth+padding,offy+rowheight+padding);
		}
		else {			
			int gatewidth=(neededSize.x-gapsize-lineWidth)/2;
				
			gc.setBackground(lightgrey);
			gc.setForeground(selected ? blue : black);
			gc.fillRectangle(offx+0,offy+0,neededSize.x-lineWidth,neededSize.y-lineWidth);
				
			// inner structure
		
			gc.setLineWidth(lineWidth);
			c = nw.getEntity().isActive() ? calcActivationColor(((Node)nw.getEntity()).getGenActivation()) : lightgrey;
			gc.setBackground(c);
			gc.fillRectangle(offx+gatewidth+lineWidth,offy+2*rowheight,gapsize,neededSize.y-2*rowheight);
			gc.fillRectangle(offx+lineWidth,offy+rowheight,neededSize.x-2*lineWidth,rowheight);
		
			// gates
			Iterator iter = nw.getEntity().getGates();
			int i=0;
			while(iter.hasNext()) {
				Gate g = (Gate)iter.next();
				if(showActivation && g.isActive()) {
					gc.setBackground(calcActivationColor(g.getConfirmedActivation()));
					gc.fillRectangle(offx+lineWidth+gatewidth+gapsize,offy+lineWidth+(i+2)*rowheight,gatewidth,rowheight-lineWidth);
				}
				gc.drawLine(offx+lineWidth2+gatewidth+gapsize,offy+(i+2)*rowheight+lineWidth2,offx+neededSize.x-lineWidth,offy+(i+2)*rowheight+lineWidth2);
				gc.setClipping(offx+lineWidth+gatewidth+gapsize,offy+lineWidth+(i+2)*rowheight,gatewidth - padding,rowheight-lineWidth);
				gc.drawText(
					TypeStrings.gateType(g.getType()),
					offx+2*lineWidth+gatewidth+gapsize+padding,
					offy+lineWidth+(i+2)*rowheight+padding,true
				);
				gc.setClipping(offx,offy,neededSize.x,neededSize.y);
				gc.setBackground(c);
				i++;
			}

			// slots
			iter = nw.getEntity().getSlots();
			i=0;
			while(iter.hasNext()) {
				Slot s = (Slot)iter.next();
				gc.drawLine(offx,offy+(i+2)*rowheight+lineWidth2,offx+gatewidth+lineWidth,offy+(i+2)*rowheight+lineWidth2);
				gc.setClipping(offx+lineWidth,offy+lineWidth+(i+2)*rowheight,gatewidth - padding,rowheight-lineWidth);
				gc.drawText(
					TypeStrings.slotType(s.getType()),
					offx+lineWidth+padding,
					offy+lineWidth+(i+2)*rowheight+padding,true
				);
				gc.setClipping(offx,offy,neededSize.x,neededSize.y);
				i++;
			}

			// the title bar
			gc.setBackground(selected ? blue : darkgrey);
			gc.setForeground(selected ? lightgrey : ggreen);
			gc.fillRectangle(offx+lineWidth,offy+lineWidth,neededSize.x-2*lineWidth,rowheight-lineWidth);
			gc.setClipping(offx+lineWidth,offy+lineWidth,neededSize.x-2*lineWidth-padding,neededSize.y-2*lineWidth-padding);
			gc.drawText(nw.getEntity().getEntityName(),offx+lineWidth+padding,offy+padding);
			gc.setForeground(selected ? blue : black);
			gc.setBackground(c);
			gc.setClipping(offx,offy,neededSize.x,neededSize.y);	
			gc.drawText("Node: "+TypeStrings.nodeType(((Node)nw.getEntity()).getType()),offx+lineWidth+padding,offy+padding+rowheight);			
		
			// outer box
		
			gc.drawLine(offx+gatewidth+lineWidth,offy+2*rowheight+lineWidth2,offx+gatewidth+lineWidth,offy+neededSize.y);
			gc.drawLine(offx+gatewidth+gapsize+lineWidth,offy+2*rowheight+lineWidth2,offx+gatewidth+gapsize+lineWidth,offy+neededSize.y);
			gc.drawLine(offx+gatewidth+lineWidth,offy+2*rowheight+lineWidth2,offx+gatewidth+gapsize+lineWidth,offy+2*rowheight+lineWidth2);
			gc.setForeground(black);
			gc.drawRectangle(offx+lineWidth2,offy+lineWidth2,neededSize.x-lineWidth,neededSize.y-lineWidth);
		}

		gc.setClipping(offx,offy,neededSize.x,neededSize.y);
	}
	
	public int getSlotLinkAnchor(EntityWidget w, int linkType) {
//		switch(linktype) {
//			case ();
//		}
		if (showCompactNodes) return lineWidth+rowheight/2;
		else return (lineWidth+rowheight*5/2);
	}

	public int getGateLinkAnchor(EntityWidget w, int type) {
		if (showCompactNodes) {
			int height = w.getBounds().height;
			switch(type) {
				case GateTypesIF.GT_GEN: return lineWidth+rowheight/2;
				case GateTypesIF.GT_ASSOCIATION: return lineWidth+rowheight;
				case GateTypesIF.GT_DISSOCIATION: return lineWidth+rowheight;
				case GateTypesIF.GT_POR: return lineWidth+rowheight+rowheight/2;		
				case GateTypesIF.GT_RET: return -(lineWidth+rowheight+rowheight/2);
				case GateTypesIF.GT_SUR: return 0;
				case GateTypesIF.GT_SUB: return height+1;			
				case GateTypesIF.GT_CAT: return 1;			
				case GateTypesIF.GT_EXP: return -height;
				case GateTypesIF.GT_SYM: return -1;			
				case GateTypesIF.GT_REF: return height;
				case ILinkCombinedTypes.LCTYPE_POR_RET: return lineWidth+rowheight+rowheight/2;
				case ILinkCombinedTypes.LCTYPE_SUB_SUR: return height+1;
				case ILinkCombinedTypes.LCTYPE_CAT_EXP: return 1;
				case ILinkCombinedTypes.LCTYPE_SYM_REF: return -1;

			}												
		}
		else {
			Iterator iter = w.getEntity().getGates();
			int i=0;
			while(iter.hasNext()) {
				if(((Gate)iter.next()).getType() == type)
					return  (i+2)*rowheight+rowheight/2;
				i++;
			}
			return lineWidth+rowheight*3/2;
		}
		return lineWidth+rowheight/2;
	}
	
	public int[] getAcceptableLinkTypes(EntityWidget nw) {
		int[] toReturn = {
			SlotTypesIF.ST_GEN
		};
		return toReturn;		
	}
	
	public int[] getCreatableLinkTypes(EntityWidget nw) {
		int type = ((Node)nw.getEntity()).getType(); 
		if(	type == NodeFunctionalTypesIF.NT_CONCEPT || type == NodeFunctionalTypesIF.NT_TOPO) {
			int[] toReturn = {
				ILinkCombinedTypes.GT_GEN,
				ILinkCombinedTypes.LCTYPE_POR_RET,
				ILinkCombinedTypes.LCTYPE_SUB_SUR,
				ILinkCombinedTypes.LCTYPE_CAT_EXP,
				ILinkCombinedTypes.LCTYPE_SYM_REF,
			};
			return toReturn;
		} else if(type == NodeFunctionalTypesIF.NT_CHUNK) {
			int[] toReturn = {
					ILinkCombinedTypes.GT_GEN,
					ILinkCombinedTypes.GT_SUB,
					ILinkCombinedTypes.LCTYPE_POR_RET,
					ILinkCombinedTypes.LCTYPE_SUB_SUR,
					ILinkCombinedTypes.LCTYPE_CAT_EXP,
					ILinkCombinedTypes.LCTYPE_SYM_REF,
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
		/*
		if(x > 40) return -1;
		if(y < 10) return -1;
		
		int ny = (y-24) / 12;
		
		Iterator iter = ew.getEntity().getSlots();
		int i=0;
		while(iter.hasNext()) {
			Slot s = (Slot)iter.next();
			if(i == ny) return s.getType();
			i++; 
		}
		
		return -1;
		*/
		Iterator iter=ew.getEntity().getSlots();
		return ((Slot)iter.next()).getType();		
	}
	
	public int getClickedGate(EntityWidget ew, int x, int y) {
		return -1;
	}

	// drawLink is still necessary for editing purposes

}
