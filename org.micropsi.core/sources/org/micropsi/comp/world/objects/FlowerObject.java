/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/FlowerObject.java,v 1.1 2005/01/08 03:15:33 jbach Exp $
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
 *  @author Joscha
 *
 */
public class FlowerObject extends PlantObject {


	/**
	 * Constructor for FlowerObject.
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public FlowerObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public FlowerObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(0.1, 0.1, 0.6);
		weight = 10;
		waterContent = 20;
		maxHeight = 1;
		maxDamage = 15;
		maxWaterContent = 60;
		growRate = 0.15;
	
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