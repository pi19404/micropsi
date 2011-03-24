/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/EntityWidget.java,v 1.5 2005/07/12 12:53:54 vuine Exp $
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetCycleIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.Slot;

public class EntityWidget extends Canvas {

	private EntityModel myModel;
	private NetModel netmodel;
	
	private final NodeSpaceWidget parent;
	private DragSource dragSource;

	private int touchX = 0;
	private int touchY = 0;
	
	private int newLinkType = -1; 
	
	private AbstractEntityLook entityLook;
	
	private Menu linkMenu;
	
	public EntityWidget(final NodeSpaceWidget parent, int style, NetModel netmodel, String entityid, AbstractEntityLook iniLook) throws MicropsiException {
		super(parent, style);
		
		this.parent = parent;
		this.myModel = netmodel.getModel(entityid);
		this.netmodel = netmodel;
		this.entityLook = iniLook;
		
		dragSource = new DragSource(this,DND.DROP_MOVE | DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] {TextTransfer.getInstance()});
		
		dragSource.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
				
				NetCycleIF cycle = EntityWidget.this.netmodel.getNet().getCycle(); 
				
				if(!cycle.isSuspended()) {
					cycle.suspend();
					event.doit = false;
					return;
				}
				
				
				event.detail = DND.DROP_MOVE;
				EntityWidget.this.parent.select(EntityWidget.this.getKey());
				
				if(event.detail == DND.DROP_MOVE)
					EntityWidget.this.parent.enterDragState(EntityWidget.this);
			}

			public void dragSetData(DragSourceEvent event) {
				ArrayList<EntityTransferData> toTransfer = new ArrayList<EntityTransferData>();
				Iterator iter = EntityWidget.this.parent.getSelected();
				while(iter.hasNext()) {
					String id = (String)iter.next();
					EntityWidget widget = EntityWidget.this.parent.getEntityWidget(id);
					EntityTransferData data = new EntityTransferData();
					data.net = (LocalNetFacade)EntityWidget.this.netmodel.getNet();
					data.entity = widget.myModel.getEntity();
					data.x = widget.getLocation().x;
					data.y = widget.getLocation().y;
					
					// add this element as the "selection leader"
					if(id.equals(getKey()))
						toTransfer.add(0,data);
					else
						toTransfer.add(data);
					
					EntityWidget.this.parent.addAdditionalDragEntities(id, toTransfer);
				}
				
				event.detail = DND.DROP_MOVE;
				event.data = EntityTransferData.listToText(toTransfer);
			}

			public void dragFinished(DragSourceEvent event) {				
				if(EntityWidget.this.parent.getState() != NodeSpaceWidget.STATE_DRAG) {
					// drop to another SelectionPaneWidget
					if(event.detail == DND.DROP_MOVE) {
						Iterator iter = ((ArrayList)event.data).iterator();
						while(iter.hasNext())
							EntityWidget.this.parent.removeEntityWidget((String)iter.next());					
					}
				}				
				EntityWidget.this.parent.leaveDragState();
			}
			
		});
		
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dragSource.dispose();
			}
			
		});
							
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				EntityWidget.this.paintControl(e);
			}
		});
		
		addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {

				NetCycleIF cycle = EntityWidget.this.netmodel.getNet().getCycle(); 
				
				if(!cycle.isSuspended()) {
					cycle.suspend();
					return;
				}

				
				EntityWidget.this.open();
			}
			
			public void mouseUp(MouseEvent e) {
							
				NetCycleIF cycle = EntityWidget.this.netmodel.getNet().getCycle(); 
				
				if(!cycle.isSuspended()) {
					cycle.suspend();
					return;
				}
								
				if(e.button == 1) {					
					switch(EntityWidget.this.parent.getState()) {
						case NodeSpaceWidget.STATE_NORMAL:
							if((e.stateMask & SWT.CTRL) > 0) { // if strg_pressed
								if(isSelected()) deselect();
									else select();
							} else {
								EntityWidget.this.parent.deselectEverything();
								select();
							} 
							
							parent.commitRedraw(EntityWidget.this);
							break;
						default:
					}
				}			
			}
			
			public void mouseDown(MouseEvent e) {
				
				NetCycleIF cycle = EntityWidget.this.netmodel.getNet().getCycle(); 
				
				if(!cycle.isSuspended()) {
					return;
				}

				
				touchX = e.x;
				touchY = e.y;
				switch(EntityWidget.this.parent.getState()) {
					case NodeSpaceWidget.STATE_NORMAL:
						if(e.button > 1 || ((e.stateMask & SWT.CTRL) != 0)) break;
						if(!isSelected()) {
							EntityWidget.this.parent.deselectEverything();
							select();
						}						 
						break;
					case NodeSpaceWidget.STATE_CREATELINK:
						EntityWidget.this.parent.leaveLinkCreationState(
							EntityWidget.this,
							entityLook.getClickedSlot(EntityWidget.this, e.x, e.y)
						);
						break;
					default:
				}				
			}			
		});		

		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {						
				switch(e.keyCode) {
					case SWT.DEL:
						delete();
						break;
					case SWT.ARROW_LEFT:
						moveWidget(-1,0);
						break;
					case SWT.ARROW_RIGHT:
						moveWidget(1,0);
						break;
					case SWT.ARROW_UP:
						moveWidget(0,-1);
						break;
					case SWT.ARROW_DOWN:
						moveWidget(0,1);
						break;
					
				}
			}
	
		});
								
		createPopup();
								
	}
	
	void paintControl(PaintEvent e) {
		paintToGC(e.gc,false);
	}
	
	public void switchLook(AbstractEntityLook look) {
		this.entityLook = look;
		calcLinkMenu();
		parent.commitRedraw(this);
	} 

	protected void createPopup() {

		Menu popUpMenu = new Menu(this.getShell(),SWT.POP_UP);
		this.setMenu(popUpMenu);
		final int c = popUpMenu.getItemCount();		
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu)e.widget;
				MenuItem[] items = menu.getItems();
				items[c+0].setEnabled(parent.getState() == NodeSpaceWidget.STATE_NORMAL); // link
			}
		});

		MenuItem itemL2 = new MenuItem(popUpMenu, SWT.CASCADE);
		itemL2.setText("Link...");
		Menu menuL2 = new Menu(this.getShell(), SWT.DROP_DOWN);

		this.linkMenu = menuL2;		
		itemL2.setMenu(menuL2);
		
		calcLinkMenu();
		
		MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Link wizard...");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EntityWidget.this.parent.triggerLinkCreate(myModel.getEntity().getID());
			}
		});		
		
		if(myModel.getEntity().getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE) {
			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Create slot...");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					EntityWidget.this.parent.triggerSlotCreate(myModel.getEntity().getID());
				}
			});
			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Create gate...");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					EntityWidget.this.parent.triggerGateCreate(myModel.getEntity().getID());
				}
			});
		}

		if(myModel.getEntity().getEntityType() == NetEntityTypesIF.ET_MODULE_NATIVE) {
			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Inner states...");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					EntityWidget.this.parent.triggerInnerStatesEditor(myModel.getEntity().getID());
				}
			});

