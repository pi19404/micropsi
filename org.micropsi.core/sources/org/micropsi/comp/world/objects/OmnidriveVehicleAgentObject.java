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
 * A simple Vehicle omnidirectional drive (three wheels) with light sensors 
 * between the wheels. Agent can set speed of each wheel.
 * 
 * @author Joscha (adapted Matthias' code)
 */
public class OmnidriveVehicleAgentObject extends AbstractAgentObject {
	
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
//	private double wheelRadius =1;
	
	private List<LightSensor> sensors;

	/**
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public OmnidriveVehicleAgentObject(Element configData, Logger logger)
		throws MicropsiException {
		super(configData, logger);
	}

	/**
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public OmnidriveVehicleAgentObject(
		String objectName,
		String objectClass,
		Position pos) {
		super(objectName, objectClass, pos);
	}

	/**
	 * @param objectName
	 * @param pos
	 */
	public OmnidriveVehicleAgentObject(String objectName, Position pos) {
		super(objectName, "OmnidriveVehicleAgent", pos);
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
			double speedBackWheel = Double.parseDouble(action.getParameter(2));
			if (Math.abs(speedLeftWheel) + Math.abs(speedRightWheel) + Math.abs(speedBackWheel) > 3*speedLimit) { // speed limit ;-)
				double fak = 3*speedLimit / (Math.abs(speedLeftWheel) + Math.abs(speedRightWheel) + Math.abs(speedBackWheel));
				speedLeftWheel *= fak;
				speedRightWheel *= fak;
				speedBackWheel *= fak;
			}
			moveAStep(speedLeftWheel, speedRightWheel, speedBackWheel);
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
			new MPerceptionValue("LIGHT_SENSOR_LEFT", Double.toString(sensors.get(0).getBrightness())));
		actionResp.addBodyPropertyChange(
			new MPerceptionValue("LIGHT_SENSOR_RIGHT", Double.toString(sensors.get(1).getBrightness())));
		actionResp.addBodyPropertyChange(
			new MPerceptionValue("LIGHT_SENSOR_FRONT", Double.toString(sensors.get(2).getBrightness())));
	}

	/**
	 * 
	 */
	protected void moveAStep(double speedLeftWheel, double speedRightWheel, double speedBackWheel) {
		double r = 1;
    
		double vy=-1*(speedLeftWheel-speedRightWheel)*r/Math.sqrt(3);
		double vx=((speedBackWheel-speedLeftWheel)*r+0.5*(speedLeftWheel-speedRightWheel)*r)/(-1.5);
		WorldVector effortVec = new WorldVector (vx, vy, 0);
		effortVec.rotate(getOrientationAngle());
		double rotationAngle = Math.toDegrees((speedBackWheel*r+vx)/ wheelDistance);
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
		sensors = new ArrayList<LightSensor>(3);
		sensors.add(new LightSensor(-0.43, -0.25));
		sensors.add(new LightSensor(0.43, -0.25));
		sensors.add(new LightSensor(0, 0.5));
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
