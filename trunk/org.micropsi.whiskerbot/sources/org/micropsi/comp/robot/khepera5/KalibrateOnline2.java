/*
 * Created on 21.04.2005
 *
 */
package org.micropsi.comp.robot.khepera5;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 */
public class KalibrateOnline2 {

	private static KheperaManager comPort;
	
	private static final int QUANTITY = 100;//50;
	private static int[][] data_array = new int[16][QUANTITY]; //p1-p8 l1-l8
	private static int x, sum = 0;
	private static int[][] statistics = new int[16][2]; //p_max p_min l_max l_min 
	private static BufferedReader tastatur = new BufferedReader(new InputStreamReader(System.in));
	
	public static double f(String s){
		return (-1.0 * ((Double.parseDouble(s) - 80)/970.0)) + 1.0;
	}
	
	
	public static void main(String[] args) {
		
		Logger logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		logger.addAppender(new ConsoleAppender(new PatternLayout()));
		
		comPort = KheperaManager.getInstance(logger); 
		//comPort.khepera.setSpeed_motor_left("5");
		//comPort.khepera.setLED1("1");
		long start, stop;
		boolean go = true;
		
		for(int i = 0;i<500;i++){
		//while(go){
			
			/*try {
				KheperaManager2.getInstance(logger).update.pause(true);
			} catch (RuntimeException e) {
				logger.error("Exc: ",e);
			}*/
			
			start = System.currentTimeMillis();
			comPort.khepera.getProximitySensors();
			//comPort.khepera.setLED0("2");			
			//comPort.khepera.setSpeed_motor_left("1");
			System.out.println(System.currentTimeMillis() - start+" | "+comPort.khepera.getPs1() );
			
/*						
			start = System.currentTimeMillis();
			comPort.khepera.getPs3();
			stop = System.currentTimeMillis();
			System.out.println("getPs3: "+(stop-start)+" ms");
			
			start = System.currentTimeMillis();
			comPort.khepera.getProximitySensors();
			System.out.println(start - System.currentTimeMillis()+" | "+comPort.khepera.getPs1() );
			stop = System.currentTimeMillis();
			System.out.println("getProximitySensors: "+(stop-start)+" ms");
			
			start = System.currentTimeMillis();
			comPort.khepera.getProximitySensors();
			comPort.khepera.getPs3();
			stop = System.currentTimeMillis();
			System.out.println("getProximitySensors + getPs3: "+(stop-start)+" ms");
			
			System.out.println("");
*/			
			/*start = System.currentTimeMillis();
			comPort.khepera.getLED0();
			stop = System.currentTimeMillis();
			System.out.println("getLED0: "+(stop-start)+" ms");
			
			start = System.currentTimeMillis();
			comPort.khepera.setLED0("2");
			stop = System.currentTimeMillis();
			System.out.println("setLED0: "+(stop-start)+" ms");
			
			start = System.currentTimeMillis();
			comPort.khepera.setLED0("2");
			comPort.khepera.getLED0();
			stop = System.currentTimeMillis();
			
			System.out.println("setLED0 + getLED0: "+(stop-start)+" ms");
				
			System.out.println("");
			start = System.currentTimeMillis();
			comPort.khepera.getSpeed_motor_left();
			stop = System.currentTimeMillis();
			System.out.println("getSpeed_motor_left: "+(stop-start)+" ms");
			
			start = System.currentTimeMillis();
			comPort.khepera.setSpeed_motor_left("1");
			stop = System.currentTimeMillis();
			System.out.println("setSpeed_motor_left: "+(stop-start)+" ms");
			
			start = System.currentTimeMillis();
			comPort.khepera.setSpeed_motor_left("2");
			comPort.khepera.getSpeed_motor_left();
			stop = System.currentTimeMillis();
			System.out.println("setSpeed_m_l + getSpeed_m_l: "+(stop-start)+" ms");
			*/
			System.out.println(""+i);
		
			/*try {
				KheperaManager2.getInstance(logger).update.pause(false);
			} catch (RuntimeException e) {
				logger.error("Exc: ",e);
			}*/
		}
		
		
		
	/*for (int i=0;i<QUANTITY;i++){
			comPort.khepera.getProximitySensors();
			//comPort.khepera.setLED1("2");
			//System.out.println("LED1: "+comPort.khepera.getLED1());
			System.out.println(i+" \t|\t"+comPort.khepera.getPs1()+"\t"+comPort.khepera.getPs2()
					+"\t"+comPort.khepera.getPs3()+"\t"+comPort.khepera.getPs4()
					+"\t"+comPort.khepera.getPs5()+"\t"+comPort.khepera.getPs6()
					+"\t"+(Integer.parseInt(comPort.khepera.getPs7()))+"\t"+comPort.khepera.getPs8());
			
			
			
			System.out.println(i+" \t|\t"+f(comPort.khepera.getPs1())+"\t"+f(comPort.khepera.getPs2())
					+"\t"+f(comPort.khepera.getPs3())+"\t"+f(comPort.khepera.getPs4())
					+"\t"+f(comPort.khepera.getPs5())+"\t"+f(comPort.khepera.getPs6())
					+"\t"+f(comPort.khepera.getPs7())+"\t"+f(comPort.khepera.getPs8()));
					

			comPort.khepera.getAmbientLightSensors();
			comPort.khepera.setLED1("2");
			//System.out.println("LED1: "+comPort.khepera.getLED1());
			System.out.println(i+" \t|\t"+comPort.khepera.getLs1()+"\t"+comPort.khepera.getLs2()
					+"\t"+comPort.khepera.getLs3()+"\t"+comPort.khepera.getLs4()
					+"\t"+comPort.khepera.getLs5()+"\t"+comPort.khepera.getLs6()
					+"\t"+comPort.khepera.getLs7()+"\t"+comPort.khepera.getLs8());
				
		
		}*/
		//comPort.khepera.setLED0("1");

		
						
	/*	try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			logger.error("Exception:",e1);
		}
		
		logger.debug("Stop!");
		
		try {
			KheperaManager2.getInstance(logger).update.suspended();
		} catch (RuntimeException e) {
			logger.error("Exc: ",e);
		}
		
		try {
			KheperaManager2.getInstance(logger).shutdown();
		} catch (RuntimeException e2) {
			logger.error("Exc: ",e2);
			e2.printStackTrace();
		}
		logger.debug("Speed motor left: "+comPort.khepera.getSpeed_motor_left());
	*/
		}
	
}
