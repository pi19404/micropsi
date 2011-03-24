package org.micropsi.comp.world.objects;


import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ChangeLogEntry;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.ConsumeResponseMessage;
import org.w3c.dom.Element;

/**
 * @author Joscha
 *
 */
public class PuddleObject extends AbstractCommonObject {

	protected double waterContent;
	protected double maxWaterContent;

	public PuddleObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public PuddleObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("water content", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setWaterContent(prop.getDoubleValue());
				return true;
			}
			protected String getValue() {
				return Double.toString(getWaterContent());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("max water content", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setMaxWaterContent(prop.getDoubleValue());
				return true;
			}
			protected String getValue() {
				return Double.toString(getMaxWaterContent());
			}
		});
	}
	
	protected void initObjectParameters() {
		super.initObjectParameters();
		waterContent = 400;
		maxWaterContent = 800;
		setSize(10, 10, 1);
		weight = maxWaterContent;
	}

	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);
		if (m.isContent("drink")) {
			m.answer(new ConsumeResponseMessage(0, 10, 0, null));
		}
	}
	
	
	/**
	 * Returns the waterContent.
	 * @return double
	 */
	public double getWaterContent() {
		return waterContent;
	}

	/**
	 * Returns the maxWaterContent.
	 * @return double
	 */
	public double getMaxWaterContent() {
		return maxWaterContent;
	}

	/**
	 * Sets the waterContent.
	 * @param waterContent The waterContent to set
	 */
	public void setWaterContent(double water) {
		this.waterContent = water;
		logChange(ChangeLogEntry.CT_CHANGE_OTHER);
	}

	/**
	 * Sets the maxWaterContent.
	 * @param maxWaterContent The maxWaterContent to set
	 */
	public void setMaxWaterContent(double maxWater) {
		this.maxWaterContent = maxWater;
	}

}
