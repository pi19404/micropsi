/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/NodeSpaceWidget.java,v 1.26 2006/08/03 15:42:13 rvuine Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.ILinkCombinedTypes;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.autoalign.AlignmentManager;
import org.micropsi.eclipse.mindconsole.autoalign.CascadeAlignment;
import org.micropsi.eclipse.mindconsole.autoalign.ForceAlignment;
import org.micropsi.eclipse.mindconsole.autoalign.IAutoAligner;
import org.micropsi.eclipse.mindconsole.autoalign.IAutoAlignment;
import org.micropsi.eclipse.mindconsole.autoalign.ProtocolChainAlignment;
import org.micropsi.eclipse.mindconsole.autoalign.SchemaAlignment;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetCycleIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NetWeaver;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.NodeSpaceModule;
import org.micropsi.nodenet.NodeSpaceObserverIF;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;

public class NodeSpaceWidget extends Composite implements NodeSpaceObserverIF, IAutoAligner {
	
	
	// when the class is loaded: register some auto-alignemnt strategies
	static {
		
		AlignmentManager.getInstance().registerAlignmentStrategy(new ProtocolChainAlignment());
		AlignmentManager.getInstance().registerAlignmentStrategy(new SchemaAlignment());
		AlignmentManager.getInstance().registerAlignmentStrategy(new CascadeAlignment());		
		AlignmentManager.getInstance().registerAlignmentStrategy(new ForceAlignment());
		
	}
	
	
	private class RedrawCommitter implements Runnable {
		
		private Composite element;
		
		public void run() {
			try {
				element.redraw();
			
				if(element instanceof EntityWidget)
					redrawEntityLinks(gc, (EntityWidget)element);
			} catch (Throwable e) {
			}
		}		 
	}
	
	private class CreateCommitter implements Runnable {
		public String entityID;
		public NodeSpaceWidget widget;
		public AbstractEntityLook look;
		public void run() {
			EntityWidget nw;
			try {
				nw = new EntityWidget(widget, SWT.NONE, netmodel, entityID, look);
				EntityModel model = netmodel.getModel(nw.getKey()); 
				addEntityWidget(nw);
				model.setX(nw.getLocation().x,scalingInPercent);
				model.setY(nw.getLocation().y,scalingInPercent);													
				redrawEntityLinks(gc,nw);
			} catch (MicropsiException e) {
				callback.handleException(e);
			}		
		}
	}
	
	private class DeleteCommitter implements Runnable {
		public String key;
		public void run() {
			removeEntityWidget(key);
		}
	}
	
	private static final int MAX_NUMBER_OF_NODES = 2000;
	
	public static final int STATE_NORMAL = 0;
	public static final int STATE_DRAG = 1;
	public static final int STATE_SELECT = 2;
	public static final int STATE_CREATELINK = 3;

	protected static final int DETAIL_FULL = 0;
	protected static final int DETAIL_COMPACT = 1;
	protected static final int DETAIL_DEFAULT = 2;

	protected static final int DRAW_NONE = 0;
	protected static final int DRAW_SELECTED = 1;
	protected static final int DRAW_DRAGGED = 2;
	protected static final int DRAW_ALL = 3;
	protected static final int DRAW_ACTIVE = 4;

	protected int STATE = STATE_NORMAL;
	protected int DETAIL = DETAIL_DEFAULT;
	protected int DRAW_LINKS = DRAW_ALL;
	
	protected static final int minWidth=992;
	protected static final int minHeight=688;
	protected static final int marginTop=48;
	protected static final int marginLeft=48;
	protected static final int marginBottom=48;
	protected static final int marginRight=48;
	
	protected int scalingInPercent = 100;
	protected int gridWidth = 4; // should match style definitions
	
	protected HashMap<String, EntityWidget> items = new HashMap<String, EntityWidget>();
	protected HashSet<String> selectedItems = new HashSet<String>();
	
	public GC gc;
	protected EntityWidget workWidget = null;
	protected Rectangle workRect = new Rectangle(0,0,1,1);
	protected ArrayList<String> workList = new ArrayList<String>();
	protected Point rightClickPoint = new Point(10,10);
	protected Point dragStartPoint = new Point(10,10);

	
	private NetModel netmodel;
	private EntityModel myModel;
	
	private Thread uiThread;
	
	private Color bgColor;
	private Color selColor;
	
	AbstractEntityLook selectedNodeLook;
	AbstractEntityLook selectedModuleLook;
	
	AbstractEntityLook fullLook;
	AbstractEntityLook compactLook;
	AbstractEntityLook moduleLook;
	AbstractStyledEntityLook styledLook;
	AbstractStyledEntityLook styledModuleLook;
	
	protected DropTarget dropTarget;
	protected Clipboard clipboard;
	
	private IMindEditCallback callback;
	private RedrawCommitter redrawCommitter = new RedrawCommitter();

	
	int numberOfNodes = 0;
		
