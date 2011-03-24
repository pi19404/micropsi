package org.micropsi.eclipse.media;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.console.worldconsole.CoordinateGridRenderer;
import org.micropsi.comp.console.worldconsole.IOverlayRenderer;
import org.micropsi.comp.console.worldconsole.IRenderInfo;
import org.micropsi.comp.console.worldconsole.model.WorldObject;
import org.micropsi.media.VideoServer;
import org.micropsi.media.VideoServerRegistry;


public class VideoView extends ViewPart implements IRenderInfo {
	
	private static VideoView instance;
	
	public static VideoView getInstance() {
		return instance;
	}
	
	public static final int MODE_SELECT_NORMAL = 0;
	public static final int MODE_SELECT_ORIGIN = 1;
	public static final int MODE_SELECT_POINT = 2;
	
	private Thread swtThread = null;
	private Frame awtFrame = null;
	private Composite swtBed;
	private Composite topLevel; 
	private Component visualComponent;
	private Panel awtRoot;
	private JLayeredPane layeredPane;
	private OverlayCanvas overlay;
	private VideoServer server;
	private Color bgColor;
	
	private double scaleX = 1;
	private double scaleY = 1;
	
	double worldOriginX = 0;
	double worldOriginY = 0;
	
	double pixelsPerUnitX = 1;
	double pixelsPerUnitY = 1;
	
	private int mode = MODE_SELECT_NORMAL;
	
	private String initialServer = null;
	
	/**
	 * The constructor.
	 */
	public VideoView() {
		instance = this;
	}
	
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site,memento);

		try {
			scaleX = memento.getFloat("scaleX");
			scaleY = memento.getFloat("scaleY");
			
			worldOriginX = memento.getFloat("worldOriginX");
			worldOriginY = memento.getFloat("worldOriginY");
	
			pixelsPerUnitX = memento.getFloat("worldScaleX");
			pixelsPerUnitY = memento.getFloat("worldScaleY");
			
			initialServer = memento.getString("server");
			if("null".equals(initialServer)) {
				initialServer = null;
			}
		} catch (Exception e) {
			// that's ok, the memento may not have been used
		}		
	}
	
	public void dispose() {
		super.dispose();
		
		awtFrame.dispose();
		
//		import java.awt.BorderLayout;
//		import java.awt.Component;
//		import java.awt.Cursor;
//		import java.awt.Dimension;
//		import java.awt.Frame;
//		import java.awt.Panel;
	}
	
	public void saveState(IMemento memento) {
		super.saveState(memento);
		
		memento.putFloat("scaleX",(float)scaleX);
		memento.putFloat("scaleY",(float)scaleY);
		
		memento.putFloat("worldOriginX",(float)worldOriginX);
		memento.putFloat("worldOriginY",(float)worldOriginY);
		
		memento.putFloat("worldScaleX",(float)pixelsPerUnitX);
		memento.putFloat("worldScaleY",(float)pixelsPerUnitY);
		
		if(server != null) {
			memento.putString("server",server.getName());
		} else {
			memento.putString("server","null");
		}
	}
	
	public void createPartControl(Composite parent) {
		
		swtThread = Thread.currentThread();
		
		bgColor = new Color(parent.getDisplay(),0,0,0);
		
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				bgColor.dispose();
			}
		});
		
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		parent.setLayout(layout);
		
		topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayoutData(new GridData(GridData.FILL_BOTH));
		topLevel.setBackground(bgColor);
		
		swtBed = new Composite(topLevel,SWT.EMBEDDED);
		swtBed.setSize(100,100);
		topLevel.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				swtBed.setLocation((topLevel.getSize().x/2)-(swtBed.getSize().x/2),(topLevel.getSize().y/2)-(swtBed.getSize().y/2));
			}
		});
		swtBed.setLayout(null);		
		
		awtFrame = SWT_AWT.new_Frame(swtBed);		
		
		awtFrame.setBackground(new java.awt.Color(0,0,0));
		
		// heavyweight component so cursor changes and mouse events work
		awtRoot = new Panel();
		awtRoot.setLayout(new BorderLayout());
		awtRoot.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				switch(mode) {
					case MODE_SELECT_NORMAL:
						break;
					case MODE_SELECT_ORIGIN:
						calibrateOrigin(e.getX(),e.getY());
						break;
					case MODE_SELECT_POINT:
						calibrateOtherPoint(e.getX(),e.getY());
						break;
				}
			}			
		});
		awtFrame.add(awtRoot);
		
		layeredPane = new JLayeredPane();
		layeredPane.setOpaque(true);
		layeredPane.setDoubleBuffered(true);
		layeredPane.setPreferredSize(new Dimension(swtBed.getSize().x,swtBed.getSize().x));
		awtRoot.add(layeredPane,BorderLayout.CENTER);		

		overlay = new OverlayCanvas(swtBed.getShell(),this);
		layeredPane.add(overlay,new Integer(5));
		overlay.setSize(100,100);
		overlay.setLocation(0, 0);
		
		awtFrame.pack();
								
