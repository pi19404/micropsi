/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 28.07.2003
 *
 */
package org.micropsi.common.coordinates;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author Matthias
 *
 */
public class WorldVector {
	private double x, y, z;

	private static final NumberFormat numFormat = positionNumFormat();

	/**
	 * 
	 */
	public WorldVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public WorldVector(double x, double y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	public WorldVector() {
		x = 0;
		y = 0;
		z = 0;
	}

	public WorldVector(WorldVector vec) {
		x = vec.getX();
		y = vec.getY();
		z = vec.getZ();
	}

	public WorldVector(String s) {
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
	
	/**
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @param z
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	public void set(WorldVector vec) {
		x = vec.getX();
		y = vec.getY();
		z = vec.getZ();
	}
	
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getLength() {
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	/**
	 * Scales vector to new length. Does nothing if vector has length 0. 
	 * 
	 * @param newlen - new vector length
	 */
	public void setLength(double newlen) {
		double oldlen = getLength();
		if (oldlen != 0) {
			scaleBy(newlen/oldlen);
		}
	}
	
	public void scaleBy(double fak) {
		x *= fak;
		y *= fak;
		z *= fak;
	}
	
	public void rotate(double alpha) {
		alpha = Math.toRadians(alpha);
		double oldx = x; double oldy = y;
		x = oldx*Math.cos(alpha) + oldy*Math.sin(alpha);
		y = -oldx*Math.sin(alpha) + oldy*Math.cos(alpha);
	}
	
	/**Gets the orientation angle of the vector in degrees in the intervall [0, 360).
	 * 0 is north. Orientation is clockwise.
	 * 
	 * @return
	 */
	public double getAngle() {
		if (getLength() == 0) {
			return 0;
		}
		double x = getX() / getLength();
		double y = getY() / getLength();
		double angle = Math.toDegrees(Math.acos(y));
		if (x < 0) {
			angle = 360 - angle;
		}
		return angle;
	}
	
	public void add(WorldVector vec) {
		x += vec.getX();
		y += vec.getY();
		z += vec.getZ();
	}

	public void subtract(WorldVector vec) {
		x -= vec.getX();
		y -= vec.getY();
		z -= vec.getZ();
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
	


}
