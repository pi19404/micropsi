package org.micropsi.comp.agent.micropsi.llhpc.level2;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.micropsi.llhpc.level1.LineDetector;
import org.micropsi.comp.agent.micropsi.llhpc.level1.LineDetectorWOHypotheses;


public class FormHypothesis implements HypothesisIF {
	
	public BufferedImage hypothesisImg;
	private double[][] hypo;
	private double[][] lastMatchRealValues;
	//private LineDetector linedetector;
	private LineDetectorWOHypotheses linedetector;
	private double damp = 1;
	private String name;
		
	public FormHypothesis(String name, BufferedImage hypothesis, double damp, Logger logger) throws MicropsiException, IOException {
		this.hypothesisImg = hypothesis;
		this.name = name;
		hypo = SampleGenerator.getInstance().generateSample(hypothesis,1,logger);
		lastMatchRealValues = new double[hypo.length][hypo[0].length];
		
//		linedetector = new LineDetector(logger,32,24);
		linedetector = new LineDetectorWOHypotheses(logger,32,24);
	}
	
	/* (non-Javadoc)
	 * @see test.level2.HypothesisIF#calculateMatch(java.awt.image.BufferedImage, int, int)
	 */
	public double calculateMatch(BufferedImage img, int shiftx, int shifty) throws MicropsiException {
				
		int hypoElements = 0;
		double matchSum = 0;
		
		int hypohalfx = hypo.length / 2;
		int hypohalfy = hypo[0].length / 2;
		
		int xFail = 0;
		int yFail = 0;
		
		for(int x=1;x<hypo.length-1;x++) {
			for(int y=1;y<hypo[0].length-1;y++) {
				
				if(x+shiftx < 1 || x+shiftx > lastMatchRealValues.length-2 || y+shifty < 1 || y+shifty > lastMatchRealValues[0].length-2)
					continue;
				
				
				lastMatchRealValues[x+shiftx][y+shifty] = -1;
				
				if(hypo[x][y] > 0) {
					
					double match = 0;
					
					// center
					//linedetector.detectLine(img,x+shiftx,y+shifty,hypo[x][y],true);
					linedetector.detectLine(img,x+shiftx,y+shifty,true);
					
					double hypoMatch = 1-Math.abs(linedetector.getAngle()[x+shiftx][y+shifty] - hypo[x][y]);
					double verdict = linedetector.getGrey()[x+shiftx][y+shifty];
					if(verdict < linedetector.getMatchThreshold()) {
						match += hypoMatch;
						lastMatchRealValues[x+shiftx][y+shifty] = linedetector.getAngle()[x+shiftx][y+shifty];
					}
					hypoElements++;
					
					// left
					//linedetector.detectLine(img,x+shiftx-1,y+shifty,hypo[x][y],true);
					linedetector.detectLine(img,x+shiftx-1,y+shifty,true);
					
					hypoMatch = 1-Math.abs(linedetector.getAngle()[x+shiftx-1][y+shifty] - hypo[x][y]);
					verdict = linedetector.getGrey()[x+shiftx-1][y+shifty];
					if(verdict < linedetector.getMatchThreshold()) {
						match += hypoMatch;
					}

					// right
//					linedetector.detectLine(img,x+shiftx+1,y+shifty,hypo[x][y],true);
					linedetector.detectLine(img,x+shiftx+1,y+shifty,true);
					
					hypoMatch = 1-Math.abs(linedetector.getAngle()[x+shiftx+1][y+shifty] - hypo[x][y]);
					verdict = linedetector.getGrey()[x+shiftx+1][y+shifty];
					if(verdict < linedetector.getMatchThreshold()) {
						match += hypoMatch;
					}

					// up
//					linedetector.detectLine(img,x+shiftx,y+shifty-1,hypo[x][y],true);
					linedetector.detectLine(img,x+shiftx,y+shifty-1,true);
					
					hypoMatch = 1-Math.abs(linedetector.getAngle()[x+shiftx][y+shifty-1] - hypo[x][y]);
					verdict = linedetector.getGrey()[x+shiftx][y+shifty-1];
					if(verdict < linedetector.getMatchThreshold()) {
						match += hypoMatch;
					}

					// down
//					linedetector.detectLine(img,x+shiftx,y+shifty+1,hypo[x][y],true);
					linedetector.detectLine(img,x+shiftx,y+shifty+1,true);
					
					hypoMatch = 1-Math.abs(linedetector.getAngle()[x+shiftx][y+shifty+1] - hypo[x][y]);
					verdict = linedetector.getGrey()[x+shiftx][y+shifty+1];
					if(verdict < linedetector.getMatchThreshold()) {
						match += hypoMatch;
					}

					if(match < 1) {
						xFail += (x < hypohalfx) ? -1 : +1;
						yFail += (y < hypohalfy) ? -1 : +1;											
					}
					
					matchSum += match;
				}
			}
		}
		
		double match = matchSum / hypoElements;
		
		return match * damp;
	}
	
	public double[][] getHypotheses() {
		return hypo;
	}

	/* (non-Javadoc)
	 * @see test.level2.HypothesisIF#getLastMatchRealValues()
	 */
	public double[][] getLastMatchRealValues() {
		return lastMatchRealValues;
	}

	/* (non-Javadoc)
	 * @see test.level2.HypothesisIF#getName()
	 */
	public String getName() {
		return name;
	}

}
