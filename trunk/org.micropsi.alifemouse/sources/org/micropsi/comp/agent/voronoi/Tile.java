package org.micropsi.comp.agent.voronoi;

import java.util.ArrayList;

import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.nodenet.NetEntity;
/**
 * 
 * @author Lorgod
 * Note: this class has a natural ordering that is inconsistent with equals.
 * only the cost is compared and thus two tiles with equal cost are not necessarily
 * equal
 */
public class Tile implements Comparable {
	public NetEntity voronoiNode;
	private Tile predecessor;
	private ArrayList<Tile> neighbours = null;
	private int cost = 0;
	private boolean searched;
	private Position goal;
	
	public Tile(NetEntity node) {
		voronoiNode = node;
		predecessor = null;
		goal = null;
		searched = false;
		neighbours = new ArrayList<Tile>();
	}
	
	public Tile(NetEntity node, Tile predecessor) {
		voronoiNode = node;
		this.predecessor = predecessor;
		goal = null;
		searched = false;
		neighbours = new ArrayList<Tile>();
	}
	
	public Tile(NetEntity node, Position goal) {
		this(node);
		this.goal = goal;
	}
	
	public void addNeighbour(Tile node) {
		if(neighbours == null)
			neighbours = new ArrayList<Tile>();
		if(!neighbours.contains(node))
			neighbours.add(node);
	}

	public int getCost() {
		return cost;
	}
	
	public int getApproxCost() {
		if(goal == null)
			return cost;
		else {
			return cost + (int)NodeFunctions.getPosition(voronoiNode).distance2D(goal);
		}
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}

	public ArrayList<Tile> getNeighbours() {
		return neighbours;
	}

	public Tile getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Tile predecessor) {
		this.predecessor = predecessor;
	}
	
	public void clear() {
		if(neighbours != null) {
			neighbours.clear();
			neighbours = null;
		}

		predecessor = null;
	}

	/**
	 * @param t should always be instance of Tile
	 */
	public int compareTo(Object t) {
		return this.getApproxCost() - ((Tile)t).getApproxCost();
	}

	public void setGoal(Position goal) {
		this.goal = goal;
	}
}
