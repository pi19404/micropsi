/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/PostOffice.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.world;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.micropsi.comp.world.messages.AbstractWorldMessage;

/**
 *  @author matthias
 * 
 * Can be used to deliver @see WorldMessage - immediatly or delayed.
 *
 */
public class PostOffice {
	private SortedSet<MessageContainer> messageQueue;
	//TODO Matthias: Use weak references for recipient
	// Do we need weak references? Is this a memory hole? If it is, Fix, if not,
	// remove the todo.
	
	private class MessageContainer {
		public AbstractWorldMessage message;
		public WorldMessageHandlerIF recipient;
		public long sendingTime;
		
		MessageContainer(AbstractWorldMessage message, WorldMessageHandlerIF recipient,	long sendingTime) {
			this.message = message;
			this.recipient = recipient;
			this.sendingTime = sendingTime;
		}
	}
	
	private class MessageContainerComparator implements Comparator<MessageContainer> {
		
		/**
		 * @see java.util.Comparator#compare(Object, Object)
		 */
		public int compare(MessageContainer o1, MessageContainer o2) {
			MessageContainer m1 = o1;
			MessageContainer m2 = o2;
			if (m1.sendingTime == m2.sendingTime) {
				return m1.message.hashCode() - m2.message.hashCode();
			} else {
				return (int) (m1.sendingTime - m2.sendingTime);
			}
		}

	}
	
	public PostOffice() {
		messageQueue = new TreeSet<MessageContainer>(new MessageContainerComparator());
	}
	
	public static void sendMessage(AbstractWorldMessage message, WorldMessageHandlerIF recipient) {
		recipient.handleMessage(message);
	}
	
	public void send(AbstractWorldMessage message, WorldMessageHandlerIF recipient) {
		sendMessage(message, recipient);
	}
	
	public void send(AbstractWorldMessage message, WorldMessageHandlerIF recipient, long sendingTime) {
		messageQueue.add(new MessageContainer(message, recipient, sendingTime));
	}


	public void tick(long simStep){
		Iterator i = messageQueue.iterator();
		if (i.hasNext()) {
			MessageContainer m;
			int messages = 0;
			do {
				m = (MessageContainer) i.next();
				messages++;
			} while (i.hasNext() && m.sendingTime <= simStep);
			
			if ((messages > 1) || (messages == 1 && m.sendingTime <= simStep)) {
				Set currentMessages = null;
				
				// all messages are to be sent?
				if (m.sendingTime <= simStep) {
					currentMessages = new HashSet<MessageContainer>(messageQueue);
					messageQueue.clear();
				} else {
					Set<MessageContainer> currentSubset = messageQueue.headSet(m);
					currentMessages = new HashSet<MessageContainer>(currentSubset);
					currentSubset.clear();
				}
				i = currentMessages.iterator();
				while (i.hasNext()) {
					m = (MessageContainer) i.next();
					send(m.message, m.recipient);
				}
			}
			
		}
	}
}
