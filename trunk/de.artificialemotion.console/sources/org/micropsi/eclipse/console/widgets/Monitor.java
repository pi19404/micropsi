/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/widgets/Monitor.java,v 1.3 2004/11/24 21:17:55 vuine Exp $ 
 */
package org.micropsi.eclipse.console.widgets;

import org.eclipse.swt.graphics.Color;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.console.*;


public class Monitor {

	public static final int BACKLIST_LENGTH = 200;

	private IParameterMonitor parametermonitor;
	
	private double[] backlist = new double[BACKLIST_LENGTH]; 
	private int currentPos = 0;
	
	private long lastredraw = System.currentTimeMillis();
	private long nextredraw = 0;
	
	private double normFactor = 1;
	
	private String display;
	boolean isInvalid = false;
	
	private Color color;
		
	public Monitor(IParameterMonitor mon, Color color, String display) {
		this.parametermonitor = mon;
		this.color = color;
		this.display = display;

		try {
			backlist[currentPos] = parametermonitor.getCurrentValue();
		} catch (Exception e) {
			backlist[currentPos] = 0;
			isInvalid = true;
		}
		
	}
	
	public void wasRedrawn(long timestamp) {
		
		if(timestamp < lastredraw) {
			// reset
			nextredraw = timestamp;
		}
		
		lastredraw = timestamp;		
		if(lastredraw >= nextredraw) {
			currentPos++;
			if(currentPos >= backlist.length) currentPos = 0;
			try {
				backlist[currentPos] = parametermonitor.getCurrentValue();
				isInvalid = false;
			} catch (MicropsiException e) {
				backlist[currentPos] = 0;
				isInvalid = true;
			}			
			//System.err.println("cur: "+backlist[currentPos]);
			
			nextredraw = timestamp + parametermonitor.getIntervalMillis();			
		}
	}
	
	public long getRedrawDelay(long currenttimemillis) {
		return ((lastredraw+parametermonitor.getIntervalMillis())-currenttimemillis);
	}

	public double getPreviousValue() {
		return backlist[getPreviousIndex(1)];
	}
	
	public double getPreviousValue(int back) {
		return backlist[getPreviousIndex(back)];
	}

	public double getCurrentValue() {
		return backlist[currentPos];
	}

	public String getDisplayName() {
		return display;
	}

	public String getID() {
		return parametermonitor.getID();
	}

	private int getPreviousIndex(int back) {
		if(currentPos - back >= 0) { 
			return currentPos-back;
		} else {
			return backlist.length-(back - currentPos);
		}
	}

	/**
	 * @return
	 */
	public double getNormFactor() {
		return normFactor;
	}

	/**
	 * @param d
	 */
	public void setNormFactor(double d) {
		normFactor = d;
	}

	/**
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	public IParameterMonitor accessParameterMonitor() {
		return parametermonitor;
	}

	/**
	 * @return
	 */
	public boolean isInvalid() {
		return isInvalid;
	}
	
	public void clearBacklist() {
		backlist = new double[BACKLIST_LENGTH]; 
		currentPos = 0;
		try {
			backlist[currentPos] = parametermonitor.getCurrentValue();
		} catch (MicropsiException e) {
			// doesn't matter then
		}
	}

}
