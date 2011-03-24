/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/LinkST.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

/**
 * Spacio-Temporal links have, additional to weight and confidence, parameters
 * for describing the relation of the linked entities in time and space.
 */
public class LinkST extends Link {

	public static final int LINKPARAM_T = 2;
	public static final int LINKPARAM_X = 3;
	public static final int LINKPARAM_Y = 4;
	public static final int LINKPARAM_Z = 5;

	private double x;
	private double y;
	private double z;
	private int t;	
	
	/**
	 * @see org.micropsi.nodenet.Link#Link(Gate, String, int, NetEntityManager, double, double)
	 */
	protected LinkST(Gate from, String to, int slot, NetEntityManager manager, double weight, double confidence) {
		super(from, to, slot, manager, weight, confidence);
	}
	
	/**
	 * @see org.micropsi.nodenet.Link#getType()
	 */
	public int getType() {
		return LinkTypesIF.LINKTYPE_SPACIOTEMPORAL;
	}	
	
	/**
	 * Returns the t (time) value.
	 * @return int
	 */
	public int getT() {
		return t;
	}

	/**
	 * Returns the x value.
	 * @return double
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y value.
	 * @return double
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the z value.
	 * @return double
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Sets the t.
	 * @param t The t to set
	 */
	protected void setT(int t) {
		this.t = t;
	}

	/**
	 * Sets the x.
	 * @param x The x to set
	 */
	protected void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y.
	 * @param y The y to set
	 */
	protected void setY(double y) {
		this.y = y;
	}

	/**
	 * Sets the z.
	 * @param z The z to set
	 */
	protected void setZ(double z) {
		this.z = z;
	}

}
