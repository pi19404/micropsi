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
import org.micropsi.eclipse.mindconsole.groups.LinkGroup;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LinkST;
import org.micropsi.nodenet.NetFacadeIF;

/**
 * 
 * 
 * 
 */
public class LinkDialog extends Dialog {
	
	private LinkGroup linkGroup;
	private NetFacadeIF net;
	private Link link;
	
	public LinkDialog(Shell shell, NetModel netmodel, Link link) {
		super(shell);
		this.net = netmodel.getNet();
		this.link = link;		
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());

		linkGroup = new LinkGroup(topLevel,net,link);
				
		return topLevel;
	}
	
	protected void okPressed() {
		
		try {
			
			if(linkGroup.isWeightChanged())
			net.changeLinkParameter(
				Link.LINKPARAM_WEIGHT,
				link.getLinkingGate().getNetEntity().getID(),
				link.getLinkingGate().getType(),
				link.getLinkedEntityID(),
				link.getLinkedSlot().getType(),
				linkGroup.getWeight());

			if(linkGroup.isConfidenceChanged())
			net.changeLinkParameter(
				Link.LINKPARAM_CONFIDENCE,
				link.getLinkingGate().getNetEntity().getID(),
				link.getLinkingGate().getType(),
				link.getLinkedEntityID(),
				link.getLinkedSlot().getType(),
				linkGroup.getConfidence());

			if(linkGroup.isTChanged())
			net.changeLinkParameter(
				LinkST.LINKPARAM_T,
				link.getLinkingGate().getNetEntity().getID(),
				link.getLinkingGate().getType(),
				link.getLinkedEntityID(),
				link.getLinkedSlot().getType(),
				linkGroup.getT());

			if(linkGroup.isXChanged())
			net.changeLinkParameter(
				LinkST.LINKPARAM_X,
				link.getLinkingGate().getNetEntity().getID(),
				link.getLinkingGate().getType(),
				link.getLinkedEntityID(),
				link.getLinkedSlot().getType(),
				linkGroup.getX());

			if(linkGroup.isYChanged())
			net.changeLinkParameter(
				LinkST.LINKPARAM_Y,
				link.getLinkingGate().getNetEntity().getID(),
				link.getLinkingGate().getType(),
				link.getLinkedEntityID(),
				link.getLinkedSlot().getType(),
				linkGroup.getY());

			if(linkGroup.isZChanged())
			net.changeLinkParameter(
				LinkST.LINKPARAM_Z,
				link.getLinkingGate().getNetEntity().getID(),
				link.getLinkingGate().getType(),
				link.getLinkedEntityID(),
				link.getLinkedSlot().getType(),
				linkGroup.getZ());

		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);	
		}
			
		super.okPressed();	
	}

}
