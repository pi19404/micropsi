package org.micropsi.comp.robot.khepera8.khepera;
/*
 * Created on 24.03.2005
 *
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import org.apache.log4j.Logger;


/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 */
public class Khepera {
  
	
	private Object writelock = new Object();
	private Object lock_sensors = new Object();
	private Logger logger;
	
	private SerialPort com = null;

	private OutputStream out ;
	private BufferedReader in;
	private String comPort = null;
	
	private final int BAUDRATE = 57600;  //baudrate for serial comm port and khepera. must correlate with the comm port driver settings
										 //(windows device manager) and the khepera hardware settings (knob on khepera body).
	private InputStream inStream;
	private boolean debug;
	private KheperaUpdateManager update;
	private BufferedKheperaAnswer bufferedKheperaAnswer;
	private String answer_complete_help;
	private String answer_complete = null;
	
	
	
	//proximity sensors
	private volatile String[] proximitySensors  = new String[]{"70", "70", "70", "70", "70", "70", "70", "70"};
	
	//ambient light sensors
	private volatile String[] ambientLightSensors = new String[]{"0", "0", "0", "0", "0", "0", "0", "0"};
	
	//orientation sensors
	private String orientation = "0.0";
	private String positionY   = "0.0";
	private String positionX   = "0.0";
 
	//Froncam Color Intensities
	
	private String red = "0.0";
	private String green = "0.0";
	private String blue = "0.0";
	private String yellow = "0.0";
	
	
	public Khepera(String comPortNumber, Logger logger, boolean debugging){
		logger.info("Khepera constructor...");
		this.comPort = comPortNumber;
		this.logger = logger;
		this.debug   = debugging;
		
		initialize();
	}
	
	public Khepera(Logger logger, boolean debugging){
		logger.info("Khepera constructor...");
		this.logger = logger;
		this.debug   = debugging;
		
		initialize();
	}
	
  
  
	private void initialize() {

// ==========================================
		/* 
		 * get System porperties:
		 */
		String operatingSystem = null;
			Properties p = System.getProperties();
			Enumeration e = p.elements();
			while (e.hasMoreElements()) {
				operatingSystem = e.nextElement().toString();
//				System.out.println(operatingSystem);
				if(operatingSystem.equals("Linux")){
					logger.debug("found "+operatingSystem+" Operating System");
					comPort = "/dev/ttyS0";
					break;
				}
				if(operatingSystem.equals("Windows XP")) {
					logger.debug("found "+operatingSystem+" Operating System");
					comPort = "COM1";
					break;
				}
		    	
//				if(operatingSystem.equals("Linux") || operatingSystem.equals("Windows")) {
//					logger.debug("found "+operatingSystem+" Operating System");
//					break;
//				}
			}
//	===============end===========================
			
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
//		logger.debug("(Khepera) initializeeeee()...");
		
		while(pList.hasMoreElements()){
			CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
			
			if (cpi.getName().equals(comPort)){
				try {
					com = null;
					com = (SerialPort)cpi.open("KHEPERA_"+comPort,1000);
					try {
						
//	========================================
						/* under Linux this block is crucial to setSerialPortParams()
						 * - i have no idea why...
						 */
						String s = "default settings: " + com.getBaudRate()
								+ " " + com.getDataBits() + " " + com.getParity()
								+ " " + com.getStopBits();
						System.out.println(s);
//	===============end=========================
						
						com.setSerialPortParams(57600,
								SerialPort.DATABITS_8, SerialPort.STOPBITS_2,
								SerialPort.PARITY_NONE);
						System.out.println("Current settings: " + com.getBaudRate()
								+ " " + com.getDataBits() + " " + com.getParity()
								+ " " + com.getStopBits());
					} catch (UnsupportedCommOperationException e1) {
						logger.debug("(Khepera) UnsupportedCommOperation: "+e1);
						e1.printStackTrace();
						}
//					try {
//						com.setSerialPortParams(BAUDRATE,
//								com.DATABITS_8,
//								com.STOPBITS_2,
//								com.PARITY_NONE);
//						com.setFlowControlMode(com.FLOWCONTROL_NONE);
//					} catch (UnsupportedCommOperationException e1) {
//						logger.debug("(Khepera) UnsupportedCommOperation: "+e1);
//						e1.printStackTrace();
//					}
					logger.debug("[Khepera.initialize()] established "+comPort);
					
					out = null;
					out = com.getOutputStream();
					inStream = null;
					inStream = com.getInputStream();
					
					byte[] readBuffer2 = new byte[1000];
					if(inStream.available() > 0){
						int numBytes2 = inStream.read(readBuffer2);
						String result2 = new String(readBuffer2,0,numBytes2);
						logger.debug("result2: "+result2);
					}
					
//					in = null;
					in = new BufferedReader(new InputStreamReader(inStream));
		
					bufferedKheperaAnswer = new BufferedKheperaAnswer(logger, inStream, 
							this, writelock, debug, operatingSystem);
					try {
						com.addEventListener(bufferedKheperaAnswer);
						
						com.notifyOnOutputEmpty(true);
						com.notifyOnDataAvailable(true);
						com.notifyOnOverrunError(true);
						com.notifyOnBreakInterrupt(true);
						com.notifyOnCarrierDetect(true);
						com.notifyOnCTS(true);
						com.notifyOnDSR(true);
						com.notifyOnFramingError(true);
						com.notifyOnParityError(true);
						com.notifyOnRingIndicator(false);
					} catch (TooManyListenersException e1) {
						logger.debug("[Khepera.initialize()] can not add further eventListeners to serialPort object"+e);
						e1.printStackTrace();
					}				
				} catch (PortInUseException e2) {
					logger.debug("[Khepera.initialize()] Port in use: "+e2);
					e2.printStackTrace();
				} catch (IOException e3) {
					logger.debug("[Khepera.initialize()] IOException while initialising COMport: <"+comPort+">"+e);
					e3.printStackTrace();
				}
				break;
			}
			
		}
		if(com == null)
			logger.error("[Khepera.initialize()] unable to find specified COMport "+comPort+". giving up! (the robot is not connected yet.)");
		else{
			logger.debug("[Khepera.initialize()] starting bufferedKheperaAnswer.start()");
			bufferedKheperaAnswer.start();
		
			if(debug) logger.debug("[Khepera.initialize()] initialize() finished");
			
			update = new KheperaUpdateManager(this, logger, debug);
			logger.debug("[Khepera.initialize()] starting KheperaUpdateManager update.start()");
			update.start();
		}
	}
	  
	
	public void setAnswer_complete(String answer_complete){
//		logger.debug("calling notify!");
		this.answer_complete = answer_complete;
		synchronized (writelock) {
			writelock.notify();
		}
//		logger.debug("called notify!");
	}
	
