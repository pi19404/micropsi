/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/LocationBasedMessageDistributor.java,v 1.3 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.world;

import java.util.Iterator;
import java.util.Set;

import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.objects.AbstractObject;

/**
 * Forwards messages to all highlevel objects that are within the specified radius around the specified
 * position.
 * Can be used either statically by calling static sendMessage(message, position, radius)
 * or as a MessageHandlerIF that forwards all incoming messages.
 * 
 *  @author matthias
 */
public class LocationBasedMessageDistributor implements WorldMessageHandlerIF {
	private Position pos;
	private double radius;
	private static World world = null;

	
	public LocationBasedMessageDistributor(Position pos, double radius) {
		this.pos = pos;
		this.radius = radius;
	}
	
	public void handleMessage(AbstractWorldMessage m) {
		sendMessage(m, pos, radius);
	}
	
	/**
	 * Sends the given message to all highlevel objects within radius radius around position pos.
	 * 
	 * @param message
	 * @param pos
	 * @param radius
	 */
	public static void sendMessage(AbstractWorldMessage message, Position pos, double radius) {
		Set objects = getWorld().getObjectsByPosition(pos, radius);
		Iterator it = objects.iterator();
		while (it.hasNext()) {
			AbstractObject obj = (AbstractObject) it.next();
			if (!(message.getSender() == obj))
				PostOffice.sendMessage(message, obj);
		}
	}
	
	/**
	 * Returns the position.
	 * @return Position
	 */
	public Position getPos() {
		return pos;
	}

	/**
	 * Returns the radius.
	 * @return double
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * Sets the position.
	 * @param pos The pos to set
	 */
	public void setPos(Position pos) {
		this.pos = pos;
	}

	/**
	 * Sets the radius.
	 * @param radius The radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

	/**
	 * Returns the world.
	 * @return WorldComponent
	 */
	public static World getWorld() {
		return world;
	}

	/**
	 * Sets the world.
	 * @param world The world to set
	 */
	public static void setWorld(World world) {
		LocationBasedMessageDistributor.world = world;
	}

}
