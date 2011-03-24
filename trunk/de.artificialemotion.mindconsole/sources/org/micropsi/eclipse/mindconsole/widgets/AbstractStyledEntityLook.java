/**
 *  $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/AbstractStyledEntityLook.java,v 1.4 2005/09/30 15:17:11 vuine Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LinkST;
import org.micropsi.nodenet.LinkTypesIF;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetIntegrityException;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractStyledEntityLook extends AbstractEntityLook {
	
	// switches 
	
	protected boolean showPlusWhenNeeded = true;
	protected boolean showArrowheads = true;
	protected boolean showActivation = true;
	protected boolean showActivatedLinks = true;
	protected boolean showCompactNodes = true;	
	protected boolean showAnnotationsWhenNeeded = true;

	protected Color fullweight;
	protected Color negweight;
	protected Color noweight;
	protected Color fullact;
	protected Color negact;
	protected Color noact;
	private Color linkcolor;
	
	protected Color darkgrey;
	protected Color ggreen;
	protected Color lightgrey;
	protected Color blue;
	protected Color black;
	protected Color red;
	
	protected Color pluscolor;

	protected Font plusFont;
	protected Font nodeFont;
	
	// values for links 

	protected int linkWidth = 1;
	protected int arrowLength = 7;
	protected int arrowWidth = 3;

	protected int bent = 10; // this is the distance where an outgoing link bents
	protected int pull = 16; // this is the distance where an incoming link bents
	
	
	protected int offCenter = 6; // this determines the distance between paired links
	
	protected int plusCircleSize = 10; // this is the size of the circle containing linknumbers
	protected int plusFontSize = 6; // this is the size of the corresponding font
	
	protected int annotationDistance = 7; // distance of link annotations from the link's middle

	// values for nodes and modules
	
	protected int fontSize = 7;
	protected int padding = 1; // offset for text in row
	protected int rowheight = 13; 
	protected int lineWidth = 1;
	
	protected int nodewidth = 80;
	protected int compactnodewidth = 52;
	protected int gapsize = 4; // gap between gates and slots
	protected int modulewidth = 96;				
	
	protected final static int FROM_E = 1;
	protected final static int FROM_S = 2;
	protected final static int FROM_W = 3;
	protected final static int FROM_N = 4;
	protected final static int FROM_NE = 5;
	protected final static int FROM_SE = 6;
	protected final static int FROM_SW = 7;
	protected final static int FROM_NW = 8;
	
	public final static int SHOW_PLUS_WHEN_NEEDED = 		1<<0;
	public final static int SHOW_ACTIVATION_IN_NODES = 		1<<1;
	public final static int SHOW_ACTIVATION_ON_LINKS =		1<<2;
	public final static int SHOW_COMPACT_NODES = 			1<<3;
	public final static int SHOW_ARROWHEADS =				1<<4;	
	public final static int SHOW_ANNOTATIONS_WHEN_NEEDED =	1<<5;	
	
	public AbstractStyledEntityLook(Color bgColor, Font parentFont) {
		super(bgColor,parentFont);
		fullweight = new Color(null,0,0,0);
		noweight = new Color(null,200,200,200);
		negweight = new Color(null,0,0,255);
		fullact = new Color(null,0,200,90);
		noact = new Color(null,0,0,0);
		negact = new Color(null,255,0,0);
		
		darkgrey = new Color(null,0x66,0x66,0x66);
		lightgrey = new Color(null,0xDD,0xDD,0xDD);
		ggreen = new Color(null, 0xAA,0xCC,0xAA);
		blue = new Color(null,0x66,0x66,0xAA);
		black = new Color(null,0,0,0);
		red = new Color(null,0xBB,0x33,0x33);	
		
		plusColor = new Color(null,180,20,20);	
		
		setScale(100);	
	}
	public void dispose() {
		fullweight.dispose();
		noweight.dispose();
		negweight.dispose();
		fullact.dispose();
		noact.dispose();
		negact.dispose();
		darkgrey.dispose();
		lightgrey.dispose();
		ggreen.dispose();
		blue.dispose();
		black.dispose();
		red.dispose();
		plusColor.dispose();
		
		if (nodeFont!=null) nodeFont.dispose();
		if (plusFont!=null) plusFont.dispose();
	}
	
	/**
	 * Sets the values for the display of entity widgets and links.
	 * All values are recorded here and scaled according to the parameter (a percentage).
	 * @param percent the scale factor
	 * @return the scale factor after bounding
	 */
	public int setScale(int percent) {
		if (percent<1) percent = 1;
		if (percent>9999) percent = 9999;
		
		int f = 100000/percent;

		// all values * 1000
		// edit these values to change the appearance of the style 

		linkWidth = 	scaleValue(  1200,f,1);
		arrowLength = 	scaleValue(  7000,f);
		arrowWidth =	scaleValue(  3000,f);
		
		bent =			scaleValue(  7000,f); // this is the distance where an outgoing link bents
		pull =			scaleValue( 16000,f); // this is the distance where an incoming link bents
	
		offCenter =		scaleValue(  6000,f); // this determines the distance between paired links
	
		plusCircleSize =scaleValue( 12000,f,1); // this is the size of the circle containing linknumbers
		plusFontSize =	scaleValue(  7000,f,1); // this is the size of the corresponding font
		
		annotationDistance = scaleValue (  4000,f,1); // distance of annotation from link's middle

		// values for nodes and modules 
	
		fontSize =		scaleValue(  7000,f,1);
		padding =		scaleValue(  2000,f); // vertical and horizontal offset for text in row
		rowheight =		scaleValue( 16000,f,1); 
		
		lineWidth =		scaleValue(   800,f,1);
	
		nodewidth =		scaleValue( 80800,f,3);
		gapsize =		scaleValue(  7000,f); 		// gap between gates and slots
		modulewidth	=	scaleValue(100800,f,3);
		compactnodewidth =	scaleValue( 52800,f,3);
		
		// set font sizes

		if (nodeFont!=null) nodeFont.dispose();
		if (plusFont!=null) plusFont.dispose();
		FontData[] customFontData = parentFont.getFontData();
		customFontData[0].height=fontSize; 
		nodeFont = new Font (Display.getCurrent(),customFontData);
		customFontData[0].height=plusFontSize; 
		plusFont = new Font (Display.getCurrent(),customFontData);

		return percent;
	}
	
	/**
	 * Sets the appearance of entity widgets and links.
	 * @param style an integer consisting of OR-merged style bits
	 */
	public void setAppearance(int style) {
		showCompactNodes = (style & SHOW_COMPACT_NODES) > 0 ;
		showPlusWhenNeeded = (style & SHOW_PLUS_WHEN_NEEDED) > 0 ;
		showArrowheads = (style & SHOW_ARROWHEADS) > 0 ; 
		showActivation = (style & SHOW_ACTIVATION_IN_NODES) > 0 ;
		showActivatedLinks = (style & SHOW_ACTIVATION_ON_LINKS) > 0 ;
		showAnnotationsWhenNeeded = (style & SHOW_ANNOTATIONS_WHEN_NEEDED) > 0 ;
	}
	
	private int scaleValue(int value, int factor, int min) {
		value = (value + (factor>>1)) / factor; 
		return (value > min ? value : min );
	}

	private int scaleValue(int value, int factor) {
		return (value + (factor>>1)) / factor; 
	}
	
	/**
	 * Allows to set fontSize externally (on some systems nessesary)
	 * @param fontSize - font size at 100%
	 * @param percent - scale value
	 */
	public void setFontSize(int fontSize, int percent) {
		int f = 100000/percent;
		fontSize =	scaleValue(fontSize*1000,f,1);
		if (nodeFont!=null) nodeFont.dispose();
		if (plusFont!=null) plusFont.dispose();
		FontData[] customFontData = parentFont.getFontData();
		customFontData[0].height=fontSize; 
		nodeFont = new Font (Display.getCurrent(),customFontData);
		customFontData[0].height=plusFontSize; 
		plusFont = new Font (Display.getCurrent(),customFontData);
	}
	
	
	/**
	 * Draws a link between an entity widget and a pair of coordinates. 
	 * This is usually called when a link is not yet connected.
	 * @param from Starting widget
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param gc the graphics context
	 * @param linkType the linkType
	 */
	public void drawLink(EntityWidget from, int x, int y, GC gc, int linkType) {
		gc.setForeground(fullweight);
		draw(from,null,x,y,gc,null,linkType,0,0,false);
	}
	
	/**
	 * Draws a link between two entity widgets. If one of them is not given,
	 * the link is assumed to end in another nodespace and only the connector is drawn.
	 * Positions of slots and gates are inferred from the link.
	 * @param from Starting widget
	 * @param to Ending widget
	 * @param gc the graphics context
	 * @param link the link
	 */
	public void drawLink(EntityWidget from, EntityWidget to, GC gc, Link link) {
		int slots=0;
		int gates=0;
		
		// somewhat unclear why this is necessary, but it is.			
		gc.setBackground(bgColor);
			
		try {
			if(to != null && from == null) {
				slots = link.getLinkedSlot().getNumberOfIncomingLinks();
				drawOnlyInput(to,gc,link,link.getLinkingGate().getType(),slots,false);
				return;
			}
			if(from != null && to == null) {
				gates=link.getLinkingGate().getNumberOfLinks();
				drawOnlyOutput(from,gc,link,link.getLinkingGate().getType(),gates,false);
				return;
			}
				
			if (showActivatedLinks) {
				calcActivationColor(link.getLinkingGate().getConfirmedActivation(),link.getWeight());
				gc.setForeground(linkcolor);
			} else {
				gc.setForeground(fullweight);	
			}
			draw(from, to, 0,0,gc,link,link.getLinkingGate().getType(),slots,gates,false);
						
		} catch (NetIntegrityException e) {
			throw new RuntimeException(e);
		}	
	}
	/**
	 * Selection routine for links, must be synchronous to drawLink
	 */
	public boolean drawLinkSelect(EntityWidget from, EntityWidget to, Link link, int mouseX, int mouseY) {
		int slots=0;
		int gates=0;
					
		try {
			if(to != null) {
				slots = link.getLinkedSlot().getNumberOfIncomingLinks();
				if(from == null) {
					return drawOnlyInputSelect(to,link,link.getLinkingGate().getType(),slots,false,mouseX,mouseY);
				}
			}
			if(from != null) {
				gates=link.getLinkingGate().getNumberOfLinks();
				if (to == null) {
					return drawOnlyOutputSelect(from,link,link.getLinkingGate().getType(),gates,false,mouseX,mouseY);
				}
			} 
			
			if (to==null || from==null) return false;	
			
			return drawSelect(from, to, 0,0,link,link.getLinkingGate().getType(),slots,gates,false,mouseX,mouseY);
						
		} catch (NetIntegrityException e) {
			throw new RuntimeException(e);
		}	
	}
	/**
	 * Erases a link between an entity widget and a pair of coordinates. 
	 * This is usually called when a link is not yet connected.
	 * (For redrawing purposes.)
	 * @param from Starting widget
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param gc the graphics context
	 * @param linkType the linkType
	 */
	public void eraseLink(EntityWidget from, int x, int y, GC gc, int linkType) {
		gc.setBackground(bgColor);
		gc.setForeground(bgColor);
		draw(from,null,x,y,gc,null,linkType,0,0,true);
	}
	
	/**
	 * Erases a link between two entity widgets. If one of them is not given,
	 * the link is assumed to end in another nodespace and only the connector is drawn.
	 * Positions of slots and gates are inferred from the link.
	 * (For redrawing purposes.)
	 * @param from Starting widget
	 * @param to Ending widget
	 * @param gc the graphics context
	 * @param link the link
	 */
	public void eraseLink(EntityWidget from, EntityWidget to, GC gc, Link link) {
		
		int slots = 0;
		int gates = 0;
		try {
			if(to != null) {
				slots = link.getLinkedSlot().getNumberOfIncomingLinks();
				if (from == null) {
					gc.setForeground(bgColor);
					drawOnlyInput(to,gc,link,link.getLinkingGate().getType(),slots,true);
				}
			} 
			if(from != null) {
				gates=link.getLinkingGate().getNumberOfLinks();
				if (to == null) {
					gc.setForeground(bgColor);
					drawOnlyOutput(from,gc,link,link.getLinkingGate().getType(),gates,true);
				}
			}
			
			if(from == null || to == null) return;

			gc.setBackground(bgColor);
			gc.setForeground(bgColor);
			
			draw(from, to, to.getLocation().x,0,gc,link,link.getLinkingGate().getType(),slots,gates,true);
			
		} catch (NetIntegrityException e) {
			throw new RuntimeException(e);
		}
		
		gc.setBackground(bgColor);
		gc.setForeground(bgColor);
		draw(from, to, to.getLocation().x,0,gc,link,link.getLinkingGate().getType(),slots,gates,true);
	}

	/**
	 * Returns the direction a link is sticking out of a node
	 * @param from the node in question
	 * @param linkType the linkType
	 * @return int direction
	 */
	private int getDirectionFrom(EntityWidget from, int linkType) {
		int ystart = from.getLook().getGateLinkAnchor(from, linkType); 
		int height = from.getBounds().height;
		if (ystart == 0) return FROM_N;
		if (ystart == -1) return FROM_NW;
		if (ystart == 1) return FROM_NE;
		if (ystart == height) return FROM_SE;
		if (ystart == -height) return FROM_SW;
		if ((ystart < -1) && (ystart-1 >-height)) return FROM_W;
		if (ystart > height) return FROM_S;
		return FROM_E;		
	}
	
	/**
	 * Returns the starting point of a link, given a direction of origin.
	 * @param from the entity widget
	 * @param directionOfOrigin the direction
	 * @param linkType the linkType
	 * @return Point the coordinates of the link origin
	 */
	private Point calculateOriginOfLinks(EntityWidget from, int directionOfOrigin, int linkType){
		int x = from.getLocation().x;
		int width = from.getBounds().width;
		int y = from.getLocation().y;
		int height = from.getBounds().height;
		switch (directionOfOrigin) {
			case (FROM_N) : return new Point(x+width/2+offCenter/2,y);
			case (FROM_S) : return new Point(x+width/2-offCenter/2,y+height);
			case (FROM_W) : return new Point(x,y-from.getLook().getGateLinkAnchor(from,linkType)-offCenter);
			case (FROM_NE) : return new Point(x+width-offCenter*3/4,y);
			case (FROM_SE) : return new Point(x+width-offCenter*3/4,y+height);
			case (FROM_SW) : return new Point(x+offCenter*3/4,y+height);
			case (FROM_NW) : return new Point(x+offCenter*3/4,y);
			default : return new Point(x+width,y+from.getLook().getGateLinkAnchor(from,linkType));
		}
	}

	/**
	 * Calculates the point where an outgoing link bents, given its direction
	 * @param origin the point of origin
	 * @param directionOfOrigin the direction of origin
	 * @return Point the outgoing bent point
	 */
	private Point calculateBentPoint(Point origin, int directionOfOrigin){
		switch (directionOfOrigin) {
			case (FROM_N) : return new Point(origin.x,origin.y-bent);
			case (FROM_S) : return new Point(origin.x,origin.y+bent);
			case (FROM_W) : return new Point(origin.x-bent,origin.y);
			case (FROM_NE) : return new Point(origin.x+bent*2/3,origin.y-bent*2/3);
			case (FROM_SE) : return new Point(origin.x+bent*2/3,origin.y+bent*2/3);
			case (FROM_SW) : return new Point(origin.x-bent*2/3,origin.y+bent*2/3);
			case (FROM_NW) : return new Point(origin.x-bent*2/3,origin.y-bent*2/3);
			default : return new Point(origin.x+bent,origin.y);
		}
	}
	
	/**
	 * Returns the ending point of a link, given its direction of origin.
	 * @param to the entity widget where the link ends
	 * @param directionOfOrigin the direction at the starting entity widget
	 * @param linkType the linkType
	 * @return Point the coordinates of the link origin
	 */
	private Point calculateTargetOfLinks(EntityWidget to, int directionOfOrigin, Link link, int linkType){
		int x,y;
		boolean targetIsNotANode;
		if (to!=null) {
			try {
				x = to.getLocation().x;
				y = to.getLocation().y+to.getLook().getSlotLinkAnchor(to,link.getLinkedSlot().getType());
				if (to.getEntity().getEntityType() != NetEntityTypesIF.ET_NODE) { 
					targetIsNotANode = true; 
				} else {
					targetIsNotANode = false;
				}
			} catch (NetIntegrityException e1) {
				throw new RuntimeException(e1);
			}
		}
		else return new Point(0,0); // hey! you did not give me a target entitity! I mess up your display!
		
		if (targetIsNotANode) return new Point(x,y);
		
		y = to.getLocation().y; 
		
		int width = to.getBounds().width;
		int height = to.getBounds().height;
		// we assume that slots of nodes are just opposite the gates...
		switch (directionOfOrigin) {
			case (FROM_N) : return new Point(x+width/2+offCenter/2,y+height);
			case (FROM_S) : return new Point(x+width/2-offCenter/2,y);
			case (FROM_W) : return new Point(x+width,y-to.getLook().getGateLinkAnchor(to,linkType)-offCenter);
			case (FROM_NE) : return new Point(x,y+height-offCenter*3/4);
			case (FROM_SE) : return new Point(x,y+offCenter*3/4);
			case (FROM_SW) : return new Point(x+width,y+offCenter*3/4);
			case (FROM_NW) : return new Point(x+width,y+height-offCenter*3/4);
			default : return new Point(x,y+to.getLook().getGateLinkAnchor(to,linkType));
		}
	}

	/**
	 * Calculates the point where an incoming link bents, given its direction
	 * @param target the point where the link ends
	 * @param directionOfOrigin the direction of origin at the starting entity widget
	 * @return Point the incoming bent point
	 */	
	private Point calculatePullPoint(Point target, int directionOfOrigin){
		switch (directionOfOrigin) {
			case (FROM_N) : return new Point(target.x,target.y+pull);
			case (FROM_S) : return new Point(target.x,target.y-pull);
			case (FROM_W) : return new Point(target.x+pull,target.y);
			case (FROM_NE) : return new Point(target.x-pull*2/3,target.y+pull*2/3);
			case (FROM_SE) : return new Point(target.x-pull*2/3,target.y-pull*2/3);
			case (FROM_SW) : return new Point(target.x+pull*2/3,target.y-pull*2/3);
			case (FROM_NW) : return new Point(target.x+pull*2/3,target.y+pull*2/3);
			default : return new Point(target.x-pull,target.y);
		}
	}
		
	/**
	 * Draws spatio-temporal annotations and c-values if needed
	 * @param bentPoint
	 * @param pullPoint
	 * @param gc
	 * @param erase
	 */
	private void drawLinkAnnotations(Point bentPoint, Point pullPoint, Link link, GC gc, boolean erase) {
		
		if(link==null) return;
				
		// calculate annotation string
		if (link.getType() == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL) {
			LinkST linkST = (LinkST) link;
			StringBuffer buf = null;
			double sx=linkST.getX();
			double sy=linkST.getY();
			double sz=linkST.getZ();
			if (sx!=0 || sy!=0 || sz!=0) {
				buf = new StringBuffer(30);
				buf.append('(');
				buf.append(Double.toString(Math.round(sx*100)/100.0));
				buf.append(',');
				buf.append(Double.toString(Math.round(sy*100)/100.0));
				if(sz != 0) {
					buf.append(',');
					buf.append(Double.toString(Math.round(sz*100)/100.0));
				}
				buf.append(')');
			}
			double t=linkST.getT();
			if (t!=0) {
				if(buf == null) buf = new StringBuffer(20);
				buf.append('(');
				buf.append(Double.toString(Math.round(t*100)/100.0));
				buf.append(')');				
			}
			double c=linkST.getConfidence();
			if (c!=1) {
				if(buf == null) {
					buf = new StringBuffer(10);
					buf.append("c=");
				} else {
					buf.append(" c=");
				}
				buf.append(Double.toString(Math.round(c*100)/100.0));				
			}
			
			String annotations = null;
			if(buf != null)
				annotations = buf.toString();
			
			int xdiff = pullPoint.x-bentPoint.x;
			int ydiff = pullPoint.y-bentPoint.y;
			int x = (bentPoint.x + pullPoint.x)/2;
			int y = (bentPoint.y + pullPoint.y)/2;
			if (annotations!=null) {
				Point textExtent = gc.textExtent(annotations);
				
				if (xdiff ==0 || Math.abs(ydiff*2)>Math.abs(xdiff)){ // treat as vertical
					if (link.getLinkingGate().getType()==GateTypesIF.GT_POR) { // to the left
						x=x-annotationDistance-textExtent.x;
						y=y-textExtent.y/2;
					} else { // to the right
						x=x+annotationDistance;
						y=y-textExtent.y/2;
					}
				} else { // treat as horizontal
					if (link.getLinkingGate().getType()==GateTypesIF.GT_POR) { // draw below
						x=x-textExtent.x/2;
						y=y+annotationDistance;
					} else { // draw above
						x=x-textExtent.x/2;
						y=y-annotationDistance-textExtent.y;
					}
				}
				gc.setFont(plusFont);
				//if (erase) gc.drawText(annotations,x,y,false);
				gc.drawText(annotations,x,y,false);
			}
		}
	}
	
	/**
	 * Here is where the actual link drawing takes place
	 * @param from
	 * @param to
	 * @param x
	 * @param y
	 * @param gc
	 * @param link
	 * @param linkType
	 * @param slots
	 * @param gates
	 * @param erase
	 */
	private void draw(EntityWidget from, EntityWidget to, int x, int y, GC gc, Link link, int linkType, int slots, int gates, boolean erase) {
		
		int directionOfOrigin = getDirectionFrom(from,linkType);		
		Point origin = calculateOriginOfLinks(from,directionOfOrigin,linkType); 
		Point target;
		if (to==null) target = new Point (x,y);
		else target = calculateTargetOfLinks(to,directionOfOrigin,link,linkType);
		Point bentPoint = calculateBentPoint(origin,directionOfOrigin);
		Point pullPoint = calculatePullPoint(target,directionOfOrigin);
		
		gc.setLineWidth(linkWidth);
					
		gc.drawLine(origin.x,origin.y,bentPoint.x,bentPoint.y);
		gc.drawLine(bentPoint.x,bentPoint.y,pullPoint.x,pullPoint.y);
		gc.drawLine(pullPoint.x,pullPoint.y,target.x,target.y);
		
		if (showArrowheads) {
			int arrow[] = calculateDirectedArrow(directionOfOrigin,target);			
			gc.fillPolygon(arrow);
			gc.drawPolygon(arrow);	
		}
			
		if(showAnnotationsWhenNeeded) {
			drawLinkAnnotations(bentPoint, pullPoint, link, gc, erase);
		}
				
		if(showPlusWhenNeeded) {
			if (slots > 1) {
				drawPlusValue(pullPoint.x,pullPoint.y,gc,slots,erase);
			}
			if (gates > 1) {
				drawPlusValue(bentPoint.x,bentPoint.y,gc,gates,erase);
			}
		}
	}
	
	/**
	 * Selection routine for links. Must be synchronous to draw
	 * @param from
	 * @param to
	 * @param x
	 * @param y
	 * @param link
	 * @param linkType
	 * @param slots
	 * @param gates
	 * @param erase
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	
	private boolean drawSelect(EntityWidget from, EntityWidget to, int x, int y, Link link, int linkType, int slots, int gates, boolean erase, int mouseX, int mouseY) {
		
		int directionOfOrigin = getDirectionFrom(from,linkType);		
		Point origin = calculateOriginOfLinks(from,directionOfOrigin,linkType); 
		Point target;
		if (to==null) target = new Point (x,y);
		else target = calculateTargetOfLinks(to,directionOfOrigin,link,linkType);
		Point bentPoint = calculateBentPoint(origin,directionOfOrigin);
		Point pullPoint = calculatePullPoint(target,directionOfOrigin);
		
		if (isOnLine(origin.x,origin.y,bentPoint.x,bentPoint.y,mouseX,mouseY)) return true;
		if (isOnLine(bentPoint.x,bentPoint.y,pullPoint.x,pullPoint.y,mouseX,mouseY)) return true;
		if (isOnLine(pullPoint.x,pullPoint.y,target.x,target.y,mouseX,mouseY)) return true;
	
		//@todo Joscha: make arrowheads and stuff selectable
		// ---> Implement this or remove the todo.
		/*		
		if (showArrowheads) {
			int arrow[] = calculateDirectedArrow(directionOfOrigin,target);			
			gc.fillPolygon(arrow);
			gc.drawPolygon(arrow);	
		}
			
		if(showAnnotationsWhenNeeded) {
			drawLinkAnnotations(bentPoint, pullPoint, link, gc, erase);
		}
				
		if(showPlusWhenNeeded) {
			if (slots > 1) {
				drawPlusValue(pullPoint.x,pullPoint.y,gc,slots,erase);
			}
			if (gates > 1) {
				drawPlusValue(bentPoint.x,bentPoint.y,gc,gates,erase);
			}
		}
		*/
		return false; // no element matched the mouse position
	}
	
	/**
	 * Check whether (mouse) is on the line (a,b)
	 * @param aX
	 * @param aY
	 * @param bX
	 * @param bY
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	private boolean isOnLine(int aX, int aY, int bX, int bY, int mouseX, int mouseY) {
	
		int dX,dY;
	
		if (aX==bX) {
			dX=aX;
			dY=mouseY;
		}
		else {
			double m=((double)aY-(double)bY)/((double)aX-(double)bX);
			double n=aY-m*aX;
		
			double ddX=(-m*n+mouseX+m*mouseY)/(m*m+1);
			dX=(int)ddX;
			dY=(int)(m*ddX+n);
		}
		// outside line?
		if (square(dX-mouseX)+square(dY-mouseY)>Math.max(2,linkWidth)) return false;
		// within line, but before start or after end?
		if (square(2*dX-aX-bX)+square(2*dY-aY-bY)>square(aX-bX)+square(aY-bY)) return false;
		// must be on the line
		return true;
	}
	
	private int square(int value) {
		return value*value;
	}
	
	/**
	 * Here is where the link drawing takes place for incoming connectors only
	 * @param to
	 * @param gc
	 * @param link
	 * @param linkType
	 * @param slots
	 * @param erase
	 */
	private void drawOnlyInput(EntityWidget to, GC gc, Link link, int linkType, int slots, boolean erase) {
	
		int directionOfOrigin = FROM_E;
		// guess where that link came from to get a direction
		if (to.getEntity().getEntityType() == NetEntityTypesIF.ET_NODE) { 
			directionOfOrigin = getDirectionFrom(to,linkType); 
		}
		Point target = calculateTargetOfLinks(to,directionOfOrigin,link,linkType);
		Point pullPoint = calculatePullPoint(target,directionOfOrigin);
		
		if(!showPlusWhenNeeded) {
			Color currentForeground = gc.getForeground();
			if(erase) 
				gc.setForeground(bgColor);
			else
				gc.setForeground(plusColor);
			gc.setLineWidth(lineWidth);
			gc.drawOval(pullPoint.x-plusCircleSize/4,pullPoint.y-plusCircleSize/4,plusCircleSize/2,plusCircleSize/2);
			gc.setForeground(currentForeground);
		}			
		gc.setLineWidth(linkWidth);
		gc.drawLine(pullPoint.x,pullPoint.y,target.x,target.y);
		
		if (showArrowheads) {
			int arrow[] = calculateDirectedArrow(directionOfOrigin,target);			
			gc.fillPolygon(arrow);
			gc.drawPolygon(arrow);	
		}	
		
		if(showPlusWhenNeeded) {
			drawPlusValue(pullPoint.x,pullPoint.y,gc,slots,erase);
		}
	}
	
	/**
	 * Routine to select link fragments. Must be synchronous to drawOnlyInput
	 * @param to
	 * @param link
	 * @param linkType
	 * @param slots
	 * @param erase
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */

	private boolean drawOnlyInputSelect(EntityWidget to, Link link, int linkType, int slots, boolean erase, int mouseX, int mouseY) {
	
		int directionOfOrigin = FROM_E;
		// guess where that link came from to get a direction
		if (to.getEntity().getEntityType() == NetEntityTypesIF.ET_NODE) { 
			directionOfOrigin = getDirectionFrom(to,linkType); 
		}
		Point target = calculateTargetOfLinks(to,directionOfOrigin,link,linkType);
		Point pullPoint = calculatePullPoint(target,directionOfOrigin);
		
		if(!showPlusWhenNeeded) {
			//gc.drawOval(pullPoint.x-plusCircleSize/4,pullPoint.y-plusCircleSize/4,plusCircleSize/2,plusCircleSize/2);
			if (square(pullPoint.x-mouseX)+square(pullPoint.y-mouseY)<=square(plusCircleSize/2)) return true;
		}			
					
		if (isOnLine(pullPoint.x,pullPoint.y,target.x,target.y,mouseX,mouseY)) return true;

		//@todo Joscha: make arrowheads and stuff selectable
		/*				
		if (showArrowheads) {
			int arrow[] = calculateDirectedArrow(directionOfOrigin,target);			
			gc.fillPolygon(arrow);
			gc.drawPolygon(arrow);	
		}	
		
		if(showPlusWhenNeeded) {
			drawPlusValue(pullPoint.x,pullPoint.y,gc,slots,erase);
		}
		*/
		
		return false;
	}
	
	/**
	 * Here is where the link drawing takes place for outgoing connectors only
	 * @param from
	 * @param gc
	 * @param link
	 * @param linkType
	 * @param gates
	 * @param erase
	 */
	private void drawOnlyOutput(EntityWidget from, GC gc, Link link, int linkType, int gates, boolean erase) {
		
		int directionOfOrigin = getDirectionFrom(from,linkType);		
		Point origin = calculateOriginOfLinks(from,directionOfOrigin,linkType); 
		Point bentPoint = calculateBentPoint(origin,directionOfOrigin);
		
		if(!showPlusWhenNeeded) {
			Color currentForeground = gc.getForeground();
			if(erase) 
				gc.setForeground(bgColor);
			else
				gc.setForeground(plusColor);
			gc.setLineWidth(lineWidth);
			gc.drawOval(bentPoint.x-plusCircleSize/4,bentPoint.y-plusCircleSize/4,plusCircleSize/2,plusCircleSize/2);
			gc.setForeground(currentForeground);
		}
		gc.setLineWidth(linkWidth);			
		gc.drawLine(origin.x,origin.y,bentPoint.x,bentPoint.y);
		
		if(showPlusWhenNeeded) {
			drawPlusValue(bentPoint.x,bentPoint.y,gc,gates,erase);
		}
	}

	/**
	 * Routine to select link fragments. Must be synchronous to drawOnlyOutput
	 * @param from
	 * @param link
	 * @param linkType
	 * @param gates
	 * @param erase
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	private boolean drawOnlyOutputSelect(EntityWidget from, Link link, int linkType, int gates, boolean erase, int mouseX, int mouseY) {
		
		int directionOfOrigin = getDirectionFrom(from,linkType);		
		Point origin = calculateOriginOfLinks(from,directionOfOrigin,linkType); 
		Point bentPoint = calculateBentPoint(origin,directionOfOrigin);
	
		if(!showPlusWhenNeeded) {
			//gc.drawOval(bentPoint.x-plusCircleSize/4,bentPoint.y-plusCircleSize/4,plusCircleSize/2,plusCircleSize/2);
			if (square(bentPoint.x-mouseX)+square(bentPoint.y-mouseY)<=square(plusCircleSize/2)) return true;
		}			
		if (isOnLine(origin.x,origin.y,bentPoint.x,bentPoint.y,mouseX,mouseY)) return true;
		//@Joscha: todo make arrowheads and stuff selectable
		/*				
		if(showPlusWhenNeeded) {
			drawPlusValue(bentPoint.x,bentPoint.y,gc,gates,erase);
		}
		*/
		return false;
	}
	/**
	 * Here is where the number of incoming/outgoing links is drawn (usually over a bent point)
	 * @param x the x coordinate where the number is drawn (middle)
	 * @param y the x coordinate where the number is drawn (middle)
	 * @param gc the graphics context
	 * @param value the value to be drawn
	 * @param erase whether the value is drawn or erased
	 */
	private void drawPlusValue(int x, int y, GC gc, int value, boolean erase) {
		
		gc.setFont(plusFont);

		Point textExtent = gc.textExtent(Integer.toString(value));

		Color currentForeground = gc.getForeground();
		if(erase) 
			gc.setForeground(bgColor);
		else
			gc.setForeground(plusColor);
		
		gc.setLineWidth(lineWidth);
		gc.fillOval(x-plusCircleSize/2,y-plusCircleSize/2,plusCircleSize,plusCircleSize);
		gc.drawOval(x-plusCircleSize/2,y-plusCircleSize/2,plusCircleSize,plusCircleSize);

		if (!erase) 
			gc.drawText(Integer.toString(value), x-textExtent.x/2, y-textExtent.y/2,true);
		
		gc.setForeground(currentForeground);
	}
	
	/**
	 * Returns a directed arrowhead
	 * @param directionOfOrigin the opposite direction of the head 
	 * @param target the point where the tip of the arrow should point
	 * @return int[] the array of coordinates
	 */
	private int[] calculateDirectedArrow(int directionOfOrigin, Point target) {
		int targetX = target.x;
		int targetY = target.y;
		switch (directionOfOrigin) {
			case (FROM_N) : return new int[] {	
				targetX - arrowWidth,
				targetY + arrowLength,
				targetX + arrowWidth,
				targetY + arrowLength,
				targetX,
				targetY
			};
			case (FROM_S) : return new int[] {	
				targetX - arrowWidth,
				targetY - arrowLength,
				targetX + arrowWidth,
				targetY - arrowLength,
				targetX,
				targetY
			};
			case (FROM_W) : return new int[] {
				targetX + arrowLength,
				targetY - arrowWidth,
				targetX + arrowLength,
				targetY + arrowWidth,	
				targetX,
				targetY
			};
			case (FROM_NE) : return new int[] {	
				targetX - arrowWidth*2/3 - arrowLength*2/3,
				targetY - arrowWidth*2/3 + arrowLength*2/3,
				targetX + arrowWidth*2/3 - arrowLength*2/3,
				targetY + arrowWidth*2/3 + arrowLength*2/3,
				targetX,
				targetY
			};
			case (FROM_SE) : return new int[] {	
				targetX - arrowWidth*2/3 - arrowLength*2/3,
				targetY + arrowWidth*2/3 - arrowLength*2/3,
				targetX + arrowWidth*2/3 - arrowLength*2/3,
				targetY - arrowWidth*2/3 - arrowLength*2/3,
				targetX,
				targetY
			};
			case (FROM_SW) : return new int[] {	
				targetX + arrowWidth*2/3 + arrowLength*2/3,
				targetY + arrowWidth*2/3 - arrowLength*2/3,
				targetX - arrowWidth*2/3 + arrowLength*2/3,
				targetY - arrowWidth*2/3 - arrowLength*2/3,
				targetX,
				targetY
			};
			case (FROM_NW) : return new int[] {	
				targetX + arrowWidth*2/3 + arrowLength*2/3,
				targetY - arrowWidth*2/3 + arrowLength*2/3,
				targetX - arrowWidth*2/3 + arrowLength*2/3,
				targetY + arrowWidth*2/3 + arrowLength*2/3,
				targetX,
				targetY
			};
			default : // from E
				return new int[] { 
					targetX - arrowLength,
					targetY - arrowWidth, 
					targetX - arrowLength,
					targetY + arrowWidth,
					targetX,
					targetY
				};
		}
	}

	/**
	 * Calculates the color of an (activated) link
	 * @param linkact the activation value
	 * @param linkweight the weight of the link
	 * @return the color the link should be drawn
	 */
	public Color calcActivationColor(double linkact, double linkweight) {
		
		if(linkcolor != null)
			linkcolor.dispose();
		
		linkact *= linkweight;
		if (linkact > 1.0) linkact = 1.0;
		if (linkact < -1.0) linkact = -1.0;
		if (linkweight > 1.0) linkweight = 1.0;
		if (linkweight < -1.0) linkweight = -1.0;		
			
		if (linkact == 0.0) { // there is no activation on the link, we use linkcolor
			if (linkweight >= 0.0) {
				linkcolor = new Color (
					null, 
					(int)((1.0-linkweight)*noweight.getRed()+linkweight*fullweight.getRed()),
					(int)((1.0-linkweight)*noweight.getGreen()+linkweight*fullweight.getGreen()),
					(int)((1.0-linkweight)*noweight.getBlue()+linkweight*fullweight.getBlue())
				);
			} else {
				linkcolor = new Color (
					null,
					(int)((1.0+linkweight)*noweight.getRed()-linkweight*negweight.getRed()),
					(int)((1.0+linkweight)*noweight.getGreen()-linkweight*negweight.getGreen()),
					(int)((1.0+linkweight)*noweight.getBlue()-linkweight*negweight.getBlue())
				);			
			}
		}
		else {
			if (linkact >= 0.0) { // positive activation, use activation color
				linkcolor = new Color (
					null,
					(int)((1.0-linkact)*noweight.getRed()+linkact*fullact.getRed()),
					(int)((1.0-linkact)*noweight.getGreen()+linkact*fullact.getGreen()),
					(int)((1.0-linkact)*noweight.getBlue()+linkact*fullact.getBlue())
				);
			} else {
					linkcolor = new Color (
					null,
					(int)((1.0+linkact)*noweight.getRed()-linkact*negact.getRed()),
					(int)((1.0+linkact)*noweight.getGreen()-linkact*negact.getGreen()),
					(int)((1.0+linkact)*noweight.getBlue()-linkact*negact.getBlue())
				);			
			}
		}
		return linkcolor;
	}
	
	/**
	 * Calculates the color of (activated) entity element
	 * @param linkact the activation value
	 * @return the color the element should be drawn
	 */
	public Color calcActivationColor(double act) {
		if (Math.abs(act) > 0.05)
			return calcActivationColor(act,1.0);
		else 
			return noweight;	
	}
}
