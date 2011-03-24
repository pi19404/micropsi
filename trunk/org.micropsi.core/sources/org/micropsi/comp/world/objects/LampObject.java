/*
 * Created on 10.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ObjectProperty;
import org.w3c.dom.Element;

/**
 * @author Joscha
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LampObject extends AbstractCommonObject implements LightSourceIF {
	
	private double brightness = 100;
	private double lightSourceLowAngle = 0;
	private double lightSourceHighAngle = 360;

	/**
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public LampObject(Element configData, Logger logger)
		throws MicropsiException {
		super(configData, logger);
	}

	/**
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public LampObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("brightness", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setBrightness(prop.getDoubleValue());
				return true;
			}
			protected String getValue() {
				return Double.toString(getBrightness());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("light_low_angle", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setLightSourceLowAngle(prop.getDoubleValue());
				return false;
			}
			protected String getValue() {
				return Double.toString(getLightSourceLowAngle());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("light_high_angle", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setLightSourceHighAngle(prop.getDoubleValue());
				return false;
			}
			protected String getValue() {
				return Double.toString(getLightSourceHighAngle());
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.LightSourceIF#getBrightnessForPosition(org.micropsi.common.coordinates.Position)
	 */
	public double getBrightnessForPosition(Position pos) {
		WorldVector lightVec = pos.getDifferenceVector(getPosition());
		double lightAngle = lightVec.getAngle();
		if (lightAngle < lightSourceLowAngle || lightAngle > lightSourceHighAngle) {
			return 0;
		} else {
			double dist = lightVec.getLength() + 1;
			return getBrightness()/dist/dist;
		}
	}

	/**
	 * @return
	 */
	public double getBrightness() {
		return brightness;
	}

	/**
	 * @return
	 */
	public double getLightSourceHighAngle() {
		return lightSourceHighAngle;
	}

	/**
	 * @return
	 */
	public double getLightSourceLowAngle() {
		return lightSourceLowAngle;
	}

	/**
	 * @param d
	 */
	public void setBrightness(double d) {
		brightness = d;
	}

	/**
	 * @param d
	 */
	public void setLightSourceHighAngle(double d) {
		lightSourceHighAngle = d;
	}

	/**
	 * @param d
	 */
	public void setLightSourceLowAngle(double d) {
		lightSourceLowAngle = d;
	}

}
