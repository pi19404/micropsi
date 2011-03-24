/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/coordinates/Position.java,v 1.5 2005/01/30 21:36:21 fuessel Exp $
 */
package org.micropsi.common.coordinates;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;

public class Position {
	private double x, y, z;
	
	private static final NumberFormat numFormat = positionNumFormat();

	/**
	 * Constructor Position.
	 * @param position
	 */
	public Position(Position position) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
	}

	public Position(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}

	public Position(double x, double y) {
		this.x = x; this.y = y; this.z = 0;
	}
	
	public Position(String s) {
		if (s.startsWith("(") && s.endsWith(")")) {
			s = s.substring(1, s.length()-1);
		}
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		try {
			x = Double.parseDouble(tokenizer.nextToken());
			y = Double.parseDouble(tokenizer.nextToken());
			z = Double.parseDouble(tokenizer.nextToken());
		} catch (NoSuchElementException e) {
			throw new NumberFormatException();
		}
		if (tokenizer.hasMoreTokens()) {
			throw new NumberFormatException();
		}
	}
	
	public Position(Element configData) throws MicropsiException {
		try {
			x = Double.parseDouble(XMLElementHelper.getElementValueByTagName(configData, "x"));
			y = Double.parseDouble(XMLElementHelper.getElementValueByTagName(configData, "y"));
			z = Double.parseDouble(XMLElementHelper.getElementValueByTagName(configData, "z"));
		} catch (XMLElementNotFoundException e) {
			throw new MicropsiException(10, "Error reading Position setup file", e);
		}
	}
	
	public void set(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	public void set(double x, double y) {
		this.x = x; this.y = y; this.z = 0;
	}
	
	/**
	 * Returns the x.
	 * @return float
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y.
	 * @return float
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the z.
	 * @return float
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Sets the x.
	 * @param x The x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Sets the y.
	 * @param y The y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Sets the z.
	 * @param z The z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	public void set(Position pos) {
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}
	
	public double sqrDistance(Position pos) {
		double xdiff = getX() - pos.getX();
		double ydiff = getY() - pos.getY();
		double zdiff = getZ() - pos.getZ();
		return xdiff*xdiff + ydiff*ydiff + zdiff*zdiff; 
	}
	
	public double distance(Position pos) {
		return Math.sqrt(sqrDistance(pos));
	}
	
	public double sqrDistance2D(Position pos) {
		return (x-pos.x)*(x-pos.x) + (y-pos.y)*(y-pos.y);
	}

	public double distance2D(Position pos) {
		return Math.sqrt(sqrDistance2D(pos));
	}
	
	public String toString() {
		return "(" + numFormat.format(x) + ", " + numFormat.format(y) + ", " + numFormat.format(z) + ")";
	}

	private static NumberFormat positionNumFormat() {
		DecimalFormatSymbols syms = new DecimalFormatSymbols();
		syms.setDecimalSeparator('.');
		syms.setGroupingSeparator(' ');
		
		DecimalFormat nFormat = new DecimalFormat();
		nFormat.setDecimalFormatSymbols(syms);
		nFormat.setMinimumFractionDigits(1);
		nFormat.setMaximumFractionDigits(2);
		return nFormat;
	}
	
	public void add(WorldVector vec) {
		x += vec.getX();
		y += vec.getY();
		z += vec.getZ();
	}
	
	/**
	 * Method add.
	 * @param position
	 */
	public void add(Position position) {
		x += position.x;
		y += position.y;
		z += position.z;
	}
	
	/**
	 * Method subtract
	 * @param position
	 */
	public void subtract(Position position) {
		x -= position.x;
		y -= position.y;
		z -= position.z;
	}
	
	public void subtract(WorldVector vec) {
		x -= vec.getX();
		y -= vec.getY();
		z -= vec.getZ();
	}
	
	/**
	 * Returns a WorldVector that points FROM the argument position TO the instance position
	 * @param pos - vector start position
	 * @return the resulting WorldVector
	 */
	public WorldVector getDifferenceVector(Position pos) {
		return new WorldVector(x - pos.x, y - pos.y, z - pos.z);
	}
	
	public boolean equals(Object o) {
		if (o instanceof Position) {
			Position p = (Position) o;
			return (getX() == p.getX() && getY() == p.getY() && getZ() == p.getZ());
		} else {
			return false;
		}
	}
	
}
