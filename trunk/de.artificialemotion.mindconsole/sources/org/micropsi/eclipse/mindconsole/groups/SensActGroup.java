package org.micropsi.eclipse.mindconsole.groups;

import org.micropsi.nodenet.ActorNode;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.SensorNode;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

/**
 * 
 * 
 * 
 */
public class SensActGroup {
	
	private Button connect;
	private List list;	
	private boolean isSensor;
	
	public SensActGroup(Composite parent, NetFacadeIF net, Node node) {
		
		isSensor = (node.getType() == NodeFunctionalTypesIF.NT_SENSOR);
		createControls(parent);
		connect.setSelection(isSensor ? ((SensorNode)node).isConnected() : ((ActorNode)node).isConnected());	
		Iterator iter = (isSensor ? net.getAvailableDataSources() : net.getAvailableDataTargets());
		
		while(iter.hasNext()) {
			String next = (String)iter.next();
			if(next == null) continue;
			list.add(next);
			String currentconnection = (isSensor) ? ((SensorNode)node).getDataType() : ((ActorNode)node).getDataType();
			if(currentconnection != null)	
				if(currentconnection.equals(next)) list.select(list.getItemCount()-1);
		}
	}
	
	public SensActGroup(Composite parent, NetFacadeIF net, int type) {
		isSensor = (type == NodeFunctionalTypesIF.NT_SENSOR);
		createControls(parent);
		connect.setSelection(false);

		Iterator iter = (isSensor ? net.getAvailableDataSources() : net.getAvailableDataTargets());
		while(iter.hasNext()) {
			String next = (String)iter.next();
			list.add(next);
		}

		list.setEnabled(false);
		
	}
	
	private void createControls(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());

		connect = new Button(topLevel,SWT.CHECK);
		connect.setText("Connect");
		connect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				list.setEnabled(connect.getSelection());
			}
		});

		list = new List(topLevel,SWT.SINGLE | SWT.V_SCROLL);
		list.setEnabled(connect.getSelection());
		list.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
	}
		
	public boolean isConnected() {
		return connect.getSelection();
	}
	
	public String getConnection() {
		if(!isConnected()) return null;
		if(list.getSelectionIndex() < 0) return null;
		return list.getItem(list.getSelectionIndex());
	}
}
