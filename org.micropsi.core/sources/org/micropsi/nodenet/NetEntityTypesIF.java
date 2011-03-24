/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetEntityTypesIF.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

/**
 * Return values of NetEntity.getType()
 * 
 * @see org.micropsi.nodenet.NetEntity
 */
 
public interface NetEntityTypesIF {

	/**
	 * Node: 1 Slot, 9 Gates.
	 */
	public static final int ET_NODE = 0;
	
	/**
	 * NodeSpaceModule: n Slots, n Gates
	 */
	public static final int ET_MODULE_NODESPACE = 1;
	
	/**
	 * NativeModule: n Slots, n Gates, contains implementation
	 */
	public static final int ET_MODULE_NATIVE = 2;

}
