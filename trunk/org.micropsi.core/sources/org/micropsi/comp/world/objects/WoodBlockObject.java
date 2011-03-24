package org.micropsi.comp.world.objects;


import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.w3c.dom.Element;

/**
 * @author Joscha
 *
 */
public class WoodBlockObject extends AbstractCommonObject {

	public WoodBlockObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public WoodBlockObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	
	protected void initProperties() {
		super.initProperties();
		}
	
	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(0.8, 0.8, 1);
		weight = 540;
		maxDamage = 9000;
	}

	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);
	}
	
}
