/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 24.05.2003
 *
 */
package org.micropsi.comp.world.messages;

import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 * @author matthias
 *
 */
public class TickMessage extends WorldMessage {
	private long simStep;

	/**
	 * @param messageClass
	 * @param messageContent
	 * @param sender
	 */
	public TickMessage(String messageClass, String messageContent, String simStep, AbstractObjectPart sender) {
		super(messageClass, messageContent, sender);
		this.simStep = Long.parseLong(simStep);
	}
	
	public TickMessage(String messageClass, String messageContent, long simStep) {
		super(messageClass, messageContent, null);
		this.simStep = simStep;
	}

	/**
	 * @return
	 */
	public long getSimStep() {
		return simStep;
	}

}
