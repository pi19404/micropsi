package org.micropsi.eclipse.mindconsole.wizards;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.groups.IGateCallback;
import org.micropsi.eclipse.mindconsole.groups.GateGroup;
import org.micropsi.eclipse.mindconsole.groups.NetHierarchyGroup;
import org.micropsi.eclipse.mindconsole.groups.ISlotCallback;
import org.micropsi.eclipse.mindconsole.groups.SlotGroup;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.SlotTypesIF;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * 
 * 
 */
public class CreateLinkWizard extends Wizard {

	class SelectOriginPage extends WizardPage {		
		protected NetHierarchyGroup netGroup;
		
		private NetModel netmodel;
		private String preSelectedEntity;
		
		public SelectOriginPage(NetModel netmodel, String preSelectedEntity) {
			super("selectorigin");
			this.netmodel = netmodel;
			this.preSelectedEntity = preSelectedEntity;

			setTitle("Origin");
			setDescription("Select origin");
		}
		
		public void createControl(Composite parent) {
			
			// top level group
			Composite topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(new GridLayout());
			topLevel.setFont(parent.getFont());
			
			netGroup = new NetHierarchyGroup(topLevel,netmodel.getNet(),null);
			
			final GateGroup gg = new GateGroup(topLevel,null,null,new IGateCallback() {
				public void selectedGate(NetEntity entity, int type) {
					origin = entity;
					originGate = type;
					getContainer().updateButtons();
				}
				public void changedSomething(GateGroup group) {
					// won't happen
				}
			},false);
			gg.setButtonsEnabled(false);
			
			if(preSelectedEntity != null) {
				netGroup.select(preSelectedEntity);
				gg.setEntity(netGroup.getSelected());
				originGate = -1;
			} 
			
			netGroup.addSelectionListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					gg.setEntity(netGroup.getSelected());
					originGate = -1;
					getContainer().updateButtons();
				}
			});
			
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);
		}
	}

	class SelectTargetPage extends WizardPage {		
		protected NetHierarchyGroup netGroup;
		
		private NetModel netmodel;
		
		public SelectTargetPage(NetModel netmodel) {
			super("selecttarget");
			this.netmodel = netmodel;

			setTitle("Target");
			setDescription("Select target");
		}
		
		public void createControl(Composite parent) {
			
			// top level group
			Composite topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(new GridLayout());
			topLevel.setFont(parent.getFont());

			netGroup = new NetHierarchyGroup(topLevel,netmodel.getNet(),null);
			
			final SlotGroup sg = new SlotGroup(topLevel,null,null,new ISlotCallback() {
				public void selectedSlot(NetEntity entity, int type) {
					target = entity;
					targetSlot = type;
					getContainer().updateButtons();
				}
				public void changedSomething(SlotGroup group) {
					// won't happen
				}
			},false);
			sg.setButtonsEnabled(false);
			
			netGroup.addSelectionListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					sg.setEntity(netGroup.getSelected());
					targetSlot = -1;
					getContainer().updateButtons();
				}
			});
			
			final Button st = new Button(topLevel,SWT.CHECK);
			st.setText("Create link with spacio-temporal attributes (only with POR/RET)");
			st.setSelection(false);	
			st.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					if(	originGate != GateTypesIF.GT_POR &&
						originGate != GateTypesIF.GT_RET) {
						st.setSelection(false);
					}
					
					stlink = st.getSelection();
				}
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});	


			final Button rec = new Button(topLevel,SWT.CHECK);
			rec.setText("Create also reciprocal link (only between concept nodes)");
			rec.setSelection(false);
			rec.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					
					if(origin == null || target == null) {
						rec.setSelection(false);
						reclink = rec.getSelection();
					}
					 
					if(origin.getEntityType() != NetEntityTypesIF.ET_NODE)
						rec.setSelection(false);
					
					Node n = (Node)origin;	
					if(n.getType() != NodeFunctionalTypesIF.NT_CONCEPT && n.getType() != NodeFunctionalTypesIF.NT_TOPO)
						rec.setSelection(false);

					if(target.getEntityType() != NetEntityTypesIF.ET_NODE)
						rec.setSelection(false);
					
					n = (Node)target;	
					if(n.getType() != NodeFunctionalTypesIF.NT_CONCEPT && n.getType() != NodeFunctionalTypesIF.NT_TOPO)
						rec.setSelection(false);

					if(originGate == GateTypesIF.GT_GEN) rec.setSelection(false);
				
					reclink = rec.getSelection();
				}
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});	

			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);
		}
	}
	
	private NetModel netmodel;
	
	private SelectOriginPage selectOriginPage;
	private SelectTargetPage selectTargetPage;
	
	private NetEntity origin;
	private int originGate = -1;
	
	private NetEntity target;
	private int targetSlot = -1;
		
	private boolean stlink = false;
	private boolean reclink = false;
		
	public CreateLinkWizard(NetModel netmodel, String preSelectedWidget) {
		super();
		this.netmodel = netmodel;
		setHelpAvailable(false);
		setWindowTitle("Create Link");
		selectOriginPage = new SelectOriginPage(netmodel,preSelectedWidget);
		addPage(selectOriginPage);
		selectTargetPage = new SelectTargetPage(netmodel);
		addPage(selectTargetPage);
	}
	
	public boolean canFinish() {
		if(origin == null || target == null) return false;
		if(origin.getGate(originGate) == null) return false;
		if(target.getSlot(targetSlot) == null) return false;
		return true;
	}
	
	public boolean performFinish() {
		
		try {
			netmodel.getNet().createLink(
				origin.getID(),
				originGate,
				target.getID(),
				targetSlot,
				1.0,
				1.0,
				stlink
			);
			
			if(reclink) {
				int rectype = -1;
				switch(originGate) {
					case GateTypesIF.GT_POR:
						rectype = GateTypesIF.GT_RET; break;
					case GateTypesIF.GT_RET:
						rectype = GateTypesIF.GT_POR; break;
					case GateTypesIF.GT_SUB:
						rectype = GateTypesIF.GT_SUR; break;
					case GateTypesIF.GT_SUR:
						rectype = GateTypesIF.GT_SUB; break;
					case GateTypesIF.GT_CAT:
						rectype = GateTypesIF.GT_EXP; break;
					case GateTypesIF.GT_EXP:
						rectype = GateTypesIF.GT_CAT; break;
					case GateTypesIF.GT_SYM:
						rectype = GateTypesIF.GT_REF; break;
					case GateTypesIF.GT_REF:
						rectype = GateTypesIF.GT_SYM; break;

				}
				netmodel.getNet().createLink(
					target.getID(),
					rectype,
					origin.getID(),
					SlotTypesIF.ST_GEN,
					1.0,
					1.0,
					stlink
				);				
			}
			
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
			return false;
		}
		
		return true;
	}

}
