package org.micropsi.eclipse.mindconsole;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.console.controller.IViewControllerListener;
import org.micropsi.eclipse.mindconsole.groups.IGateCallback;
import org.micropsi.eclipse.mindconsole.groups.GateGroup;
import org.micropsi.eclipse.mindconsole.groups.ISlotCallback;
import org.micropsi.eclipse.mindconsole.groups.SlotGroup;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetParametersIF;



import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * 
 * 
 */
public class EntityEditView extends ViewPart implements IViewControllerListener {

	private static final String NAMEDEFAULT = "";

	private NetModel netmodel;
	private EntityModel entityModel;

	private IStatusLineManager statusLineManager;
	
	
	private Thread uiThread;
	private Text entityName;
	private GateGroup gg;
	private SlotGroup sg;

 	public EntityEditView() {
		super();
		netmodel = AgentNetModelManager.getInstance().getNetModel();
		EntityEditController.getInstance().registerView(this);
		uiThread = Thread.currentThread();
  	}

	public void createPartControl(Composite parent) {
				
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		topLevel.setLayout(gridLayout);
		topLevel.setFont(parent.getFont());
			
		Label label = new Label(topLevel,SWT.NONE);
		label.setText("Name");
		label.setBackground(topLevel.getBackground());
				
		entityName = new Text(topLevel,SWT.SINGLE | SWT.BORDER);
		entityName.setText(NAMEDEFAULT);
		entityName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		entityName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				try {
					netmodel.getNet().changeParameter(
						NetParametersIF.PARM_ENTITY_NAME,
						entityModel.getEntity().getID(),
						0,
						entityName.getText());
				} catch (MicropsiException e) {
					MindPlugin.getDefault().handleException(e);
				}
			}
		});
		
/*		label = new Label(topLevel,SWT.NONE);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setText("Comment");
		entityComment = new Text(topLevel,SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		entityComment.setText(DESCDEFAULT);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 60;
		data.horizontalSpan = 2;
		entityComment.setLayoutData(data);
		
		entityComment.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
					entityModel.setComment(entityComment.getText());
			}
		});*/
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 2;
		gg = new GateGroup(topLevel,data,null,new IGateCallback() {
			public void selectedGate(NetEntity entity, int type) {
				LinkageEditController.getInstance().setData(entity.getGate(type));
				IViewPart sepp = getSite().getPage().findView("org.micropsi.eclipse.mindconsole.linkageeditview");
				getSite().getPage().activate(sepp);
			}
			public void changedSomething(GateGroup group) {
			}		
		},false);
		
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.horizontalSpan = 2;
		sg = new SlotGroup(topLevel,data,null,new ISlotCallback() {
			public void selectedSlot(NetEntity entity, int type) {
				IncomingLinksController.getInstance().setData(entity.getSlot(type));
				IViewPart sepp = getSite().getPage().findView("org.micropsi.eclipse.mindconsole.incominglinksview");
				getSite().getPage().activate(sepp);
			}
			public void changedSomething(SlotGroup group) {
			}		
		},false); 
		
		entityName.setEnabled(false);				
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}

	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}
	

	/**
	 * @see org.micropsi.comp.console.controller.ViewControllerListenerIF#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {
		netmodel = (NetModel)o;
		setData(null);
	}

	/** 
	 * @see org.micropsi.comp.console.controller.ViewControllerListenerIF#setData(java.lang.Object)
	 */
	public void setData(Object o) {
		if(o != null) {
			try {
				this.entityModel = netmodel.getModel((String)o);
				this.setPartName(entityModel.getEntity().getID());
				entityName.setText(entityModel.getEntity().getEntityName());
				entityName.setEnabled(true);
				gg.setEntity(entityModel.getEntity());
				sg.setEntity(entityModel.getEntity());
				
				if(entityModel.getEntity().getGates().hasNext()) {
					gg.selectGate(entityModel.getEntity().getGate(entityModel.getEntity().getGates().next().getType()));
				} else {
					LinkageEditController.getInstance().setData(null);
				}

			} catch (MicropsiException e) {
				MindPlugin.getDefault().handleException(e);
			}
		} else {			
			// the call with null comes from a non-ui thread
			Display.findDisplay(uiThread).asyncExec(new Runnable() {
				public void run() {
					entityName.setEnabled(false);
					gg.setEntity(null);
				}
			});
			
			LinkageEditController.getInstance().setData(null);
		}
		
	}

	public void selectGate(Gate gate) {
		gg.selectGate(gate);
	}

}
