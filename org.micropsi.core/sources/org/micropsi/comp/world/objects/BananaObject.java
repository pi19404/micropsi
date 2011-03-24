package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.ConsumeResponseMessage;
import org.micropsi.comp.world.messages.ObjectCreationMessage;
import org.w3c.dom.Element;

/**
 * @author Gregor
 *
 */
public class BananaObject extends AbstractCommonObject implements EatableIF, AgingIF {

	private double nutrientContent;
	private double waterContent;
	
	private int age = 0;

	/**
	 * Constructor for BananaObject.
	 * @param objectName
	 * @param poss
	 */
	public BananaObject(
		String objectName,
		String objectClass,
		Position pos) {
		super(objectName, objectClass, pos);
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		nutrientContent = 50;
		waterContent = 1;
		setWeight(0.2);
		setSize(0.5, 0.2, 0.1);
	}

	/**
	 * Constructor for BananaObject.
	 * @param configData
	 * @throws MicropsiException
	 */
	public BananaObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AgingIF#setAge(int)
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AgingIF#getAge()
	 */
	public int getAge() {
		return age;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AgingIF#becomeOlder(int)
	 */
	public void becomeOlder(int ageDiff) {
		setAge(getAge() + ageDiff);
	}
	
	public void drop() {
		if (getContainingPart() != null) {
			getContainingPart().removeSubPart(this);
		}
		double maxDiff = getPosition().getZ() / 5;
		Position pos = new Position(getPosition().getX() - maxDiff
				+ Math.random() * 2 * maxDiff, getPosition().getY() - maxDiff
				+ Math.random() * 2 * maxDiff, 0);
		moveTo(pos);
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#setNutrientContent(double)*/
	public void setNutrientContent(double nutrient) {
		nutrientContent = nutrient;
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#setWaterContent(double)*/
	public void setWaterContent(double water) {
		waterContent = water;
		
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processEatAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processEatAction(AbstractWorldMessage m) {
		m.answer(new ConsumeResponseMessage(nutrientContent, waterContent, 0, this));
		if (world.getDynamicLevel() >= 1) {
			ObjectCreationMessage message = new ObjectCreationMessage(getObjectName(), getObjectClass(), getClass().getName(), getPosition(), getOptionalProperties(), null);
			world.getPostOffice().send(message, world.getObjectCreator(), world.getSimStep() + 600);
			world.removeObject(this);
		}
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processDrinkAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processDrinkAction(AbstractWorldMessage m) {
		// can't drink Bananas
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#getNutrientContent()*/
	public double getNutrientContent() {
		return nutrientContent;
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#getWaterContent()*/
	public double getWaterContent() {
		return waterContent;
	}

}
