/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/agent/SituationElement.java,v 1.5 2004/11/20 21:15:56 vuine Exp $
 */
package org.micropsi.nodenet.agent;

public class SituationElement {
	
	public static final double TOLERANCE = 0.1;
	
	private long id;
	private String type;
	private double x;
	private double y;
	
	public SituationElement(long id, String type, double x, double y) {
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @param attentionElement
	 */
	public SituationElement(SituationElement element) {
		this.id = element.id;
		this.type = element.type;
		this.x = element.x;
		this.y = element.y;
	}

	public String getType() {
		return type;
	}
		
	public long getWorldID() {
		return id;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public boolean isAt(double x, double y) {
		return (
			this.x >= x - TOLERANCE &&
			this.x <= x + TOLERANCE &&
			this.y >= y - TOLERANCE &&
			this.y <= y + TOLERANCE
		);
	}
	
	public String toString() {
		return "SituationElement: "+type+" id: "+id+" x: "+x+" y: "+y;
	}

	protected void setId(long id) {
		this.id = id;
	}
	protected void setType(String type) {
		this.type = type;
	}
	protected void setX(double x) {
		this.x = x;
	}
	protected void setY(double y) {
		this.y = y;
	}
}
