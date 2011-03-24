/*
 * Created on 03.08.2004
 *
 */

package org.micropsi.comp.world.objects;

/**
 * @author Matthias
 *
 */
public class ObjectShape {
	public static ObjectShape sphere = new ObjectShape("Sphere");
	public static ObjectShape upperHalfSphere = new ObjectShape("Upper half sphere");
	public static ObjectShape LowerHalfSphere = new ObjectShape("Lower half sphere");
	public static ObjectShape cone = new ObjectShape("Cone");
	public static ObjectShape cylinder = new ObjectShape("Cylinder");
	public static ObjectShape box = new ObjectShape("Box");
	
	private String shape = null;

	/**
	 * @param shape
	 */
	public ObjectShape(String shape) {
		this.shape = shape;
	}
	
	/**
	 * @return Returns the shape.
	 */
	public String getShape() {
		return shape;
	}
	
	/**
	 * @param shape The shape to set.
	 */
	public void setShape(String shape) {
		this.shape = shape;
	}
}
