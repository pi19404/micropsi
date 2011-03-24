/*
 * Created on 05.08.2004
 *
 */

package org.micropsi.comp.world.objects;

import org.micropsi.comp.world.messages.AbstractWorldMessage;

/**
 * @author Matthias
 *
 */
public interface EatableIF {
	
	double getNutrientContent();
	double getWaterContent();
	void setNutrientContent(double nutrient);
	void setWaterContent(double water);
	
	void processEatAction(AbstractWorldMessage m);
	void processDrinkAction(AbstractWorldMessage m);

}
