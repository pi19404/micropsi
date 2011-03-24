package org.micropsi.comp.agent.micropsi.llhpc.level1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.MultiPassInputStream;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternGenerator;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Noise;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Pattern;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetCycleIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.NetParametersIF;
import org.micropsi.nodenet.NetPropertiesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.SensorDataSourceIF;
import org.micropsi.nodenet.outputfunctions.OFLogistic;

public class PatternTrainer {
	
	private class PixelDataSource implements SensorDataSourceIF {
		
		private int x,y;
		
		public PixelDataSource(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public String getDataType() {
			return "patch_"+x+"/"+y;
		}

		public double getSignalStrength() {
			if(netImage == null) return 0;
			
			return (double)netImage.getRaster().getSample(x,y,0) / (double)255;
		}
		
	}

	private class SignalDataSource implements SensorDataSourceIF {
		
		private double value = 0;
		private String type;
		
		public SignalDataSource(String type) {
			this.type = "patch_"+type;
		}

		public String getDataType() {
			return type;
		}

		public double getSignalStrength() {
			//System.out.println(type+" "+value);
			
			return value;
		}
		
		public void setValue(double value) {
			this.value = value;
		}
		
	}

	
	private Logger logger;
	private LocalNetFacade patternNet;
	private NetPropertiesIF props;
	private BufferedImage netImage;
	
	private Pattern trainTo;
	private double targetValue;
	
	private SignalDataSource target;
	private SignalDataSource trainingTrigger;
	
	public PatternTrainer(Pattern pattern, File rawNet) throws FileNotFoundException, MicropsiException {
		super();
		
		this.trainTo = pattern;
		
		logger = Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new SimpleLayout()));
		
		props = new NetPropertiesIF() {
			public String getProperty(String propertyName) {
				throw new RuntimeException("No such property: "+propertyName);
			}
		};
		
		patternNet = new LocalNetFacade(logger,props);
		patternNet.loadNet(new MultiPassInputStream(rawNet),false);
		randomizeNet();
		logger.debug("net randomized...");
		
		
		
		
		for(int x=0;x<PatternIF.SIZE;x++) {
			for(int y=0;y<PatternIF.SIZE;y++) {
				patternNet.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
			}
		}
		
		target = new SignalDataSource("target");
		trainingTrigger = new SignalDataSource("trigger");
		patternNet.getSensorRegistry().registerSensorDataProvider(target);
		patternNet.getSensorRegistry().registerSensorDataProvider(trainingTrigger);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
	}
	boolean stop = false;
	
	public void train() throws NetIntegrityException {

		
		
		JFrame patternFrame = new JFrame("Patterns");
		patternFrame.setSize(352,75);
		
		JButton stopButton = new JButton();
		stopButton.setText("stop and save");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.err.println("Stopping...");
				stop = true;
			}
		});
		
		patternFrame.add(stopButton);
		patternFrame.setEnabled(true);
		patternFrame.setAlwaysOnTop(true);
		patternFrame.setVisible(true);
		
//		BufferedImage horizontal = PatternGenerator.generatePattern(Pattern.HORIZONTAL,Matte.CLEAR);
//		BufferedImage vertical = PatternGenerator.generatePattern(Pattern.VERTICAL,Matte.CLEAR);
//		BufferedImage asc = PatternGenerator.generatePattern(Pattern.ASCENDING,Matte.CLEAR);
//		BufferedImage desc = PatternGenerator.generatePattern(Pattern.DESCENDING,Matte.CLEAR);
		
		Pattern goodSample = null;
		ArrayList<Pattern> badSamples = new ArrayList<Pattern>();

		Noise noise = Noise.LOWNOISE;
			
		goodSample = trainTo;
		switch(trainTo) {
			case HORIZONTAL:
				badSamples.add(Pattern.VERTICAL);
				badSamples.add(Pattern.ASCENDING);
				badSamples.add(Pattern.DESCENDING);
				break;
			case VERTICAL:
				badSamples.add(Pattern.HORIZONTAL);
				badSamples.add(Pattern.ASCENDING);
				badSamples.add(Pattern.DESCENDING);
				break;				
			case ASCENDING:
				badSamples.add(Pattern.HORIZONTAL);
				badSamples.add(Pattern.VERTICAL);
				badSamples.add(Pattern.DESCENDING);
				break;				
			case DESCENDING:
				badSamples.add(Pattern.HORIZONTAL);
				badSamples.add(Pattern.VERTICAL);
				badSamples.add(Pattern.ASCENDING);
				break;				
		}

		logger.info("Testing positive: ");
		test(PatternGenerator.getInstance().generatePattern(goodSample,noise),"100-050918/191642");
		logger.info("Testing negative: ");
		for(int i=0;i<badSamples.size();i++) {
			test(PatternGenerator.getInstance().generatePattern(badSamples.get(i),noise),"100-050918/191642");
		}					

		logger.info(" ---- start learning ----");
		
		
		int j = 0;
		while(!stop) {
			// 5 times the good sample
			// 1 time each of the bad sample

			if(j % 100 == 0) {
				logger.debug("step "+j+": ");
				test(PatternGenerator.getInstance().generatePattern(goodSample,noise),"100-050918/191642");
				for(int i=0;i<badSamples.size();i++) {
					test(PatternGenerator.getInstance().generatePattern(badSamples.get(i),noise),"100-050918/191642");
				}					
				
			}
			
			for(int i=0;i<10;i++) {
				netImage = PatternGenerator.getInstance().generatePattern(goodSample,noise);
				targetValue = 1.0;
				learn();
			}		
			
			//for(int k=0;k<5;k++)
			for(int i=0;i<badSamples.size();i++) {
				netImage = PatternGenerator.getInstance().generatePattern(badSamples.get(i),noise);
				targetValue = 0.1;
				learn();
			}
			
			j++;
		}
		
		logger.info("Testing positive: ");
		test(PatternGenerator.getInstance().generatePattern(goodSample,noise),"100-050918/191642");
		logger.info("Testing negative: ");
		for(int i=0;i<badSamples.size();i++) {
			test(PatternGenerator.getInstance().generatePattern(badSamples.get(i),noise),"100-050918/191642");
		}				

