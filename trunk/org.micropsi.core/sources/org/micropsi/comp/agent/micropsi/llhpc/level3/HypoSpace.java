package org.micropsi.comp.agent.micropsi.llhpc.level3;

import java.util.ArrayList;
import java.util.Stack;

import org.micropsi.comp.agent.micropsi.llhpc.level2.FormHypothesis;
import org.micropsi.comp.agent.micropsi.llhpc.level2.HypothesisIF;


public class HypoSpace {
	
	public class AssessedHypo {
		public HypothesisIF hypo;
		public double value;
		public int epoch;
	}
	
	private class Level {
		public AssessedHypo parent;
		public ArrayList<AssessedHypo> hypos = new ArrayList<AssessedHypo>();
	}
	
	
	private int nextToTest = 0;
	private HypothesesGeneratorIF generator;
	public Stack<Level> levels = new Stack<Level>();
	
	public HypoSpace(HypothesesGeneratorIF generator) {
		this.generator = generator;
		enterLevel();
	}
	
	public void enterLevel() {
		HypothesisIF bestHypothesis = null;
		
		AssessedHypo bestAssessedHypo = getBestHypothesis();
		if(bestAssessedHypo != null) {
			bestHypothesis = bestAssessedHypo.hypo;
		}	
		ArrayList<HypothesisIF> nextLevelHypos = generator.generateHypothesesFor(bestHypothesis);
		
		if(nextLevelHypos == null || nextLevelHypos.size() == 0) {
			return;
		}
		
		Level level = new Level();
		level.parent = bestAssessedHypo;
		
		ArrayList<AssessedHypo> nextLevelAssessedHypos = new ArrayList<AssessedHypo>();
		for(int i=0;i<nextLevelHypos.size();i++) {
			AssessedHypo as = new AssessedHypo();
			as.hypo = nextLevelHypos.get(i);
			as.value = -1;
			nextLevelAssessedHypos.add(as);
		}
		level.hypos = nextLevelAssessedHypos;		
		levels.push(level);	
	}
	
	public void leaveLevel() { 
		if(levels.size() > 1) {
			levels.pop();
		}
	}
		
	public AssessedHypo getBestHypothesis() {
		
		if(levels.isEmpty()) return null;
		
		double bestVal = Double.MIN_VALUE;
		AssessedHypo bestHypo = null;
		for(int i=0;i<levels.peek().hypos.size();i++) {
			if(levels.peek().hypos.get(i).value > bestVal) {
				bestVal = levels.peek().hypos.get(i).value;
				bestHypo = levels.peek().hypos.get(i);
			}
		}
		if(bestVal < 0.8) bestHypo = null;
		
		return bestHypo;
	}

	public ArrayList<AssessedHypo> getBestHypothesesAllLevels() {
		
		ArrayList<AssessedHypo> bestHypotheses = new ArrayList<AssessedHypo>();
		
		// TODO: Implement
		
		return bestHypotheses;
	}

	
	public void update(HypothesisIF hypothesis, double value) {
		for(int i=0;i<levels.peek().hypos.size();i++) {
			if(levels.peek().hypos.get(i).hypo == hypothesis) {
				levels.peek().hypos.get(i).value = value;
				levels.peek().hypos.get(i).epoch++;
				nextToTest = i+1;
				if(nextToTest > levels.peek().hypos.size()-1) {
					nextToTest = 0;
					if(getBestHypothesis() != null) {
						enterLevel();
					} else {
						leaveLevel();
					}
				}
				break;
			}
		}
	}
	
	public AssessedHypo getNextToTest() {
		return levels.peek().hypos.get(nextToTest);
	}
	

}
