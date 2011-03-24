/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/utils/ListenerIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.utils;

public interface ListenerIF {
	
	public void fireNotification(int notificationType, Object parameter);

}
