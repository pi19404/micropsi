/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.world.mars/src/org/micropsi/comp/world/objects/SolarStationObject.java,v 1.1 2006/01/23 15:10:46 fuessel Exp $
 */
package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.ConsumeResponseMessage;
import org.micropsi.comp.world.messages.WorldMessage;
import org.w3c.dom.Element;

public class SolarStationObject extends AbstractCommonMarsianObject implements MarsianEatableIF {
	
	private boolean working;
	private int outOfOrderTime;
	private double energyContent;
	private double waterContent;
	
	protected long timeResumeWork = -1;

	public SolarStationObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public SolarStationObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("working", ObjectProperty.VTYPE_BOOLEAN) {
			protected boolean _setProperty(ObjectProperty prop) {
				setWorking(prop.getBoolValue());
				return true;
			}
			protected String getValue() {
				return Boolean.toString(isWorking());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("outOfOrderTime", ObjectProperty.VTYPE_BOOLEAN) {
			protected boolean _setProperty(ObjectProperty prop) {
				setOutOfOrderTime(prop.getIntValue());
				return true;
			}
			protected String getValue() {
				return Integer.toString(getOutOfOrderTime());
			}
		});
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(5, 5, 1);
		maxDamage = 800;
		setWeight(800);
		setWorking(true);
		setOutOfOrderTime(55);
		setEnergyContent(5);
	}

	protected void initObjectState() {
		super.initObjectState();
	}

	/**
	 * @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(WorldMessage)
	 */
	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);
		if (m.isContent("hit")) {
			setWorking(false);
			timeResumeWork = world.getSimStep() + getOutOfOrderTime();
			world.getPostOffice().send(new WorldMessage("REMINDME", "resumework", this), this, timeResumeWork);
		}
		
		if (m.isClass("REMINDME")) {
			if (m.isContent("resumework")) {
				if (isAlive() && world.getSimStep() >= timeResumeWork) {
					setWorking(true);
				}
			}
		}
	}
	
	/**
	 * @return Returns the working.
	 */
	public boolean isWorking() {
		return working;
	}

	/**
	 * @param working The working to set.
	 */
	public void setWorking(boolean working) {
		this.working = working;
	}

	/**
	 * @return Returns the outOfOrderTime.
	 */
	public int getOutOfOrderTime() {
		return outOfOrderTime;
	}

	/**
	 * @param outOfOrderTime The outOfOrderTime to set.
	 */
	public void setOutOfOrderTime(int outOfOrderTime) {
		this.outOfOrderTime = outOfOrderTime;
	}

	public void processEatAction(AbstractWorldMessage m) {
		if (isWorking()) {
			m.answer(new ConsumeResponseMessage(getEnergyContent(), 0, 0, this));
		}
	}

	public void processDrinkAction(AbstractWorldMessage m) {
		// can't drink solar stations...
	}

	/**
	 * @return Returns the energyContent.
	 */
	public double getEnergyContent() {
		return energyContent;
	}

	/**
	 * @param energyContent The energyContent to set.
	 */
	public void setEnergyContent(double energyContent) {
		this.energyContent = energyContent;
	}

	/**
	 * @return Returns the waterContent.
	 */
	public double getWaterContent() {
		return waterContent;
	}

	/**
	 * @param waterContent The waterContent to set.
	 */
	public void setWaterContent(double waterContent) {
		this.waterContent = waterContent;
	}
	
	

}