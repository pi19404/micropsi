/*
 * Created on 24.03.2005
 *
 */
package org.micropsi.comp.robot.khepera5;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 */
public class Khepera {

	//motor
	private static String speed_motor_left="0";
	private static String speed_motor_right="0";
	private static String position_motor_left="0";
	private static String position_motor_right="0";
	private static String pos_left="0";
	private static String pos_right="0";
	
	//proximity sensors
	private static String ps1="70", ps2="70", ps3="70", ps4="70", ps5="70", ps6="70", ps7="70", ps8="70";
	   //private static Hashtable lookupTable = new Hashtable();
	
	//ambient light sensors
	private static String ls1="0", ls2="0", ls3="0", ls4="0", ls5="0", ls6="0", ls7="0", ls8="0";
	
	//position (has to be read from placecell file...)
	private static int[] positionXY = new int[] {11,22};
	
	//LEDs (0=off, 1=on, 2=change)
	private static String led0="0";
	private static String led1="0";
	
	
	private static OutputStreamWriter command;
	private static BufferedReader answer;
	private static String str;  //helper string
	private static StringTokenizer token;
	
	private Object lock = new Object();
	private Logger logger;
	
	public Khepera(OutputStreamWriter command, BufferedReader answer, Logger logger){
		Khepera.command = command;
		Khepera.answer = answer;
		this.logger = logger;

		/*BufferedReader in_sensor = null;
		BufferedReader in_cm = null;
		
		try {
			in_cm = new BufferedReader(new FileReader("matlab/sensor_prox(70to1100)_extrapol_cm(0to14)__distance.txt"));
			in_sensor = new BufferedReader(new FileReader("matlab/sensor_prox(70to1100)_extrapol_cm(14to0)__sensordata.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		//("C:\eclipse-SDK-3.0.1-win32-READ\eclipse\workspace\org.micropsi.whiskerbot\Matlab\sensor_prox(70to1100)_extrapol_cm(14to0)__sensordata.txt"));
		String sensor;
		String cm;
		
		try {
			while(((sensor=in_sensor.readLine()) != null) && ((cm=in_cm.readLine()) != null)){
				System.out.println(sensor+"  "+cm);
				lookupTable.put(sensor,cm);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Enumeration en = lookupTable.keys();
		while(en.hasMoreElements()){
			String alias = (String)en.nextElement();
			System.out.println(alias+" --> "+lookupTable.get(alias)
					+" | double: "+Double.parseDouble((String)lookupTable.get(alias)) * (-0.32));
		}
		
		try {
			in_sensor.close();
			in_cm.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}*/
		
	}
	
	/**
	 * @return Returns if operation runs well or not.
	 */
	public int getAmbientLightSensors() {
		
		//logger.debug("enterAmbient before lock");
		
		synchronized(lock) {
		
			//logger.debug("Enter ambient block: "+Thread.currentThread().getName());
			
			try {
				command.write("O\n");
				command.flush();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			} catch (IOException e) {
				logger.error("Robot communication problem: write() in getAmbientLightSensors()",e);
			}
			
			try {
				do {
					str = answer.readLine();
					//System.out.println("Antwort im buffer: "+help+" | "+help.charAt(0));
				} while (str.charAt(0)!='o');
			} catch (IOException e1) {
				logger.error("Robot communication problem: readLine() in getAmbientLightSensors()",e1);
			}
		
		}
		
		token = new StringTokenizer(str,",");
		token.nextToken(); //skip first element

		ls1 = token.nextToken();
		ls2 = token.nextToken();
		ls3 = token.nextToken();
		ls4 = token.nextToken();
		ls5 = token.nextToken();
		ls6 = token.nextToken();
		ls7 = token.nextToken();
		ls8 = token.nextToken();
	
		if(token.hasMoreTokens())
			return 0;
		else 
			return 1;
	}

