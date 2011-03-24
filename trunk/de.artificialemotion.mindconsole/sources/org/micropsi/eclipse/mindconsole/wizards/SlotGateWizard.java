package org.micropsi.eclipse.mindconsole.wizards;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.groups.GateGroup;
import org.micropsi.eclipse.mindconsole.groups.SlotGroup;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NetParametersIF;
import org.micropsi.nodenet.NodeSpaceModule;

import java.util.Iterator;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * 
 * 
 */
public class SlotGateWizard extends Wizard {

	class SlotGatePage extends WizardPage {
		protected GateGroup gg;
		protected SlotGroup sg;

		private NodeSpaceModule space;

		public SlotGatePage(NodeSpaceModule space) {
			super("modslotsgates");
			this.space = space;
			setTitle("NodeSpace");
			setDescription("Modify Slots and Gates");
		}

		public void createControl(Composite parent) {

			// top level group
			Composite topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(new GridLayout());
			topLevel.setFont(parent.getFont());

			sg = new SlotGroup(
				topLevel,
				new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL),
				space,
				null,
				true
			);

			gg = new GateGroup(
				topLevel,
				new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL),
				space,
				null,
				true
			);
	
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);
		}
	}

	private NetFacadeIF net;
	private String parentID;
	private NodeSpaceModule space;

	private SlotGatePage slotGatePage;

	public SlotGateWizard(NetFacadeIF net, NodeSpaceModule space) {
		super();
		this.net = net;
		this.space = space;
		setHelpAvailable(false);
		setWindowTitle("Slots and Gates");
		slotGatePage = new SlotGatePage(space);
		addPage(slotGatePage);
	}

	public SlotGateWizard(NetFacadeIF net, String parentID) {
		super();
		this.net = net;
		this.parentID = parentID;
		setHelpAvailable(false);
		setWindowTitle("Create NodeSpace / Slots and Gates");
		slotGatePage = new SlotGatePage(space);
		addPage(slotGatePage);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {

		try {
			
			// if space is null, we've been called insid the NewNetEntityWizard
			if(space == null) {
				space = net.createNodeSpace(parentID);
				
				String entityName = ((NewNetEntityWizard.TypeSelectPage)slotGatePage.getPreviousPage())
					.entityName.getText();
				if(!entityName.equals(""))
					net.changeParameter(NetParametersIF.PARM_ENTITY_NAME, space.getID(), 0, entityName);
			}
			
			Iterator iter = slotGatePage.gg.getNewGates();
			while(iter.hasNext()) {
				Integer type = (Integer)iter.next();
				net.createNodeSpaceGate(space.getID(), type.intValue());
			}
			
			iter = slotGatePage.sg.getNewSlots();
			while(iter.hasNext()) {
				Integer type = (Integer)iter.next();
				net.createNodeSpaceSlot(space.getID(), type.intValue());
			}

			iter = slotGatePage.gg.getDeletedGates();
			while(iter.hasNext()) {
				Integer type = (Integer)iter.next();
				net.deleteNodeSpaceGate(space.getID(), type.intValue());
			}

			iter = slotGatePage.sg.getDeletedSlots();
			while(iter.hasNext()) {
				Integer type = (Integer)iter.next();
				net.deleteNodeSpaceSlot(space.getID(), type.intValue());
			}

		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
			return false;
		}

		return true;
	}


}
