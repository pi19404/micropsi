package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.ChangeLogEntry;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.ConsumeResponseMessage;
import org.micropsi.comp.world.messages.ObjectCreationMessage;
import org.w3c.dom.Element;

/**
 * @author Joscha
 *
 */
public class RedMushroomObject extends AbstractCommonObject implements EatableIF {

	private double nutrientContent;
	private double waterContent;
	private int poisonDamage;

	/**
	 * Constructor for RedMushroomObject.
	 * @param objectName
	 * @param poss
	 */
	public RedMushroomObject(
		String objectName,
		String objectClass,
		Position pos) {
		super(objectName, objectClass, pos);
		this.initObjectParameters();
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		nutrientContent = 30;
		waterContent = 0.1;
		poisonDamage = 3;
		maxDamage = 5;
		setWeight(0.1);
		setSize(0.1, 0.1, 0.2);
	}

	/**
	 * Constructor for FlyAgaricObject.
	 * @param configData
	 * @throws MicropsiException
	 */
	public RedMushroomObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	


	/**
	 * @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(org.micropsi.comp.world.messages.WorldMessage)
	 */
	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);
		if (m.isContent("eat")) {
			m.answer(new ConsumeResponseMessage(nutrientContent, waterContent, poisonDamage, this));
			if (world.getDynamicLevel() >= 1) {
				world.removeObject(this);
			}
		}
	}

	/**
	 * @return int
	 */
	public double getWaterContent() {
		return waterContent;
	}

	/**
	 * Sets the waterContent.
	 * @param waterContent The waterContent to set
	 */
	public void setWaterContent(double liters) {
		this.waterContent = liters;
		logChange(ChangeLogEntry.CT_CHANGE_OTHER);
	}

	/**
	 * @return int
	 */
	public double getNutrientContent() {
		return nutrientContent;
	}

	/**
	 * Sets the nutrientContent.
	 * @param nutrientContent The nutrientContent to set
	 */
	public void setNutrientContent(double kiloJoules) {
		this.nutrientContent = kiloJoules;
		logChange(ChangeLogEntry.CT_CHANGE_OTHER);
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processEatAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processEatAction(AbstractWorldMessage m) {
		m.answer(new ConsumeResponseMessage(nutrientContent, waterContent, poisonDamage, this));
		if (world.getDynamicLevel() >= 1) {
			ObjectCreationMessage message = new ObjectCreationMessage(getObjectName(), getObjectClass(), getClass().getName(), getPosition(), getOptionalProperties(), null);
			world.getPostOffice().send(message, world.getObjectCreator(), world.getSimStep() + 600);
			world.removeObject(this);
		}
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processDrinkAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processDrinkAction(AbstractWorldMessage m) {
		// can't drink fungus
	}
}
