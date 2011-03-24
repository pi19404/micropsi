/*
 * Created on 28.06.2004
 *
 */

package org.micropsi.comp.world.objects;

/**
 * @author Matthias
 *
 */
public class ObjectColor {
	
	public static ObjectColor red = new ObjectColor(100, 0, 0);
	public static ObjectColor green = new ObjectColor(0, 100, 0);
	public static ObjectColor brown = new ObjectColor(20, 30, 0);
	public static ObjectColor yellow = new ObjectColor(60, 100, 0);
	public static ObjectColor blue = new ObjectColor(0, 0, 100);
	
	private int redAmount;
	private int greenAmount;
	private int blueAmount;

	/**
	 * @param redAmount
	 * @param greenAmount
	 * @param blueAmount
	 */
	public ObjectColor(int redAmount, int greenAmount, int blueAmount) {
		this.redAmount = redAmount;
		this.greenAmount = greenAmount;
		this.blueAmount = blueAmount;
	}

	public void setRedAmount(int redAmount) {
		this.redAmount = redAmount;
	}

	public int getRedAmount() {
		return redAmount;
	}

	public void setGreenAmount(int greenAmount) {
		this.greenAmount = greenAmount;
	}

	public int getGreenAmount() {
		return greenAmount;
	}

	public void setBlueAmount(int blueAmount) {
		this.blueAmount = blueAmount;
	}

	public int getBlueAmount() {
		return blueAmount;
	}
	
	public String toString() {
		return "R:" + getRedAmount() + ",G:" + getGreenAmount() + ",B:" + getBlueAmount();
	}
}
