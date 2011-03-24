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
public interface MarsianEatableIF {
	
	double getEnergyContent();
	double getWaterContent();
	void setEnergyContent(double nutrient);
	void setWaterContent(double water);
	
	void processEatAction(AbstractWorldMessage m);
	void processDrinkAction(AbstractWorldMessage m);

}