	public String getAnswer_complete(){
		return this.answer_complete;
	}
	
	
	public synchronized String write(String msg) throws IOException {
		
		try {
//			synchronized(writelock) {
				out.write(msg.getBytes());
				out.flush();
				//writelock.notify();
//			}
		} catch (IOException e) {
			e.printStackTrace();
			String s = "[Khepera.write()] failed: NoData";
			return s;
		}
		while(answer_complete == null) {
			try {
//				logger.debug("now waiting");
				synchronized (writelock) {
					writelock.wait();					
				}
//				logger.debug("waiting done");
			} catch (InterruptedException e) {
				logger.error("Failed waiting",e);
			}
		}
//		logger.debug("Answer of complete: "+answer_complete);
/*		if (answer_complete == null) {
			try {
				long start = System.currentTimeMillis();
				if (debug)
					logger.debug("[Khepera.write()] start wait()");
				try {
					if (debug)
						logger.debug("[Khepera.write()] lock.wait() ("
								+ System.currentTimeMillis() + ")");
					synchronized(waitForAnswerLock) {
						waitForAnswerLock.wait();
					}
					if (debug)
						logger.debug("[Khepera.write()] nach wait() ("
								+ System.currentTimeMillis()
								+ "): answer_complete=" + answer_complete);
				} catch (RuntimeException e1) {
					logger.error(
							"[Khepera.write()] lock.wait() RuntimeException",
							e1);
					e1.printStackTrace();
				}
				if (debug)
					logger.debug("[Khepera.write()] stop wait(): "
							+ (System.currentTimeMillis() - start) + "ms");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			logger.debug("[Khepera.write()] kann nich sein...");
		}
*/
		answer_complete_help = answer_complete;
		answer_complete = null;
//		writelock.notify();

		return answer_complete_help;
			
		}  
	
	
	public synchronized int updateProximitySensors() {
		String s = null;
		try {
			s = write("N\n");
			if(debug)logger.debug("[Khepera.updateProximitySensors()] answer="+s);
		} catch (IOException e) {
			logger.debug("[Khepera.updateProximitySensors()] write(), s="+s+" ",e);
			e.printStackTrace();
			return 0;
		}
		
		setProximitySensors(s);
		return 1;
		
	
	}
	
	public String[] getProximitySensors() {
		synchronized(lock_sensors){
			return proximitySensors;
		}
	}
	
