package org.micropsi.eclipse.mindconsole.groups;

import org.micropsi.nodenet.NetEntity;

/**
 * 
 * 
 * 
 */
public interface IGateCallback {
	
	public void selectedGate(NetEntity entity, int type);
		
	public void changedSomething(GateGroup group);

}
