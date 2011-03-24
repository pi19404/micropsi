/*
 * Created on 10.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.micropsi.comp.world.objects;

import org.micropsi.common.coordinates.Position;

/**
 * @author Joscha
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface LightSourceIF {
	
	public double getBrightnessForPosition(Position pos);

}
