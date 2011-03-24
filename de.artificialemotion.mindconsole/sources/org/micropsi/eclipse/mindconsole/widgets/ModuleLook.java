/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/ModuleLook.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Slot;

public class ModuleLook extends AbstractEntityLook {
	
	private Color darkgrey;
	private Color ggreen;
	private Color lightgrey;
	private Color blue;
	private Color black;
	private Color red;
	
	public ModuleLook() {
		super(new Color(null,255,255,255),null);
		darkgrey = new Color(null,0x66,0x66,0x66);
		lightgrey = new Color(null,0xDD,0xDD,0xDD);
		ggreen = new Color(null, 0xAA,0xCC,0xAA);
		blue = new Color(null,0x66,0x66,0xAA);
		black = new Color(null,0,0,0);
		red = new Color(null,0xBB,0x33,0x33);			
	}
	
	public ModuleLook(Color bgColor) {
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
		int fromy = from.getLocation().y + from.getLook().getGateLinkAnchor(from, linkType);
		gc.setForeground(black);
		gc.setLineWidth(2);
		gc.drawLine(fromx+15,fromy,x-15,y);
		gc.drawLine(fromx+15,fromy,fromx,fromy);
		gc.drawLine(x,y,x-15,y);
	}
	
	public Point getNeededSize(EntityWidget nw) {
		Point p = new Point(0,0);
		NetEntity entity = nw.getEntity();
		p.x = 140;
		p.y = 	(entity.getNumberOfGates() > entity.getNumberOfSlots()) ?
				nw.getEntity().getNumberOfGates() * 20 + 40 :
				nw.getEntity().getNumberOfSlots() * 20 + 40;
		return p;
	}

	public void paintToGC(GC gc, EntityWidget nw, boolean selected, boolean absolute) {
		Point neededSize = getNeededSize(nw);
		int offx = 0;
		int offy = 0;
		if(absolute) {
			offx = nw.getLocation().x;
			offy = nw.getLocation().y;
		}
		
		Color c = selected ? blue : lightgrey;
		gc.setBackground(c);
		gc.setForeground(black);
		gc.fillRectangle(offx+0,offy+0,neededSize.x-1,neededSize.y-1);
				
		// inner structure
		gc.setLineWidth(1);
		gc.drawLine(offx+60,offy+40,offx+60,offy+neededSize.y);
		gc.drawLine(offx+80,offy+40,offx+80,offy+neededSize.y);
		gc.drawLine(offx+60,offy+40,offx+80,offy+40);
		
		// gates
		Iterator iter = nw.getEntity().getGates();
		int i=0;
		while(iter.hasNext()) {
			Gate g = (Gate)iter.next();
			gc.drawLine(offx+80,offy+40+(i*20),offx+neededSize.x-1,offy+40+(i*20));
			if(g.isActive()) {
				gc.setBackground(red);
				gc.fillRectangle(offx+81,offy+41+(i*20),58,19);
			}
			gc.drawText(
				TypeStrings.gateType(g.getType()),
				offx+85,
				offy+42+(i*20)
			);
			gc.setBackground(c);
			i++;
		}

		// slots
		iter = nw.getEntity().getSlots();
		i=0;
		while(iter.hasNext()) {
			Slot s = (Slot)iter.next();
			gc.drawLine(offx+1,offy+40+(i*20),offx+59,offy+40+(i*20));
			gc.drawText(
				TypeStrings.slotType(s.getType()),
				offx+5,
				offy+42+(i*20)
			);
			i++;
		}

		// the title bar
		gc.setBackground(nw.getEntity().isActive() ? red : darkgrey);
		gc.setForeground(ggreen);
		gc.fillRectangle(offx+1,offy+1,neededSize.x-2,19);
		gc.drawText(nw.getEntity().getEntityName(),offx+2,offy+2);
		gc.setForeground(black);
		gc.setBackground(c);
			
		String nstring = "NATIVE";

		if(nw.getEntity().getEntityType() == NetEntityTypesIF.ET_MODULE_NATIVE) {
			nstring = ((NativeModule)nw.getEntity()).getImplementationClassName();
			nstring = nstring.substring(nstring.lastIndexOf(".")+1);
			
			NativeModule mod = (NativeModule)nw.getEntity();
			gc.drawText(nstring,offx+2,offy+22);
			if(mod.isImplementationBad()) {
				gc.setForeground(red);
				gc.drawText(mod.getBadMessage(), offx+5, neededSize.y-20);
				gc.setLineWidth(5);
				gc.drawLine(offx+0, offy+0, offx+neededSize.x, offy+neededSize.y);
				gc.drawLine(offx+0, offy+neededSize.y,offx+neededSize.x,offy+0);
				gc.setLineWidth(1);
			}
		} else {
			gc.drawText("NODESPACE",offx+2,offy+22);			
		}
		
		// outer box
		gc.drawRectangle(offx+0,offy+0,neededSize.x-1,neededSize.y-1);

	}
	
	public int getSlotLinkAnchor(EntityWidget ew, int linkType) {
		Iterator iter = ew.getEntity().getSlots();
		int i=0;
		while(iter.hasNext()) {
			if(((Slot)iter.next()).getType() == linkType)
				return (50 + (i*20));
			i++; 
		}
		return 30;
	}

	public int getGateLinkAnchor(EntityWidget nw, int type) {
		Iterator iter = nw.getEntity().getGates();
		int i=0;
		while(iter.hasNext()) {
			if(((Gate)iter.next()).getType() == type)
				return (50 + (i*20));
			i++;
		}
		return 30;
		
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
