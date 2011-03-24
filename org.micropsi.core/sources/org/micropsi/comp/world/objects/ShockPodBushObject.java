/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/ShockPodBushObject.java,v 1.3 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.world.objects;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.WorldMessage;
import org.w3c.dom.Element;

/**
 *  $Header $
 *  @author henning
 *
 */
public class ShockPodBushObject extends PlantObject {

	private int shockPods;
	private int maxShockPods;
	private int shockPodDamage;

	/**
	 * Constructor for ShockPodBushObject.
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public ShockPodBushObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public ShockPodBushObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("shock pods", ObjectProperty.VTYPE_INT) {
			protected boolean _setProperty(ObjectProperty prop) {
				setShockPods(prop.getIntValue());
				return true;
			}
			protected String getValue() {
				return Integer.toString(getShockPods());
			}
		});
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(1, 1.2, 0.8);
		maxDamage = 80;
		waterContent = 1;
		maxHeight = 1.2;
		maxWaterContent = 10;
		growRate = 0.2;
		shockPods = 10;
		shockPodDamage = 2;
		weight = 8;

	}

	protected void initObjectState() {
		super.initObjectState();
	}

	/**
	 * @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(WorldMessage)
	 */
	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);

		// handle Message "lowResTick"
		if (m.isContent("lowresolutiontick")) {
			shockPods = Math.min(maxShockPods, shockPods + 5);
			if (!world.getHighResolutionTimer().isSubscribed(this)) {
				world.getHighResolutionTimer().subscribe(this);
			}
		}
		if (m.isContent("highresolutiontick")) {
			Set objects;
			objects = world.getObjectsByPosition(this.getPosition(), 5.0);
			Iterator iter = objects.iterator();
			while (iter.hasNext()) {
				AbstractObject o = (AbstractObject) iter.next();
				if (o instanceof AgentObjectIF) {
					AbstractWorldMessage shockPodHit =
						new WorldMessage("ENVIRONMENT_EVENT", "hit", this);
					shockPodHit.addParameter(new Integer(this.shockPods * this.shockPodDamage));
					world.getPostOffice().send(shockPodHit, o);
					shockPods = 0;
				}

			}
			if (shockPods == 0) {
				world.getHighResolutionTimer().unsubscribe(this);
			}

		}
	}
	/**
	 * Returns the shockPods.
	 * @return int
	 */
	public int getShockPods() {
		return shockPods;
	}

	/**
	 * Sets the shockPods.
	 * @param shockPods The shockPods to set
	 */
	public void setShockPods(int shockPods) {
		this.shockPods = shockPods;
	}

	/**
	 * Returns the maxShockPods.
	 * @return int
	 */
	public int getMaxShockPods() {
		return maxShockPods;
	}

	/**
	 * Returns the shockPodDamage.
	 * @return int
	 */
	public int getShockPodDamage() {
		return shockPodDamage;
	}

}