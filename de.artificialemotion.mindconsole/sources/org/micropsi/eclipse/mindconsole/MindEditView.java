package org.micropsi.eclipse.mindconsole;


import java.text.NumberFormat;
import java.util.List;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.console.controller.IViewControllerListener;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.mindconsole.pref.INetEditorPrefKeys;
import org.micropsi.eclipse.mindconsole.widgets.AbstractStyledEntityLook;
import org.micropsi.eclipse.mindconsole.widgets.EntityTransferData;
import org.micropsi.eclipse.mindconsole.widgets.NodeSpaceWidget;
import org.micropsi.eclipse.model.net.AgentNetModel;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.nodenet.NetCycleIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NodeSpaceModule;

/**
 * @author daniel
 *
 */
public class MindEditView extends ViewPart implements IViewControllerListener {
	
	private AgentNetModel netmodel;
	private String currentNodeSpaceID;
	private String previousNodeSpaceID;
	private String currentTitle;
  
	private NodeSpaceWidget nodeEditor;
  
	private ScrolledComposite scroller;
	private IStatusLineManager statusLineManager;
	  
	private Thread uiThread;
	private Composite topLevel;
	
	private double timesum = 0;
	private long steps;
	private NumberFormat format = NumberFormat.getInstance();
	
	boolean watch = true;  
    
	/**
	 * Constructor for BrainEditView.
	 */
	public MindEditView() {
    	super();
		uiThread = Thread.currentThread();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);		
		
		if(memento != null) { 
			IMemento m = memento.getChild("agentmanager");
			if(m != null)
				AgentManager.getInstance().loadState(m);
		}

		MindEditController.getInstance().registerView(this);
		netmodel = AgentNetModelManager.getInstance().getNetModel();
					
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(final Composite parent){
       
		topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 7;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());
  	
		scroller = new ScrolledComposite(topLevel, SWT.H_SCROLL | SWT.V_SCROLL);
		GridData griddata = new GridData(GridData.FILL_BOTH);
		griddata.horizontalSpan = 7;
		scroller.setLayoutData(griddata); 

    	statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();    
    