	public int setProximitySensors(String s){
		synchronized(lock_sensors){
			String[] sBuff = new String[8];
			try {
				sBuff = s.substring(2).split(",",8);
			} catch (IndexOutOfBoundsException e) {
				logger.error("[Khepera.setProximitySensors()] in s.substring(2), s="+s+" ",e);
				e.printStackTrace();
				return 0;
			}
			
			int i=0;
			try {
				for(i=0; i<8; i++){
					Integer.parseInt(sBuff[i]);
				}	
			} catch (NumberFormatException e) {
				logger.error("[Khepera.setProximitySensors()] in sBuff["+i+"]="+sBuff[i]+" ",e);
				e.printStackTrace();
				return 0;
			}		
			proximitySensors = sBuff;
			return 1;
		}
	}
	
	
	public synchronized int updateAmbientLightSensors() {
		String s = null;
		try {
			s = write("O\n");
			if(debug)logger.debug("[Khepera.updateAmbientLightSensors()] answer="+s);
		} catch (IOException e) {
			logger.debug("[Khepera.updateAmbientLightSensors()] write(), s="+s+" ",e);
			e.printStackTrace();
			return 0;
		} 
		
		setAmbientLightSensors(s);
		return 1;
	}
	
	
	public String[] getAmbientLightSensors() {
		return ambientLightSensors;
	}

	
	public int setAmbientLightSensors(String s){
		synchronized(lock_sensors){
			String[] sBuff = new String[8];
			try {
				sBuff = s.substring(2).split(",",8);
			} catch (IndexOutOfBoundsException e) {
				logger.error("[Khepera.setAmbientLightSensors()] in s.substring(2), s="+s+" ",e);
				e.printStackTrace();
				return 0;
			}
			
			int i=0;
			try {
				for(i=0; i<8; i++){
					Integer.parseInt(sBuff[i]);
				}	
			} catch (NumberFormatException e) {
				logger.error("[Khepera.setAmbientLightSensors()] in sBuff["+i+"]="+sBuff[i]+" ",e);
				e.printStackTrace();
				return 0;
			}		
			ambientLightSensors = sBuff;
			return 1;
		}
	}
	
	
  public synchronized int setSpeed_motor(String speed_motor_left, String speed_motor_right) {
      
	  try{
        Integer.parseInt(speed_motor_left);
      } catch(NumberFormatException e){
        logger.error("[Khepera.setSpeed_motor()] false numberFormat! left = "+speed_motor_left);
        return 0;
      }
      
      try{
        Integer.parseInt(speed_motor_right);
      } catch(NumberFormatException e){
        logger.error("[Khepera.setSpeed_motor()] false numberFormat! left = "+speed_motor_right);
        return 0;
      }

	  try {
	        String s = write("D,"+speed_motor_left+","+speed_motor_right+"\n");
	        if(debug)logger.debug("[Khepera.setSpeed_motor()] answer="+s);
	  } catch (IOException e) {
		  logger.debug("[Khepera.setSpeed_motor()] write() IOException",e);
		  e.printStackTrace();
		  return 0;
	  }  

      
//      this.speed_motor_left = speed_motor_left;	
//      this.speed_motor_right = speed_motor_right;
      
        return 1;
    
  }

  
  public synchronized int stop() {

	  try {
	        String s = write("D,0,0\n");
	        if(debug)logger.debug("[Khepera.stop()] answer="+s);
	  } catch (IOException e) {
		  logger.debug("[Khepera.stop()] write() IOException",e);
		  e.printStackTrace();
		  return 0;
	  }  

      
//      this.speed_motor_left = speed_motor_left;	
//      this.speed_motor_right = speed_motor_right;
      
        return 1;
    
  }
    
  
  public synchronized int setLED0(String led0) {
	  
	  try{
		  Integer.parseInt(led0);
	  } catch(NumberFormatException e){
		  logger.error("[Khepera.setLED0()] false numberFormat! LED0 = "+led0);
		  return 0;
	  }
	  
	  if (led0=="0" || led0=="1" || led0=="2"){
		  
		  try {
			  String s = write("L,0," + led0 + "\n");
			  if(debug)logger.debug("[Khepera.setLED0()] answer="+s);    	
		  }catch (IOException e) {
			  logger.error("[Khepera.setLED0()] Robot communication problem: write() in setLED0()",e);
			  e.printStackTrace();
			  return 0;
		  }
	  }
	  else {
		  if(debug)logger.error("[Khepera.setLED0()] false numberValues (!=0,1,2)! LED0 = "+led0);
		  return 0;
	  }
		  
	  return 1;
	  
  }
  
  
  public synchronized int setLED1(String led1) {
	  
	  try{
		  Integer.parseInt(led1);
	  } catch(NumberFormatException e){
		  logger.error("[Khepera.setLED1()] false numberFormat! LED1 = "+led1);
		  return 0;
	  }
	  
	  if (led1=="0" || led1=="1" || led1=="2"){
			 
	  try {
		  String s = write("L,1," + led1 + "\n");
		  if(debug)logger.debug("[Khepera.setLED1()] answer="+s);    	
	  }catch (IOException e) {
		  logger.error("[Khepera.setLED1()] Robot communication problem: write() in setLED1()",e);
		  e.printStackTrace();
		  return 0;
	  }

	  }
	  else {
		  if(debug)logger.error("[Khepera.setLED1()] false numberValues (!=0,1,2)! LED1 = "+led1);
		  return 0;
	  }

	  
	  return 1;
	  
  }

  
  public synchronized int restart(){
	  
	  logger.debug("[Khepera.restart()] Trying khepera restart...");
	  
	  String s;
	  try {
		  s = write("restart\n");
	  }catch (IOException e) {
		  logger.error("[Khepera.restart()] Robot communication problem: write() in restart()",e);
		  e.printStackTrace();
		  return 0;
	  }
	  logger.debug(s);
	  
	  
	  //give signal:
	  for(int i=0;i<6;i++){
		  this.setLED0("2");
		  try {
			  Thread.sleep(400);
		  } catch (InterruptedException e) {
			  e.printStackTrace();
		  }
	  }
	  
	  logger.debug("[Khepera.restart()] ...khepera restarted!");
	  
	  return 1;
  }
  
 
  public synchronized void shutdown(){

		  update.setUpdate(false);
		  try {
			  Thread.sleep(300);
		  } catch (InterruptedException e3) {
			  e3.printStackTrace();
		  }
		  
		  try {
			  String s = write("restart\n");
			  logger.debug("[Khepera.shutdown()] restart(): "+s);
		  } catch (IOException e2) {
			  logger.error("[Khepera.shutdown()] IOException1!!",e2);
			  e2.printStackTrace();
		  }
	      
		  
		  //give signal:	
		  this.setLED0("1");
		  this.setLED1("1");
		  try {
			  Thread.sleep(3000);
		  } catch (InterruptedException e) {
			  e.printStackTrace();
		  }
		  this.setLED0("0");
		  this.setLED1("0");
		  
		  
		  
		  try {
			  Thread.sleep(300);
		  } catch (InterruptedException e3) {
			  e3.printStackTrace();
		  }
		  
		  bufferedKheperaAnswer.setUpdate(false);
		  
		  try {
			  Thread.sleep(300);
		  } catch (InterruptedException e3) {
			  e3.printStackTrace();
		  }
		  
		  com.removeEventListener();
		  
		  try {
			  com.close();
		  } catch (RuntimeException e) {
			  logger.error("[Khepera.shutdown()] IOException1!!",e);
			  e.printStackTrace();
		  }
		  
		  try {
			  out.close();
		  } catch (IOException e) {
			  logger.error("[Khepera.shutdown()] IOException2!!",e);
			  e.printStackTrace();
		  }
		  
		  try {
			  in.close();
		  } catch (IOException e1) {
			  logger.error("[Khepera.shutdown()] IOException3!!",e1);
			  e1.printStackTrace();
		  }
		  out = null;
		  in = null;
		  
		  logger.debug("[Khepera.shutdown()] ...khepera system halted!");
  }
  
