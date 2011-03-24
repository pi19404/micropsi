/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/Circle.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.world;

import org.micropsi.common.coordinates.Position;

/**
 * @author Gregor
 *
 */
public class Circle {
	private Position position;
	private double radius;

	public Circle(Position position, double radius) {
		this.position = position;
		this.radius = radius;
	}

	public boolean contains(Position position) {

		double xDiff = this.position.getX() - position.getX();
		double yDiff = this.position.getY() - position.getY();

		if (Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2)) < radius) {
			return true;
		}
		return false;
	}

}
