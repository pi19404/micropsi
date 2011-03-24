/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/config/MicropsiConfigException.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.config;

import org.micropsi.common.exception.MicropsiException;

public class MicropsiConfigException extends MicropsiException {
	

	public MicropsiConfigException(int id, String key) {
		super(id);
		super.description = "Key: "+key;
	}
	
	

}
