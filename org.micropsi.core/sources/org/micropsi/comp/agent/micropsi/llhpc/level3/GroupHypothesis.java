package org.micropsi.comp.agent.micropsi.llhpc.level3;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.micropsi.llhpc.level2.FormHypothesis;
import org.micropsi.comp.agent.micropsi.llhpc.level2.HypothesisIF;


public class GroupHypothesis implements HypothesisIF {

	private class Element {
		public FormHypothesis form;
		public int shiftx;
		public int shifty;
	}
	
	private ArrayList<Element> elements = new ArrayList<Element>(); 
	
	private String name; 
	
	public GroupHypothesis(String name) {
		super();
		this.name = name;
	}
	
	public void addFormHypothesis(FormHypothesis hypo, int relativeshiftx, int relativeshifty) {
		Element element = new Element();
		element.form = hypo;
		element.shiftx = relativeshiftx;
		element.shifty = relativeshifty;
		elements.add(element);
	}

	public double calculateMatch(BufferedImage img, int shiftx, int shifty) throws MicropsiException {
		
		double cumulativeMatch = 0;
		for(int i=0;i<elements.size();i++) {
			Element e = elements.get(i); 
			cumulativeMatch += e.form.calculateMatch(img,shiftx+e.shiftx,shifty+e.shifty);
		}
		
		return cumulativeMatch / elements.size();
	}

	public String getName() {
		return name;
	}

	public Collection<FormHypothesis> getFormHypotheses() {
		ArrayList<FormHypothesis> forms = new ArrayList<FormHypothesis>();
		for(int i=0;i<elements.size();i++)
			forms.add(elements.get(i).form);
		return forms;
	}

}
