/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/WorldMessageHandlerIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.world;

import org.micropsi.comp.world.messages.AbstractWorldMessage;

public interface WorldMessageHandlerIF {
	public void handleMessage(AbstractWorldMessage m);

}
