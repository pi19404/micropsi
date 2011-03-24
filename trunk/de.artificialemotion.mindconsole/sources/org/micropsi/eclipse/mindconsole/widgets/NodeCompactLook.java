/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/NodeCompactLook.java,v 1.4 2006/08/03 15:42:13 rvuine Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import org.micropsi.eclipse.mindconsole.ILinkCombinedTypes;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.SlotTypesIF;

public class NodeCompactLook extends AbstractEntityLook {
	
	private Color darkgrey;
	private Color ggreen;
	private Color lightgrey;
	private Color blue;
	private Color black;
	private Color red;
	
	private Color linkSubSur;
	private Color linkPorRet;
	private Color linkCatSep;
	private Color linkSymRef;
	
	public NodeCompactLook() {
		super(new Color(null,255,255,255),null);
		initializeResources();
	}
	
	public NodeCompactLook(Color bgColor) {
		super(bgColor,null);
		initializeResources();
	}
	
	private void initializeResources() {
		darkgrey = new Color(null,0x66,0x66,0x66);
		lightgrey = new Color(null,0xDD,0xDD,0xDD);
		ggreen = new Color(null, 0xAA,0xCC,0xAA);
		blue = new Color(null,0x66,0x66,0xAA);
		black = new Color(null,0,0,0);	
		red = new Color(null,0xBB,0x33,0x33);
		linkSubSur = new Color(null, 230,128,30);
		linkPorRet = new Color(null, 40,220,100);
		linkCatSep = new Color(null, 90,120,178);
		linkSymRef = new Color(null, 250,250,50);
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "linkcombinedtypes";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case ILinkCombinedTypes.LCTYPE_SUB_SUR:
						return "SUB/SUR";
					case ILinkCombinedTypes.LCTYPE_POR_RET:
						return "POR/RET";
					case ILinkCombinedTypes.LCTYPE_CAT_EXP:
						return "CAT/EXP";
					case ILinkCombinedTypes.LCTYPE_SYM_REF:
						return "SYM/REF";
					default:	return null;
				}
			}

			public String slotType(int type) {
				return null;
			}
			
		});
	}

	public void dispose() {
		super.dispose();
		darkgrey.dispose();
		lightgrey.dispose();
		ggreen.dispose();
		blue.dispose();
		black.dispose();
		red.dispose();
		linkSubSur.dispose();
		linkPorRet.dispose();
		linkCatSep.dispose();
		linkSymRef.dispose();
	}
		
	public void eraseLink(EntityWidget from, int x, int y, GC gc, int linkType) {
		int fromx = from.getLocation().x + from.getBounds().width;
		int fromy = from.getLocation().y + getGateLinkAnchor(from, linkType);
		gc.setForeground(super.bgColor);
		gc.setLineWidth(2);
		gc.drawLine(fromx+15,fromy,x-15,y);
		gc.drawLine(fromx+15,fromy,fromx,fromy);
		gc.drawLine(x,y,x-15,y);	
	}
		
	public void drawLink(EntityWidget from, int x, int y, GC gc, int linkType) {
		int fromx = from.getLocation().x + from.getBounds().width;
		int fromy = from.getLocation().y + from.getLook().getGateLinkAnchor(from, linkType);
		
		gc.setLineWidth(2);

		switch(linkType) {
			case GateTypesIF.GT_SUB:
			case ILinkCombinedTypes.LCTYPE_SUB_SUR:
				gc.setForeground(linkSubSur);
				gc.drawLine(fromx+15,fromy,x-15,y);
				gc.drawLine(fromx+15,fromy,fromx,fromy);
				gc.drawLine(x,y,x-15,y);
				break;
			case GateTypesIF.GT_POR:
			case ILinkCombinedTypes.LCTYPE_POR_RET:	
				gc.setForeground(linkPorRet);
				gc.drawLine(fromx+15,fromy,x-15,y);
				gc.drawLine(fromx+15,fromy,fromx,fromy);
				gc.drawLine(x,y,x-15,y);				
				break;
			case ILinkCombinedTypes.LCTYPE_CAT_EXP:
			case GateTypesIF.GT_CAT:
				gc.setForeground(linkCatSep);
				gc.drawLine(fromx+15,fromy,x-15,y);
				gc.drawLine(fromx+15,fromy,fromx,fromy);
				gc.drawLine(x,y,x-15,y);
				break;
			case ILinkCombinedTypes.LCTYPE_SYM_REF:
			case GateTypesIF.GT_SYM:
				gc.setForeground(linkSymRef);
				gc.drawLine(fromx+15,fromy,x-15,y);
				gc.drawLine(fromx+15,fromy,fromx,fromy);
				gc.drawLine(x,y,x-15,y);
				break;
			case GateTypesIF.GT_GEN:
				gc.setForeground(black);
				gc.drawLine(fromx+15,fromy,x-15,y);
				gc.drawLine(fromx+15,fromy,fromx,fromy);
				gc.drawLine(x,y,x-15,y);			
				break;
			case -1:
				gc.setForeground(black);
				gc.drawLine(fromx+15,fromy,x-15,y);
				gc.drawLine(fromx+15,fromy,fromx,fromy);
				gc.drawLine(x,y,x-15,y);			
				break;			
			default:			
		}
		
	}
	
	public Point getNeededSize(EntityWidget nw) {
		Point p = new Point(0,0);
		p.x = 60;
		p.y = 40;
		return p;
	}

	public void paintToGC(GC gc, EntityWidget nw, boolean selected,boolean absolute) {
		Point neededSize = getNeededSize(nw);
		int offx = 0;
		int offy = 0;
		if(absolute) {
			offx = nw.getLocation().x;
			offy = nw.getLocation().y;
		}

		// outer box
		Color c = selected ? blue : lightgrey;
		gc.setLineWidth(1);
		gc.setBackground(c);
		gc.setForeground(black);
		gc.fillRectangle(offx+0,offy+0,neededSize.x-1,neededSize.y-1);
		gc.drawRectangle(offx+0,offy+0,neededSize.x-1,neededSize.y-1);
						
		// the title bar		
		gc.setBackground(nw.getEntity().isActive() ? red : darkgrey);
		gc.setForeground(ggreen);
		gc.fillRectangle(offx+1,offy+1,neededSize.x-2,19);
		gc.drawText(nw.getEntity().getEntityName(),offx+2,offy+2);
		gc.setForeground(black);
		gc.setBackground(c);
		gc.drawText(TypeStrings.nodeType(((Node)nw.getEntity()).getType()),offx+2,offy+22);
	}
	
	public int getSlotLinkAnchor(EntityWidget nw, int linkType) {
		return 30;
	}

	public int getGateLinkAnchor(EntityWidget nw, int type) {
		return 30;
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
				ILinkCombinedTypes.LCTYPE_POR_RET,
				ILinkCombinedTypes.LCTYPE_SUB_SUR,
				ILinkCombinedTypes.LCTYPE_CAT_EXP,
				ILinkCombinedTypes.LCTYPE_SYM_REF
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
		return SlotTypesIF.ST_GEN;
	}
	
	public int getClickedGate(EntityWidget ew, int x, int y) {
		return -1;
	}


}