//		setWorldOrigin(0, 0);
//		setWorldTranslation(320, 240, 82, 62);
		
		addUninitializedRenderer("grid", new CoordinateGridRenderer());
		
		getViewSite().getActionBars().getToolBarManager().add(new SetTranslationAction(this));
		getViewSite().getActionBars().getToolBarManager().add(new ScalingPulldownAction(this));
		getViewSite().getActionBars().getToolBarManager().add(new ServerPulldownAction(this));
		getViewSite().getActionBars().getToolBarManager().add(new CloneViewAction(this));
		
		if(initialServer != null) {
			
//			VideoServerRegistry.getInstance().waitForAllServers();
			
			VideoServer server = VideoServerRegistry.getInstance().getServer(initialServer);
			if(server != null) {
				setServer(initialServer);
			}
		}
		
	}
	
	public void addUninitializedRenderer(String name, IOverlayRenderer renderer) {
		overlay.addOverlayRenderer(name,renderer);
		renderer.init(overlay,this);
	}

	public void setServer(final String name) {

		server = VideoServerRegistry.getInstance().getServer(name);
		swtBed.setSize((int)Math.floor(server.getVisualSize().width*scaleX), (int)Math.floor(server.getVisualSize().height*scaleY));
		swtBed.setLocation((topLevel.getSize().x/2)-(swtBed.getSize().x/2),(topLevel.getSize().y/2)-(swtBed.getSize().y/2));
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					if(visualComponent != null) {
						layeredPane.remove(visualComponent);
					}					
					Dimension newDimensionForAll = new Dimension((int)Math.floor(server.getVisualSize().width*scaleX), (int)Math.floor(server.getVisualSize().height*scaleY));
					awtRoot.setSize((Dimension)newDimensionForAll.clone());
					layeredPane.setSize((Dimension)newDimensionForAll.clone());
					visualComponent = server.getVisualComponent();
					if(visualComponent == null) {
						awtFrame.pack();
						return;
					}							
					visualComponent.setSize((Dimension)newDimensionForAll.clone());
					layeredPane.add(visualComponent,new Integer(0));
					visualComponent.setLocation(0, 0);
					overlay.setSize((Dimension)newDimensionForAll.clone());
					overlay.updateImages();
					awtRoot.doLayout();
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}	
	}

	public void setFocus() {
	}
	
	public void setWorldOrigin(int originX, int originY) {
		worldOriginX = originX / scaleX;
		worldOriginY = originY / scaleY;
		overlay.updateImages();
	}
	
	public void setWorldTranslation(int pixelX, int pixelY, double isX, double isY) {
		pixelsPerUnitX = ((pixelX/scaleX - worldOriginX) / isX);
		pixelsPerUnitY = ((pixelY/scaleY - worldOriginY) / isY);
		overlay.updateImages();
	}

	public int getScreenX(Position worldPosition) {
		return getScreenX(worldPosition.getX());
	}

	public int getScreenX(double worldX) {
		return (int)Math.round((worldX*pixelsPerUnitX + worldOriginX) * scaleX);
	}

	public int getScreenY(Position worldPosition) {
		return getScreenY(worldPosition.getY());
	}

	public int getScreenY(double worldY) {
		return (int)Math.round((worldY*pixelsPerUnitY + worldOriginY) * scaleY);
	}

	public Position getWorldPosition(int x, int y) {
		return new Position(((x/scaleX)-worldOriginX)/pixelsPerUnitX,((y/scaleY)-worldOriginY)/pixelsPerUnitY);
	}

	public Rectangle getObjectBounds(WorldObject arg0) {
		return null;
	}

	public Point getSizeRenderedWorld() {
		if(server == null) {
			return new Point(0,0);
		}
		
		return new Point((int)Math.floor(server.getVisualComponent().getWidth()*scaleX*pixelsPerUnitX),(int)Math.floor(server.getVisualComponent().getHeight()*scaleY*pixelsPerUnitY));
	}

	public Position getWorldHighestCoords() {
		if(server == null) {
			return new Position(0,0);
		}
		
		return new Position(server.getVisualComponent().getWidth()*scaleX,server.getVisualComponent().getHeight()*scaleY);
	}

	public Position getWorldLowestCoords() {
		return new Position(worldOriginX*scaleX,worldOriginY*scaleY);
	}

	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void beginCalibration() {
		mode = MODE_SELECT_ORIGIN;
		MessageDialog dlg = new MessageDialog(topLevel.getShell(),"Select origin",null,"Please click the new coordinate origin",MessageDialog.QUESTION,new String[] {"OK"},0);
		dlg.open();
		layeredPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	protected void calibrateOrigin(int x, int y) {
		mode = MODE_SELECT_POINT;
		setWorldOrigin(x,y);
		
		Display.findDisplay(swtThread).syncExec(new Runnable() {
			public void run() {
				MessageDialog dlg = new MessageDialog(topLevel.getShell(),"Select a point",null,"Please click a point where you know the coordinates",MessageDialog.QUESTION,new String[] {"OK"},0);
				dlg.open();
			}
		});
		
		layeredPane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	protected void calibrateOtherPoint(final int x, final int y) {
		mode = MODE_SELECT_NORMAL;
		
		final IInputValidator validator = new IInputValidator() {
			public String isValid(String newText) {
				if(newText.indexOf('/') < 0) return "'/' missing. Format is x/y.";
				try {
					String value = newText;
					Double.parseDouble(value.substring(0,value.indexOf('/')));
					Double.parseDouble(value.substring(value.indexOf('/')+1));					
				} catch (Exception e) {
					return "Invalid number(s).";
				}
				return null;
			}
			
		};
		
		Display.findDisplay(swtThread).syncExec(new Runnable() {
			public void run() {
				InputDialog dlg = new InputDialog(
						topLevel.getShell(),
						"Create coordinate translation",
						"Please enter the coordinates for the point you clicked in the format x/y",
						x+"/"+y,
						validator);
					
				dlg.open();
				if(dlg.getReturnCode() != InputDialog.CANCEL) {
					String value = dlg.getValue();
					double tx = Double.parseDouble(value.substring(0,value.indexOf('/')));
					double ty = Double.parseDouble(value.substring(value.indexOf('/')+1));
					setWorldTranslation(x,y,tx,ty);			
				}
			}
		});
				
		layeredPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	public void setScaling(double d) {
		scaleX = d;
		scaleY = d;
		setServer(server.getName());
	}
	
}