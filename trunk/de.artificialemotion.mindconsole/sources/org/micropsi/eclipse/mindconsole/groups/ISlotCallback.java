package org.micropsi.eclipse.mindconsole.groups;

import org.micropsi.nodenet.NetEntity;

/**
 * 
 * 
 * 
 */
public interface ISlotCallback {
	
	public void selectedSlot(NetEntity entity, int type);
	
	public void changedSomething(SlotGroup group);

}
