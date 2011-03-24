package org.micropsi.comp.agent.voronoi;

import java.util.ArrayList;

import org.micropsi.common.coordinates.Position;

/**
 * 
 * @author Lorgod
 * Fifo-list for way-handling
 */
public class Waypoint {

	private Position position;
	public Waypoint next;
	
	public Waypoint(Position position) {
		this.position = position;
		next = null;
	}
	
	public Waypoint(Position position, Waypoint next) {
		this(position);
		this.next = next;
	}
	
	public void add(Waypoint newWaypoint) {
		Waypoint temp = this;
		while(temp.next != null)
			temp = temp.next;
		
		temp.next = newWaypoint;
	}
	
	public Waypoint delete() {
		Waypoint toReturn = next;
		
		position = null;
		next = null;
		
		return toReturn;
	}
	
	public void deleteAll() {
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		Waypoint temp = this;
		do {
			waypoints.add(temp);
			temp = temp.next;
		} while(temp != null);
		
		for(int i = 0; i < waypoints.size(); i++) {
			temp = waypoints.get(i);
			temp.position = null;
			temp.next = null;
		}
	}

	public Position getPosition() {
		return position;
	}
}
