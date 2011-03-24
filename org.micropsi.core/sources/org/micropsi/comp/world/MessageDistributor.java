package org.micropsi.comp.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.micropsi.comp.world.messages.AbstractWorldMessage;

/**
 * Forwards incoming messages to all subscribed objects.
 * 
 * @author Henning
 */
public class MessageDistributor implements WorldMessageHandlerIF {
	
	private World world = null;

	private List<WorldMessageHandlerIF> handlers = null;
	// TODO Matthias: use weak references
	// this todo is several months old -- do we NEED weak references here? If
	// we do: use them NOW, if we dont't, remove the todo, please

	public MessageDistributor(World world) {
		this.world = world;
		handlers = new ArrayList<WorldMessageHandlerIF>();
	}

	public void subscribe(WorldMessageHandlerIF h) {
		if (handlers.size() == 0) {
			activate();
		}
		handlers.add(h);
	}
	
	public void unsubscribe(WorldMessageHandlerIF h) {
		handlers.remove(h);
		if (handlers.size() == 0) {
			deactivate();
		}
	}
	
	public void deactivate() {
		if (world != null) {
			// deactivate
		}
	}
	
	public void activate() {
		if (world != null) {
			// activate
		}
	}
	
	public boolean isSubscribed(WorldMessageHandlerIF h) {
		if (handlers.contains(h)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void handleMessage(AbstractWorldMessage message) {
		ArrayList toDo = new ArrayList<WorldMessageHandlerIF>(handlers);
		
		Iterator it = toDo.iterator();
		
		while (it.hasNext()){
			WorldMessageHandlerIF h = (WorldMessageHandlerIF)it.next();
			PostOffice.sendMessage(message, h);	
		}
		
	}

}
