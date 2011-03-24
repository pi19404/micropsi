package org.micropsi.nodenet.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


//@todo: Ronnie: Add "isValid()" checks
public class Situation {
	
	protected static HashMap<String,Situation> situations = new HashMap<String,Situation>();
	
	public static Situation getInstance(String situationID) {
		if(!situations.containsKey(situationID)) {
			Situation ns = new Situation(situationID);
			situations.put(situationID, ns);
		}
		return situations.get(situationID);
	}
		
	private double foveax;
	private double foveay;
		
	double zoomOffsetX =  0;
	double zoomOffsetY =  0;
	
	private boolean zoomed = false;
	
	private String key = null;
	
	private ArrayList<SituationElement> elements = new ArrayList<SituationElement>();
	private SituationElement foveaElement = null;
	
	private Situation(String key) {
		this.key = key;
		lookAtHomePosition();
	}
	
	public void clear() {
		unzoom();
		synchronized(elements) {
			elements.clear();
		}
		update();
	}
	
		
	public void updateElement(String type, double x, double y, long id) {
				
		SituationElement e = null;
		
		synchronized(this) {
			for(int i=0;i<elements.size();i++) {
				SituationElement tmp = elements.get(i); 
				if(tmp.getWorldID() == id) 
					e = elements.get(i);
				else {
					if(tmp.getX() == x && tmp.getY() == y) return;
				}
			}
	
			if(e == null) {
				e = new SituationElement(id,type,x,y);
				elements.add(e);
			}
		}
		update();
	}
	
	public void lookAtHomePosition() {	
		foveax = 0;
		foveay = 0;
		update();
	}
				
	public void foveaRight(double v) {
		foveax += v;
		update();
	}
		
	public void foveaDown(double v) {
		foveay += v;
		update();
	}
	
	public void lookAtX(double v) {
		foveax = v;
		update();
	}

	public void lookAtY(double v) {
		foveay = v;
		update();
	}
	
	public SituationElement getElementInFovea() {
		return foveaElement;
	}
			
	private void update() {
		synchronized(this) {
			foveaElement = null;
			for(int i=0;i<elements.size();i++) {
				if(elements.get(i).isAt(foveax+zoomOffsetX, foveay+zoomOffsetY)) {
					foveaElement = elements.get(i);
					break;
				}
			}
		}
	}
	
	public Iterator<SituationElement> getWholeSituation() {
		if(zoomed) {
			if(getElementInFovea() != null) {
				ArrayList<SituationElement> tmp = new ArrayList<SituationElement>(1);
				SituationElement tmpElement = new SituationElement(getElementInFovea());
				tmpElement.setX(0);
				tmpElement.setY(0);
				tmp.add(tmpElement);
				return tmp.iterator();
			} else {
				return new ArrayList<SituationElement>().iterator();
			}
		} else {
			return elements.iterator();
		}
	}
	
	public String toString() {
		return key+"("+super.toString()+")";
	}
	
	public double getFoveaX() {
		return foveax;
	}

	public double getFoveaY() {
		return foveay;
	}
		
	public void zoom() {
		if(zoomed) return;
		
		SituationElement elementToZoom = null;
		synchronized(elements) {
			for(int i=0;i<elements.size();i++) {
				if(elements.get(i).isAt(foveax, foveay)) {
					elementToZoom = elements.get(i);
					break;
				}
			}
		}

		if(elementToZoom == null) return;
		
		foveaElement = elementToZoom;
		
		zoomed = true;
		zoomOffsetX = foveax;
		zoomOffsetY = foveay;
		foveax = 0;
		foveay = 0;
		//update();
	}
	
	public void unzoom() {
		
		if(!zoomed) return;
		
		zoomed = false;
		zoomOffsetX = 0;
		zoomOffsetY = 0;
		update();
	}
	
	public boolean isZoomed() {
		return zoomed;
	}
	
	public String describe() {

		try {
		
		String desc = Integer.toString(elements.size());
		
		if(!zoomed) {
			for(int i=0;i<elements.size();i++) {
				SituationElement e = elements.get(i);
				desc += e.getType().charAt(0);
			}
		} else {
			if(getElementInFovea() != null)
				desc += "z:"+getElementInFovea().getType();
		}
		return desc;
		
		} catch (Exception e) {
			return e.getMessage();
		}
		

	}
}