/*			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Edit implementation");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					EntityWidget.this.parent.triggerOpen(myModel.getEntity().getID());
				}
			});*/
						
			item = new MenuItem(popUpMenu, SWT.CHECK);
			item.setText("Auto-update");
			item.setSelection(true);
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					EntityWidget.this.parent.triggerAutoUpdateStateChange(
					myModel.getEntity().getID(), ((MenuItem)e.widget).getSelection()
					);
				}
			});

		}
		
		if(myModel.getEntity().getEntityType() == NetEntityTypesIF.ET_NODE) {
			Node node = (Node)myModel.getEntity();
			if(	node.getType() == NodeFunctionalTypesIF.NT_SENSOR ||
				node.getType() == NodeFunctionalTypesIF.NT_ACTOR) {
				
				item = new MenuItem(popUpMenu, SWT.CASCADE);
				item.setText("Data connection...");
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						EntityWidget.this.parent.triggerConnect(myModel.getEntity().getID());
					}
				});	
			}		
		}

		item = new MenuItem(popUpMenu, SWT.SEPARATOR);
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EntityWidget.this.parent.triggerOpen(myModel.getEntity().getID());
			}
		});		
		
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Edit");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EntityWidget.this.parent.triggerOpen(myModel.getEntity().getID());
			}
		});		

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Delete");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EntityWidget.this.parent.triggerEntityDelete(myModel.getEntity().getID());
			}
		});		

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Send to back");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveBelow(null);
			}
		});		

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Bring to front");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveAbove(null);
			}
		});
		
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Copy");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EntityWidget.this.parent.copySelected();
			}
		});		

	}
	
	protected void calcLinkMenu() {

		linkMenu.setEnabled(false);

		for(int i=linkMenu.getItemCount()-1;i>=0;i--)
			linkMenu.getItem(i).dispose();
		
		final int[] lt = entityLook.getCreatableLinkTypes(this);
		for(int i=0;i<lt.length;i++) {
			MenuItem item = new MenuItem(linkMenu, SWT.CASCADE);
			item.setText(TypeStrings.gateType(lt[i]));
			final int type = i;
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					newLinkType = lt[type];
					parent.enterLinkCreationState(EntityWidget.this);
				}
			});
		}
		
		linkMenu.setEnabled(true);
	}
	
	public GC paintToGC(GC gc,boolean absolute) {
		if(entityLook != null)
			entityLook.paintToGC(gc,this,isSelected(),absolute);
		return gc;
	}
	
	public void eraseLinkTo(EntityWidget otherWidget, GC gc, Link link) {
		entityLook.eraseLink(this,otherWidget,gc,link);
	}
	
	public void eraseLinkTo(int x, int y, GC gc, int type) {
		entityLook.eraseLink(this,x,y,gc,type);
	}

	public void drawLinkTo(EntityWidget otherWidget, GC gc, Link link) {
		entityLook.drawLink(this,otherWidget,gc,link);
	}

	private boolean drawLinkToSelect(EntityWidget otherWidget, Link link, int mouseX, int mouseY) {
		return entityLook.drawLinkSelect(this,otherWidget,link,mouseX,mouseY);
	}
	
	public void drawLinkFrom(EntityWidget otherWidget, GC gc, Link link) {
		entityLook.drawLink(otherWidget,this,gc,link);
	}

	public void drawLinkTo(int x, int y, GC gc, int type) {
		entityLook.drawLink(this,x,y,gc,type);
	}

	public void eraseLinks(GC gc, boolean drawAlsoIncomingLinks) {
		Iterator iter = myModel.getEntity().getGates();
		while(iter.hasNext()) {
			Gate g = (Gate)iter.next();
			Iterator iter2 = g.getLinks();
			while(iter2.hasNext()) {
				Link link = (Link)iter2.next();
				eraseLinkTo(
					parent.getEntityWidget(link.getLinkedEntityID()),
					gc,
					link
				);
			}
		}
		if(drawAlsoIncomingLinks) {
			iter = myModel.getEntity().getSlots();
			while(iter.hasNext()) {
				Slot s = (Slot)iter.next();
				Iterator iter2 = s.getIncomingLinks();
				while(iter2.hasNext()) {
					Link l = (Link)iter2.next();
					EntityWidget nw = parent.getEntityWidget(l.getLinkingGate().getNetEntity().getID());
					if(nw != null) {
						nw.eraseLinks(gc,false);
					} else { 
						entityLook.eraseLink(null,this,gc,l);
					}
				}
			}		
		}
	}
	
	/**
	 * Returns the first link that matches the clicked position
	 * according to a line structure that must match the drawing
	 */
    public Link drawLinksSelect (boolean drawAlsoIncomingLinks, int mouseX, int mouseY) {
	   Iterator iter = myModel.getEntity().getGates();
	   while(iter.hasNext()) {
		   Gate g = (Gate)iter.next();
		   Iterator iter2 = g.getLinks();
		   while(iter2.hasNext()) {
			   Link link = (Link)iter2.next();
			   if (drawLinkToSelect(
				   parent.getEntityWidget(link.getLinkedEntityID()),
				   link,
				   mouseX, mouseY
			   )) return link;
		   }
	   }
	   if(drawAlsoIncomingLinks) {
	   	   Link selectedLink = null;
		   iter = myModel.getEntity().getSlots();
		   while(iter.hasNext()) {
			   Slot s = (Slot)iter.next();
			   Iterator iter2 = s.getIncomingLinks();
			   while(iter2.hasNext()) {
				   Link l = (Link)iter2.next();
				   EntityWidget nw = parent.getEntityWidget(l.getLinkingGate().getNetEntity().getID());
				   if(nw != null) {
					   selectedLink=nw.drawLinksSelect(false,mouseX,mouseY);
					   if (selectedLink!=null) return selectedLink;
				   } else { 
					   if (entityLook.drawLinkSelect(null,this,l,mouseX,mouseY)) return l;
				   }
			   }
		   }		
	   }
	   return null;
   }
	
	public void drawLinks(GC gc, boolean drawAlsoIncomingLinks) {
		Iterator iter = myModel.getEntity().getGates();
		while(iter.hasNext()) {
			Gate g = (Gate)iter.next();
			Iterator iter2 = g.getLinks();
			while(iter2.hasNext()) {
				Link link = (Link)iter2.next();
				drawLinkTo(
					parent.getEntityWidget(link.getLinkedEntityID()),
					gc,
					link
				);
			}
		}
		if(drawAlsoIncomingLinks) {
			iter = myModel.getEntity().getSlots();
			while(iter.hasNext()) {
				Slot s = (Slot)iter.next();
				Iterator iter2 = s.getIncomingLinks();
				while(iter2.hasNext()) {
					Link l = (Link)iter2.next();
					EntityWidget nw = parent.getEntityWidget(l.getLinkingGate().getNetEntity().getID());
					if(nw != null) {
						nw.drawLinks(gc,false);
					} else { 
						entityLook.drawLink(null,this,gc,l);
					}
				}
			}		
		}
	}
	
	protected Point getNeededSize() {
		if(entityLook == null) return new Point(10,10);
		return entityLook.getNeededSize(this);
	}

	public NetEntity getEntity() {
		return myModel.getEntity();
	}

	public int getNewLinkType() {
		return newLinkType;
	}
	
	public void open() {
		parent.triggerOpen(getEntity().getID());
	}
	
	public void delete() {
		parent.triggerEntityDelete(getEntity().getID());
	}
	
	/**
	 * Shifts an entity widget by deltax, deltay. Because a grid 
	 * is applied, the distance must be >= the grid size
	 * for the shifting to apply.
	 * @param deltax the relative x coordinate 
	 * @param deltay the relative y coordinate
	 */
	public void moveWidget(int deltax, int deltay) {
		int scalingInPercent=parent.getScaling();
		int gridWidth=parent.getGridWidth();
		
		deltax = gridWidth * deltax*scalingInPercent/100;
		deltay = gridWidth * deltay*scalingInPercent/100;
		parent.moveWidgetTo(this, getLocation().x+deltax, getLocation().y+deltay);
		parent.setSize(parent.recomputeSize(0,0,this));
	}
	
	public String getKey() {
		return getEntity().getID();
	}
	
	/**
	 * Positions an entity widget at displayed coordinates x, y. 
	 * After applying the scaling factor of the parent pane widget,
	 * the true coordinates are written into the entity model.
	 */	
	public void moveTo(int x, int y) {
		int scalingInPercent=parent.getScaling();
		if(parent.DRAW_LINKS != NodeSpaceWidget.DRAW_NONE)
			this.eraseLinks(parent.gc,true);
		this.setLocation(x,y);
		if(parent.DRAW_LINKS != NodeSpaceWidget.DRAW_NONE)
			this.drawLinks(parent.gc,true);
		
		myModel.setX(x,scalingInPercent);
		myModel.setY(y,scalingInPercent);
	}
	
	/**
	 * Returns the location of the entity widget according to its
	 * entity model as it looks after applying the scaling factor
	 * @param scalingInPercent the scaling factor
	 * @return the location
	 */
	public Point getPositionFromEntityModel(int scalingInPercent) {
		return new Point (myModel.getX(scalingInPercent),myModel.getY(scalingInPercent));
	}
	
	/**
	 * Offers a way to put positions directly into the model
	 */
	public void setPositionIntoEntityModel(int scalingInPercent,int x, int y) {
		myModel.setX(x,scalingInPercent);
		myModel.setY(y,scalingInPercent);
	}
	
	public AbstractEntityLook getLook() {
		return this.entityLook;
	}

	/**
	 * Returns the comment.
	 * @return String
	 */
	public String getComment() {
		return myModel.getComment();
	}

	/**
	 * Sets the comment.
	 * @param comment The comment to set
	 */
	public void setComment(String comment) {
		myModel.setComment(comment);
		setToolTipText(comment);
	}
	
	public String getToolTipText() {
		//super.getToolTipText();
		return myModel.getComment();
	}
	
	public void destroy() {		
		dispose();
		entityLook = null;
		linkMenu = null;
		netmodel = null;
		myModel = null;
	}

	protected void triggerDrop(ArrayList<EntityTransferData> list, int x, int y) {
		if(myModel.getEntity().getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE)
			parent.triggerDrop(myModel.getEntity().getID(), list);
	}

	protected boolean isSelected() {
		return parent.isSelected(getKey());
	}

	public int getTouchX() {
		return touchX;
	}

	public int getTouchY() {
		return touchY;
	}

	protected void select() {
		parent.select(getKey());
		parent.commitRedraw(this);
	}
	
	protected void deselect() {
		parent.deselect(getKey());
		parent.commitRedraw(this);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return getNeededSize();
	}

}
