/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/HazelObject.java,v 1.1 2005/01/30 23:05:17 jbach Exp $
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
public class HazelObject extends PlantObject {


	/**
	 * Constructor for HazelObject.
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public HazelObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public HazelObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(2, 2, 3);
		weight = 150;
		waterContent = 170;
		maxHeight = 10;
		maxDamage = 700;
		maxWaterContent = 380;
		growRate = 0.6;
	
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