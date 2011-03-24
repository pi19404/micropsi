package org.micropsi.comp.agent.micropsi.llhpc.level3;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.micropsi.llhpc.level2.FormHypothesis;
import org.micropsi.comp.agent.micropsi.llhpc.level2.HypothesisIF;
import org.micropsi.comp.agent.micropsi.llhpc.level2.SampleGenerator;


public class TestHypothesesGenerator implements HypothesesGeneratorIF {

	private Logger logger;
	
	private ArrayList<HypothesisIF> rootHypotheses = new ArrayList<HypothesisIF>();
	
	private FormHypothesis eyeCircleHypothesis; 
	
	
	public TestHypothesesGenerator(Logger logger) throws MicropsiException, IOException {
		super();
		
		this.logger = logger;
		
		// the very big ones
		
//		BufferedImage hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY); 
//		SampleGenerator.createEllipsoidHypothesis(hypoImg, 85, 15, 140, 180);
//		rootHypotheses.add(new FormHypothesis("Very big high oval",hypoImg,1.0,logger));
//
//		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY); 
//		SampleGenerator.createEllipsoidHypothesis(hypoImg, 65, 35, 170, 140);
//		rootHypotheses.add(new FormHypothesis("Very big flat oval",hypoImg,1.0,logger));
//
//		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY);
//		SampleGenerator.createRectangularHypothesis(hypoImg, 85, 12, 140, 180);
//		rootHypotheses.add(new FormHypothesis("Very big high rectangle",hypoImg,0.8,logger));
//		
//		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY);
//		SampleGenerator.createRectangularHypothesis(hypoImg, 65, 32, 170, 140);
//		rootHypotheses.add(new FormHypothesis("Very big flat rectangle",hypoImg,0.8,logger));

		// the big ones
		
		BufferedImage hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY); 
		SampleGenerator.createEllipsoidHypothesis(hypoImg, 95, 25, 120, 160);
		rootHypotheses.add(new FormHypothesis("Big high oval",hypoImg,1.0,logger));

		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY); 
		SampleGenerator.createEllipsoidHypothesis(hypoImg, 75, 45, 150, 120);
		rootHypotheses.add(new FormHypothesis("Big flat oval",hypoImg,1.0,logger));

		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY);
		SampleGenerator.createRectangularHypothesis(hypoImg, 95, 22, 120, 160);
		rootHypotheses.add(new FormHypothesis("Big high rectangle",hypoImg,0.7,logger));
		
		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY);
		SampleGenerator.createRectangularHypothesis(hypoImg, 75, 42, 150, 120);
		rootHypotheses.add(new FormHypothesis("Big flat rectangle",hypoImg,0.7,logger));

		// the middlesized
		
		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY); 
		SampleGenerator.createEllipsoidHypothesis(hypoImg, 105, 35, 100, 140);
		rootHypotheses.add(new FormHypothesis("High oval",hypoImg,1.0,logger));

		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY); 
		SampleGenerator.createEllipsoidHypothesis(hypoImg, 85, 55, 130, 100);
		rootHypotheses.add(new FormHypothesis("Flat oval",hypoImg,1.0,logger));

		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY);
		SampleGenerator.createRectangularHypothesis(hypoImg, 105, 32, 100, 120);
		rootHypotheses.add(new FormHypothesis("High rectangle",hypoImg,0.7,logger));
		
		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY);
		SampleGenerator.createRectangularHypothesis(hypoImg, 85, 52, 130, 100);
		rootHypotheses.add(new FormHypothesis("Flat rectangle",hypoImg,0.7,logger));
		
		hypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY); 
		SampleGenerator.createEllipsoidHypothesis(hypoImg, 10, 10, 25, 25);
		eyeCircleHypothesis = new FormHypothesis("Circle",hypoImg,1.0,logger);
		
	}

	public ArrayList<HypothesisIF> generateHypothesesFor(HypothesisIF confirmedHypo) {
		
		if(confirmedHypo == null) {
			return rootHypotheses;
		} else {
			// ovals: face?
			if(confirmedHypo.getName().indexOf("val") > 0) {
				try {
					GroupHypothesis faceGroupHypothesis = new GroupHypothesis(confirmedHypo.getName()+" face");
					faceGroupHypothesis.addFormHypothesis(eyeCircleHypothesis,2,4);
					faceGroupHypothesis.addFormHypothesis(eyeCircleHypothesis,6,4);
				} catch (Exception e) {
					
				}

			}
		}
		
		return new ArrayList<HypothesisIF>();
	}

}
