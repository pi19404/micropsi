/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.world.mars/src/org/micropsi/comp/world/objects/AbstractCommonMarsianObject.java,v 1.1 2006/01/23 15:10:46 fuessel Exp $
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
public class AbstractCommonMarsianObject extends AbstractObject {
	protected int damage = 0;
	protected int maxDamage = 100;
	
	public AbstractCommonMarsianObject(Element configData, Logger logger) throws MicropsiException{
		super(configData, logger);
	}

	public AbstractCommonMarsianObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}
	
	
	// initialize
	
	protected void initProperties() {
		super.initProperties();
		if (this instanceof MarsianEatableIF) {
			addOptionalProperty(new AbstractPropertyAccessor("energy content", ObjectProperty.VTYPE_DOUBLE) {
				protected boolean _setProperty(ObjectProperty prop) {
					((MarsianEatableIF) AbstractCommonMarsianObject.this).setEnergyContent(prop.getDoubleValue());
					return true;
				}
				protected String getValue() {
					return Double.toString(((MarsianEatableIF) AbstractCommonMarsianObject.this).getEnergyContent());
				}
				
			});
			addOptionalProperty(new AbstractPropertyAccessor("water content", ObjectProperty.VTYPE_DOUBLE) {
				protected boolean _setProperty(ObjectProperty prop) {
					((MarsianEatableIF) AbstractCommonMarsianObject.this).setWaterContent(prop.getDoubleValue());
					return true;
				}
				protected String getValue() {
					return Double.toString(((MarsianEatableIF) AbstractCommonMarsianObject.this).getWaterContent());
				}
				
			});
		}
	}
	
	
	// external
	
	public void handleMessageAgentAction(AbstractWorldMessage m) {
		if (isHighLevelObject() && originatorsMatchSubParts(m)) {
			if (this instanceof MarsianEatableIF) {
				if (m.isContent("eat")) {
					((MarsianEatableIF) this).processEatAction(m);
				} else if (m.isContent("drink")) {
					((MarsianEatableIF) this).processDrinkAction(m);
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