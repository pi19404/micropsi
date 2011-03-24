/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MVersion.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.messages;

public class MVersion implements MessageIF {

	
	public static final int MAJOR = 0;
	public static final int MINOR = 1;
	
	public static final String NAME = "alpha stage"; 
	
	
	private int remoteMajor = MAJOR;
	private int remoteMinor = MINOR;
	private String remoteName = NAME;
	
	public int getMessageType() {
		return MessageTypesIF.MTYPE_COMMON_VERSION;
	}

	public boolean isRemoteVersionOK() {
		if(remoteMajor != MAJOR) return false;
		if(remoteMinor < MINOR) return false;
		return true;   
	}

	/**
	 * Returns the remoteMajor.
	 * @return int
	 */
	public int getRemoteMajor() {
		return remoteMajor;
	}

	/**
	 * Returns the remoteMinor.
	 * @return int
	 */
	public int getRemoteMinor() {
		return remoteMinor;
	}

	/**
	 * Returns the remoteName.
	 * @return String
	 */
	public String getRemoteName() {
		return remoteName;
	}

	/**
	 * Sets the remoteMajor.
	 * @param remoteMajor The remoteMajor to set
	 */
	public void setRemoteMajor(int remoteMajor) {
		this.remoteMajor = remoteMajor;
	}

	/**
	 * Sets the remoteMinor.
	 * @param remoteMinor The remoteMinor to set
	 */
	public void setRemoteMinor(int remoteMinor) {
		this.remoteMinor = remoteMinor;
	}

	/**
	 * Sets the remoteName.
	 * @param remoteName The remoteName to set
	 */
	public void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}

}
