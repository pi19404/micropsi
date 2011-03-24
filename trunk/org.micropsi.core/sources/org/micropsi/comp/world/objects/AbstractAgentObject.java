/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 19.05.2003
 *
 */
package org.micropsi.comp.world.objects;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MPerceptionResp;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.World;
import org.w3c.dom.Element;

/**
 * @author matthias
 *
 */
public abstract class AbstractAgentObject extends AbstractCommonObject implements AgentObjectIF {

	protected double visionRange;
	protected Collection<MActionResponse> actionAnswers = new ArrayList<MActionResponse>(1);
	
	/**
	 * Some statistics...
	 */
	public long actionsSuccessful = 0;
	/**
	 * Some statistics...
	 */
	public long actionsFailed = 0;
	public long timeOfBirth;
	/**
	 * Some statistics...
	 */
	public double visitedLowX;
	/**
	 * Some statistics...
	 */
	public double visitedHighX;
	/**
	 * Some statistics...
	 */
	public double visitedLowY;
	/**
	 * Some statistics...
	 */
	public double visitedHighY;
	

	/**
	 * Used to read agent object from world file
	 * Use initObjectParameters(), not constructor, to initialise member variables!
	 * 
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public AbstractAgentObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	/**
	 * Creates new agent object
	 * Use initObjectParameters(), not constructor, to initialise member variables!
	 * 
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public AbstractAgentObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
		visitedLowX = getPosition().getX();
		visitedHighX = getPosition().getX();
		visitedLowY = getPosition().getY();
		visitedHighY = getPosition().getY();
	}

	/**
	 * Creates new agent object, sets objectClass to something usefull.
	 * This constructor is usually called by the framework.
	 * Use initObjectParameters(), not constructor, to initialise member variables!
	 * 
	 * @param objectName
	 * @param pos
	 */
	public AbstractAgentObject(String objectName, Position pos) {
		this(objectName, "agent", pos);
		visitedLowX = getPosition().getX();
		visitedHighX = getPosition().getX();
		visitedLowY = getPosition().getY();
		visitedHighY = getPosition().getY();
	}

	/**
	 * Used to initialise member variables.
	 * Should call super.initObjectParameters
	 */
	protected void initObjectParameters() {
		super.initObjectParameters();
		persistent = false;
		visionRange = 5;
	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("vision range", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setVisionRange(prop.getDoubleValue());
				return false;
			}
			protected String getValue() {
				return Double.toString(getVisionRange());
			}
		});
	}
	
	public String getAgentName() {
		return getObjectName();
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AgentObjectIF#getPerception()
	 */
	public abstract MPerceptionResp getPerception();

	/**
	 * Method getVisibleObjects.
	 */
	public Collection<AbstractObject> getVisibleObjects() {
		return world.getObjectsByPosition(position, visionRange);
	}
	
	public void insertStatisticDataIn(MTreeNode node) {
		if (isAlive()) {
			String s = Long.toString(world.getSimStep() - timeOfBirth);
			node.addChild(new MTreeNode("time alife:", s, null));
			s = Long.toString(actionsSuccessful) + " / " + Long.toString(actionsFailed);
			node.addChild(new MTreeNode("actions successful/failed:", s, null));
			s = Double.toString(visitedHighX - visitedLowX) + " x " + Double.toString(visitedHighY - visitedLowY);
			node.addChild(new MTreeNode("visited area:", s, null));
		}
	}

	/**
	 * Executes actions in the world.
	 * This implementation finds the target object reference if given, and calls
	 * handleAction(action, targetObject).
	 * 
	 * You should overwrite either this or handleAction(action, targetObject)
	 * 
	 * @param action - action that should be executed
	 * @return MActionResponse
	 */
	public void handleAction(MAction action) {
		AbstractObjectPart targetObject;
		if (action.getTargetObject() >= 0) {
			targetObject = world.getObjectPart(action.getTargetObject());
			if (targetObject == null) {
				world.getLogger().error("Action " + action.getActionType() + " refers to nonexisiting object " + action.getTargetObject());
			}
		} else {
			targetObject = null;
		}
		handleAction(action, targetObject);
	}

	/**
	 * Called by handleAction(action) in order to execute the specified action (on the given object,
	 * if applicable)
	 * 
	 * You should overwrite either this or handleAction(action)
	 * 
	 * @param action
	 * @param targetObject - null, if none was given
	 * @return MActionResponse
	 */
	protected void handleAction(MAction action, AbstractObjectPart targetObject) {
	}
	

	/**
	 * @return Returns the visionRange.
	 */
	public double getVisionRange() {
		return visionRange;
	}
	/**
	 * @param visionRange The visionRange to set.
	 */
	public void setVisionRange(double visionRange) {
		this.visionRange = visionRange;
	}

	/**
	 * @param actionResp
	 */
	protected void addActionAnswer(MActionResponse actionResp) {
		actionAnswers.add(actionResp);
		if (actionResp.getSuccess() > 0) {
			actionsSuccessful++;
		} else {
			actionsFailed++;
		}
	}

	public Collection<MActionResponse> returnActionAnswers() {
		Collection<MActionResponse> res = actionAnswers;
		actionAnswers = new ArrayList<MActionResponse>(1);
		return res;
	}

	/* @see org.micropsi.comp.world.objects.AbstractCommonObject#breakToPeaces(java.lang.String)*/
	protected void breakToPeaces(String action) {
		// Tell agent component that agent has died and let agent component handle this.
		// Do NOT remove agent object directly.
	}
	/* @see org.micropsi.comp.world.objects.AbstractObjectPart#moveTo(org.micropsi.common.coordinates.Position)*/
	public void moveTo(Position newPosition) {
		super.moveTo(newPosition);
		if (getPosition().getX() < visitedLowX) {
			visitedLowX = getPosition().getX();
		} else if (getPosition().getX() > visitedHighX) {
			visitedHighX = getPosition().getX();
		}
		if (getPosition().getY() < visitedLowY) {
			visitedLowY = getPosition().getY();
		} else if (getPosition().getY() > visitedHighY) {
			visitedHighY = getPosition().getY();
		}
	}
	/* @see org.micropsi.comp.world.objects.AbstractObjectPart#init(org.micropsi.comp.world.World)*/
	public void init(World world) {
		super.init(world);
		timeOfBirth = world.getSimStep();
	}
}
