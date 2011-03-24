package org.micropsi.eclipse.mindconsole.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.groups.SpaceGroup;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.NetParametersIF;
import org.micropsi.nodenet.NodeSpaceModule;

/**
 * 
 * 
 * 
 */
public class SpaceDialog extends Dialog {
	
	private NetModel netmodel;
	private NodeSpaceModule space;
	private SpaceGroup spaceGroup;
	
	public SpaceDialog(Shell shell, NetModel netmodel, String spaceID) throws MicropsiException {
		super(shell);
		this.netmodel = netmodel;
		this.space = (NodeSpaceModule)netmodel.getModel(spaceID).getEntity();
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());

		spaceGroup = new SpaceGroup(topLevel,netmodel.getNet(),space);
				
		return topLevel;
	}
	
	protected void okPressed() {
		
		try {
			if(spaceGroup.isAssoChanged())
			netmodel.getNet().changeParameter(
				NetParametersIF.PARM_NODESPACE_ASSO,
				space.getID(),
				0,
				Double.toString(spaceGroup.getAssoConst())
			);

			if(spaceGroup.isDissoChanged())
			netmodel.getNet().changeParameter(
				NetParametersIF.PARM_NODESPACE_DISSO,
				space.getID(),
				0,
				Double.toString(spaceGroup.getDissoConst())
			);

			if(spaceGroup.isStrengtheningChanged())
			netmodel.getNet().changeParameter(
				NetParametersIF.PARM_NODESPACE_STRENGTHENING,
				space.getID(),
				0,
				Double.toString(spaceGroup.getStrengtheningConst())
			);

			if(spaceGroup.isDecayAllowedChanged())
				netmodel.getNet().changeParameter(
					NetParametersIF.PARM_NODESPACE_DECAYALLOWED,
					space.getID(),
					0,
					Boolean.toString(spaceGroup.getDecayAllowed())
				);

		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);	
		}
			
		super.okPressed();	
	}

}
