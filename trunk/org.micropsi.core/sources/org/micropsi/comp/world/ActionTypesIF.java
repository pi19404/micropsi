/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/ActionTypesIF.java,v 1.3 2005/01/30 21:36:21 fuessel Exp $
 */
package org.micropsi.comp.world;

public interface ActionTypesIF {

	/*
	 * do nothing
	 * parameters: none
	 */
	public static final int ACTION_NOOP = 0;
	
	/*
	 * move the agent relative to its current position
	 * parameters: list are relative xyz-coordinates
	 */
	public static final int ACTION_MOVE = 1; 
	
	/*
	 * eat targetObject
	 * parameters: none
	 */
	public static final int ACTION_EAT = 2;

	/*
	 * drink targetObject parameters: none
	 */
	public static final int ACTION_DRINK = 3;		
	/*
	 * let the agent die
	 * parameters: none
	 */
	//public static final int ACTION_DIE = 4;

	public static final int ACTION_FOCUS = 5;
 
}