	public NodeSpaceWidget(Composite parent, int style, int lookStyle, Font entityFont, NetModel netmodel, String spaceid, IMindEditCallback callback, int initialscale) throws MicropsiException {
		super(parent, style);
		setSize(computeSize(0,0,false));
		setFont(entityFont);
		
		gc = new GC(this);
		bgColor = new Color(null, 255, 255, 255);
		selColor = new Color(null, 0, 0, 0);
		
		setBackground(bgColor);
		
		Menu popUpMenu = new Menu(this.getShell(), SWT.POP_UP);												
		this.setMenu(popUpMenu);
		
		uiThread = Thread.currentThread();

		this.callback = callback;

		styledLook = new StyledNodeLook(bgColor, entityFont);
		fullLook = new NodeFullLook(bgColor);
		compactLook = new NodeCompactLook(bgColor);
		moduleLook = new ModuleLook(bgColor);
		styledModuleLook = new StyledModuleLook(bgColor,entityFont);

		selectedNodeLook = styledLook;
		selectedModuleLook = styledModuleLook;
		
		setScaling(initialscale);
		styledLook.setAppearance(lookStyle);
		styledModuleLook.setAppearance(lookStyle);
		
		this.netmodel = netmodel;
		this.myModel = netmodel.getModel(spaceid);
		loadSpace();	
		extendPopup();

		clipboard = new Clipboard(getDisplay());

		dropTarget = new DropTarget(this,DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] {TextTransfer.getInstance()});
		dropTarget.addDropListener(new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {		
			}

			public void dragLeave(DropTargetEvent event) {				
			}

			public void dragOperationChanged(DropTargetEvent event) {
				if(event.detail != DND.DROP_MOVE) {
					dragTo(dragStartPoint.x,dragStartPoint.y);
				}
			}

			public void dragOver(DropTargetEvent event) {
				if(STATE == STATE_DRAG && event.detail == DND.DROP_MOVE)				
					dragTo(event.x,event.y);				
			}

			public void dropAccept(DropTargetEvent event) {				
				if(STATE == STATE_DRAG) {
					if(event.detail == DND.DROP_MOVE) {
						// move from the same widget doesn't require anything
						// as the coordinate changes have been done while dragging
						event.detail = DND.DROP_NONE;
						dragTo(event.x,event.y);	
					}
					leaveDragState();
				}				
			}

			public void drop(DropTargetEvent event) {
				try {
					List<EntityTransferData> l = EntityTransferData.textToList((String)event.data);			
					dropItems(l,event.x,event.y);
					leaveDragState();
				} catch (Exception e) {
					MindPlugin.getDefault().handleException(e);
				}
			}			
		});
						
		addMouseMoveListener(new MouseMoveListener() {
			int c = 0;
			public void mouseMove(MouseEvent e) {
				switch(STATE) {
					case STATE_SELECT:
						//redraw(workRect.x+1,workRect.y+1,workRect.width-1,workRect.height-1,false);
						gc.setXORMode(true);
						drawSelectionRect(bgColor);
						workRect.width = e.x - workRect.x;
						workRect.height = e.y - workRect.y;
						drawSelectionRect(bgColor);
						gc.setXORMode(false);
						break;
					case STATE_CREATELINK: 
						c++;
						if(c!=2) return;
						c = 0; 
						EntityWidget nw = workWidget;
						Rectangle r = workRect;
						nw.eraseLinkTo(r.x,r.y,gc,nw.getNewLinkType());
						//redrawLinks(gc);
						nw.drawLinkTo(e.x,e.y,gc,nw.getNewLinkType());
						//NodeSpaceWidget.this.getDisplay().readAndDispatch();
						r.x = e.x;
						r.y = e.y;
						break;
					default:
				}
			}
		});
				
		addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				
				NetCycleIF cycle = NodeSpaceWidget.this.netmodel.getNet().getCycle(); 
				
				if(!cycle.isSuspended()) {
					cycle.suspend();
					return;
				}

				
				doubleClickSelectLink(e.x,e.y);
			}

			public void mouseUp(MouseEvent e) {

				NetCycleIF cycle = NodeSpaceWidget.this.netmodel.getNet().getCycle(); 
				
				if(!cycle.isSuspended()) {
					cycle.suspend();
					return;
				}
				
				boolean realselect = (workRect.x != e.x && workRect.y != e.y);
				if(STATE == STATE_SELECT && realselect) {
					gc.setXORMode(true);
					drawSelectionRect(bgColor);
					gc.setXORMode(false);
					redraw();										
					selectInSelRect();
					workRect.width = 0;
					workRect.height = 0;					
					STATE = STATE_NORMAL;
				}  
				
				if(STATE == STATE_SELECT)
					STATE = STATE_NORMAL;
			}
			
			public void mouseDown(MouseEvent e) {
				
				NetCycleIF cycle = NodeSpaceWidget.this.netmodel.getNet().getCycle(); 
				
				if(!cycle.isSuspended()) {
					return;
				}
				
				switch(STATE) {
					case STATE_NORMAL:
						if(e.button == 1) {
							workRect.x = e.x;
							workRect.y = e.y;
							if(e.stateMask != SWT.SHIFT) deselectEverything();
							STATE = STATE_SELECT;
						} else {
							rightClickPoint.x = e.x;
							rightClickPoint.y = e.y;
						}
						if(e.button == 1) clickSelectLink(e.x,e.y);
						break;
					case STATE_CREATELINK:
						leaveLinkCreationState(null, 0);
						break;
					default:
				}
			}
		});			
						
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				clipboard.dispose();
				dropTarget.dispose();
				bgColor.dispose();
				selColor.dispose();
				gc.dispose();		
				fullLook.dispose();
				compactLook.dispose();
				styledLook.dispose();
				moduleLook.dispose();
				styledModuleLook.dispose();
			}
		});		
		
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				
				redrawAllLinks(e.gc);
				
				if(numberOfNodes == MAX_NUMBER_OF_NODES) {
					e.gc.setForeground(selColor);
					e.gc.drawString("Too many nodes to display all of them. Only the first "+MAX_NUMBER_OF_NODES+" nodes are shown. Link drawing was disabled.", 10, 10);
				}
			}
		});									
	}
		
	/**
	 * Calculates the size of the canvas (i.e. the drawable area)
	 * @return size The size of the canvas
	 */
	public Point computeSize(int wHint, int hHint, boolean changed) {
		
		if (scalingInPercent < 1) scalingInPercent =1;
		
		int marginT = marginTop* scalingInPercent /100;
		int marginL = marginLeft* scalingInPercent /100;
		int marginR = marginRight* scalingInPercent /100;
		int marginB = marginBottom* scalingInPercent /100;
		int minW = Math.max(wHint, minWidth* scalingInPercent /100);
		int minH = Math.max(hHint, minHeight* scalingInPercent /100);
		int minX=9999,minY=9999,maxX=0,maxY=0;
		
		// determine used area
		Iterator<EntityWidget> iter = items.values().iterator();
		while(iter.hasNext()) {
			EntityWidget nw = iter.next();
			minX=Math.min(minX,nw.getPositionFromEntityModel(scalingInPercent).x);
			minY=Math.min(minY,nw.getPositionFromEntityModel(scalingInPercent).y);
			maxX=Math.max(maxX,nw.getPositionFromEntityModel(scalingInPercent).x+nw.getNeededSize().x);
			maxY=Math.max(maxY,nw.getPositionFromEntityModel(scalingInPercent).y+nw.getNeededSize().y);
		}
		
		// shift entities
		int offsetX=marginL-minX;
		int offsetY=marginT-minY;
		
		if (offsetX!=0 || offsetY!=0) {
			iter = items.values().iterator();
			while(iter.hasNext()) {
				EntityWidget nw = iter.next();
				nw.setPositionIntoEntityModel(scalingInPercent,
					nw.getPositionFromEntityModel(scalingInPercent).x+offsetX,
					nw.getPositionFromEntityModel(scalingInPercent).y+offsetY);
				nw.setLocation(nw.getPositionFromEntityModel(scalingInPercent));
			}
			maxX=maxX+offsetX;
			maxY=maxY+offsetY;
		}
		
		int height = Math.max(minH,maxY+marginB);
		int width = Math.max(minW,maxX+marginR);
		return new Point(width,height);
	}
	
	/**
	 * This method recalculates the size of the canvas based on the
	 * changes made to an individual widget. This is supposed to be
	 * much faster and should be called if you are sure that only
	 * one widget has been changed.
	 * @return size The size of the canvas 
	 */
	public Point recomputeSize(int wHint, int hHint, EntityWidget cnw) {
		
		if (cnw==null) return computeSize(wHint,hHint,true); 
		
		if (scalingInPercent < 1) scalingInPercent =1;
		
		int marginT = marginTop* scalingInPercent /100;
		int marginL = marginLeft* scalingInPercent /100;
		int marginR = marginRight* scalingInPercent /100;
		int marginB = marginBottom* scalingInPercent /100;
		
		int currentW=this.getSize().x;
		int currentH=this.getSize().y;
		int offsetX=0;
		int offsetY=0;
		
		if (cnw.getPositionFromEntityModel(scalingInPercent).x < marginL) offsetX=marginL-cnw.getPositionFromEntityModel(scalingInPercent).x;
		if (cnw.getPositionFromEntityModel(scalingInPercent).y < marginT) offsetY=marginT-cnw.getPositionFromEntityModel(scalingInPercent).y;
		if (offsetX!=0 || offsetY!=0) { //shift entities
			Iterator<EntityWidget> iter = items.values().iterator();
			while(iter.hasNext()) {
				EntityWidget nw = iter.next();
				nw.setPositionIntoEntityModel(scalingInPercent,
					nw.getPositionFromEntityModel(scalingInPercent).x+offsetX,
					nw.getPositionFromEntityModel(scalingInPercent).y+offsetY);
				nw.setLocation(nw.getPositionFromEntityModel(scalingInPercent));
			}
			currentW+=offsetX;
			currentH+=offsetY;
		}
		
		if (cnw.getLocation().x+cnw.getNeededSize().x+marginR > currentW) 
			currentW=cnw.getPositionFromEntityModel(scalingInPercent).x+cnw.getNeededSize().x+marginR;
		if (cnw.getLocation().y+cnw.getNeededSize().y+marginB > currentH) 
			currentH=cnw.getPositionFromEntityModel(scalingInPercent).y+cnw.getNeededSize().y+marginB;

		return new Point(currentW,currentH);
	}

	
	
	private void redrawEntities(GC gc) {
		Iterator<EntityWidget> iter = items.values().iterator();
		while(iter.hasNext()) {
			EntityWidget nw = iter.next();
			nw.paintToGC(gc,true); 
		}
	}
	
	private void redrawAllLinks(GC gc) {	
		
		Iterator<EntityWidget> iter = items.values().iterator();
		while(iter.hasNext()) {
			EntityWidget nw = iter.next();
			redrawEntityLinks(gc, nw);
		}
	}
	
	/**
	 * Selects a link from the background. This code is still sort of
	 * ugly, because links are not proper entities, and thus the 
	 * selection mechanism has to mimic the drawing mechanism.
	 */
	public boolean clickSelectLink(int mouseX, int mouseY) {
		Link selectedLink = selectLink(mouseX,mouseY);
		if (selectedLink!=null) {
			return true;
		}
		return false;
	}
	
	public boolean doubleClickSelectLink(int mouseX, int mouseY) {
		Link selectedLink = selectLink(mouseX,mouseY);
		if (selectedLink!=null) {
			// open dialog for selected link
			callback.openLinkDialog(getShell(),selectedLink);
			STATE = STATE_NORMAL;
			return true;
		}
		return false;
	}
	
	
	private Link selectLink(int mouseX, int mouseY) {
		Link selectedLink = null;
		// "draw" all links
		Iterator<EntityWidget> iter = items.values().iterator();
		while(iter.hasNext() && selectedLink==null) {
			EntityWidget nw = iter.next();
			selectedLink = redrawEntityLinksSelect(nw,mouseX,mouseY);
		}
		// show selected link
		if (selectedLink!=null) {
			//callback.selectEntity(selectedLink.getLinkingEntity().getID());
			callback.selectLink(selectedLink);
			
			EntityWidget w = items.get(selectedLink.getLinkedEntityID());
			if(w != null) w.moveAbove(null);
			
			w = items.get(selectedLink.getLinkingEntity().getID()); 
			if(w != null) w.moveAbove(null);
			
			return selectedLink;
		}
		return null;
	}

	
	/**
	 * Needs to be synchronous to redrawEntityLinks
	 */
	
	private Link redrawEntityLinksSelect(EntityWidget nw, int mouseX, int mouseY) {
				
		boolean draw = false;
		boolean drawRec = false;
		switch(DRAW_LINKS) {
			case DRAW_ALL:
				draw = true;				
				drawRec = true;
				break;
			case DRAW_NONE:
				draw = false;
				drawRec = false;
				break;
			case DRAW_SELECTED: 
				draw = isSelected(nw.getKey());
				break; 
			case DRAW_DRAGGED: 
				draw = (isSelected(nw.getKey()) && STATE == STATE_DRAG);
				break;
			case DRAW_ACTIVE:
				draw = nw.getEntity().isActive();
			default: 
		}
	 
		if(draw) return nw.drawLinksSelect(drawRec,mouseX,mouseY);
		return null;
	}

	
	private void redrawEntityLinks(GC gc, EntityWidget nw) {
				
		boolean draw = false;
		boolean drawRec = false;
		switch(DRAW_LINKS) {
			case DRAW_ALL:
				draw = true;
				drawRec = true;
				break;
			case DRAW_NONE:
				draw = false;
				drawRec = false;
				break;
			case DRAW_SELECTED: 
				draw = isSelected(nw.getKey());
				break; 
			case DRAW_DRAGGED: 
				draw = (isSelected(nw.getKey()) && STATE == STATE_DRAG);
				break;
			case DRAW_ACTIVE:
				draw = nw.getEntity().isActive();
			default: 
		}
	 
		if(draw) nw.drawLinks(gc,drawRec);
	}
		
	private void loadSpace() throws MicropsiException {

		numberOfNodes = 0;
		
		DRAW_LINKS = DRAW_NONE;
		
		Iterator iter = ((NodeSpaceModule)myModel.getEntity()).getAllLevelOneEntities();
		while(iter.hasNext()) {
			NetEntity entity = (NetEntity) iter.next();
			
			if(entity.getEntityType() == NetEntityTypesIF.ET_NODE) {
				numberOfNodes++;
				if(numberOfNodes > MAX_NUMBER_OF_NODES) {
					continue;
				}
			}
			
			AbstractEntityLook look = selectedNodeLook;
			if(entity.getEntityType() != NetEntityTypesIF.ET_NODE)
				look = selectedModuleLook;
			EntityWidget nw = new EntityWidget(this, SWT.NONE, netmodel, entity.getID() ,look);
		 	addEntityWidget(nw);
		 	
			EntityModel em = netmodel.getModel(entity.getID());
			if(em.getX() < 0) {
				em.setX(10);
				System.err.println("warning, negative x position found");
			}

			if(em.getY() < 0) {
				em.setY(10);
				System.err.println("warning, negative y position found");
			}
			nw.setLocation(em.getX(scalingInPercent),em.getY(scalingInPercent));
			nw.setComment(em.getComment());
		}
				
		if(numberOfNodes <= MAX_NUMBER_OF_NODES) {
			DRAW_LINKS = DRAW_ALL;
		}
		
		this.setSize(computeSize(0,0,true));
		pack();
	}		
	
	public void enterLinkCreationState(EntityWidget fromLink) {
		workRect = new Rectangle(0,0,1,1);
		workWidget = fromLink;
		STATE = STATE_CREATELINK;
	}
	
	public void leaveLinkCreationState(EntityWidget toLink, int slot) {
		STATE = STATE_NORMAL;
		if(toLink == null || slot < 0) {
			redraw();
			return;
		}
				
		try {
			EntityWidget nw = workWidget;
			switch(nw.getNewLinkType()) {
				case ILinkCombinedTypes.LCTYPE_POR_RET:
					if(toLink.getEntity().getEntityType() == NetEntityTypesIF.ET_NODE)
					if(((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_CONCEPT || ((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_TOPO) {
						netmodel.getNet().createLink(nw.getEntity().getID(),ILinkCombinedTypes.GT_POR,toLink.getEntity().getID(),slot,1.0,1.0,true);
						netmodel.getNet().createLink(toLink.getEntity().getID(),ILinkCombinedTypes.GT_RET,nw.getEntity().getID(),slot,1.0,1.0,true);
					}
					if(((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_CHUNK) {
						netmodel.getNet().createLink(nw.getEntity().getID(),ILinkCombinedTypes.GT_POR,toLink.getEntity().getID(),SlotTypesIF.ST_POR,1.0,1.0,true);
						netmodel.getNet().createLink(toLink.getEntity().getID(),ILinkCombinedTypes.GT_RET,nw.getEntity().getID(),SlotTypesIF.ST_RET,1.0,1.0,true);
					}
					break;
				case ILinkCombinedTypes.LCTYPE_SUB_SUR:
					if(toLink.getEntity().getEntityType() == NetEntityTypesIF.ET_NODE)
					if(((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_CONCEPT || ((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_TOPO) {
						netmodel.getNet().createLink(nw.getEntity().getID(),ILinkCombinedTypes.GT_SUB,toLink.getEntity().getID(),slot,1.0,1.0,false);
						netmodel.getNet().createLink(toLink.getEntity().getID(),ILinkCombinedTypes.GT_SUR,nw.getEntity().getID(),slot,1.0,1.0,false);
					}
					if(((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_CHUNK) {
						netmodel.getNet().createLink(nw.getEntity().getID(),ILinkCombinedTypes.GT_SUB,toLink.getEntity().getID(),SlotTypesIF.ST_SUB,1.0,1.0,false);
						netmodel.getNet().createLink(toLink.getEntity().getID(),ILinkCombinedTypes.GT_SUR,nw.getEntity().getID(),SlotTypesIF.ST_SUR,1.0,1.0,false);
					}
					if(	((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_SENSOR ||
						((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_ACTOR) {
						netmodel.getNet().createLink(nw.getEntity().getID(),ILinkCombinedTypes.GT_SUB,toLink.getEntity().getID(),SlotTypesIF.ST_GEN,1.0,1.0,false);
						netmodel.getNet().createLink(toLink.getEntity().getID(),ILinkCombinedTypes.GT_GEN,nw.getEntity().getID(),SlotTypesIF.ST_SUR,1.0,1.0,false);
					}
					break;
				case ILinkCombinedTypes.LCTYPE_CAT_EXP:
					if(toLink.getEntity().getEntityType() == NetEntityTypesIF.ET_NODE)
					if(((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_CONCEPT || ((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_TOPO) {
						netmodel.getNet().createLink(nw.getEntity().getID(),ILinkCombinedTypes.GT_CAT,toLink.getEntity().getID(),slot,1.0,1.0,false);
						netmodel.getNet().createLink(toLink.getEntity().getID(),ILinkCombinedTypes.GT_EXP,nw.getEntity().getID(),slot,1.0,1.0,false);
					}
					if(((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_CHUNK) {
						netmodel.getNet().createLink(nw.getEntity().getID(),ILinkCombinedTypes.GT_CAT,toLink.getEntity().getID(),SlotTypesIF.ST_GEN,1.0,1.0,false);
						netmodel.getNet().createLink(toLink.getEntity().getID(),ILinkCombinedTypes.GT_EXP,nw.getEntity().getID(),SlotTypesIF.ST_GEN,1.0,1.0,false);
					}
					break;
				case ILinkCombinedTypes.LCTYPE_SYM_REF:
					if(toLink.getEntity().getEntityType() == NetEntityTypesIF.ET_NODE)
					if(((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_CONCEPT || ((Node)toLink.getEntity()).getType() == NodeFunctionalTypesIF.NT_TOPO) {
						netmodel.getNet().createLink(nw.getEntity().getID(),ILinkCombinedTypes.GT_SYM,toLink.getEntity().getID(),slot,1.0,1.0,false);
						netmodel.getNet().createLink(toLink.getEntity().getID(),ILinkCombinedTypes.GT_REF,nw.getEntity().getID(),slot,1.0,1.0,false);
					}
					break;
				default:		
					netmodel.getNet().createLink(nw.getEntity().getID(),nw.getNewLinkType(),toLink.getEntity().getID(),slot,1.0,1.0,false);
			}
			redraw();
		} catch (MicropsiException e) {
			callback.handleException(e);
		}
	}
			
	protected void extendPopup() {
		
		Menu popUpMenu = this.getMenu();
		final int c = popUpMenu.getItemCount();
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu)e.widget;
				MenuItem[] items = menu.getItems();
				items[c+0].setEnabled(true); // save
				items[c+1].setEnabled(true); // load
				items[c+2].setEnabled(true); // create
			}
		});
				
		MenuItem itemL2 = new MenuItem(popUpMenu, SWT.CASCADE);
		itemL2.setText("Create Node...");
		Menu menuL2 = new Menu(this.getShell(), SWT.DROP_DOWN);
		
		MenuItem item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.REGISTER);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_REGISTER);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.CONCEPT);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_CONCEPT);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.CHUNK);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_CHUNK);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.TOPO);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_TOPO);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.SENSOR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_SENSOR);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACTOR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACTOR);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ASSOCIATOR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ASSOCIATOR);				
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.DISSOCIATOR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_DISSOCIATOR);				
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACTIVATOR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACTIVATOR);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.DEACTIVATOR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_DEACTIVATOR);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACT_POR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACT_POR);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACT_RET);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACT_RET);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACT_SUR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACT_SUR);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACT_SUB);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACT_SUB);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACT_CAT);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACT_CAT);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACT_EXP);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACT_EXP);				
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACT_SYM);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACT_SYM);
			}
		});
		item = new MenuItem(menuL2, SWT.CASCADE);
		item.setText(TypeStrings.ACT_REF);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeCreate(NodeFunctionalTypesIF.NT_ACT_REF);				
			}
		});
		itemL2.setMenu(menuL2);
	
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Create NodeSpace");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				triggerNodeSpaceCreate();
			}
		});
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Create NativeModule");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				callback.createNativeModule(myModel.getEntity().getID());
			}
		});

		
		// remove this if nobody complains.
		