		try {
			if(netmodel == null || netmodel.getNet() == null) {
				openNodeSpace(null);
			} else {
				if(netmodel.getNet().getRootNodeSpaceModule() == null)
					netmodel.getNet().createNodeSpace(null);
				String initialNodeSpaceID = netmodel.getNet().getRootNodeSpaceModule().getID();
				openNodeSpace(initialNodeSpaceID);				
			}
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}		
	}
	
	public void setZoom(int zoom) {
		nodeEditor.setViewScale(zoom);
	}
	

	private void openNodeSpace(String id) {
  	 	
		timesum = 0;
		steps = 0;
		
  		if(id == null) {
 			currentTitle = "     NodeSpace: none    Agent: "+AgentManager.getInstance().getCurrentAgent()+" From state: none";
			setPartName(currentTitle);
  			
			try {
				if(nodeEditor != null && currentNodeSpaceID != null) {
					nodeEditor.destroy();
					netmodel.getNet().unregisterSpaceObserver(currentNodeSpaceID, nodeEditor);
				}
			} catch (MicropsiException e) {
				MindPlugin.getDefault().handleException(e);
			}
			scroller.setContent(null);
			scroller.setEnabled(false);
			currentNodeSpaceID = null;
			return;			
  		}
  	
  		if(id.equals(currentNodeSpaceID)) return;
  	
		try {

			int oldLinkDrawMode = -1;
			if(nodeEditor != null && currentNodeSpaceID != null) {
				oldLinkDrawMode = nodeEditor.getLinkDrawMode();
				netmodel.getNet().unregisterSpaceObserver(currentNodeSpaceID, nodeEditor);
				nodeEditor.destroy();
			}
			
			NetEntity entity = netmodel.getModel(id).getEntity(); 
			currentTitle = 
				"     NodeSpace: "+
				(entity.hasName() ? entity.getEntityName()+"   " : "")+
				entity.getID()+
				"    Agent: "+AgentManager.getInstance().getCurrentAgent()+
				"    From state: "+netmodel.getLastLoadedState();
			setPartName(currentTitle);
   		
		
			currentNodeSpaceID = id;
  		
  			IPreferenceStore prefs = PlatformUI.getPreferenceStore();
  			  			
  			prefs.setDefault(INetEditorPrefKeys.SCALE, 100);
			int initialscale = prefs.getInt(INetEditorPrefKeys.SCALE);
			prefs.setDefault(INetEditorPrefKeys.FONT,scroller.getFont().toString());
			Font entityFont = getSite().getShell().getFont();
			try {
				entityFont = new Font(Display.getCurrent(),new FontData(prefs.getString(INetEditorPrefKeys.FONT)));
			} catch (Exception e) {
				// deliberately empty
			}
			prefs.setDefault(INetEditorPrefKeys.SHOWPLUSVALUE, false);
			boolean showPlusValue = prefs.getBoolean(INetEditorPrefKeys.SHOWPLUSVALUE);
			prefs.setDefault(INetEditorPrefKeys.SHOWCOMPACTNODES, true);
			boolean showCompactNodes = prefs.getBoolean(INetEditorPrefKeys.SHOWCOMPACTNODES);
			prefs.setDefault(INetEditorPrefKeys.SHOWARROWHEADS, true);
			boolean showArrowheads = prefs.getBoolean(INetEditorPrefKeys.SHOWARROWHEADS);
			prefs.setDefault(INetEditorPrefKeys.SHOWANNOTATIONS, true);
			boolean showAnnotations = prefs.getBoolean(INetEditorPrefKeys.SHOWANNOTATIONS);
			prefs.setDefault(INetEditorPrefKeys.SHOWENTITYACTIVATION, true);
			boolean showNodeActivation = prefs.getBoolean(INetEditorPrefKeys.SHOWENTITYACTIVATION);
			prefs.setDefault(INetEditorPrefKeys.SHOWLINKACTIVATION, true);
			boolean showLinkActivation = prefs.getBoolean(INetEditorPrefKeys.SHOWLINKACTIVATION);
			
			//@todo Joscha: make font configurable
			nodeEditor = new NodeSpaceWidget(
				scroller, 
				SWT.NONE, 
				  (showPlusValue ? AbstractStyledEntityLook.SHOW_PLUS_WHEN_NEEDED :0)
				| (showCompactNodes ? AbstractStyledEntityLook.SHOW_COMPACT_NODES :0)
				| (showArrowheads ? AbstractStyledEntityLook.SHOW_ARROWHEADS :0)
				| (showAnnotations ? AbstractStyledEntityLook.SHOW_ANNOTATIONS_WHEN_NEEDED :0)
				| (showNodeActivation ? AbstractStyledEntityLook.SHOW_ACTIVATION_IN_NODES :0)
				| (showLinkActivation ? AbstractStyledEntityLook.SHOW_ACTIVATION_ON_LINKS :0),
				entityFont,
				AgentNetModelManager.getInstance().getNetModel(),
				currentNodeSpaceID, 
				new MindEditCallback(this.getSite().getShell(),ModuleJavaManager.getInstance()),
				initialscale
			);
			if(oldLinkDrawMode != -1) nodeEditor.setLinkDrawMode(oldLinkDrawMode);

			scroller.setContent(nodeEditor);
			scroller.setEnabled(true);
			
			netmodel.getNet().registerSpaceObserver(currentNodeSpaceID, nodeEditor);

			
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}
	}
  
	public void lookUpNode(final String id) {
		
		Display.findDisplay(uiThread).asyncExec(new Runnable() {
			public void run() {
				
				String parent;
				try {
					parent = netmodel.getModel(id).getEntity().getParentID();
					openNodeSpace(parent);
					nodeEditor.deselectEverything();
					nodeEditor.select(id);
 		 		
					Point p = nodeEditor.getSelectedItemPosition();
 		
					p.x = p.x - 50;
					p.y = p.y - 50;
 		 		
					if(p.x < 0) p.x = 0;
					if(p.y < 0) p.y = 0;
 		
					scroller.setOrigin(p);
				} catch (MicropsiException e) {
					MindPlugin.getDefault().handleException(e);
				}
			}
		});							
	}

	public void bringToFront(String id) {
		nodeEditor.bringToFront(id);
	}
  
	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		try{
			nodeEditor.setFocus();
		} catch (Exception e){
		}
  	}
  
	public String getCurrentNodeSpaceID() {
		return currentNodeSpaceID;
	} 

	/**
	 * Returns the statusLineManager.
	 * @return StatusLineManager
	 */
	public IStatusLineManager getStatusLineManager() {
    	return statusLineManager;
	}

	/**
	 * @see org.micropsi.comp.console.controller.ViewControllerListenerIF#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {
		
		if(o == null) {
			netmodel = null;
			currentNodeSpaceID = null;
			
			Display.findDisplay(uiThread).syncExec(new Runnable() {
				public void run() {
					openNodeSpace(null);
				}
			});			
			return;		
		}
		
		netmodel = (AgentNetModel)o;
		currentNodeSpaceID = null;
						
		try {
			if(netmodel.getNet().getRootNodeSpaceModule() == null)
				netmodel.getNet().createNodeSpace(null);
		  	
			final String initialNodeSpaceID = netmodel.getNet().getRootNodeSpaceModule().getID();
			
			Display.findDisplay(uiThread).syncExec(new Runnable() {
				public void run() {
					if(watch) {
						openNodeSpace(initialNodeSpaceID);
					} else {
						previousNodeSpaceID = initialNodeSpaceID;
					}
						
				}
			});					
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}

	}

	/**
	 * @see org.micropsi.comp.console.controller.ViewControllerListenerIF#setData(Object)
	 */
	public void setData(final Object o) {
		Display.findDisplay(uiThread).asyncExec(new Runnable() {
			public void run() {
				openNodeSpace((String)o);
				previousNodeSpaceID = (String)o;
			}
		});					
	}

	/**
	 * @param netstep
	 */
	public void setNetStep(long netstep) {
		updatePartName();
	}
		
	private void updatePartName() {
		Display.findDisplay(uiThread).syncExec(new Runnable() {
			public void run() {
				
				steps++;
				long lastLength = netmodel.getNet().getCycle().getLastCycleLength();
				timesum += lastLength;
				
				StringBuffer buf = new StringBuffer(Long.toString(netmodel.getNet().getNetstep()));
				try {
					buf.append(currentTitle);
					buf.append("     [");
					buf.append(format.format(timesum/steps));
					buf.append(" ms/cycle  ");
					buf.append(netmodel.getNet().getRootNodeSpaceModule().getNumberOfEntities()+1);
					buf.append(" entities]");
				} catch (Exception e) {
					MindPlugin.getDefault().handleException(e);
				}
				
				setPartName(buf.toString());
			}
		});		
	}
	
	public boolean toggleUpdate() {
		if(!watch) {
			watch = true;
			openNodeSpace(previousNodeSpaceID);
			previousNodeSpaceID = currentNodeSpaceID;
		} else {
			watch = false;
			previousNodeSpaceID = currentNodeSpaceID;			
			openNodeSpace(null);
		}
		return watch;
	}
	
	public void saveState(IMemento memento) {
		IMemento m = memento.createChild("agentmanager");
		AgentManager.getInstance().saveState(m);
		memento.putMemento(m);
	}

	public void triggerRedraw() {
		Display.findDisplay(uiThread).asyncExec(new Runnable() {
			public void run() {
				nodeEditor.redraw();
			}
		});			
	}

	public void openParentSpace() {
		try {
			if(currentNodeSpaceID == null) return;
			
			NetCycleIF cycle = netmodel.getNet().getCycle(); 
			
			if(!cycle.isSuspended()) {
				cycle.suspend();
			}

			
			NodeSpaceModule m = netmodel.getNet().getNodeSpaceModule(currentNodeSpaceID);
			if(!m.isRoot())
				openNodeSpace(m.getParent().getID());
		} catch (Exception exc) {
			MindPlugin.getDefault().handleException(exc);
		}
	}

	public void dropItems(String where, List<EntityTransferData> items) {
		openNodeSpace(where);
		nodeEditor.dropItems(items, 30, 30);
	}
}
