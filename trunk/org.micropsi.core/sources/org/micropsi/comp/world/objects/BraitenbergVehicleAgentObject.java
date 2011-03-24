/*
 * Created on 10.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.micropsi.comp.world.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MPerceptionResp;
import org.micropsi.comp.messages.MPerceptionValue;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ObjectProperty;
import org.w3c.dom.Element;

/**
 * A simple Braitenberg Vehicle with light sensors and two wheels. Agent can set speed of each wheel.
 * 
 * @author Matthias
 */
public class BraitenbergVehicleAgentObject extends AbstractAgentObject {
	
	public class LightSensor {
		private double posOffsetX, posOffsetY;
		public LightSensor(double offsX, double offsY) {
			posOffsetX = offsX; posOffsetY = offsY;
		}
		
		public void rotateBy(double angle) {
			WorldVector relativePos = new WorldVector(posOffsetX, posOffsetY, 0);
			relativePos.rotate(angle);
			posOffsetX = relativePos.getX();
			posOffsetY = relativePos.getY();
		}
		
		public double getBrightness() {
			double res = 0;
			Position pos = new Position(getPosition().getX() + posOffsetX, getPosition().getY() + posOffsetY);
			Iterator it = world.getObjectParts().iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof LightSourceIF) {
					res += ((LightSourceIF) obj).getBrightnessForPosition(pos);
				}
			}
			return res;
		}
	}
	
	private double speedLimit;
	private double wheelDistance = 1;
	
	private List<LightSensor> sensors;

	/**
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public BraitenbergVehicleAgentObject(Element configData, Logger logger)
		throws MicropsiException {
		super(configData, logger);
	}

	/**
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public BraitenbergVehicleAgentObject(
		String objectName,
		String objectClass,
		Position pos) {
		super(objectName, objectClass, pos);
	}

	/**
	 * @param objectName
	 * @param pos
	 */
	public BraitenbergVehicleAgentObject(String objectName, Position pos) {
		super(objectName, "BraitenbergVehicleAgent", pos);
	}

	
	
	/* @see org.micropsi.comp.world.objects.AbstractObjectPart#initObjectParameters()*/
	protected void initObjectParameters() {
		super.initObjectParameters();
		speedLimit = 1.5;
	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("speed limit", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setSpeedLimit(prop.getDoubleValue());
				return true;
			}
			protected String getValue() {
				return Double.toString(getSpeedLimit());
			}
		});
	}

	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AgentObjectIF#getPerception()
	 */
	public MPerceptionResp getPerception() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AgentObjectIF#handleAction(org.micropsi.comp.messages.MAction)
	 */
	public void handleAction(MAction action) {
		double success = -1;
		
		if (action.getActionType().equals("SETSPEED")) {
			// action should be renamed to "MOVEBY" now, because it only executes one single movement.
			double speedLeftWheel = Double.parseDouble(action.getParameter(0));
			double speedRightWheel = Double.parseDouble(action.getParameter(1));
			if (speedLeftWheel + speedRightWheel > 2*speedLimit) { // speed limit ;-)
				double fak = 2*speedLimit / (speedLeftWheel + speedRightWheel);
				speedLeftWheel *= fak;
				speedRightWheel *= fak;
			}
			moveAStep(speedLeftWheel, speedRightWheel);
			success = 1;
		}
		
		MActionResponse actionResp = new MActionResponse(
				getAgentName(),
				success,
				action.getTicket()
				
		);
		addBodyPropertyChanges(actionResp);
		
		addActionAnswer(actionResp);
	}

	protected void addBodyPropertyChanges(MActionResponse actionResp) {
		actionResp.addBodyPropertyChange(
			new MPerceptionValue("BLUE_EYE_LEFT", Double.toString(sensors.get(0).getBrightness())));
		actionResp.addBodyPropertyChange(
			new MPerceptionValue("BLUE_EYE_RIGHT", Double.toString(sensors.get(1).getBrightness())));
	}

	/**
	 * 
	 */
	protected void moveAStep(double speedLeftWheel, double speedRightWheel) {
		double rotationAngle = Math.toDegrees(speedLeftWheel - speedRightWheel) / wheelDistance;
		WorldVector effortVec = new WorldVector(0, (speedRightWheel + speedLeftWheel) / 2, 0);
		effortVec.rotate(getOrientationAngle() + rotationAngle / 2);
		moveBy(world.getEffectiveMoveVector(this, effortVec));
		rotateBy(rotationAngle);
	}
	
	public void rotateBy(double angle) {
		super.rotateBy(angle);
		Iterator it = sensors.iterator();
		while (it.hasNext()) {
			((LightSensor) it.next()).rotateBy(angle);
		}
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractHierarchicObject#initSubobjects()
	 */
	protected void initSubobjects() {
		// Sensors should become REAL subobjects later...
		super.initSubobjects();
		sensors = new ArrayList<LightSensor>(2);
		sensors.add(new LightSensor(-1, 0.5));
		sensors.add(new LightSensor(1, 0.5));
	}

	/**
	 * @return Returns the speedLimit.
	 */
	public double getSpeedLimit() {
		return speedLimit;
	}
	/**
	 * @param speedLimit The speedLimit to set.
	 */
	public void setSpeedLimit(double speedLimit) {
		this.speedLimit = speedLimit;
	}
}