	/**
	 * @return Returns if operation runs well or not.
	 */
	public int getProximitySensors() {
		
		long start = System.currentTimeMillis();	
		//synchronized(lock) {
			//logger.debug("Enter proximity block: "+Thread.currentThread().getName());	
			try {
				command.write("N\n");
				command.flush();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			} catch (IOException e) {
				logger.error("Robot communication problem: write() in getProximitySensors()",e);
			}
			long start2 = System.currentTimeMillis();
			try {
				str = answer.readLine();
					//logger.debug("answer: "+str);
				
				
				/*do {
					str = answer.readLine();
					answer.close();
					//System.out.println("Antwort im buffer: "+help+" | "+help.charAt(0));
				} while (str.charAt(0)!='n');*/
			} catch (IOException e1) {
				logger.error("Robot communication problem: readLine() in getProximitySensors()",e1);
			}
		//}
		long stop = System.currentTimeMillis();
		logger.debug("getProx: w="+(start2-start)+" r="+(stop - start2)+" | "+(stop - start)+" ms");
		
		
		token = new StringTokenizer(str,",");
		token.nextToken(); //skip first element

		ps1 = token.nextToken();
		ps2 = token.nextToken();
		ps3 = token.nextToken();
		ps4 = token.nextToken();
		ps5 = token.nextToken();
		ps6 = token.nextToken();
		ps7 = token.nextToken();
		ps8 = token.nextToken();
	
		//logger.warn("updated proximity values");
		
		if(token.hasMoreTokens()){
			logger.error("ProxSens has more tokens!!");
			return 0;	
		}
		else 
			return 1;	
	}
	
	
	/**
	 * @return Returns if operation runs well or not.
	 */
	public int getSpeed_motor() {
		
		synchronized(lock) {
			
			try {
				command.write("E\n");
				command.flush();
			} catch (IOException e) {
				logger.error("Robot communication problem: write() in getSpeed_motor()",e);
			}
			
			try {
				do {
					str = answer.readLine();
					//System.out.println("Antwort im buffer: "+help+" | "+help.charAt(0));
				} while (str.charAt(0)!='e');
			} catch (IOException e1) {
				logger.error("Robot communication problem: readLine() in getSpeed_motor()",e1);
			}
				
		}
		
		token = new StringTokenizer(str,",");
		token.nextToken(); //skip first element

		speed_motor_left = token.nextToken();
		speed_motor_right = token.nextToken();
		
		if(token.hasMoreTokens())
			return 0;
		else 
			return 1;	
	}
	
	/**
	 * @param parameter
	 * @param parameter2
	 */
	public int setSpeed_motor(String speed_motor_left, String speed_motor_right) {

		synchronized(lock) {
			
			//logger.debug("Enter speed block: "+Thread.currentThread().getName());
			//long start = System.currentTimeMillis();
			
			try {
				command.write("D,"+speed_motor_left+","+speed_motor_right+"\n");
				command.flush();
				answer.read();
			} catch (IOException e) {
				logger.error("Robot communication problem: setSpeed_motor()",e);
				return 0;
			}
		
			//long stop = System.currentTimeMillis();
			//logger.info("setSpeed: "+(stop-start)+" ms");
			
		}
		Khepera.speed_motor_left = speed_motor_left;	
		Khepera.speed_motor_right = speed_motor_right;
		return 1;
	}

	/**
	 * @param speed_motor_left The speed_motor_left to set.
	 */
	public int setSpeed_motor_left(String speed_motor_left) {
		
		synchronized(lock) {
			
			try {
				command.write("D,"+speed_motor_left+","+speed_motor_right+"\n");
				command.flush();
			} catch (IOException e) {
				logger.error("Robot communication problem: setSpeed_motor_left()",e);
				return 0;
			}
		
		}
		Khepera.speed_motor_left = speed_motor_left;	
		
		return 1;
	}
	
	/**
	 * @param speed_motor_right The speed_motor_right to set.
	 */
	public int setSpeed_motor_right(String speed_motor_right) {
		
		synchronized(lock) {
		
			try {
				command.write("D,"+speed_motor_left+","+speed_motor_right+"\n");
				command.flush();
			} catch (IOException e) {
				logger.error("Robot communication problem: setSpeed_motor_right()",e);
				return 0;
			}
		}
		
		Khepera.speed_motor_right = speed_motor_right;
		return 1;
	}
	
