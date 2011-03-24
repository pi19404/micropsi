/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 10.01.2004
 *
 */
package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.w3c.dom.Element;


/**
 * @author Matthias
 *
 */
public class LeafObject extends AbstractObjectPart {

	/**
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public LeafObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public LeafObject(String objectClass, Position pos) {
		super(objectClass, pos);
	}

}
