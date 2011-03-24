package org.micropsi.comp.robot.khepera8;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.micropsi.comp.robot.khepera8.khepera.Khepera;
import java.util.*;

public class CopyOfTestServer {

	public static void main(String args[]) {
		
		System.err.println("Sepp!");
		System.err.println(System.getProperty("java.library.path"));

		
		
		final int CYCLES = 200000;
		Logger logger;
		boolean debug = true;//false;
		long start = 0, stop = 0, go = 0;
		logger = Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new PatternLayout()));
		logger.setLevel(Level.DEBUG);
		Khepera khepera = new Khepera("/dev/ttyS0", logger, debug);
		
		String[] proximitySensors  = new String[]{"70", "70", "70", "70", "70", "70", "70", "70"};
		String[] ambientLightSensors = new String[]{"0", "0", "0", "0", "0", "0", "0", "0"};


		logger.debug("====================START====================================");		

//		khepera.setSpeed_motor("2", "2");
		
		for (int i = 0; i < CYCLES; i++) {
			logger.debug("(main) i="+i);
			int caseNumber = (i+(int)(Math.random()*10)) % 6;
			logger.debug("(main) case = "+caseNumber);
			switch (caseNumber) {
//			case 0: {
//				start = System.currentTimeMillis();
//				if(debug)logger.debug("===========LED0=============================================");
//				khepera.setLED0("2");
//				logger.debug("\tLED0\t"+ (System.currentTimeMillis() - start));
//				break;
//			}
//			case 1: {
//				start = System.currentTimeMillis();
//				if(debug)logger.debug("=============SPEED(2,2)===========================================");
//				khepera.setSpeed_motor("2", "2");
//				logger.debug("\tSpeed1\t"+(System.currentTimeMillis() - start));
//				break;
//			}
			case 2: {
				start = System.currentTimeMillis();
				if(debug)logger.debug("=============PROX===========================================");
				khepera.getProximitySensors();
				logger.debug("\tProx\t"+(System.currentTimeMillis() - start));
				break;
			}
//			case 3: {
//				start = System.currentTimeMillis();
//				if(debug)logger.debug("=================LED1=======================================");
//				khepera.setLED1("2");
//				logger.debug("\tLED1\t"+(System.currentTimeMillis() - start));
//				break;
//			}
//			case 4: {
//				start = System.currentTimeMillis();
//				if(debug)logger.debug("=====================LIGHT===================================");
//				khepera.getAmbientLightSensors();
//				logger.debug("\tLight\t"+(System.currentTimeMillis() - start));
//				break;
//			}
//			case 5: {
//				start = System.currentTimeMillis();
//				if(debug)logger.debug("=======================SPEED(-2,-2)=================================");
//				khepera.setSpeed_motor("-2", "-2");
//				logger.debug("\tSpeed2\t"+(System.currentTimeMillis() - start));
//				break;
//			}

			default:
				break;
			}

			
			proximitySensors = khepera.getProximitySensors();
			for(int j=0;j<8;j++){
				System.out.print(proximitySensors[j]+",");
			}
			System.out.println();
			
//			ambientLightSensors = khepera.getAmbientLightSensors();
//			for(int j=0;j<8;j++){
//				System.out.print(ambientLightSensors[j]+",");
//			}
//			System.out.println();

			try {
				int t = 110 + (int) ((Math.random() * 40) - 20);
//				System.out.println(t);
				Thread.sleep(t);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		logger.debug("feeeerrrtiiiig!!!!!!!");
		
		try {
			int t = 1100 + (int) ((Math.random() * 40) - 20);
			System.out.println(t);
			Thread.sleep(t);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		khepera.restart();
		try {
			int t = 4100 + (int) ((Math.random() * 40) - 20);
//			System.out.println(t);
			Thread.sleep(t);
		} catch (InterruptedException e3) {
			e3.printStackTrace();
		}
		
		khepera.shutdown();

	}

}