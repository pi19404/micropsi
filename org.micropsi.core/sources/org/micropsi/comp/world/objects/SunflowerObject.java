/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/SunflowerObject.java,v 1.1 2005/01/08 03:15:33 jbach Exp $
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
public class SunflowerObject extends PlantObject {


	/**
	 * Constructor for SunflowerObject.
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public SunflowerObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public SunflowerObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(0.3, 0.3, 1.3);
		weight = 0.8;
		waterContent = 70;
		maxHeight = 2;
		maxDamage = 55;
		maxWaterContent = 180;
		growRate = 0.45;
	
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