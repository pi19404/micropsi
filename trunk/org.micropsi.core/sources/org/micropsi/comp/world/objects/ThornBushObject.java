/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/ThornBushObject.java,v 1.3 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.world.objects;


import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.WorldMessage;
import org.w3c.dom.Element;

/**
 *  $Header $
 *  @author henning
 *
 */
public class ThornBushObject extends PlantObject {


	/**
	 * Constructor for ThornBushObject.
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public ThornBushObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public ThornBushObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(0.5, 1, 0.8);
		weight = 10;
		waterContent = 1;
		maxHeight = 12;
		maxDamage = 1200;
		maxWaterContent = 50;
		growRate = 0.2;
	
	}

	protected void initObjectState() {
		super.initObjectState();
	}
	

	/**
	 * @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(WorldMessage)
	 */
	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);

	}
}