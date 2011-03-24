/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/NettleObject.java,v 1.1 2005/01/08 03:15:33 jbach Exp $
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
 *  @author Joscha
 *
 */
public class NettleObject extends PlantObject {

	private int nettlings;
	private int maxNettlings;
	private int nettleDamage;

	/**
	 * Constructor for ShockPodBushObject.
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public NettleObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public NettleObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	
	protected void initProperties() {
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("shock pods", ObjectProperty.VTYPE_INT) {
			protected boolean _setProperty(ObjectProperty prop) {
				setNettlings(prop.getIntValue());
				return true;
			}
			protected String getValue() {
				return Integer.toString(getNettlings());
			}
		});
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(0.3, 0.3, 0.8);
		maxDamage = 10;
		waterContent = 20;
		maxHeight = 1.2;
		maxWaterContent = 80;
		growRate = 0.4;
		nettlings = 10;
		nettleDamage = 2;
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
			nettlings = Math.min(maxNettlings, nettlings + 5);
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
					shockPodHit.addParameter(new Integer(this.nettlings * this.nettleDamage));
					world.getPostOffice().send(shockPodHit, o);
					nettlings = 0;
				}

			}
			if (nettlings == 0) {
				world.getHighResolutionTimer().unsubscribe(this);
			}

		}
	}
	/**
	 * Returns the nettlings.
	 * @return int
	 */
	public int getNettlings() {
		return nettlings;
	}

	/**
	 * Sets the nettlings.
	 * @param nettlings
	 */
	public void setNettlings(int nettlings) {
		this.nettlings = nettlings;
	}

	/**
	 * Returns the maxNettlings.
	 * @return int
	 */
	public int getMaxNettlings() {
		return maxNettlings;
	}

	/**
	 * Returns the nettleDamage.
	 * @return int
	 */
	public int getNettleDamage() {
		return nettleDamage;
	}

}