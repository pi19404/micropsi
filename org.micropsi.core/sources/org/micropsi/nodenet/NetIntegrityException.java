/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetIntegrityException.java,v 1.3 2005/05/29 16:26:43 vuine Exp $
 */
package org.micropsi.nodenet;

import org.micropsi.common.exception.MicropsiException;

/**
 * NetIntegrityExceptions are thrown when some implementation if NetIntegrityIF
 * detects that the net's integrity is violated or would be violated if the
 * requested operation would be performed.
 *
 */
public class NetIntegrityException extends MicropsiException {
	
	private String integrityReport = "na"; 
	
	/**
	 * Creates the exception. For the possible values of the "type" parameter,
	 * seee NetIntegrityIF.
	 * @param type The type of the violation
	 * @param key the entity where the violation was detected or would be if the
	 * requested operation was performed.
	 * @param violator the NetIntegrityIF implementation that violated the
	 * integrity or would violate it or detected the violation
	 */
	public NetIntegrityException(int type, String key, NetIntegrityIF violator) {
		super(10000);
		
		switch(type) {
			case NetIntegrityIF.UNKNOWN_ENTITY: 
				description = "Bad reference to entity with key '"+key+"' (entity unknown)";
				break;
			case NetIntegrityIF.UNKNOWN_MODULE:
				description = "Bad reference to module with key '"+key+"' (nodespace unknown)";
				break;
			case NetIntegrityIF.BAD_KEY:
				if(violator != null) integrityReport = violator.reportIntegrityStatus();
				description = "Bad key '"+key+"' ";
				break;
			case NetIntegrityIF.DUPLICATE_KEY:
				if(violator != null) integrityReport = violator.reportIntegrityStatus();
				description = "Duplicate key '"+key+"' ";
				break;
			case NetIntegrityIF.BAD_LINK:
				if(violator != null) integrityReport = violator.reportIntegrityStatus();
				description = "Bad link (targeted node didn't know the incoming link) to Node with key: '"+key+"'";				
				break;
			case NetIntegrityIF.BAD_SLOT:
				description = "Bad slot (not present) at '"+key+"'";
				break;
			case NetIntegrityIF.BAD_OUTPUT_FUNCTION:
				description = "Bad output function at '"+key+"'";
				break;					
		}
	}
	
	/**
	 * Creates the exception. For the possible values of the "type" parameter,
	 * seee NetIntegrityIF.
	 * @param badType The type of the violation
	 * @param key the entity where the violation was detected or would be if the
	 * requested operation was performed.
	 */	
	public NetIntegrityException(int badType, String key) {
		this(badType,key,null);
	}
		
	/**
	 * Returns the integrity status
	 * @return String the integrity report
	 */
	public String getIntegrityReport() {
		return integrityReport;
	}

}
