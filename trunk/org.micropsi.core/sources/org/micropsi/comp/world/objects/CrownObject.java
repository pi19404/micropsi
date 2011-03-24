/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 13.01.2004
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
public class CrownObject extends AbstractObjectPart {

	/**
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public CrownObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	/**
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public CrownObject(String objectClass, Position pos) {
		super(objectClass, pos);
	}

	public void addLeaves(String leafClass, int count) {
		for (int i = 0; i < count; i++) {
			Position pos = getRandomSubposition();
			LeafObject leaf = new LeafObject(leafClass, pos);
			addSubPart(leaf);
		}
	}

	/**
	 * @return
	 */
	protected Position getRandomSubposition() {
		double x = Math.random() * getXSize() + getPosition().getX() - getXSize()/2;
		double y = Math.random() * getYSize() + getPosition().getY() - getYSize()/2;
		double z = Math.random() * getZSize() + getPosition().getZ() - getZSize()/2;
		Position pos = new Position(x, y, z);
		return pos;
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractHierarchicObject#initObjectParameters()
	 */
	protected void initObjectParameters() {
		super.initObjectParameters();
		setSize(2, 2, 2);
	}
}
