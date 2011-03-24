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
public class TreeStumpObject extends AbstractCommonObject {

	public TreeStumpObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public TreeStumpObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}
	
	protected void initProperties() {
		super.initProperties();
		}
	
	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(0.7, 0.7, 0.3);
		weight = 340;
		maxDamage = 8000;
	}

	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);
	}
	
}
