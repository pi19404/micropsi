/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.world.mars/src/org/micropsi/comp/world/objects/TransformerObject.java,v 1.1 2006/01/23 15:10:46 fuessel Exp $
 */
package org.micropsi.comp.world.objects;

import java.util.Set;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.World;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.WorldMessage;
import org.w3c.dom.Element;

public class TransformerObject extends AbstractCommonMarsianObject {
	
	private boolean working;
	private int damageRadius;
	private int damageAmount;
	private int outOfOrderTime;
	
	protected long timeResumeWork = -1;

	public TransformerObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public TransformerObject(Element configData, Logger logger) throws MicropsiException {
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
		addOptionalProperty(new AbstractPropertyAccessor("damageRadius", ObjectProperty.VTYPE_BOOLEAN) {
			protected boolean _setProperty(ObjectProperty prop) {
				setDamageRadius(prop.getIntValue());
				return true;
			}
			protected String getValue() {
				return Integer.toString(getDamageRadius());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("damageAmount", ObjectProperty.VTYPE_BOOLEAN) {
			protected boolean _setProperty(ObjectProperty prop) {
				setDamageAmount(prop.getIntValue());
				return true;
			}
			protected String getValue() {
				return Integer.toString(getDamageAmount());
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
		setSize(3, 1, 2);
		maxDamage = 800;
		setWeight(8);
		setWorking(true);
		setDamageRadius(6);
		setDamageAmount(1);
		setOutOfOrderTime(78);
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

		if (m.isContent("highresolutiontick") && isWorking()) {
			Set objects = world.getObjectsByPosition(getPosition(), 5.0);
			for (Object o : objects) {
				if (o instanceof MarsianAgentObject) {
					MarsianAgentObject agent = (MarsianAgentObject) o;
					AbstractWorldMessage repairMessage =
						new WorldMessage("ENVIRONMENT_EVENT", "DAMAGE", this);
					repairMessage.addParameter(getDamageAmount());
					world.getPostOffice().send(repairMessage, agent);
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
	 * @return Returns the repairAmount.
	 */
	public int getDamageAmount() {
		return damageAmount;
	}

	/**
	 * @param repairAmount The repairAmount to set.
	 */
	public void setDamageAmount(int repairAmount) {
		this.damageAmount = repairAmount;
	}

	/* @see org.micropsi.comp.world.objects.AbstractObject#init(org.micropsi.comp.world.World)*/
	@Override
	public void init(World world) {
		super.init(world);
		world.getHighResolutionTimer().subscribe(this);
	}

	/**
	 * @return Returns the repairRadius.
	 */
	public int getDamageRadius() {
		return damageRadius;
	}

	/**
	 * @param repairRadius The repairRadius to set.
	 */
	public void setDamageRadius(int repairRadius) {
		this.damageRadius = repairRadius;
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
	
	

}