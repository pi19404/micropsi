/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetIntegrityIF.java,v 1.3 2005/05/29 16:26:43 vuine Exp $
 */
package org.micropsi.nodenet;

/**
 * Implementations of this interface can be checked for integrity errors. The
 * whole net can be checked by calling checkIntegrity() on all known
 * implementations of this interface. <br/><br/> This is needed to avoid any
 * confusion about bad links, inexistent entities, slots or gates.
 */
public interface NetIntegrityIF {
	
	/**
	 * Integrity failure: The requested entity was not found
	 */
	public static final int UNKNOWN_ENTITY = 0;
	
	/**
	 * Integrity failure: The requested entity is not known to be a module
	 */
	public static final int UNKNOWN_MODULE = 1;
	
	/**
	 * Integrity failure: The key was bad
	 */
	public static final int BAD_KEY = 2;
	
	/**
	 * Integrity failure: Bad link found
	 */
	public static final int BAD_LINK = 3;
	
	/**
	 * Integrity failure: No such slot.
	 */
	public static final int BAD_SLOT = 4;
	
	/**
	 * Integrity failure: Duplicated key
	 */
	public static final int DUPLICATE_KEY = 5;

	/**
	 * Integrity failure: Bad output function
	 */
	public static final int BAD_OUTPUT_FUNCTION = 6;
	
	/**
	 * Checks the integrity. 
	 * @throws NetIntegrityException if the implementation violates the net's
	 * integrity.
	 */
	public void checkIntegrity() throws NetIntegrityException;
	
	/**
	 * Returns a String with integrity-relevant information about the entity 
	 * @return String the status
	 */
	public String reportIntegrityStatus();

}
