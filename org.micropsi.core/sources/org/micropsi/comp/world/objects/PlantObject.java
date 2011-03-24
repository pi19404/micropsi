/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/PlantObject.java,v 1.3 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ChangeLogEntry;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.World;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.WorldMessage;
import org.w3c.dom.Element;

/**
 *  $Header $
 *  @author henning
 *
 */
public class PlantObject extends AbstractCommonObject {
	protected double growRate;
	protected double maxHeight;
	protected int maxWaterContent;
	protected int waterContent;

	public PlantObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	/**
	 * Constructor for PlantObject
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public PlantObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);

	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("grow rate", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setGrowRate(prop.getDoubleValue());
				return true;
			}
			protected String getValue() {
				return Double.toString(getGrowRate());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("max height", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setMaxHeight(prop.getDoubleValue());
				return true;
			}
			protected String getValue() {
				return Double.toString(getMaxHeight());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("water content", ObjectProperty.VTYPE_INT) {
			protected boolean _setProperty(ObjectProperty prop) {
				setWaterContent(prop.getIntValue());
				return true;
			}
			protected String getValue() {
				return Integer.toString(getWaterContent());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("max water content", ObjectProperty.VTYPE_INT) {
			protected boolean _setProperty(ObjectProperty prop) {
				setMaxWaterContent(prop.getIntValue());
				return true;
			}
			protected String getValue() {
				return Integer.toString(getMaxWaterContent());
			}
		});
	}

	/**
	 * @return double
	 */
	public double getGrowRate() {
		return growRate;
	}
	
	public double getHeight() {
		return getZSize();
	}

	/**
	 * @return double
	 */
	public double getMaxHeight() {
		return maxHeight;
	}

	/**
	 * @return int
	 */
	public int getMaxWaterContent() {
		return maxWaterContent;
	}

	/**
	 * @return int
	 */
	public int getWaterContent() {
		return waterContent;
	}

	/**
	 * @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(WorldMessage)
	 */
	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);

/*
		// handle Message "burn"
		if (m.getMessageClass() == "burn" && this.state.stateExists("INFLAMMABLE")) {
			actWather = (int) Math.max(0, actWather - ((actHeight / maxHeight) * maxWather) / 12);
			hitPoints = (int) Math.max(0, hitPoints - ((actHeight / maxHeight) * maxHitPoints) / 20);

			if ((actWather < (((actHeight / maxHeight) * maxWather) / 2))
				&& (!this.state.stateExists("BURNING"))) {
				this.state.addState("BURNING", "1");
				try {
					world.registerMulticastTickHandler(this, "mediumresolutiontick");
				} catch (MicropsiException e) {
					world.getLogger().info("could not add medium resolution timer");
				}

				return;
			}
		}
		// is Plant Burning
		if (m.getMessageClass() == "mediumresolutiontick" && this.state.stateExists("BURNING")) {

			world.getLogger().debug(
				"Round " + this.world.getSimStep() + ":" + this.getObjectName() + " is burning!");

			actWather = (int) Math.max(0, actWather - ((actHeight / maxHeight) * maxWather) / 6);
			hitPoints = (int) Math.max(0, hitPoints - ((actHeight / maxHeight) * maxHitPoints) / 10);

			// send a BURN-WorldMessage by the LocationBasedMessageDistributor
			LocationBasedMessageDistributor.sendMessage(
				new WorldMessage("burn", (ArrayList) null, null),
				this.getPosition(),
				this.actHeight * 2);

			return;

		}

		// handle Message "lowResTick"
		if (m.getMessageClass() == "lowresolutiontick") {
			actHeight =
				Math.min(
					maxHeight,
					(actHeight
						* (1 + (growRate * (actWather / ((actHeight / maxHeight) * maxWather))))));
			actWather = Math.max(0, actWather - (((actHeight / maxHeight) * maxWather) / 4));
			hitPoints =
				(int) Math.min(
					(hitPoints + ((actHeight / maxHeight) * maxHitPoints)),
					((actHeight / maxHeight) * maxHitPoints));

			world.getLogger().debug("object" + this.getObjectName());
			world.getLogger().debug(
				"\n wather: "
					+ actWather
					+ "\n hight: "
					+ actHeight
					+ "\n hitpoints: "
					+ hitPoints
					+ "\n");
			return;
		}

		if (m.getMessageClass() == "rain") {
			actWather = Math.min(maxWather, (actWather + Integer.parseInt(m.getMessageContend(0))));

		}

		// handle Message "lightning"
	
		if (m.getMessageClass() == "lightning" && this.state.stateExists("INFLAMMABLE")) {
			world.getLogger().debug(
				"Round "
					+ this.world.getSimStep()
					+ ": A lightning strikes "
					+ this.getObjectName()
					+ "!");
			actWather = (int) Math.max(0, actWather - ((actHeight / maxHeight) * maxWather) / 4);
			hitPoints = (int) Math.max(0, hitPoints - ((actHeight / maxHeight) * maxHitPoints) / 8);

			if ((actWather < (((actHeight / maxHeight) * maxWather) / 2))
				&& (!this.state.stateExists("BURNING"))) {
				this.state.addState("BURNING", "1");
				try {
					world.registerMulticastTickHandler(this, "mediumresolutiontick");
				} catch (MicropsiException e) {
					world.getLogger().info("could not add medium resolution timer");
				}

				return;
			}
		}
*/
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObject#init(org.micropsi.comp.world.World)
	 */
	public void init(World world) {
		super.init(world);
		world.getLowResolutionTimer().subscribe(this);
	}
	
	protected void initObjectParameters() {
		super.initObjectParameters();
		waterContent = 0;
		maxWaterContent = 100;
		maxHeight = 10;
		growRate = 0;
	}

	/**
	 * Sets the growRate.
	 * @param growRate The growRate to set
	 */
	public void setGrowRate(double growRate) {
		this.growRate = growRate;
	}

	/**
	 * Sets the maxHeight.
	 * @param maxHeight The maxHeight to set
	 */
	public void setMaxHeight(double maxHight) {
		this.maxHeight = maxHight;
	}

	/**
	 * Sets the maxWather.
	 * @param maxWather The maxWather to set
	 */
	public void setMaxWaterContent(int maxWater) {
		this.maxWaterContent = maxWater;
	}

	/**
	 * Sets the actWather.
	 * @param actWather The actWather to set
	 */
	public void setWaterContent(int waterContent) {
		this.waterContent = waterContent;
		logChange(ChangeLogEntry.CT_CHANGE_OTHER);
	}

	public String toString() {
		return super.toString()
			+ "\nwater content: "
			+ waterContent
			+ "\nmax water content: "
			+ maxWaterContent
			+ "\nmax height: "
			+ maxHeight
			+ "\ngrow rate: "
			+ growRate
			+ "\n";
	}

}
