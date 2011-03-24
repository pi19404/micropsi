package org.micropsi.eclipse.mindconsole.groups;

import java.util.Iterator;
import java.util.ArrayList;

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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Slot;

/**
 * 
 * 
 * 
 */
public class SlotGroup {
	
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
	
	private List slotList;
	
	private ArrayList<Integer> newSlots = new ArrayList<Integer>();
	private ArrayList<Integer> delSlots = new ArrayList<Integer>();
	
	private ArrayList<Integer> exiSlots = new ArrayList<Integer>();
	
	private ToolItem createSlotButton;
	private ToolItem deleteSlotButton;
	
	private ISlotCallback callback;
	private NetEntity entity;
	
	private boolean buttons = false;

	public SlotGroup(Composite parent, GridData gridData, NetEntity entity, ISlotCallback callback, boolean buttons) {
		
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
			Iterator iter = entity.getSlots();
			while(iter.hasNext())
				exiSlots.add(new Integer(((Slot)iter.next()).getType()));
		}
		refreshLists();
	}
	
	public void setEntity(NetEntity entity) {
		this.entity = entity;
		
		setButtonsEnabled(false);
		
		newSlots = new ArrayList<Integer>();
		delSlots = new ArrayList<Integer>();

		exiSlots = new ArrayList<Integer>();

		if(	entity != null &&
			entity.getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE)
			setButtonsEnabled(true);


		if(entity != null) {
			Iterator iter = entity.getSlots();
			while(iter.hasNext())
				exiSlots.add(new Integer(((Slot)iter.next()).getType()));
		}
		refreshLists();
	}
	
	private void refreshLists() {
		slotList.removeAll();
		
		for(int i=0;i<exiSlots.size();i++)
			slotList.add(TypeStrings.slotType( exiSlots.get(i).intValue()  ));	
	
	}
	
	private int getWrapperIndex(int type, ArrayList<Integer> toSearch) {
		for(int i=0;i<toSearch.size();i++) 
			if(toSearch.get(i).intValue() == type) return i;
		return -1;	
	}
			
	protected void createSlot(int type) {
		Integer wrapper = new Integer(type);
		
		int delSlotIndex = getWrapperIndex(type, delSlots);
		if(delSlotIndex > -1) delSlots.remove(delSlotIndex);

		newSlots.add(wrapper);
		exiSlots.add(wrapper);
		if(callback != null) callback.changedSomething(this);
	}
	
	protected void deleteSlot(int index) {
		Integer removed = exiSlots.remove(index);	
		
		if(newSlots.contains(removed)) {
			newSlots.remove(removed);
		} else {
			delSlots.add(removed);
		}
		if(callback != null) callback.changedSomething(this);
	}
		
	private void createControls(Composite parent, GridData gridData) {
		final Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		topLevel.setLayout(layout);
		topLevel.setLayoutData(gridData);
		topLevel.setFont(parent.getFont());

		Label label = new Label(topLevel, SWT.NONE);
		label.setText("Slots");
		label.setLayoutData(new GridData(50,25));
	
		slotList = new List(topLevel,SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 65;
		slotList.setLayoutData(data);
		
		if(callback != null) slotList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int slotType = exiSlots.get(slotList.getSelectionIndex()).intValue();
				callback.selectedSlot(entity, slotType);
			}			
		});
		
		if(buttons) {
			ToolBar toolBar = new ToolBar(topLevel, SWT.NONE);
			toolBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			createSlotButton = new ToolItem(toolBar,SWT.BUTTON1);
			createSlotButton.setText("Create new");
			createSlotButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {			
					InputDialog input = new InputDialog(
						topLevel.getShell(),
						"New Slot",
						"Please enter the type of the new slot", 
						"",
						new SlotGateValidator(exiSlots)) {
				
							protected void okPressed() {
								createSlot(Integer.parseInt(this.getText().getText()));
								refreshLists();					
								super.okPressed();
							}
						};	
					input.open();
				}
			});
			deleteSlotButton = new ToolItem(toolBar,SWT.BUTTON1);
			deleteSlotButton.setText("Delete");
			deleteSlotButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if(slotList.getSelectionCount() > 0)	
						deleteSlot(slotList.getSelectionIndex());
					refreshLists();	
				}
			});
		}		
	}
		
	public Iterator<Integer> getNewSlots() {
		return newSlots.iterator();
	}

	public Iterator<Integer> getDeletedSlots() {
		return delSlots.iterator();
	}

	public void setButtonsEnabled(boolean enable) {
		if(buttons) {
			createSlotButton.setEnabled(enable);
			deleteSlotButton.setEnabled(enable);
		}
	}

}
