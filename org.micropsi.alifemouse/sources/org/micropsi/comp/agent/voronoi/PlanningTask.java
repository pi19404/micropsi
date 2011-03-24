package org.micropsi.comp.agent.voronoi;

import java.util.ArrayList;

import org.micropsi.common.coordinates.Position;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;

public class PlanningTask {
	private NetEntity start;
	private NetEntity startChild = null;
	private NetEntity goal;
	private boolean complete = false;
	private int layer = 0;
	
	private ArrayList<NetEntity> way;
	private ArrayList<NetEntity> watchedNodes;
	private ArrayList<PlanningTask> subTasks;
	
	private PlanningTask parentTask = null;
	private PlanningTask nextTask = null; // same layer
	
	private boolean finalLayer = false;
	
	public PlanningTask(NetEntity start, NetEntity goal, int layer) throws NetIntegrityException {
		this.start = start;
		this.goal = goal;
		this.layer = layer;
		way = new ArrayList<NetEntity>();
		watchedNodes = new ArrayList<NetEntity>();
		subTasks = new ArrayList<PlanningTask>();
		if(goal.getFirstLinkAt(GateTypesIF.GT_SUB).getWeight() > 0.99) {
			for(int i = 0; i < goal.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); i++) {
				NetEntity watched = goal.getLink(GateTypesIF.GT_SUB, i).getLinkedEntity();
				
				if((watched.getFirstLinkAt(GateTypesIF.GT_SUB).getWeight() < 0.99) 
					&& (watched.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= 8) 
					&& (watched.getLink(GateTypesIF.GT_SUB, 7).getWeight() > 0.5)) {
					continue;
				}
				
				if((watched.getFirstLinkAt(GateTypesIF.GT_SUB).getWeight() < 0.99) 
					&& (watched.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= 8) 
					&& (watched.getLink(GateTypesIF.GT_SUB, 2).getWeight() < 0.5)) {
					System.out.println("error in hierachy");
					continue;
				}
				
				watchedNodes.add(watched);
			}
		} else 
			finalLayer = true;
	}
	
	public PlanningTask(NetEntity start, NetEntity goal, int layer, NetEntity startChild) throws NetIntegrityException {
		this(start, goal, layer);
		this.startChild = startChild;
	}
	
	public void addWay(ArrayList<NetEntity> newWay) {
		if(way.size() == 0) {
			way = newWay;
			return;
		}
		
		if(way.get(way.size() - 1).getID().equals(newWay.get(0).getID())) {
			for(int i = 1; i < newWay.size(); i++)
				way.add(newWay.get(i));
		}
	}

	public NetEntity getGoal() {
		return goal;
	}

	public NetEntity getStart() {
		return start;
	}
	
	public boolean isFinalLayer() {
		return finalLayer;
	}

	public ArrayList<NetEntity> getWatchedNodes() {
		return watchedNodes;
	}
	
	public void setWatchedNode(NetEntity endNode) {
		watchedNodes = new ArrayList<NetEntity>();
		watchedNodes.add(endNode);
	}

	public NetEntity getStartChild() {
		return startChild;
	}
	
	public void setStartChild(NetEntity startChild) {
		this.startChild = startChild;
	}

	public PlanningTask getParentTask() {
		return parentTask;
	}

	public void setParentTask(PlanningTask parentTask) {
		this.parentTask = parentTask;
	}

	public ArrayList<PlanningTask> getSubTasks() {
		return subTasks;
	}
	
	public void addSubTask(PlanningTask subTask) {
		subTasks.add(subTask);
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public void clear() {
		for(PlanningTask subTask : subTasks)
			subTask.clear();
		subTasks.clear();
		parentTask = null; 
	}

	public PlanningTask getNextTask() {
		return nextTask;
	}

	public void setNextTask(PlanningTask nextTask) {
		this.nextTask = nextTask;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
}