/*		itemL2 = new MenuItem(popUpMenu, SWT.CASCADE);
		itemL2.setText("Set node detail...");
		menuL2 = new Menu(this.getShell(), SWT.DROP_DOWN);
		menuL2.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu)e.widget;
				MenuItem[] items = menu.getItems();
				items[0].setSelection(DETAIL == DETAIL_DEFAULT); // styled
				items[1].setSelection(DETAIL == DETAIL_COMPACT); // compact
				items[2].setSelection(DETAIL == DETAIL_FULL); // full
			}
		});

		item = new MenuItem(menuL2, SWT.CHECK);
		item.setText("Default");	
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setDetail(DETAIL_DEFAULT);
			}
		});
		
		item = new MenuItem(menuL2, SWT.CHECK);
		item.setText("Oldschool compact");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setDetail(DETAIL_COMPACT);
			}
		});
		item = new MenuItem(menuL2, SWT.CHECK);
		item.setText("Oldschool full");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setDetail(DETAIL_FULL);
			}
		});
		itemL2.setMenu(menuL2);
*/

		itemL2 = new MenuItem(popUpMenu, SWT.CASCADE);
		itemL2.setText("Draw links...");
		
		itemL2.setEnabled(true);
		menuL2 = new Menu(this.getShell(), SWT.DROP_DOWN);
		menuL2.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu)e.widget;
				MenuItem[] items = menu.getItems();
				items[0].setSelection(DRAW_LINKS == DRAW_ALL); // all
				items[1].setSelection(DRAW_LINKS == DRAW_NONE); // full
				items[2].setSelection(DRAW_LINKS == DRAW_SELECTED); // selected
				items[3].setSelection(DRAW_LINKS == DRAW_DRAGGED); // dragged
				items[4].setSelection(DRAW_LINKS == DRAW_ACTIVE); // active
			}
		});
		
		
		item = new MenuItem(menuL2, SWT.CHECK);
		item.setText("All");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DRAW_LINKS = DRAW_ALL;
				redraw();
			}
		});
		item = new MenuItem(menuL2, SWT.CHECK);
		item.setText("None");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DRAW_LINKS = DRAW_NONE;
				redraw();
			}
		});
		item = new MenuItem(menuL2, SWT.CHECK);
		item.setText("Selected only");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DRAW_LINKS = DRAW_SELECTED;
				redraw();
			}
		});
		item = new MenuItem(menuL2, SWT.CHECK);
		item.setText("Dragged only");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DRAW_LINKS = DRAW_DRAGGED;
				redraw(); 				
			}
		});
		item = new MenuItem(menuL2, SWT.CHECK);
		item.setText("Active only");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DRAW_LINKS = DRAW_ACTIVE;
				redraw(); 				
			}
		});
		itemL2.setMenu(menuL2);
		

		itemL2 = new MenuItem(popUpMenu, SWT.CASCADE);
		itemL2.setText("Auto-align...");
		menuL2 = new Menu(this.getShell(), SWT.DROP_DOWN);

		Iterator iter = AlignmentManager.getInstance().getAlignmentStrategies();
		while(iter.hasNext()) {
			final IAutoAlignment alignment = (IAutoAlignment)iter.next();	
			item = new MenuItem(menuL2, SWT.CASCADE);
			item.setText(alignment.getDisplayName());
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						alignment.align(NodeSpaceWidget.this);
						setSize(computeSize(0,0,true));
					} catch (MicropsiException exc) {
						MindPlugin.getDefault().handleException(exc);
					}
					redraw();
				}
			});
		}

		itemL2.setMenu(menuL2);	
						
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Search node...");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				callback.lookUp(NodeSpaceWidget.this.getShell(), NodeSpaceWidget.this.myModel.getEntity().getID());
			}
		});

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Space properties...");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				callback.editSpaceProperties(NodeSpaceWidget.this.getShell(), NodeSpaceWidget.this.myModel.getEntity().getID());
			}
		});
		
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Save graphics");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				saveGraphics(0);
			}
		});
		
		item = new MenuItem(popUpMenu, SWT.SEPARATOR);
		
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Copy selected");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				copySelected();
			}
		});
		
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Paste");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					String s = (String) clipboard.getContents(TextTransfer.getInstance());
					List<EntityTransferData> l = EntityTransferData.textToList(s);
					dropItems(l, rightClickPoint.x, rightClickPoint.y);				
				} catch (Exception exc) {
					MindPlugin.getDefault().handleException(exc);
				}
			}
		});

	}

	public void copySelected() {
		ArrayList<EntityTransferData> toTransfer = new ArrayList<EntityTransferData>();
		Iterator<String> iter = getSelected();
		while(iter.hasNext()) {
			String id = iter.next();
			EntityWidget widget = getEntityWidget(id);
			EntityTransferData data = new EntityTransferData();
			
			data.net = (LocalNetFacade)netmodel.getNet();
			data.entity = widget.getEntity();
			data.x = widget.getLocation().x;
			data.y = widget.getLocation().y;
					
			toTransfer.add(data);
					
			addAdditionalDragEntities(id, toTransfer);
		}
		
		String s = EntityTransferData.listToText(toTransfer);
		clipboard.setContents(new Object[] {s}, new Transfer[] {TextTransfer.getInstance()});				
	}

	public void setDetail(int detail) {
		DETAIL = detail;
		switch(detail) {
			case DETAIL_FULL:
				selectedNodeLook = fullLook;
				selectedModuleLook = moduleLook;
				break;
			case DETAIL_COMPACT:
				selectedNodeLook = compactLook;
				selectedModuleLook = moduleLook;
				break;
			case DETAIL_DEFAULT:
				selectedNodeLook = styledLook;
				selectedModuleLook = styledModuleLook;
				break;
			default: selectedNodeLook = compactLook;
		}
		Iterator<EntityWidget> iter = items.values().iterator();
		while(iter.hasNext()) {
			EntityWidget nw = iter.next();
			if((nw.getEntity().getEntityType() != NetEntityTypesIF.ET_NODE)) { 
				nw.switchLook(selectedModuleLook);
				nw.setSize(nw.getSize());
				nw.pack();
			} else {
				nw.switchLook(selectedNodeLook);
				nw.setSize(nw.getSize());
				nw.pack();
			}
		}
//		redrawLinks();
		redraw();
	}
	
	public void commitRedraw(Composite c) {		
		redrawCommitter.element = c;
		Display.findDisplay(uiThread).syncExec(redrawCommitter);
		Thread.yield();
	}
	
	public void commitEntityCreation(String entityID, AbstractEntityLook look) {
		
		CreateCommitter createCommitter = new CreateCommitter();
		
		createCommitter.entityID = entityID;
		createCommitter.look = look;
		createCommitter.widget = this;
		
		Display.findDisplay(uiThread).syncExec(createCommitter);
		Thread.yield();
	}
	
	public void commitEntityDeletion(String key) {
		DeleteCommitter deleteCommitter = new DeleteCommitter();
		deleteCommitter.key = key;
		Display.findDisplay(uiThread).syncExec(deleteCommitter);
		Thread.yield();
	}

	public void updateEntities(Iterator changedNodeKeys, long netstep) {				
				
		// if the space is "dirty" and the net deleted some links
		// without us knowing where they were, everything needs to
		// be redrawn.
		if(	((NodeSpaceModule)myModel.getEntity()).hasDeletedLinks() &&
			DRAW_LINKS != DRAW_NONE) {
			commitRedraw(this);
		}
		
		while(changedNodeKeys.hasNext()) {	
			String key = (String)changedNodeKeys.next();
												
			if(!((NodeSpaceModule)myModel.getEntity()).containsEntityDirectly(key)) continue;
			
			EntityWidget nw = items.get(key);
			if(nw == null) continue;
						
			if(	nw.getEntity().getEntityType() != NetEntityTypesIF.ET_NODE &&
				uiThread.equals(Thread.currentThread())) {
					
				nw.calcLinkMenu();
				nw.setSize(nw.getSize());
				nw.pack();		
			}	
			commitRedraw(nw);
		}
		
		// this method is called from the agent control thread and we have
		// posted slow, async redraws. To avoid those redraws from being lost
		// and to allow the ui to catch up with the net, we yield.
		Thread.yield();
	}
	
	public void createEntities(Iterator newNodeKeys, long netstep) {			
		while(newNodeKeys.hasNext()) { 
			
			
			String key = (String)newNodeKeys.next();
			if(!items.containsKey(key) && ((NodeSpaceModule)myModel.getEntity()).containsEntityDirectly(key)) {			
				try {
					NetEntity entity = netmodel.getNet().getEntity(key);
					AbstractEntityLook look;
					if(entity.getEntityType() != NetEntityTypesIF.ET_NODE) {
						look = selectedModuleLook;
						commitEntityCreation(entity.getID(), look);
					} else {
						look = selectedNodeLook;
						if(numberOfNodes < MAX_NUMBER_OF_NODES) {
							commitEntityCreation(entity.getID(), look);
						}
					}
					
				} catch (MicropsiException e) {
					callback.handleException(e);
				}
			}
		}
		//commitRedraw(this);
	}
	
	public void deleteEntities(Iterator deletedNodeKeys, long netstep) {	
		boolean deletedone = false;
		while(deletedNodeKeys.hasNext()) {
			String nextkey = (String)deletedNodeKeys.next();			
			commitEntityDeletion(nextkey);
			deletedone = true;
		}
		if(deletedone) commitRedraw(this);
	}
	
	public void triggerEntityDelete(String id) {
		ArrayList<String> tmp = new ArrayList<String>();
		tmp.addAll(selectedItems);
		try { 
			Iterator<String> iter = tmp.iterator();
			while(iter.hasNext()) {
				String next = iter.next();					
				netmodel.getNet().deleteEntity(next);
			}
		} catch (MicropsiException e) {
			callback.handleException(e);
		}
	}
	
	public void triggerNodeCreate(int type) {
		try {
			netmodel.getNet().createNode(type,myModel.getEntity().getID());
		} catch (MicropsiException exception) {
			callback.handleException(exception);
		}
	}
	
	public void triggerNodeSpaceCreate() {
		try {
			netmodel.getNet().createNodeSpace(myModel.getEntity().getID());
		} catch (MicropsiException exception) {
			callback.handleException(exception);
		}
	}

	public void triggerLinkCreate(String id) {
		callback.createLink(this.getShell(), id);
	}
	
	public void triggerSlotCreate(String entityID) {
		callback.createSlot(entityID);
	} 
	
	public void triggerGateCreate(String entityID) {
		callback.createGate(entityID);
	}
	
	public void triggerOpen(String entityID) {
		callback.openEntity(entityID);
		redrawEntities();
	}
	
	public void triggerConnect(String nodeID) {
		callback.connectSensAct(nodeID);
	}
	
	public void triggerAutoUpdateStateChange(String entityID, boolean newState) {
		callback.setAutoUpdate(getShell(), entityID, newState);
	}
	
	protected void triggerInnerStatesEditor(String id) {
		callback.openInspector(getShell(), id);		
	}
	
	public void triggerDrop(String where, ArrayList<EntityTransferData> list) {
		callback.dropEntities(where,list);
	}

	
	public void redrawEntities() {
		Iterator<EntityWidget> iter = items.values().iterator();
		while(iter.hasNext()) {
			EntityWidget nw = iter.next();
			nw.pack(); 
			nw.redraw();
		}		
	}

	public void setEntityComment(String id, String comment) {
		items.get(id).setComment(comment);
	}
	
	public void saveGraphics(int type) {
		
		Point size=computeSize(0,0,false);

		Image image = new Image(null,size.x,size.y);
		GC imgGc = new GC(image);
		
		this.redrawAllLinks(imgGc);
		this.redrawEntities(imgGc);

		FileDialog fd = new FileDialog(getShell(),SWT.SAVE);
		fd.setText("Save graphics to");
		fd.setFileName(myModel.getEntity().getEntityName()+"_"+netmodel.getNet().getNetstep()+".bmp");
		fd.setFilterNames(new String[] {"Bitmaps","All files"});
		fd.setFilterExtensions(new String[] {"*.bmp","*.*"});
		fd.open();

		String name = fd.getFilterPath()+ "/"+ fd.getFileName();
		if(!name.endsWith(".bmp")) name += ".bmp";

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(name);
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] {image.getImageData()};
			loader.save(fout,SWT.IMAGE_BMP);
		} catch (Exception e) {
			callback.handleException(e);
		}
		
		imgGc.dispose();
		image.dispose();
		
	}

	public void bringToFront(String id) {
		if(!items.containsKey(id)) return;
		items.get(id).moveAbove(null);
	}
	
	public void destroy() {	
		selectedNodeLook = null;
		selectedModuleLook = null;
		callback = null;
		myModel = null;
		netmodel = null;
		workWidget = null;
		//selectedItems.clear();
		selectedItems = null;
		
		Iterator<EntityWidget> iter = items.values().iterator();
		while(iter.hasNext()) {
			EntityWidget e = iter.next();
			e.destroy();
		}
		//items.clear();
		items = null;
		dispose();	
		fullLook = null;
		compactLook = null;	
		styledLook = null;
		styledModuleLook = null;
		moduleLook = null;	
	}

	public void moveElement(EntityModel model) {
		// no need to call the real move method, as the model is already updated 
		getEntityWidget(model.getEntity().getID()).setLocation(model.getX(), model.getY());
	}

	public ArrayList<EntityModel> getElements() {		
		ArrayList<EntityModel> toReturn = new ArrayList<EntityModel>(items.size());
		Iterator<String> iter = items.keySet().iterator();
		try {
			while(iter.hasNext())
				toReturn.add(netmodel.getModel(iter.next()));
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
			return new ArrayList<EntityModel>();
		}
		return toReturn;
	}
	/**
	 * Sets the scaling of the widget and tells children
	 * to do the same - but applies only to the values (i.e.
	 * does not change the display yet)
	 * @param scaling the scaling factor (percentage)
	 */
	public void setScaling(int scaling) {	
		if (scaling < 1) scaling = 1;
		scalingInPercent = scaling;
		styledModuleLook.setScale(scalingInPercent);
		styledLook.setScale(scalingInPercent);	
	}
	
	/**
	 * Returns the scale value of the pane (a percentage)
	 * @return scaling
	 */
	public int getScaling() {
		return scalingInPercent;
	}
	
	/**
	 * Is meant to be called by an external zoom widget.
	 * Calls scaling, sets the size of the canvas,
	 * repositions all widgets and returns the scale value
	 * (which might differ because of rounding and bounding)
	 * @param viewScaleInPercent the scaling value
	 * @return the (corrected) scaling value
	 */
	public int setViewScale(int viewScaleInPercent) {
		// this function is called from the zoom button
		setScaling(viewScaleInPercent);
		setSize(computeSize(0,0,true));
		Iterator<EntityWidget> iter = items.values().iterator();
		
		while(iter.hasNext()) {
			EntityWidget nw = iter.next();
			nw.setLocation(nw.getPositionFromEntityModel(scalingInPercent));
		}
		redrawEntities();
		commitRedraw(this);
		return getScaling();
	}

	public void dropItems(List<EntityTransferData> toDrop, int x, int y) {
		
		deselectEverything();
		
		try {
	
			NetFacadeIF net = netmodel.getNet();
			ArrayList<NetEntity> entities = new ArrayList<NetEntity>();
			
			for(int i=0;i<toDrop.size();i++) {
				EntityTransferData data = toDrop.get(i);
				NetEntity entity = data.entity;
				entities.add(entity);
			}
			
			HashMap<String, String> cloneMap = new HashMap<String, String>();
		
			MessageDialog dlg = new MessageDialog(
				getShell(),
				"Preserve external links?",
				null,
				"Preserve all links or only the links between the items to be inserted?",
				0,
				new String[] {"Only between items","All","No links","Cancel"},
				0);
				
			dlg.open();
			int ret = dlg.getReturnCode();
			int preservemode = NetWeaver.PM_PRESERVE_INTER;
			switch(ret) {
				case 1: preservemode = NetWeaver.PM_PRESERVE_ALL; break;
				case 2: preservemode = NetWeaver.PM_PRESERVE_NONE; break;
				case 3: return;
			}
			
			NetWeaver.insertEntities(
				net, 
				entities, 
				myModel.getEntity().getID(),
				preservemode, 
				cloneMap,
				callback.createProgressMonitor("Inserting entities...")
			);

			Point p = toControl(x, y);
			
			EntityTransferData refdata = toDrop.get(0);
			int refx = refdata.x;
			int refy = refdata.y;			

			// move the drop point if positions would get messed up (by the borders)
			
			int leftshift = 0;
			int upshift = 0;
			
			for(int i=0;i<toDrop.size();i++) {
				EntityTransferData data = toDrop.get(i);
				NetEntity clonedEntity = data.entity;
				String cloneId = cloneMap.get(clonedEntity.getID());

				if(this.items.containsKey(cloneId)) {
					int realx = p.x+(data.x-refx);
					int realy = p.y+(data.y-refy);
					
					if(realx < 0 && realx < leftshift) {
						leftshift = realx;	
					}
					
					if(realy < 0 && realy < upshift) {
						upshift = realy;	
					}
				}	
			}			
			
			if(leftshift < 0) leftshift -= 30;
			if(upshift < 0) upshift -= 30;
			
			p.x -= leftshift;
			p.y -= upshift;
			
			for(int i=0;i<toDrop.size();i++) {
				EntityTransferData data = toDrop.get(i);
				NetEntity clonedEntity = data.entity;
				String cloneId = cloneMap.get(clonedEntity.getID());

				if(this.items.containsKey(cloneId)) {
					String newId = cloneMap.get(clonedEntity.getID());
					EntityWidget clonew = getEntityWidget(newId);
					int realx = p.x+(data.x-refx);
					int realy = p.y+(data.y-refy);
					moveWidgetTo(clonew, realx, realy);
					select(newId);
				} else {
					EntityModel newItemModel = netmodel.getModel(cloneId);
					newItemModel.setX(data.x);
					newItemModel.setY(data.y);
				}	
			}			


			EntityWidget leaderWidget = null;
			
			for(int i=0;i<toDrop.size();i++) {
				EntityTransferData data = toDrop.get(i);
				NetEntity clonedEntity = data.entity;
				String cloneId = cloneMap.get(clonedEntity.getID());

				if(this.items.containsKey(cloneId)) {
					String newId = cloneMap.get(clonedEntity.getID());
					EntityWidget clonew = getEntityWidget(newId);
					
					if(i == 0) leaderWidget = clonew;
					
					int realx = p.x+(data.x-refx);
					int realy = p.y+(data.y-refy);

					EntityModel newItemModel = netmodel.getModel(cloneId);
					newItemModel.setX(realx);
					newItemModel.setY(realy);
					
					setSize(recomputeSize(0,0,clonew));
					moveWidgetTo(clonew, realx, realy);					
					
					select(newId);
				} else {
					EntityModel newItemModel = netmodel.getModel(cloneId);
					newItemModel.setX(data.x);
					newItemModel.setY(data.y);
				}	
			}
			
		} catch (Exception e) {
			callback.handleException(e);			
		}
		
	}

	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.plugin.ui.SelectionPaneWidget#addAdditionalDragEntities(java.lang.String, java.util.ArrayList)
	 * This method is called when an entity is about to be dragged somewhere and
	 * could have nested entities that would also have to be added to the list of
	 * transferred objects. This is the case if the entity is a nodespace.
	 */
	public void addAdditionalDragEntities(String id, ArrayList<EntityTransferData> toTransfer) {
		
		try {
			EntityModel m = netmodel.getModel(id);
			if(m.getEntity().getEntityType() != NetEntityTypesIF.ET_MODULE_NODESPACE) 
				return; 
			
			Iterator iter = ((NodeSpaceModule)m.getEntity()).getAllEntities();
			while(iter.hasNext()) {
				NetEntity entity = (NetEntity)iter.next();
				EntityModel nestedM = netmodel.getModel(entity.getID());
				EntityTransferData data = new EntityTransferData();
				data.net = (LocalNetFacade)netmodel.getNet();
				data.entity = nestedM.getEntity();
				data.x = nestedM.getX();
				data.y = nestedM.getY();
				toTransfer.add(data);
			}
		} catch (MicropsiException e) {
			callback.handleException(e);
		}
		
	}
	
	public boolean isSelected(String key) {
		return selectedItems.contains(key);
	}

	public void deselectEverything() {
		workList.clear();
		workList.addAll(selectedItems);
		selectedItems.clear();
		for(int i=0;i<workList.size();i++) items.get(workList.get(i)).redraw();
		redraw();
	}
	
	public void select(String key) {
		if(items.containsKey(key)) {
			selectedItems.add(key);
			getEntityWidget(key).setFocus();
			getEntityWidget(key).moveAbove(null);
			getEntityWidget(key).redraw();
			redraw();
		}
		callback.selectEntity(key);
	}
	
	public void deselect(String key) {
		selectedItems.remove(key);
		redraw();		
	}
	
	public Iterator<String> getSelected() {
		return selectedItems.iterator();
	}

	
	public boolean isMultiSelect() {
		return (selectedItems.size() > 1);
	}

	/**
	 * Adds a widget on the pane
	 * (looks for grid positions and view scaling)
	 * @param nw the Selectable Widget
	 */	
	protected void addEntityWidget(final EntityWidget nw) {
		
		numberOfNodes++;
		
		Point p = nw.computeSize(50,50);
		int grid = gridWidth * scalingInPercent / 100;
		int x = (rightClickPoint.x+grid/2)/grid*grid;
		int y = (rightClickPoint.y+grid/2)/grid*grid;

		nw.setBounds(x,y,p.x,p.y);
		items.put(nw.getKey(),nw);
		
		//@todo Joscha: There's a bug that prevents new items from being created
		// in the right place when this is enabled
		
//		this.setSize(recomputeSize(0,0,nw));
	}
	
	public EntityWidget getEntityWidget(String key) {
		return items.get(key);
	}
	
	/**
	 * Sets a selectable widget to screen coordinates, which are adjusted
	 * according to view scaling and grid width. For the actual positioning,
	 * the moveTo-method of the widget is called.
	 * @param nw the selectable widget
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void moveWidgetTo(EntityWidget nw, int x, int y) {
		if(x < 0) x = 0;
		if(y < 0) y = 0;
				
		int grid = gridWidth * scalingInPercent / 100;
			
		x = (x+grid/2)/grid*grid;
		y = (y+grid/2)/grid*grid;
		
		if(x+nw.getSize().x > this.getSize().x) x = this.getSize().x - nw.getSize().x;
		if(y+nw.getSize().y > this.getSize().y) y = this.getSize().y - nw.getSize().y;
		nw.moveTo(x,y);
	}
	
	public synchronized void removeEntityWidget(String key) {
		if(items.get(key) != null) {
			
			numberOfNodes--;
			
			callback.setAutoUpdate(getShell(), key, false);
			
			EntityWidget w = items.get(key);
			w.dispose();		
			items.remove(key);
			selectedItems.remove(key);
			this.setSize(computeSize(0,0,true));
		}
	}


	public void enterDragState(EntityWidget dragged) {
		STATE = STATE_DRAG;	
		dragStartPoint = toDisplay(dragged.getLocation());
		dragStartPoint.x += dragged.getTouchX();
		dragStartPoint.y += dragged.getTouchY();
		this.workWidget = dragged;
	}
	
	public void leaveDragState() {
		STATE = STATE_NORMAL;
		this.setSize(computeSize(0,0,true)); //perhaps canvas bounds have changed
		//@todo Joscha: there is a redraw bug: links stay and should be deleted. Fix!
		redrawAllLinks(gc);
	}	
		
	public void dragTo(int x, int y) {
		
		if(STATE != STATE_DRAG) return;
				
		Point oldPos = workWidget.getLocation();		
		Point dragPoint = toControl(new Point(x,y));
		dragPoint.x -= workWidget.getTouchX();
		dragPoint.y -= workWidget.getTouchY();
	
		moveWidgetTo(workWidget,dragPoint.x, dragPoint.y);
		this.setSize(recomputeSize(0,0,workWidget));
		
		int relx = dragPoint.x - oldPos.x;
		int rely = dragPoint.y - oldPos.y;
		
		Iterator<String> iter = selectedItems.iterator();
		while(iter.hasNext()) {
			EntityWidget nw = items.get(iter.next());
			Point wpos = nw.getLocation();
			if(nw != workWidget) {
				moveWidgetTo(nw, wpos.x + relx,wpos.y + rely);
				this.setSize(recomputeSize(0,0,nw));
			}
		}
		
		this.getDisplay().readAndDispatch();
	}

	private void drawSelectionRect(Color color) {
		gc.setLineWidth(1);
		gc.setForeground(color);
		gc.setLineStyle(SWT.LINE_DOT);
		gc.drawRectangle(workRect);
		gc.setLineStyle(SWT.LINE_SOLID);
	}

	private void selectInSelRect() {
		if(workRect.width < 0) {
			workRect.width = - workRect.width;
			workRect.x = workRect.x - workRect.width;
		}
		if(workRect.height < 0) {
			workRect.height = - workRect.height;
			workRect.y = workRect.y - workRect.height;
		}
		
		Iterator<EntityWidget> iter = items.values().iterator();
		while(iter.hasNext()) {
			EntityWidget nw = iter.next();
			Point lowerRight = nw.getLocation();
			lowerRight.x += nw.getBounds().width;
			lowerRight.y += nw.getBounds().height;			
			if(workRect.contains(nw.getLocation()) &&
				workRect.contains(lowerRight)
			) {
				select(nw.getKey());
				nw.redraw();
			}
		}
	}
	
	public Point getSelectedItemPosition() {
		int x = this.getSize().x;
		int y = this.getSize().y;
		
		Iterator<String> iter = selectedItems.iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			EntityWidget w = getEntityWidget(key);
			if(w.getLocation().x < x && w.getLocation().y < y) {
				x = w.getLocation().x;
				y = w.getLocation().y;
			}
		}
		
		return new Point(x,y);
	}
	
	public int getState() {
		return STATE;
	}
	
	/**
	 * Returns the value for the positioning grid at scale 100.
	 * @return grid size
	 */
	public int getGridWidth () {
		return gridWidth;
	}

	/**
	 * Returns the current link draw mode
	 * @return
	 */
	public int getLinkDrawMode() {
		return DRAW_LINKS;
	}
	
	public void setLinkDrawMode(int mode) {
		DRAW_LINKS = mode;
		redraw();	
	}

}
