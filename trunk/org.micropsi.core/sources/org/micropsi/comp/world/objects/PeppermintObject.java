package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.ConsumeResponseMessage;
import org.w3c.dom.Element;

/**
 * @author Henning
 *
 */
public class PeppermintObject extends AbstractCommonObject implements EatableIF {

	private double kiloJoules;
	private double liters;
	private int healingPoints;

	/**
	 * Constructor for Peppermint.
	 * @param objectName
	 * @param poss
	 */
	public PeppermintObject(
		String objectName,
		String objectClass,
		Position pos) {
		super(objectName, objectClass, pos);
	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("healing points", ObjectProperty.VTYPE_INT) {
			protected boolean _setProperty(ObjectProperty prop) {
				setHealingPoints(prop.getIntValue());
				return true;
			}
			protected String getValue() {
				return Integer.toString(getHealingPoints());
			}
			
		});
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		kiloJoules = 4;
		liters = 1;
		healingPoints = -60;
		maxDamage = 20;
		setWeight(0.1);
		setSize(0.3, 0.5, 0.2);
	}

	/**
	 * Constructor for Peppermint.
	 * @param configData
	 * @throws MicropsiException
	 */
	public PeppermintObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
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
	public int getHealingPoints() {
		return healingPoints;
	}

	/**
	 * Sets the poisionDamage.
	 * @param poisionDamage The poisionDamage to set
	 */
	public void setHealingPoints(int healingPoints) {
		this.healingPoints = healingPoints;
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processEatAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processEatAction(AbstractWorldMessage m) {
		m.answer(new ConsumeResponseMessage(kiloJoules, liters, -getHealingPoints(), this));
		if (world.getDynamicLevel() >= 1) {
			world.removeObject(this);
		}
	}

	/* @see org.micropsi.comp.world.objects.EatableIF#processDrinkAction(org.micropsi.comp.world.messages.AbstractWorldMessage)*/
	public void processDrinkAction(AbstractWorldMessage m) {
		// can't drink peppermint
	}

}
