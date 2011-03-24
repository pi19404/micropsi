/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/GateTypesIF.java,v 1.3 2005/03/14 23:04:46 jbach Exp $
 */
package org.micropsi.nodenet;

/**
 * The gate types of the Nodes implemented in this package.
 */

public interface GateTypesIF {

	public static final int GT_GEN = 0;

	public static final int GT_POR = 1;
	
	public static final int GT_RET = 2;
	
	public static final int GT_SUR = 3;
	
	public static final int GT_SUB = 4;

	public static final int GT_CAT = 5;

	public static final int GT_EXP = 6;
	
	public static final int GT_SYM = 7;
	
	public static final int GT_REF = 8;

	public static final int GT_ASSOCIATION = 20;
	
	public static final int GT_DISSOCIATION = 21;
}
