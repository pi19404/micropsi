/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/AbstractEntityLook.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetIntegrityException;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public abstract class AbstractEntityLook {
	
	protected Color bgColor;
	protected Color plusColor;
	protected Font parentFont;
	
	public AbstractEntityLook(Color bgColor, Font parentFont) {
		this.bgColor = bgColor;		
		this.parentFont = parentFont;
		this.plusColor = new Color(null,255,0,0);
	}
	
	public boolean drawLinkSelect(EntityWidget from, EntityWidget to, Link link, int mouseX, int mouseY) {
		// link selection not supported here
		return false;
	}
	
	public void drawLink(EntityWidget from, EntityWidget to, GC gc, Link link) {
		try {
			if(to != null) {
				int tox = to.getLocation().x - 8;
				int toy = to.getLocation().y + to.getLook().getSlotLinkAnchor(to, link.getLinkedSlot().getType()) - 14;
				gc.setBackground(bgColor);
				gc.setForeground(plusColor);
				gc.drawText(Integer.toString(link.getLinkedSlot().getNumberOfIncomingLinks()), tox, toy,true);
			} 
			
			if(from != null) {
				int fromx = from.getLocation().x + from.getBounds().width + 5;
				int fromy = from.getLocation().y + from.getLook().getGateLinkAnchor(from, link.getLinkingGate().getType()) - 14;
				gc.setBackground(bgColor);
				gc.setForeground(plusColor);
				gc.drawText(Integer.toString(link.getLinkingGate().getNumberOfLinks()), fromx, fromy,true);
			}
			
			if(from == null || to == null) return;

			drawLink(
				from,
				to.getLocation().x,
				to.getLocation().y+to.getLook().getSlotLinkAnchor(to,link.getLinkedSlot().getType()),
				gc,
				link.getLinkingGate().getType()
			);
		} catch (NetIntegrityException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void eraseLink(EntityWidget from, EntityWidget to, GC gc, Link link) {
		try {
			if(to == null) {
				return;
			}
			if(from == null) {
				int tox = to.getLocation().x - 8;
				int toy = to.getLocation().y + to.getLook().getSlotLinkAnchor(to, link.getLinkedSlot().getType()) - 14;
				gc.setForeground(bgColor);
				gc.drawText(Integer.toString(link.getLinkedSlot().getNumberOfIncomingLinks()), tox, toy,false);
				return;			
			}
			
			int fromx = from.getLocation().x + from.getBounds().width + 5;
			int fromy = from.getLocation().y + from.getLook().getGateLinkAnchor(from, link.getLinkingGate().getType()) - 14;
			gc.setForeground(bgColor);
			gc.drawText(Integer.toString(link.getLinkingGate().getNumberOfLinks()), fromx, fromy,false);

			eraseLink(
				from,
				to.getLocation().x,
				to.getLocation().y+to.getLook().getSlotLinkAnchor(to,link.getLinkedSlot().getType()),
				gc,
				link.getLinkingGate().getType()
			);
		} catch (NetIntegrityException e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract int getClickedSlot(EntityWidget ew, int x, int y);
	
	public abstract int getClickedGate(EntityWidget ew, int x, int y);
	
	public abstract void paintToGC(GC gc, EntityWidget nw, boolean selected, boolean absolute);
	
	public abstract int[] getCreatableLinkTypes(EntityWidget nw);
	
	public abstract int[] getAcceptableLinkTypes(EntityWidget nw);
	
	public abstract Point getNeededSize(EntityWidget nw);

	protected abstract void eraseLink(EntityWidget from, int x, int y, GC gc, int linkType);

	protected abstract void drawLink(EntityWidget from, int x, int y, GC gc, int linkType);

	public abstract int getSlotLinkAnchor(EntityWidget nw, int linkType);
	
	public abstract int getGateLinkAnchor(EntityWidget nw, int linkType);
	
	public void dispose() {
		plusColor.dispose();
	}

}