	/**
	 * 
	 * @param stop
	 * @return
	 */
	public int stop() {
		
		synchronized(lock) {
		
			try {
				command.write("D,0,0\n");
				command.flush();
				answer.read();
			} catch (IOException e) {
				logger.error("Robot communication problem: stop()",e);
				return 0;
			}
		}
		
		Khepera.speed_motor_right = "0";
		Khepera.speed_motor_left = "0";
		
		return 1;
	}
	
	/**
	 * @return Returns the speed_motor_left.
	 */
	public String getSpeed_motor_left() {
		return Khepera.speed_motor_left;
	}
	
	/**
	 * @return Returns the speed_motor_right.
	 */
	public String getSpeed_motor_right() {
		return Khepera.speed_motor_right;
	}
	
	/**
	 * @return Returns the ps1.
	 */
	public String getPs1() {
		return ps1;
	}
	/**
	 * @return Returns the ps2.
	 */
	public String getPs2() {
		return ps2;
	}
	/**
	 * @return Returns the ps3.
	 */
	public String getPs3() {
		return ps3;
	}
	/**
	 * @return Returns the ps4.
	 */
	public String getPs4() {
		return ps4;
	}
	/**
	 * @return Returns the ps5.
	 */
	public String getPs5() {
		return ps5;
	}
	/**
	 * @return Returns the ps6.
	 */
	public String getPs6() {
		return ps6;
	}
	/**
	 * @return Returns the ps7.
	 */
	public String getPs7() {
		return ps7;
	}
	/**
	 * @return Returns the ps8.
	 */
	public String getPs8() {
		return ps8;
	}
	
	public int[] getPosition() {
		return positionXY;
	}
	
	/**
	 * @return Returns the ls1.
	 */
	public String getLs1() {
		return ls1;
	}
	/**
	 * @return Returns the ls2.
	 */
	public String getLs2() {
		return ls2;
	}
	/**
	 * @return Returns the ls3.
	 */
	public String getLs3() {
		return ls3;
	}
	/**
	 * @return Returns the ls4.
	 */
	public String getLs4() {
		return ls4;
	}
	/**
	 * @return Returns the ls5.
	 */
	public String getLs5() {
		return ls5;
	}
	/**
	 * @return Returns the ls6.
	 */
	public String getLs6() {
		return ls6;
	}
	/**
	 * @return Returns the ls7.
	 */
	public String getLs7() {
		return ls7;
	}
	/**
	 * @return Returns the ls8.
	 */
	public String getLs8() {
		return ls8;
	}
	
	/**
	 * @param LED0 (0=off, 1=on, 2=change).
	 * @return Returns if operation worked well.
	 */
	public int setLED0(String LED0) {

		synchronized (lock) {

			try {
				command.write("L,0," + LED0 + "\n");
				command.flush();
			} catch (IOException e) {
				logger.error("Robot communication problem: setLED0()", e);
				return 0;
			}

		}

		if (LED0.equals("2")) {
			if (Khepera.led0.equals("0"))
				Khepera.led0 = "1";
			else
				Khepera.led0 = "0";
		} else
			Khepera.led0 = LED0;
		return 1;
	}
	
	/**
	 * @param LED1 (0=off, 1=on, 2=change).
	 * @return Returns if operation worked well.
	 */
	public int setLED1(String LED1) {

		synchronized (lock) {

			try {
				command.write("L,1," + LED1 + "\n");
				command.flush();
			} catch (IOException e) {
				logger.error("Robot communication problem: setLED1()", e);
				return 0;
			}

		}

		if (LED1.equals("2")) {
			if (Khepera.led1.equals("0"))
				Khepera.led1 = "1";
			else
				Khepera.led1 = "0";
		} else
			Khepera.led1 = LED1;
		return 1;
	}
	
	/**
	 * @return Returns status of LED1 (0=off, 1=on).
	 */
	public String getLED0(){
		return led0;
	}
	
	
	/**
	 * @return Returns status of LED2 (0=off, 1=on).
	 */
	public String getLED1(){
		return led1;
	}

}
