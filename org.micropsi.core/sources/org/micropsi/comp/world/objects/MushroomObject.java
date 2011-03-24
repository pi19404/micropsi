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
 * @author Henning
 *
 */
public class MushroomObject extends AbstractCommonObject implements EatableIF {

	private double kiloJoules;
	private double liters;
	private int poisionDamage;

	/**
	 * Constructor for MushroomObject.
	 * @param objectName
	 * @param poss
	 */
	public MushroomObject(
		String objectName,
		String objectClass,
		Position pos) {
		super(objectName, objectClass, pos);
		this.initObjectParameters();
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		kiloJoules = 20;
		liters = 0.1;
		poisionDamage = 0;
		maxDamage = 10;
		setWeight(0.1);
		setSize(0.15, 0.15, 0.2);
	}

	/**
	 * Constructor for MushroomObject.
	 * @param configData
	 * @throws MicropsiException
	 */
	public MushroomObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	


	/**
	 * @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(org.micropsi.comp.world.messages.WorldMessage)
	 */
	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);
		if (m.isContent("eat")) {
			m.answer(new ConsumeResponseMessage(kiloJoules, liters, poisionDamage, this));
			if (world.getDynamicLevel() >= 1) {
				world.removeObject(this);
			}
		}
	}

	/**
	 * @return int
	 */
	public double getWaterContent() {
		return liters;
	}

	/**
	 * Sets the liters.
	 * @param liters The liters to set
	 */
	public void setWaterContent(double liters) {
		this.liters = liters;
	}

	/**
	 * @return int
	 */
	public double getNutrientContent() {
		return kiloJoules;
	}

	/**
	 * Sets the kiloJoules.
	 * @param kiloJoules The kiloJoules to set
	 */
	public void setNutrientContent(double kiloJoules) {
		this.kiloJoules = kiloJoules;
	}

	/**
	 * @return int
	 */
	public int getPoisonDamage() {
		return poisionDamage;
	}

	/**
	 * Sets the poisionDamage.
	 * @param poisionDamage The poisionDamage to set
	 */
	public void setPoisonDamage(int poisionDamage) {
		this.poisionDamage = poisionDamage;
		logChange(ChangeLogEntry.CT_CHANGE_OTHER);
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processEatAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processEatAction(AbstractWorldMessage m) {
		m.answer(new ConsumeResponseMessage(kiloJoules, liters, poisionDamage, this));
		if (world.getDynamicLevel() >= 1) {
			ObjectCreationMessage message = new ObjectCreationMessage(getObjectName(), getObjectClass(), getClass().getName(), getPosition(), getOptionalProperties(), null);
			world.getPostOffice().send(message, world.getObjectCreator(), world.getSimStep() + 600);
			world.removeObject(this);
		}
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processDrinkAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processDrinkAction(AbstractWorldMessage m) {
		// can't drink Mushroom
	}

}
