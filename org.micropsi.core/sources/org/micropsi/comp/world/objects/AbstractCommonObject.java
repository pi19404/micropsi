/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/AbstractCommonObject.java,v 1.3 2006/01/22 18:12:06 fuessel Exp $
 */
package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.w3c.dom.Element;

/**
 * @author matthias
 *
 */
public class AbstractCommonObject extends AbstractObject {
	protected int damage = 0;
	protected int maxDamage = 100;
	
	public AbstractCommonObject(Element configData, Logger logger) throws MicropsiException{
		super(configData, logger);
	}

	public AbstractCommonObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}
	
	
	// initialize
	
	protected void initProperties() {
		super.initProperties();
		if (this instanceof EatableIF) {
			addOptionalProperty(new AbstractPropertyAccessor("nutrient content", ObjectProperty.VTYPE_DOUBLE) {
				protected boolean _setProperty(ObjectProperty prop) {
					((EatableIF) AbstractCommonObject.this).setNutrientContent(prop.getDoubleValue());
					return true;
				}
				protected String getValue() {
					return Double.toString(((EatableIF) AbstractCommonObject.this).getNutrientContent());
				}
				
			});
			addOptionalProperty(new AbstractPropertyAccessor("water content", ObjectProperty.VTYPE_DOUBLE) {
				protected boolean _setProperty(ObjectProperty prop) {
					((EatableIF) AbstractCommonObject.this).setWaterContent(prop.getDoubleValue());
					return true;
				}
				protected String getValue() {
					return Double.toString(((EatableIF) AbstractCommonObject.this).getWaterContent());
				}
				
			});
		}
	}
	
	
	// external
	
	public void handleMessageAgentAction(AbstractWorldMessage m) {
		if (isHighLevelObject() && originatorsMatchSubParts(m)) {
			if (this instanceof EatableIF) {
				if (m.isContent("eat")) {
					((EatableIF) this).processEatAction(m);
				} else if (m.isContent("drink")) {
					((EatableIF) this).processDrinkAction(m);
				} else {
					super.handleMessageAgentAction(m);
				}
			} else {
				super.handleMessageAgentAction(m);
			}
		} else {
			super.handleMessageAgentAction(m);
		}
	}

	/**
	 * @param action
	 */
	protected void breakToPeaces(String action) {
		world.removeObject(this);
	}

	/**
	 * @param d
	 */
	public void setDamage(int damage) {
		this.damage = damage;		
	}

	/**
	 * @param d
	 */
	public void setMaxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
	}
	
	public void takeDamage(int damage, String action) {
		this.damage += damage;
		if (damage > maxDamage) {
			breakToPeaces(action);
		}
	}

}