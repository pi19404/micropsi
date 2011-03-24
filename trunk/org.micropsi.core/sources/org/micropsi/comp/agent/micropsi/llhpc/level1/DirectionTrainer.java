package org.micropsi.comp.agent.micropsi.llhpc.level1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class DirectionTrainer {
	
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
	
	private double targetValue0;
	private double targetValue1;
	private double targetValue2;
	
	private SignalDataSource target0;
	private SignalDataSource target1;
	private SignalDataSource target2;
	private SignalDataSource trainingTrigger;
	private SignalDataSource hypo;
	
	public DirectionTrainer(File rawNet) throws FileNotFoundException, MicropsiException {
		super();
		
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
		
		target0 = new SignalDataSource("target0");
		target1 = new SignalDataSource("target1");
		target2 = new SignalDataSource("target2");
		trainingTrigger = new SignalDataSource("trigger");
		hypo = new SignalDataSource("hypo");
		patternNet.getSensorRegistry().registerSensorDataProvider(target0);
		patternNet.getSensorRegistry().registerSensorDataProvider(target1);
		patternNet.getSensorRegistry().registerSensorDataProvider(target2);
		patternNet.getSensorRegistry().registerSensorDataProvider(hypo);
		patternNet.getSensorRegistry().registerSensorDataProvider(trainingTrigger);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
	}
	boolean stop = false;
	
	public void train() throws NetIntegrityException {

		JFrame patternFrame = new JFrame("Save?");
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

		BufferedImage sample;

		logger.info(" ---- start learning ----");
		
		
		int j = 0;
		
		double floatAverage1 = 0;
		double floatAverage2 = 0;
		double floatAverage3 = 0;
		
		while(!stop) {

			//double direction = (j % 100) / 100;
			double direction = Math.random() * 0.9;
			
			if(j % 1000 == 0) {
				logger.debug("step "+j+": ");
				//test(direction);
				logger.debug("dir "+(floatAverage1 /  j));
				logger.debug("grey "+(floatAverage2 /  j));
				logger.debug("hypo "+(floatAverage3 /  j));
			}
				
			sample = PatternGenerator.getInstance().generateDirection(direction,1.0,true);
			double sum = 0;
			for(int x=0;x<10;x++) {
				for(int y=0;y<10;y++) {
					sum += sample.getRaster().getSample(x,y,0);
				}
			}
			sum /= 100; // average
			sum /= 255; //scale to [0,1]				
				
		    double hypo = Math.random();
		    if(hypo >= direction) {
		     targetValue0 = direction + (hypo/5);
		    } else {
		     targetValue0 = direction - (hypo/5);
		    }
		    
		    targetValue1 = sum;
		    targetValue2 = 1 - Math.abs(hypo - direction);

//		    targetValue2 = 1; // Übereinstimmung des Hypothesen-Input mit direction 
				
			this.hypo.setValue(hypo);
			netImage = sample;
				
			learn();
			
			floatAverage1 += Math.abs(targetValue0 - v1);
			floatAverage2 += Math.abs(targetValue0 - v2);
			floatAverage3 += Math.abs(targetValue2 - v3);
							
			j++;
		}
		
		logger.debug("Final test:");
		for(int i=0;i<10;i++) {
			double direction = Math.random();
			test(direction);
		}
		
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
	
	double v1,v2,v3;
	
	private void learn() throws NetIntegrityException {
		
		// 10 cycles to spread activation
		NetCycleIF cycle = patternNet.getCycle();
		for(int i=0;i<10;i++) {
			cycle.nextCycle(false);
			
			if(i == 7) {
				target0.setValue(targetValue0);
				target1.setValue(targetValue1);
				target2.setValue(targetValue2);
			}
			if(i == 8) {
				trainingTrigger.setValue(1.0);
			}
		}
		
		v1 = debugNeuron("100-050918/191642");
		v2 = debugNeuron("3-050925/163530");
		v3 = debugNeuron("1-050926/134540");
		
		target0.setValue(0);
		target1.setValue(0);
		target2.setValue(0);
		trainingTrigger.setValue(0);
		
		// pace the net
		for(int i=0;i<10;i++) {
			cycle.nextCycle(false);
		}
		
	}
	
	private void test(double direction) throws NetIntegrityException {

		double hypo = Math.random();
		BufferedImage sample = PatternGenerator.getInstance().generateDirection(direction,1.0,true);
		
		double sum = 0;
		for(int x=0;x<10;x++) {
			for(int y=0;y<10;y++) {
				sum += sample.getRaster().getSample(x,y,0);
			}
		}
		sum /= 100; // average
		sum /= 255; //scale to [0,1]		
		
		// ------------------ good ---------------------------

		netImage = sample; 
		this.hypo.setValue(hypo);
		
		NetCycleIF cycle = patternNet.getCycle();
		for(int i=0;i<10;i++) {
			cycle.nextCycle(false);
		}
				
		double t = 0; 
		if(hypo >= direction) {
			t = direction + (hypo/5);
		} else {
			t = direction - (hypo/5);
		}
		
		logger.debug("direction error: "+(t - debugNeuron("100-050918/191642")));
		logger.debug("greyness error: "+(sum - debugNeuron("3-050925/163530")));
		logger.debug("hypo error: "+((1 - Math.abs(hypo - direction))-debugNeuron("1-050926/134540")));
		
		
		// ------------------ clean up ---------------------------
		
		netImage = null;
		this.hypo.setValue(0);

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
	
	private double debugNeuron(String id) {
		try {
			return ((Node)patternNet.getEntity(id)).getGenActivation();
		} catch (MicropsiException e) {
			logger.error("Debug error",e);
			return 0;
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

		File file = new File("C:/Dokumente und Einstellungen/rv.ARKTIS.000/runtime-workspace/simpleagent/src/org/micropsi/nodenet/mpn/10patch7.mpn");
		
		DirectionTrainer trainer = new DirectionTrainer(file);
		trainer.train();
	}

}
