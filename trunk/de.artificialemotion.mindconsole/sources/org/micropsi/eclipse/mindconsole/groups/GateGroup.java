package org.micropsi.eclipse.mindconsole.groups;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.micropsi.eclipse.mindconsole.GateMonitorProvider;
import org.micropsi.eclipse.mindconsole.monitors.GateMonitor;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;

/**
 * 
 * 
 * 
 */
public class GateGroup {
	
	class SlotGateValidator implements IInputValidator {
		
		private ArrayList<Integer> existing;
		
		public SlotGateValidator(ArrayList<Integer> existing) {
			this.existing = existing;
		}
		
		public String isValid(String newText) {
			String ret;
			try {
				int toTest = Integer.parseInt(newText); 
				ret = (toTest >= 0) ? null : "Type can't be negative";
					
				for(int i=0;i<existing.size();i++) {
					Integer tmp = existing.get(i);
					if(tmp.intValue() == toTest) ret = "Type already in use"; 
				}	
				
			} catch(NumberFormatException e){
				ret = "Type must be an integer";
			}
			return ret;
		}
	}
	
	private List gateList;
	
	private ArrayList<Integer> newGates = new ArrayList<Integer>();
	private ArrayList<Integer> delGates = new ArrayList<Integer>();
	
	private ArrayList<Integer> exiGates = new ArrayList<Integer>();
	
	private ToolItem createGateButton;
	private ToolItem deleteGateButton;

	private IGateCallback callback;
	private NetEntity entity;
	
	private boolean buttons = false;

	public GateGroup(Composite parent, GridData gridData, NetEntity entity, IGateCallback callback, boolean buttons) {
		
		this.callback = callback;
		this.entity = entity;
		
		this.buttons = buttons;
	
		createControls(parent, gridData);
		setButtonsEnabled(false);
		
		if(entity == null) {
			setButtonsEnabled(false);
		} else {
			if(entity.getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE)
				setButtonsEnabled(true);
		}		
		
		if(entity != null) {
			Iterator iter = entity.getGates();
			while(iter.hasNext())
				exiGates.add(new Integer(((Gate)iter.next()).getType()));
		}
		refreshLists();
	}
	
	public void setEntity(NetEntity entity) {
		this.entity = entity;
		
		setButtonsEnabled(false);
		
		newGates = new ArrayList<Integer>();
		delGates = new ArrayList<Integer>();

		exiGates = new ArrayList<Integer>();

		if(	entity != null &&
			entity.getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE)
			setButtonsEnabled(true);


		if(entity != null) {
			Iterator iter = entity.getGates();
			while(iter.hasNext())
				exiGates.add(new Integer(((Gate)iter.next()).getType()));
		}
		refreshLists();
	}
	
	private void refreshLists() {
		gateList.removeAll();
		
		for(int i=0;i<exiGates.size();i++)
			gateList.add(TypeStrings.gateType( exiGates.get(i).intValue()  ));
	
	}
	
	private int getWrapperIndex(int type, ArrayList<Integer> toSearch) {
		for(int i=0;i<toSearch.size();i++) 
			if(toSearch.get(i).intValue() == type) return i;
		return -1;	
	}
			
	protected void createGate(int type) {
		Integer wrapper = new Integer(type);

		int delGateIndex = getWrapperIndex(type, delGates);
		if(delGateIndex > -1) delGates.remove(delGateIndex);

		newGates.add(wrapper);
		exiGates.add(wrapper);
		if(callback != null) callback.changedSomething(this);
	}
	
	protected void deleteGate(int index) {
		Integer removed = exiGates.remove(index);

		if(newGates.contains(removed)) {
			newGates.remove(removed);
		} else {
			delGates.add(removed);
		}
		if(callback != null) callback.changedSomething(this);
	}
		
	private void createControls(final Composite parent, GridData gridData) {
		final Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		topLevel.setLayout(layout);
		topLevel.setLayoutData(gridData);
		topLevel.setFont(parent.getFont());
	
		ToolBar toolBar;
		
		Label label = new Label(topLevel, SWT.NONE);
		label.setText("Gates");
		label.setLayoutData(new GridData(50,25));
		gateList = new List(topLevel,SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 65;
		gateList.setLayoutData(data);	
		
		if(callback != null) gateList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				int gateType = exiGates.get(gateList.getSelectionIndex()).intValue();
				callback.selectedGate(entity, gateType);
			}
		});
		
		Menu menu = new Menu(topLevel.getShell(),SWT.POP_UP);
		gateList.setMenu(menu);
		
		MenuItem item = new MenuItem(menu,SWT.CASCADE);
		item.setText("Create monitor for this");
		item.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e) {
				if(gateList.getSelectionIndex() < 0) return;
				Integer w = exiGates.get(gateList.getSelectionIndex()); 
				GateMonitor m = new GateMonitor(entity.getID(),w.intValue());
				GateMonitorProvider.getInstance().registerParameterMonitor(m,parent.getShell());
			}			
		});

		if(buttons) {
			toolBar = new ToolBar(topLevel, SWT.NONE);
			toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			createGateButton = new ToolItem(toolBar,SWT.BUTTON1);
			createGateButton.setText("Create new");
			createGateButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					InputDialog input = new InputDialog(
						topLevel.getShell(),
						"New Gate",
						"Please enter the type of the new gate",
						"",
						new SlotGateValidator(exiGates)) {

							protected void okPressed() {
								createGate(Integer.parseInt(this.getText().getText()));
								refreshLists();
								super.okPressed();
							}
						};
					input.open();
				}
			});
			deleteGateButton = new ToolItem(toolBar,SWT.BUTTON1);
			deleteGateButton.setText("Delete");
			deleteGateButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if(gateList.getSelectionCount() > 0)
						deleteGate(gateList.getSelectionIndex());
					refreshLists();
				}
			});
		}
	}
	
	public Iterator<Integer> getNewGates() {
		return newGates.iterator();
	}
	
	public Iterator<Integer> getDeletedGates() {
		return delGates.iterator();
	}
	
	public void setButtonsEnabled(boolean enable) {
		if(buttons) {
			createGateButton.setEnabled(enable);
			deleteGateButton.setEnabled(enable);
		}
	}

	public void selectGate(Gate gate) {
		for(int i=0;i<exiGates.size();i++) {
			Integer wrapper = exiGates.get(i);
			if(wrapper == null || gate == null) return;
			if(wrapper.intValue() == gate.getType()) {
				gateList.select(i);
				break;
			}
		}
		callback.selectedGate(gate.getNetEntity(), gate.getType());
	}

}
