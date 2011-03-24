/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 24.05.2003
 *
 */
package org.micropsi.comp.world;

import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.TickMessage;

/**
 * Creates a new tick message every "threshold" simsteps. Triggered by incoming tick messages or
 * method tick(simStep).
 * 
 * @author matthias
 */
public class TimerTickGenerator extends MessageDistributor {

	private String messageContent;
	private int threshold;
	private long lastSentTick = 0;

	public TimerTickGenerator(int threshold, String messageContent, World world) {
		super(world);
		this.threshold = threshold;
		this.messageContent = messageContent;
	}

	void tick(long simStep) {
		if (simStep - lastSentTick > threshold) {
			AbstractWorldMessage message = new TickMessage ("TICK", messageContent, simStep);
			super.handleMessage(message);
			lastSentTick = simStep;
		}
	}

	/**
	 * Only reacts on tick messages. Calls tick(simStep) for each tick message.
	 */
	public void handleMessage(AbstractWorldMessage message) {
		if (message instanceof TickMessage) {
			tick(((TickMessage) message).getSimStep());
		}
	}

}
