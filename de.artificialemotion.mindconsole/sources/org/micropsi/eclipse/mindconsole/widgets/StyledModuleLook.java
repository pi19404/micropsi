/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/StyledModuleLook.java,v 1.3 2005/10/20 14:02:44 vuine Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Slot;

public class StyledModuleLook extends AbstractStyledEntityLook {
		
	public StyledModuleLook(Color bgColor, Font parentFont) {
		super(bgColor,parentFont);		
	}

	public void dispose() {
		super.dispose();
	}
	
	public Point getNeededSize(EntityWidget nw) {
		Point p = new Point(0,0);
		NetEntity entity = nw.getEntity();
		
		//todo: Why does this happen? Why are there widgets without entities
		if(entity == null) {
			p.x = modulewidth;
			return p;
		}
		p.x = modulewidth;
		p.y = 	(entity.getNumberOfGates() > entity.getNumberOfSlots()) ?
				(2+entity.getNumberOfGates()) * rowheight + lineWidth :
				(2+entity.getNumberOfSlots()) * rowheight + lineWidth;		
		return p;		
	}

	public void paintToGC(GC gc, EntityWidget nw, boolean selected, boolean absolute) {
		Point neededSize = getNeededSize(nw);
		int lineWidth2=lineWidth/2;
		Color c;
		int gatewidth=(neededSize.x-gapsize-lineWidth)/2;
		int offx = 0;
		int offy = 0;
		if(absolute) {
			offx = nw.getLocation().x;
			offy = nw.getLocation().y;
			gc.setClipping(offx, offy, neededSize.x, neededSize.y);
		}
		gc.setFont(nodeFont);
	
		gc.setBackground(lightgrey);
		gc.setForeground(selected ? blue : black);
		gc.fillRectangle(offx+0,offy+0,neededSize.x-lineWidth,neededSize.y-lineWidth);
		
		
				
		// inner structure
		
		gc.setLineWidth(lineWidth);
		if (showActivation) c = nw.getEntity().isActive() ? fullact : lightgrey;
		else c=lightgrey;
		gc.setBackground(c);
		gc.fillRectangle(offx+gatewidth+lineWidth,offy+2*rowheight,gapsize,neededSize.y-2*rowheight-lineWidth);
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
			gc.drawLine(offx+lineWidth+gatewidth+gapsize,offy+(i+2)*rowheight+lineWidth2,offx+neededSize.x-lineWidth,offy+(i+2)*rowheight+lineWidth2);
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
		gc.setClipping(offx,offy,neededSize.x,neededSize.y);
		gc.setForeground(selected ? blue : black);
		gc.setBackground(c);
			
		String nstring = "NATIVE";

		if(nw.getEntity().getEntityType() == NetEntityTypesIF.ET_MODULE_NATIVE) {
			nstring = ((NativeModule)nw.getEntity()).getImplementationClassName();
			nstring = nstring.substring(nstring.lastIndexOf(".")+1);
			
			NativeModule mod = (NativeModule)nw.getEntity();
			gc.drawText(nstring,offx+lineWidth+padding,offy+padding+rowheight, true);
			if(mod.isImplementationBad()) {
				gc.setForeground(red);
				gc.drawText(mod.getBadMessage(), offx+5, neededSize.y-20);
				gc.setLineWidth(5);
				gc.drawLine(offx+0, offy+0, offx+neededSize.x, offy+neededSize.y);
				gc.drawLine(offx+0, offy+neededSize.y,offx+neededSize.x,offy+0);
				gc.setLineWidth(lineWidth);
			}
		} else {
			gc.drawText("NODESPACE",offx+lineWidth+padding,offy+padding+rowheight);			
		}
		
		// outer box
		
		gc.drawLine(offx+gatewidth+lineWidth,offy+2*rowheight+lineWidth2,offx+gatewidth+lineWidth,offy+neededSize.y);
		gc.drawLine(offx+gatewidth+gapsize+lineWidth,offy+2*rowheight+lineWidth2,offx+gatewidth+gapsize+lineWidth,offy+neededSize.y);
		gc.drawLine(offx+gatewidth+lineWidth,offy+2*rowheight+lineWidth2,offx+gatewidth+gapsize+lineWidth,offy+2*rowheight+lineWidth2);
		gc.setForeground(black);
		gc.drawRectangle(offx+lineWidth2,offy+lineWidth2,neededSize.x-lineWidth,neededSize.y-lineWidth);
	}
	
	public int getSlotLinkAnchor(EntityWidget ew, int linkType) {
		Iterator iter = ew.getEntity().getSlots();
		int i=0;
		while(iter.hasNext()) {
			if(((Slot)iter.next()).getType() == linkType)
				return lineWidth+(i+2)*rowheight+rowheight/2;
			i++; 
		}
		return lineWidth+rowheight*3/2;
	}

	public int getGateLinkAnchor(EntityWidget nw, int type) {
		Iterator iter = nw.getEntity().getGates();
		int i=0;
		while(iter.hasNext()) {
			if(((Gate)iter.next()).getType() == type)
				return  lineWidth+(i+2)*rowheight+rowheight/2;
			i++;
		}
		return lineWidth+rowheight*3/2;
		
	}
	
	public int[] getAcceptableLinkTypes(EntityWidget nw) {
		int[] toReturn = new int[nw.getEntity().getNumberOfSlots()];
		Iterator iter = nw.getEntity().getSlots();
		int i = 0;
		while(iter.hasNext()) {
			Slot s = (Slot)iter.next();
			toReturn[i] = s.getType();
			i++;
		}
		return toReturn;		
	}
	
	public int[] getCreatableLinkTypes(EntityWidget nw) {
		int[] toReturn = new int[nw.getEntity().getNumberOfGates()];
		Iterator iter = nw.getEntity().getGates();
		int i = 0;
		while(iter.hasNext()) {
			Gate g = (Gate)iter.next();
			toReturn[i] = g.getType();
			i++;
		}
		return toReturn;		
	}

	public int getClickedSlot(EntityWidget ew, int x, int y) {
		if(x > modulewidth/2) return -1;
		if(y < 2*rowheight) return -1;
		
		int ny = (y-2*rowheight) / rowheight;
		
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
