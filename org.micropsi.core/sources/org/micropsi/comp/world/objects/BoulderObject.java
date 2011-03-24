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
public class BoulderObject extends AbstractCommonObject {

	public BoulderObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public BoulderObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	
	protected void initProperties() {
		super.initProperties();
		}
	
	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(1, 1, 1);
		weight = 80;
		maxDamage = 1000;
	}

	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);
	}
	
}
