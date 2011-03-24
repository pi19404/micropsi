package org.micropsi.comp.agent.micropsi.llhpc.level3;

import java.util.ArrayList;

import org.micropsi.comp.agent.micropsi.llhpc.level2.HypothesisIF;


public interface HypothesesGeneratorIF {

	/**
	 * Returns a list of ALTERNATIVE hypotheses for the given confirmed "parent" hypothesis.
	 * If you want to AND hypotheses when the given hypothesis was confirmed, return one
	 * GroupHypothesis for each ANDed group of hypotheses
	 * @param confirmedHypo
	 * @return
	 */
	public ArrayList<HypothesisIF> generateHypothesesFor(HypothesisIF confirmedHypo);
	
}
