/*
 * Created on 28.01.2005
 *
 */

package org.micropsi.comp.world;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.ObjectCreationMessage;

/**
 * @author Matthias
 *
 */
public class ObjectCreator implements WorldMessageHandlerIF {

	private final World world;

	/**
	 * 
	 */
	public ObjectCreator(World world) {
		this.world = world;
	}

	/* @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void handleMessage(AbstractWorldMessage m) {
		if (m instanceof ObjectCreationMessage) {
			ObjectCreationMessage message = (ObjectCreationMessage) m;
			try {
				world.createObject(message.getObjectJavaClass(), message.getObjectName(), message.getObjectClass(), message.getObjectPos(), message.getObjectProperties());
			} catch (MicropsiException e) {
				world.getLogger().error("Error creating object for message: " + message, e);
			}
		}
	}

}
