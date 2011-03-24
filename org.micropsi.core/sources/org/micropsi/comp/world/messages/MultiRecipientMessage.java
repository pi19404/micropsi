/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 04.01.2004
 *
 */
package org.micropsi.comp.world.messages;

import java.util.*;

import org.micropsi.comp.world.PostOffice;
import org.micropsi.comp.world.WorldMessageHandlerIF;
import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 * @author Matthias
 *
 */
public class MultiRecipientMessage extends AbstractWorldMessage {
	
	private Map<WorldMessageHandlerIF,Set<AbstractObjectPart>> recipientsWithOriginators = null;
	private SortedSet<WorldMessageHandlerIF> recipientsTodo = null;
	private Set<WorldMessageHandlerIF> originalRecipients = null;

	/**
	 * @param messageClass
	 * @param messageContent
	 * @param sender
	 */
	public MultiRecipientMessage(String messageClass, String messageContent,
			AbstractObjectPart sender) {
		super(messageClass, messageContent, sender);
	}
	
	public void addRecipient(WorldMessageHandlerIF recipient) {
		if (recipientsWithOriginators == null) {
			recipientsWithOriginators = new HashMap<WorldMessageHandlerIF,Set<AbstractObjectPart>>(10);
		}
		if (originalRecipients == null) {
			originalRecipients = new HashSet<WorldMessageHandlerIF>(10);
		}
		recipientsWithOriginators.put(recipient, null);
		originalRecipients.add(recipient);
	}
	
	public Set<WorldMessageHandlerIF> getRecipients() {
		if (recipientsWithOriginators != null) {
			return recipientsWithOriginators.keySet();
		} else {
			return new HashSet<WorldMessageHandlerIF>(1);
		}
	}
	
	public WorldMessageHandlerIF getCurrentRecipient() {
		if (recipientsTodo != null) {
			try {
				return recipientsTodo.first();
			} catch (NoSuchElementException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public Set getCurrentRecipientOriginators() {
		return recipientsWithOriginators.get(getCurrentRecipient());
	}
	
	public void send() {
		initRecpientsTodo();
		while (getCurrentRecipient() != null) {
			PostOffice.sendMessage(this, getCurrentRecipient());
			removeCurrentRecpient();
		}
	}

	/**
	 * 
	 */
	private void removeCurrentRecpient() {
		if (recipientsTodo != null) {
			recipientsTodo.remove(getCurrentRecipient());
		}
	}

	/**
	 * 
	 */
	private void initRecpientsTodo() {
		recipientsTodo = new TreeSet<WorldMessageHandlerIF>(new Comparator<WorldMessageHandlerIF>() {
			public int compare(WorldMessageHandlerIF o1, WorldMessageHandlerIF o2) {
				
				//TODO: Is this cast safe?
				
				AbstractObjectPart obj1 = (AbstractObjectPart) o1;
				AbstractObjectPart obj2 = (AbstractObjectPart) o2;
				int res = obj2.getPartHierarchyLevel() - obj1.getPartHierarchyLevel();
				if (res != 0) {
					return res;
				} else  {
					if (obj2.getId() < obj1.getId()) {
						return -1;
					} else {
						if (obj2.getId() > obj1.getId()) {
							return 1;
						} else {
							return 0;
						}
					}
				}
			}
		});
		recipientsTodo.addAll(getRecipients());
	}

	public SortedSet<WorldMessageHandlerIF> getRecipientsTodo() {
		return recipientsTodo;
	}

	/* @see org.micropsi.comp.world.messages.AbstractWorldMessage#delegateToParent(org.micropsi.comp.world.objects.AbstractObject, org.micropsi.comp.world.objects.AbstractObject)
	 */
	public void delegateToParent(AbstractObjectPart obj, AbstractObjectPart parent) {
		Set<AbstractObjectPart> parentOriginators = recipientsWithOriginators.get(parent);
		if (parentOriginators == null) {
			parentOriginators = new HashSet<AbstractObjectPart>(10);
			recipientsWithOriginators.put(parent, parentOriginators);
			recipientsTodo.add(parent);
		}
		parentOriginators.add(obj);
		recipientsWithOriginators.remove(obj);
	}
}
