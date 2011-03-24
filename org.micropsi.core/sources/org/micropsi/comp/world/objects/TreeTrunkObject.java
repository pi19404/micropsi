/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 18.01.2004
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
public class TreeTrunkObject extends AbstractObjectPart {

	/**
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public TreeTrunkObject(Element configData, Logger logger)
			throws MicropsiException {
		super(configData, logger);
	}

	/**
	 * @param objectClass
	 * @param pos
	 */
	public TreeTrunkObject(String objectClass, Position pos) {
		super(objectClass, pos);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractHierarchicObject#initObjectParameters()
	 */
	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(5, 0.3, 0.3);
	}
}