//		goodSample = PatternGenerator.generatePattern(Pattern.HORIZONTAL,Matte.LOWNOISE);
//		badSamples.clear();
//		badSamples.add(PatternGenerator.generatePattern(Pattern.VERTICAL,Matte.LOWNOISE));
//		badSamples.add(PatternGenerator.generatePattern(Pattern.ASCENDING,Matte.LOWNOISE));
//		badSamples.add(PatternGenerator.generatePattern(Pattern.DESCENDING,Matte.LOWNOISE));
//
//		
//		logger.info("Finished. Testing positive noisy: ");
//		test(goodSample,"100-050918/191642");
//		logger.info("Finished. Testing negative noisy: ");
//		for(int i=0;i<badSamples.size();i++) {
//			test(badSamples.get(i),"100-050918/191642");
//		}				

		
		try {
			File f = new File("C:/result.mpn");
			f.createNewFile();
			patternNet.saveNet(new FileOutputStream(f));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (MicropsiException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void learn() throws NetIntegrityException {
		
		// 10 cycles to spread activation
		NetCycleIF cycle = patternNet.getCycle();
		for(int i=0;i<10;i++) {
			cycle.nextCycle(false);
//			debugNeuron("100-050918/191642");
			
			if(i == 7) {
				target.setValue(targetValue);
			}
			if(i == 8) {
				trainingTrigger.setValue(1.0);
			}
		}
		
		target.setValue(0);
		trainingTrigger.setValue(0);
		
		// pace the net
		for(int i=0;i<10;i++) {
			cycle.nextCycle(false);
//			debugNeuron("100-050918/191642");
		}
		
	}
	
	private void test(BufferedImage img, String neuronId) throws NetIntegrityException {
		netImage = img;
		
		NetCycleIF cycle = patternNet.getCycle();
		for(int i=0;i<10;i++) {
			cycle.nextCycle(false);
		}
		debugNeuron(neuronId);
		
		netImage = null;

		for(int i=0;i<10;i++) {
			cycle.nextCycle(false);
		}
	}
	
	private void randomizeNet() throws MicropsiException {
		int i = 0;
		int j = 0;
		
		Iterator<NetEntity> allEntities = patternNet.getAllEntities();
		A:while(allEntities.hasNext()) {
			NetEntity e = allEntities.next();
			if(e.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Node n = (Node)e;
			if(n.getType() != NodeFunctionalTypesIF.NT_REGISTER && n.getType() != NodeFunctionalTypesIF.NT_SENSOR) continue;
			
			
			Iterator<Link> allLinks = n.getGate(GateTypesIF.GT_GEN).getLinks();		
			while(allLinks.hasNext()) {
				
				double randomValue = (-2 + (Math.random()*4));
				
				
				Link l = allLinks.next();
				if(l.getLinkedEntity().getEntityType() != NetEntityTypesIF.ET_NODE) continue A;
								
				try {
				patternNet.changeLinkParameter(
					Link.LINKPARAM_WEIGHT,
					l.getLinkingEntity().getID(),
					l.getLinkingGate().getType(),
					l.getLinkedEntityID(),
					l.getLinkedSlot().getType(),
					randomValue
				);
				} catch (Exception exc) {
					logger.error("could not change link",exc);
				}
				
				i++;
			}
			
			if(n.getGate(GateTypesIF.GT_GEN).getOutputFunction().getClass() == OFLogistic.class) {
				double randomValue = randomValue = (-4 + (Math.random()*8));		
				patternNet.changeParameter(NetParametersIF.PARM_ENTITY_GATE_OUTPUTFUNCTION_PARAMETER,n.getID(),GateTypesIF.GT_GEN,"theta="+randomValue);				
			} else {
				patternNet.changeParameter(NetParametersIF.PARM_ENTITY_GATE_OUTPUTFUNCTION_PARAMETER,n.getID(),GateTypesIF.GT_GEN,"theta=0.0");
			}
			j++;
		}

		logger.debug("randomized "+i+" weights at "+j+" neurons");		
	}
	
	private void debugNeuron(String id) {
		try {
			logger.debug(id+" outp = "+((Node)patternNet.getEntity(id)).getGenActivation());
		} catch (MicropsiException e) {
			logger.error("Debug error",e);
		}
	}

	/**
	 * @param args
	 * @throws MicropsiException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
//		JFrame frame = new JFrame("PatternTrainer");
//		frame.setAlwaysOnTop(true);
//		frame.setVisible(false);
//		
//		JFileChooser chooser = new JFileChooser();
//		chooser.setFileFilter(new FileFilter() {
//			public boolean accept(File pathname) {
//				return pathname.getName().endsWith("mpn");
//			}
//
//			public String getDescription() {
//				return "net files";
//			}
//		});
//				
//		chooser.showOpenDialog(frame);
//				
//		File file = chooser.getSelectedFile();

		File file = new File("C:/Dokumente und Einstellungen/rv.ARKTIS.000/runtime-workspace/simpleagent/src/org/micropsi/nodenet/mpn/10patch3.mpn");
		
		PatternTrainer trainer = new PatternTrainer(Pattern.ASCENDING,file);
		trainer.train();
	}

}
