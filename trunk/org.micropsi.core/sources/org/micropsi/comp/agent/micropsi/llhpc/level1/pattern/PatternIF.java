package org.micropsi.comp.agent.micropsi.llhpc.level1.pattern;


public interface PatternIF {
		
	public static final int SIZE = 10;
	
	enum Pattern {UNCLEAR,HORIZONTAL,VERTICAL,ASCENDING,DESCENDING}
	
	enum Noise {NONOISE, HIGHNOISE, LOWNOISE}
}
