package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.ConsumeResponseMessage;
import org.micropsi.comp.world.messages.ObjectCreationMessage;
import org.w3c.dom.Element;

public class GoodMineralObject extends AbstractCommonMarsianObject implements MarsianEatableIF {

	private double energyContent;
	private double waterContent;
	
	/**
	 * Constructor for BananaObject.
	 * @param objectName
	 * @param poss
	 */
	public GoodMineralObject(
		String objectName,
		String objectClass,
		Position pos) {
		super(objectName, objectClass, pos);
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		energyContent = 50;
		waterContent = 0;
		setWeight(1);
		setSize(0.5, 0.2, 1);
	}

	/**
	 * Constructor for BananaObject.
	 * @param configData
	 * @throws MicropsiException
	 */
	public GoodMineralObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#setNutrientContent(double)*/
	public void setEnergyContent(double nutrient) {
		energyContent = nutrient;
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#setWaterContent(double)*/
	public void setWaterContent(double water) {
		waterContent = water;
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processEatAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processEatAction(AbstractWorldMessage m) {
		m.answer(new ConsumeResponseMessage(energyContent, 0, 0, this));
		if (world.getDynamicLevel() >= 1) {
			ObjectCreationMessage message = new ObjectCreationMessage(getObjectName(), getObjectClass(), getClass().getName(), getPosition(), getOptionalProperties(), null);
			world.getPostOffice().send(message, world.getObjectCreator(), world.getSimStep() + 600);
			world.removeObject(this);
		}
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processDrinkAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processDrinkAction(AbstractWorldMessage m) {
		// can't drink GoodMinerals
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#getNutrientContent()*/
	public double getEnergyContent() {
		return energyContent;
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#getWaterContent()*/
	public double getWaterContent() {
		return waterContent;
	}

}