  public String getPositionX() {
	  synchronized(lock_sensors){
		  return positionX;
	  }
  }
  
  public void setPositionX(String positionX) {
	  synchronized(lock_sensors){
		  this.positionX = positionX;
	  }
  }
  
  public String getPositionY() {
	  synchronized(lock_sensors){
		  return positionY;
	  }
  }
  
  public void setPositionY(String positionY) {
	  synchronized(lock_sensors){
		  this.positionY = positionY;
	  }
  }
  
  public String getOrientation() {
	  synchronized(lock_sensors){
		  return orientation;
	  }
  }
  
  public void setOrientation(String orientation) {
	  synchronized(lock_sensors){
		  this.orientation = orientation;
	  }
  }
  
  //For the frontcam
  
  public void setIntensityRed(String red) {
	  synchronized(lock_sensors){
		  this.red = red;
	  }
  }
  
  public void setIntensityGreen(String green) {
	  synchronized(lock_sensors){
		  this.green = green;
	  }
  }
  
  public void setIntensityBlue(String blue) {
	  synchronized(lock_sensors){
		  this.blue = blue;
	  }
  }
  
  public void setIntensityYell(String yellow) {
	  synchronized(lock_sensors){
		  this.yellow = yellow;
	  }
  }
  
  public String getRed() {
	  synchronized(lock_sensors){
		  return red;
	  }
  }
  
  public String getGreen() {
	  synchronized(lock_sensors){
		  return green;
	  }
  }
  
  public String getBlue() {
	  synchronized(lock_sensors){
		  return blue;
	  }
  }
  
  
  public String getYell() {
	  synchronized(lock_sensors){
		  return yellow;
	  }
  }
  

}
