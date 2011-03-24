/*
 * Created on 24.03.2005
 *
 */
package org.micropsi.comp.robot.khepera7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import org.apache.log4j.Logger;
//import org.micropsi.comp.robot.khepera7.KheperaManager;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 */
public class Khepera {
  
  //motor
  private String speed_motor_left="0";
  private String speed_motor_right="0";
  private String position_motor_left="0";
  private String position_motor_right="0";
  private String pos_left="0";
  private String pos_right="0";
  
  //proximity sensors
  private volatile String ps1="70", ps2="70", ps3="70", ps4="70", ps5="70", ps6="70", ps7="70", ps8="70";
  //private static Hashtable lookupTable = new Hashtable();
  
  //ambient light sensors
  private volatile String ls1="0", ls2="0", ls3="0", ls4="0", ls5="0", ls6="0", ls7="0", ls8="0";
  
  //position (has to be read from placecell file...)
  private volatile String positionX = "3", positionY = "2";
  private volatile String orientation = "0";
   
  //LEDs (0=off, 1=on, 2=change)
  private String led0="0";
  private String led1="0";
  
  
  private String str = "";  //helper string
  private StringTokenizer token;
  
  private Object lock = new Object();
  private Logger logger;
  private boolean debug = true; //logger messages on|off
  
  private char[] cbuf = new char[100];
  
  private SerialPort com;
  private OutputStreamWriter command ;
  private BufferedReader answer;
  private String comPort = null;
  public KheperaUpdateManager update;
  private List l;
  
  private final int SLEEP = 5;  //time for robot (57600 baud = 10 ms | 112500 baud = 5 ms) to fill answer buffer
  private final int ROUNDS = 1000; //1000//how often do have getProx/getLight/setMotor/stop the chance for answer.ready().
  private final int BAUDRATE = 115200; //baudrate for serial comm port and khepera. must correlate with the comm port driver settings
  									   //(windows device manager) and the khepera hardware settings (knob on khepera body).
  
  public Khepera(String comPortNumber, Logger logger, boolean debugging){
	  logger.info("Khepera constructor...");
      this.comPort = comPortNumber;
      this.logger = logger;
      this.debug   = debugging;
 
      initialize();
  }

  public Khepera(String comPortNumber, Logger logger, boolean debugging, List l){
      this.comPort = comPortNumber;
      this.logger = logger;
      this.debug   = debugging;
      this.l = l;
      this.debug = false;//!!!!!!!!!
      
      initialize();
  }


  
  
  private void initialize() {
	    
	    Enumeration pList = CommPortIdentifier.getPortIdentifiers();
	    logger.debug("(Khepera) initialize()...");
	    
	    while(pList.hasMoreElements()){
	      CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
	      
	      if (cpi.getName().equals(comPort)){
	        try {
	          com = (SerialPort)cpi.open("KHEPERA_"+comPort,1000);
	          try {
	            com.setSerialPortParams(BAUDRATE,
	                com.DATABITS_8,
	                com.STOPBITS_2,
	                com.PARITY_NONE);
	            com.setFlowControlMode(com.FLOWCONTROL_NONE
	            );
	            
	            /*
	             try {
	             outputStream.write(messageString.getBytes());
	             } catch (IOException e) {
	             e.printStackTrace();
	             }
	             */
	            
	          } catch (UnsupportedCommOperationException e1) {
	            logger.debug("(Khepera) UnsupportedCommOperation: "+e1);
	            e1.printStackTrace();
	          }
	          logger.debug("(Khepera)established "+comPort);
	          
	          //command = new BufferedWriter(new OutputStreamWriter(com1.getOutputStream()));//,15);
	          command = new OutputStreamWriter(com.getOutputStream());//,15);
	          answer = new BufferedReader(new InputStreamReader(com.getInputStream()));//,50);
	          //answer = new InputStreamReader(com1.getInputStream());//,50);
	        } catch (PortInUseException e) {
	          logger.debug("(Khepera) Port in use: "+e);
	          e.printStackTrace();
	        } catch (IOException e) {
	          logger.debug("IOException while initialising COM1: "+e);
	          e.printStackTrace();
	        }
	        break;
	      }
	    }
	
	    try {
			command.write("serial "+BAUDRATE+"\n");
		} catch (IOException e) {
			logger.debug("[Khepera.initialize() write()] IOException while setting khepera baudrate: "+e);
			e.printStackTrace();
		}
	    try {
			flush2();
		} catch (IOException e) {
			logger.debug("[Khepera.initialize() flush2()] IOException while setting khepera baudrate: "+e);
			e.printStackTrace();
		}
	    
	    if(debug) logger.debug("(Khepera) initialize() finished");
	    
	   update = new KheperaUpdateManager(this, logger, debug);
	   update.start();
	    
	   logger.debug(update.toString());
	    
	  }
	  
  
  
  public void shutdown(){
      synchronized(lock){
          
          try {
              command.write("restart\n");
              command.flush();
          } catch (IOException e2) {
        	  logger.error("shutdown() IOException1!!",e2);
        	  e2.printStackTrace();
          }
          try {
              command.close();
          } catch (IOException e) {
        	  logger.error("shutdown() IOException2!!",e);
        	  e.printStackTrace();
          }
          try {
              answer.close();
          } catch (IOException e1) {
        	  logger.error("shutdown() IOException3!!",e1);
        	  e1.printStackTrace();
          }
      }
  }
  
  
  private void flush2(int ms){	
    
    wait(ms);
    
    try {
      command.flush();
    } catch (IOException e) {
      logger.error("(flush2(int)) flush() IOException!!",e);
      e.printStackTrace();
    }
  }
  
  
  private void flush2()
    throws IOException
  {	
      
      wait(1);
      try {
          command.flush();
        } catch (IOException e) {
          logger.error("(flush2()) flush() IOException!!",e);
          e.printStackTrace();
        } 
   	
  }
  
  
  public void wait(int ms){
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      logger.error("wait() Thread.sleep error!",e);
      e.printStackTrace();
    }
  }
  
  /*
  ///old before ronnie:
  public int getAmbientLightSensors() {
    
    int i = 0;
    String str = "";		
    int h = 0;
    int j = 0;
    int not_ready = 0;
    
    synchronized(lock){
      
      try {command.write("O\n");
      }catch (IOException e) {
        if(debug) logger.error("Robot communication problem: write() in getLightSensors()",e);
        else System.out.println("Robot communication problem: write() in getLightSensors()"+e);
        e.printStackTrace();
        return 0;
      }
      
      
      try {
        flush2();
      } catch (IOException e) {
        if(debug) logger.error("(Light) flush2() IOException!!",e);
        else System.out.println("(Light) flush2() IOException!!"+e);
        e.printStackTrace();
        return 0;
      } 
      
      for (i=0;i<ROUNDS;i++){			
        try {
          if(answer.ready()) {
            logger.debug("(Light) answer.ready() = true");
            ++j;
            wait(5);
            try {
              for (h=0;h<cbuf.length;h++){
                if((cbuf[h] = (char)answer.read())==10){ 
                  //System.out.println("cbuf["+h+"] = \\n");		
                  break;
                }
                //else System.out.println("cbuf:"+String.valueOf(cbuf));	
              }				
              str = String.valueOf(cbuf,0,h-1);
              logger.debug(str);
              wait(1);
            } catch (IOException e1) {
              e1.printStackTrace();
            }
            break;
          }
          else {
            ++not_ready;
            wait(1);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      logger.debug("(Light) after for loop i ="+i);
      if(i==ROUNDS){
        logger.error("(Light) failed!");
        return 0;
      }
      
      token = new StringTokenizer(str,",");
      
      
//       if ((token.countTokens() != 9) || !(rightChar)){
//       if(debug) logger.warn("(Light) failed! #tokens="+token.countTokens()+" | rightChar="+rightChar);
//       else System.out.println("(Light) failed! #tokens="+token.countTokens()+" | rightChar="+rightChar);
//       try {
//       answer.reset();
//       } catch (IOException e) {
//       e.printStackTrace();
//       }
//       return 0;
//       }
      
      
      token.nextToken(); //skip first element
      
      try{
        ls1 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        if(debug) logger.error("(getLightSens) false numberFormat!");
      }
      try{
        ls2 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        if(debug) logger.error("(getLightSens) false numberFormat!");
      }
      try{
        ls3 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        if(debug) logger.error("(getLightSens) false numberFormat!");
      }
      try{
        ls4 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        if(debug) logger.error("(getLightSens) false numberFormat!");
      }
      try{
        ls5 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        if(debug) logger.error("(getLightSens) false numberFormat!");
      }
      try{
        ls6 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        if(debug) logger.error("(getLightSens) false numberFormat!");
      }
      try{
        ls7 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        if(debug) logger.error("(getLightSens) false numberFormat!");
      }
      try{
        ls8 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        if(debug) logger.error("(getLightSens) false numberFormat!");
      }
      
      if(debug) logger.debug("Light geschafft!");
      else System.out.println("Light geschafft!");
      
      return 1;	
      
    }
  }
  */
  
  public int getProximitySensors() {

        int i = 0;
        String str = "";
        int h = 0;
        int j = 0;
        int not_ready = 0;

        wait(1);

        synchronized (lock) {
            try {
                command.write("N\n");
            } catch (IOException e) {
            	logger.error("Robot communication problem: write() in getProxSensors()", e);
                e.printStackTrace();
                return 0;
            }

            try {
                flush2();
            } catch (IOException e) {
            	logger.error("(Prox) flush2() IOException!!", e);
                e.printStackTrace();
                return 0;
            }

            for (i = 0; i < ROUNDS; i++) {
                try {
                    if (answer.ready()) {
                        ++j;
                        wait(5);
                        try {
                            if ((cbuf[0] = (char) answer.read()) == 110) { // first character "n"?
                                if ((cbuf[1] = (char) answer.read()) == 44) { // 2nd character                                     // ","?
                                    for (h = 2; h < cbuf.length; h++) {
                                        if ((cbuf[h] = (char) answer.read()) == 10) { // last
                                            // char
                                            // '\n'?
                                            if (cbuf[h - 1] == 13) { // char
                                                // before
                                                // last
                                                // '\r'?
                                                // System.out.println("cbuf["+h+"]
                                                // = \\n");
                                                break;
                                            }
                                        }
                                        if (debug)
                                            System.out.println("(Prox) cbuf["
                                                    + h + "]="
                                                    + String.valueOf(cbuf[h]));
                                    }
                                    try {
                                        str = String.valueOf(cbuf, 0, h - 1);
                                    } catch (RuntimeException e) {
                                    	logger
                                    	.debug("(Prox) String.valueOf(cbuf,0,"
                                    			+ (h - 1)
                                    			+ ") failed! (="
                                    			+ str + ")",e);
                                        e.printStackTrace();
                                  	return 0;
                                    }
                                    if (debug) logger.debug("(Prox) " + str);
                                    wait(1);
                                    break;
                                }
                            }
                        } catch (IOException e1) {
//                        	logger.debug("(Prox) try{ for...}catch: error",e1);
//                            e1.printStackTrace();
//                            return 0;
                        }
                        // try {
                        // for (h=0;h<cbuf.length;h++){
                        // if((cbuf[h] = (char)answer.read())==10){
                        // //System.out.println("cbuf["+h+"] = \\n");
                        // break;
                        // }
                        // //else
                        // System.out.println("cbuf:"+String.valueOf(cbuf));
                        // }
                        // str = String.valueOf(cbuf,0,h-1);
                        // if(debug) logger.debug("(Prox) "+str);
                        // else System.out.println("(Prox) "+str);
                        // wait(1);
                        // } catch (IOException e1) {
                        // e1.printStackTrace();
                        // }
                        // break;
                    } else {
                        ++not_ready;
                        wait(1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }// end synchronized

        if (debug) logger.debug("(Prox) after for loop i =" + i);
        if (i == ROUNDS) {
        	logger.error("(Prox) failed! (i=ROUNDS="+ROUNDS+", any answer.ready()="
        			+ (j != 0));
        	return 0;
        }
        if (str.length() < 20) {
        	logger.error("(Prox) failed! (str.length() < 20: "
        			+ str.length());
        	return 0;
        }
        token = new StringTokenizer(str, ",");

        if (token.countTokens() != 9){
        	logger.error("(Prox) failed! 9 != #tokens = "+token.countTokens());
        	return 0;
        }
        /*
         * if ((token.countTokens() != 9) || !(rightChar)){ if(debug)
         * logger.warn("(Light) failed! #tokens="+token.countTokens()+" |
         * rightChar="+rightChar); else System.out.println("(Light) failed!
         * #tokens="+token.countTokens()+" | rightChar="+rightChar); try {
         * answer.reset(); } catch (IOException e) { e.printStackTrace(); }
         * return 0; }
         */

        try {
            token.nextToken(); // skip first element
        } catch (RuntimeException e1) {
        	logger.error("(getProxSens) nextToken() error!",e1);
            e1.printStackTrace();
            return 0;
        }

        try {
            ps1 = String.valueOf(Integer.parseInt(token.nextToken()));
        } catch (NumberFormatException e) {
        	logger.error("(getProxSens) false numberFormat!",e);
            e.printStackTrace();
            return 0;
        }
        try {
            ps2 = String.valueOf(Integer.parseInt(token.nextToken()));
        } catch (NumberFormatException e) {
        	logger.error("(getProxSens) false numberFormat!",e);
            e.printStackTrace();
            return 0;
        }
        try {
            ps3 = String.valueOf(Integer.parseInt(token.nextToken()));
        } catch (NumberFormatException e) {
        	logger.error("(getProxSens) false numberFormat!",e);
            e.printStackTrace();
            return 0;
        }
        try {
            ps4 = String.valueOf(Integer.parseInt(token.nextToken()));
        } catch (NumberFormatException e) {
        	logger.error("(getProxSens) false numberFormat!",e);
            e.printStackTrace();
            return 0;
        }
        try {
            ps5 = String.valueOf(Integer.parseInt(token.nextToken()));
        } catch (NumberFormatException e) {
        	logger.error("(getProxSens) false numberFormat!",e);
            e.printStackTrace();
            return 0;
        }
        try {
            ps6 = String.valueOf(Integer.parseInt(token.nextToken()));
        } catch (NumberFormatException e) {
        	logger.error("(getProxSens) false numberFormat!",e);
            e.printStackTrace();
            return 0;
        }
        try {
            ps7 = String.valueOf(Integer.parseInt(token.nextToken()));
        } catch (NumberFormatException e) {
        	logger.error("(getProxSens) false numberFormat!",e);
            e.printStackTrace();
            return 0;
        }
        try {
            ps8 = String.valueOf(Integer.parseInt(token.nextToken()));
        } catch (NumberFormatException e) {
        	logger.error("(getProxSens) false numberFormat!",e);
            e.printStackTrace();
            return 0;
        }

        if (debug)
            logger.debug("Prox geschafft!");

        return 1;

    }
  
  
  
public int getAmbientLightSensors() {
    
      
      int i = 0;
      String str = "";    
      int h = 0;
      int j = 0;
      int not_ready = 0;
      
      wait(1);
 
    synchronized(lock){
      
      try {command.write("O\n");
      }catch (IOException e) {
        logger.error("Robot communication problem: write() in getLightSensors()",e);
        e.printStackTrace();
        return 0;
      }
      
      try {
        flush2();
      } catch (IOException e) {
        logger.error("(Light) flush2() IOException!!",e);
        e.printStackTrace();
        return 0;
      } 
      
      
      for (i=0;i<ROUNDS;i++){     
        try {
          if(answer.ready()) {
            ++j;
            wait(5);
            try {
              if((cbuf[0] = (char)answer.read())==111){ //first letter "o"?
            	  if(debug) System.out.println("(Light) cbuf[0]="+String.valueOf(cbuf[0]));
                if((cbuf[1] = (char)answer.read())==44){ //2nd letter ","?
                	if(debug) System.out.println("(Light) cbuf[1]="+String.valueOf(cbuf[1]));
                  for (h=2;h<cbuf.length;h++){
                    if((cbuf[h] = (char)answer.read())==10){ //last char '\n'?
                      if (cbuf[h-1]==13){ //char before last '\r'?
                        //System.out.println("cbuf["+h+"] = \\n");    
                        break;
                      }
                    }
                    if(debug) System.out.println("(Light) cbuf["+h+"]="+String.valueOf(cbuf[h]));
                  }       
                  try {
                    str = String.valueOf(cbuf,0,h-1);
                  } catch (RuntimeException e) {
                    logger.debug("(Light) String.valueOf(cbuf,0,"+(h-1)+") failed! (="+str+")");
                    return 0;
                  }
                  if(debug) logger.debug("(Light) "+str);
                  wait(1);
                  break;
                }
              }
            } catch (IOException e1) {
//            	logger.debug("(Light) try{ for...}catch: error",e1);
//              e1.printStackTrace();
//              return 0;
            }
//          try {
//          for (h=0;h<cbuf.length;h++){
//          if((cbuf[h] = (char)answer.read())==10){ 
//          //System.out.println("cbuf["+h+"] = \\n");    
//          break;
//          }
//          //else System.out.println("cbuf:"+String.valueOf(cbuf));  
//          }       
//          str = String.valueOf(cbuf,0,h-1);
//          if(debug) logger.debug("(Light) "+str);
//          else System.out.println("(Light) "+str);
//          wait(1);
//          } catch (IOException e1) {
//          e1.printStackTrace();
//          }
            //break;
          }
          else {
            ++not_ready;
            wait(1);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } //end synchronized
      
      if(debug) logger.debug("(Light) after for loop i ="+i);
      
      if(i==ROUNDS){
        logger.error("(Light) failed! (i=ROUNDS="+ROUNDS+", any answer.ready()="+(j!=0));
        return 0;
      }
      if(str.length()<20){
        logger.error("(Light) failed! (str.length() < 20: "+str.length());
        return 0;
      }
      token = new StringTokenizer(str,",");
      
      if (token.countTokens() != 9){
      	logger.error("(Light) failed! 9 != #tokens = "+token.countTokens());
      	return 0;
      }
      
      /*
       if ((token.countTokens() != 9) || !(rightChar)){
       if(debug) logger.warn("(Light) failed! #tokens="+token.countTokens()+" | rightChar="+rightChar);
       else System.out.println("(Light) failed! #tokens="+token.countTokens()+" | rightChar="+rightChar);
       try {
       answer.reset();
       } catch (IOException e) {
       e.printStackTrace();
       }
       return 0;
       }
       */
      
      try {
        token.nextToken(); //skip first element
      } catch (RuntimeException e1) {
        logger.error("(getLightSens) nextToken() error!",e1);
        e1.printStackTrace();
        return 0;
      } 
      
      
      try{
        ls1 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        logger.error("(getLightSens) false numberFormat!",e);
        e.printStackTrace();
        return 0;
      }
      try{
        ls2 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        logger.error("(getLightSens) false numberFormat!",e);
        e.printStackTrace();
        return 0;
      }
      try{
        ls3 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        logger.error("(getLightSens) false numberFormat!",e);
        e.printStackTrace();
        return 0;
      }
      try{
        ls4 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        logger.error("(getLightSens) false numberFormat!",e);
        e.printStackTrace();
        return 0;
      }
      try{
        ls5 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        logger.error("(getLightSens) false numberFormat!",e);
        e.printStackTrace();
        return 0;
      }
      try{
        ls6 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        logger.error("(getLightSens) false numberFormat!",e);
        e.printStackTrace();
        return 0;
      }
      try{
        ls7 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        logger.error("(getLightSens) false numberFormat!",e);
        e.printStackTrace();
        return 0;
      }
      try{
        ls8 = String.valueOf(Integer.parseInt(token.nextToken()));
      } catch(NumberFormatException e){
        logger.error("(getLightSens) false numberFormat!",e);
        e.printStackTrace();
        return 0;
      }
      
      
      if(debug) logger.debug("Light geschafft!");
      
      return 1; 
    
  }
  
  public int setSpeed_motor(String speed_motor_left, String speed_motor_right) {
      
      int i = 0;
      String str = "";		
      int h = 0;
      int j = 0;
      int not_ready = 0;
      
      wait(1);
      
   
        
      try{
        i = Integer.parseInt(speed_motor_left);
      } catch(NumberFormatException e){
        logger.error("(Speed) false numberFormat! left = "+speed_motor_left);
        return 0;
      }
      
      try{
        i = Integer.parseInt(speed_motor_right);
      } catch(NumberFormatException e){
        logger.error("(Speed) false numberFormat! left = "+speed_motor_right);
        return 0;
      }
   
   synchronized(lock) {
          
      try{
        command.write("D,"+speed_motor_left+","+speed_motor_right+"\n");
      }catch (IOException e) {
        logger.error("(Speed) Robot communication problem: write()",e);
        e.printStackTrace();
        return 0;
      }
      
      try {
        flush2();
      } catch (IOException e) {
        logger.error("(Speed) flush2() IOException!!",e);
        e.printStackTrace();
        return 0;
      }		
      
      
      for (i=0;i<ROUNDS;i++){			
        try {
          if(answer.ready()) {
            ++j;
            wait(5);
            try {
              if((cbuf[0] = (char)answer.read())==100){ //first letter "d"?
                for (h=1;h<cbuf.length;h++){
                  if( ((cbuf[h] = (char)answer.read())==10) || (cbuf[h]==13) ){ //last char '\n' or '\r'?
                    if ( (cbuf[h]==10) || (cbuf[h]==13) ){ //control again...
                      //System.out.println("cbuf["+h+"] = \\n");		
                      break;
                    }
                  }
                  //else System.out.println("cbuf:"+String.valueOf(cbuf));	
                }				
                try {
                  str = String.valueOf(cbuf,h-1,h);
                } catch (RuntimeException e) {
                  logger.debug("(Speed) String.valueOf(cbuf,"+(h-1)+","+(h)+") failed! (="+str+")");
                  return 0;
                }
                if(debug) logger.debug("(Speed) "+str);
                wait(1);
                break;
              }
            } catch (IOException e1) {
              e1.printStackTrace();
            }
            //break;
          }
          else {
            ++not_ready;
            wait(1);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } // end synchronized
    
      if(debug) logger.debug("(Speed) after for loop i ="+i);
      if(i==ROUNDS){
        logger.error("(Speed) failed! (i=ROUNDS="+ROUNDS+", any answer.ready()="+(j!=0));
        return 0;
      }
      
      this.speed_motor_left = speed_motor_left;	
      this.speed_motor_right = speed_motor_right;
      
      if(debug) logger.debug("Speed geschafft!");
      return 1;
    
  }
 
 public int stop() {
      
      int i = 0;
      String str = "";		
      int h = 0;
      int j = 0;
      int not_ready = 0;
      
      wait(1);
      
   
   synchronized(lock) {
          
      try{
        command.write("D,0,0\n");
      }catch (IOException e) {
        logger.error("(Speed) Robot communication problem: write()",e);
        e.printStackTrace();
        return 0;
      }
      
      try {
        flush2();
      } catch (IOException e) {
        logger.error("(Speed) flush2() IOException!!",e);
        e.printStackTrace();
        return 0;
      }		
      
      
      for (i=0;i<ROUNDS;i++){			
        try {
          if(answer.ready()) {
            ++j;
            wait(5);
            try {
              if((cbuf[0] = (char)answer.read())==100){ //first letter "d"?
                for (h=1;h<cbuf.length;h++){
                  if( ((cbuf[h] = (char)answer.read())==10) || (cbuf[h]==13) ){ //last char '\n' or '\r'?
                    if ( (cbuf[h]==10) || (cbuf[h]==13) ){ //control again...
                      break;
                    }
                  }
                  //else System.out.println("cbuf:"+String.valueOf(cbuf));	
                }				
                try {
                  str = String.valueOf(cbuf,h-1,h);
                } catch (RuntimeException e) {
                  logger.debug("(Speed) String.valueOf(cbuf,"+(h-1)+","+(h)+") failed! (="+str+")");
                  return 0;
                }
                if(debug) logger.debug("(Speed) "+str);
                wait(1);
                break;
              }
            } catch (IOException e1) {
              e1.printStackTrace();
            }
            //break;
          }
          else {
            ++not_ready;
            wait(1);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } // end synchronized
    
      if(debug) logger.debug("(Speed) after for loop i ="+i);
//      else System.out.println("(Speed) after for loop i ="+i);
      if(i==ROUNDS){
        logger.error("(Speed) failed! (i=ROUNDS="+ROUNDS+", any answer.ready()="+(j!=0));
        return 0;
      }
      
      if(debug) logger.debug("(Stop) geschafft!");
//      else System.out.println("Speed geschafft!");
      return 1;
    
  }
  
  
  /**
   * 
   * @param stop
   * @return
   */
 
 /*
  public int stop() {
      
      int i = 0;
      String str = "";		
      int h = 0;
      int j = 0;
      int not_ready = 0;
 

      
      try{
        i = Integer.parseInt(speed_motor_left);
      } catch(NumberFormatException e){
        if(debug) logger.error("(Stop) false numberFormat! left = "+speed_motor_left);
        else System.out.println("(Stop) false numberFormat! left = "+speed_motor_left);
        return 0;
      }
      
      try{
        i = Integer.parseInt(speed_motor_right);
      } catch(NumberFormatException e){
        if(debug) logger.error("(Stop) false numberFormat! left = "+speed_motor_right);
        else System.out.println("(Stop) false numberFormat! left = "+speed_motor_right);
        return 0;
      }
   
   synchronized(lock) {
      
      try{
        command.write("D,"+0+","+0+"\n");
      }catch (IOException e) {
        if(debug) logger.error("(Stop) Robot communication problem: write()",e);
        else System.out.println("(Stop) Robot communication problem: write()"+e);
        e.printStackTrace();
        return 0;
      }
      
      try {
        flush2();
      } catch (IOException e) {
        if(debug) logger.error("(Stop) flush2() IOException!!",e);
        else System.out.println("(Stop) flush2() IOException!!"+e);
        e.printStackTrace();
        return 0;
      } 
      
      for (i=0;i<ROUNDS;i++){			
        try {
          if(answer.ready()) {
            ++j;
            wait(5);
            try {
              for (h=0;h<cbuf.length;h++){
                if((cbuf[h] = (char)answer.read())==10){ 
                  //System.out.println("cbuf["+h+"] = \\n");		
                  break;
                }
                //else System.out.println("cbuf:"+String.valueOf(cbuf));	
              }				
              str = String.valueOf(cbuf,h-2,h-1);
              if(debug) logger.debug(str);
              wait(1);
            } catch (IOException e1) {
              e1.printStackTrace();
            }
            break;
          }
          else {
            ++not_ready;
            wait(1);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
   } //end synchronized
   
      if(debug) logger.debug("(Stop) after for loop i ="+i);
      if(i==ROUNDS){
        if(debug) logger.error("(Stop) failed!");
        return 0;
      }
      
      this.speed_motor_left = "0";	
      this.speed_motor_right = "0";
      
      if(debug) logger.debug("Stop geschafft!");
      else System.out.println("Stop geschafft!");
      return 1;
    
  }
  
  */
  
  /**
   * @return Returns if operation runs well or not.
   */
  public int getSpeed_motor() {
    
    synchronized(lock) {
      
      try {
        command.write("E\n");
        command.flush();
        try {
          Thread.sleep(SLEEP);
        } catch (InterruptedException e2) {
          // TODO Auto-generated catch block
          e2.printStackTrace();
        }
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
   * @param speed_motor_left The speed_motor_left to set.
   */
  public int setSpeed_motor_left(String speed_motor_left) {
    
    synchronized(lock) {
      
      try {
        command.write("D,"+speed_motor_left+","+speed_motor_right+"\n");
        command.flush();
        try {
          Thread.sleep(SLEEP);
        } catch (InterruptedException e2) {
          // TODO Auto-generated catch block
          e2.printStackTrace();
        }
        answer.readLine();   //empty buffer
      } catch (IOException e) {
        logger.error("Robot communication problem: setSpeed_motor_left()",e);
        return 0;
      }
      
    }
    this.speed_motor_left = speed_motor_left;	
    
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
        try {
          Thread.sleep(SLEEP);
        } catch (InterruptedException e2) {
          // TODO Auto-generated catch block
          e2.printStackTrace();
        }
        answer.readLine();   //empty buffer
      } catch (IOException e) {
        logger.error("Robot communication problem: setSpeed_motor_right()",e);
        return 0;
      }
    }
    
    this.speed_motor_right = speed_motor_right;
    return 1;
  }
  
  
  /**
   * @return Returns the speed_motor_left.
   */
  public String getSpeed_motor_left() {
    return this.speed_motor_left;
  }
  
  /**
   * @return Returns the speed_motor_right.
   */
  public String getSpeed_motor_right() {
    return this.speed_motor_right;
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
  /* alte version ohne readline do while überprüfung:
   public int setLED0(String LED0) {
   
   synchronized (lock) {
   
   try {
   command.write("L,0," + LED0 + "\n");
   command.flush();
   try {
   Thread.sleep(SLEEP);
   } catch (InterruptedException e2) {
   // TODO Auto-generated catch block
    e2.printStackTrace();
    }
    answer.readLine();   //empty buffer
    } catch (IOException e) {
    logger.error("Robot communication problem: setLED0()", e);
    return 0;
    }
    
    }
    
    if (LED0.equals("2")) {
    if (this.3.led0.equals("0"))
    Khepera3.led0 = "1";
    else
    Khepera3.led0 = "0";
    } else
    Khepera3.led0 = LED0;
    return 1;
    }
    */
  
  /**
   * @param LED0 (0=off, 1=on, 2=change).
   * @return Returns if operation worked well.
   */

  public int setLED0(String LED1) {
	    
	    
	    long start = System.currentTimeMillis();
	      
	      str = "";
	      int i = -3;
	      boolean ready = false;
	      boolean rightChar = false;
	      int rounds = 0;
	      int j = 0;
	      int not_ready = 0;
	      
	    synchronized(lock){
	      
	      
	       
	        try {
	            try{
	              i = Integer.parseInt(LED1);
	            } catch(NumberFormatException e){
	              logger.error("(LED0) false numberFormat! left = "+speed_motor_left);
	              return 0;
	            }
	            
	            if ( !((i==0) || (i==1) || (i==2)) ){
	              if(debug) logger.error("(LED0) false value (just 0,1,2)! number = "+led0);
	              return 0;
	            }
	     
	   
	                
	                try {command.write("L,0," + led0 + "\n");
	                }catch (IOException e) {
	                    logger.error("Robot communication problem: write() in setLED0()",e);
	                    e.printStackTrace();
	                    return 0;
	                }
	                
	                try {
	                    flush2();
	                } catch (IOException e) {
	                    logger.error("(LED0) flush2() IOException!!",e);
	                    e.printStackTrace();
	                    return 0;
	                } 
	                
	                
	                for (i=0;i<ROUNDS;i++){     
	                    try {
	                        if(answer.ready()) {
	                            ++j;
	                            wait(5);
	                            try {
	                                if((cbuf[0] = (char)answer.read())==108){ //first letter "o"?
	                                    if((cbuf[1] = (char)answer.read())==13){ //2nd letter '\r'?
	                                        if (cbuf[2]==10){ //last char '\n'?
	                                            break;
	                                        }
	                                    }       
	                                    try {
	                                        str = String.valueOf(cbuf,0,2);
	                                    } catch (RuntimeException e) {
	                                        logger.debug("(LED0) String.valueOf(cbuf,0,2) failed! (="+str+")");
	                                        return 0;
	                                    }
	                                    if(debug) logger.debug("(LED0) "+str);
	                                    
	                                    wait(1);
	                                    break;
	                                }
	                                
	                            } catch (IOException e1) {
	                                e1.printStackTrace();
	                            }
	                        }
	                        else {
	                            ++not_ready;
	                            wait(1);
	                        }
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }
	        } catch (RuntimeException e) {
	        	logger.error("(LED0) failed!",e);
	            e.printStackTrace();
	            return 0;
	        }
	         
	      } //end synchronized
	        
	    
	    if (led0.equals("2")) {
	      if (this.led0.equals("0"))
	        this.led0 = "1";
	      else
	        this.led0 = "0";
	    } else
	      this.led0 = led0;
	    
	    if(debug) logger.debug("LED0 geschafft!");
	    
	    return 1;
	  }

  
  /*
  
  public int setLED0(String LED0) {
    
    long start = System.currentTimeMillis();
   
      str = "";
      int i = -1;
      boolean ready = false;
      boolean rightChar = false;
      int rounds = 0;
      
      synchronized (lock) {      
      
      try {
        //do {
        try{
          i = Integer.parseInt(LED0);
        } catch(NumberFormatException e){
          if(debug) logger.error("(Speed) false numberFormat! left = "+speed_motor_left);
          return 0;
        }
        
        if ( !((i==0) || (i==1) || (i==2)) ){
          if(debug) logger.error("(LED0) false numer (just 0,1,2)! number = "+LED0);
          return 0;
        }
        
     
      
        answer.mark(100);
        //command.write("L,0," + LED0 + "\n");
        command.write("L,0," + LED0 + "\n");
        //command.newLine();
        command.flush();
        do {
          try {
            Thread.sleep(SLEEP);//*(++i));
          } catch (InterruptedException e2) {
            e2.printStackTrace();
          }
          if (answer.ready()){	
            ready = true;
            str = answer.readLine();
            //if (str != ""){
            if (str.length() > 0){
              if(debug) logger.debug("(LED0)Antwort im buffer: "+str);
              try {
                rightChar = (str.charAt(0) == 'l');
              } catch (StringIndexOutOfBoundsException e) {
                if(debug) logger.debug("(LED0) charAt(0): StringIndexOutOfBoundsException");
                else System.out.println("(LED0) charAt(0): StringIndexOutOfBoundsException");
                e.printStackTrace();
              }
            }
          }
          
          //System.out.println("Antwort im buffer: "+str+" | "+str.charAt(0));
          rounds++;
          if(debug) logger.debug("(LED0) rounds = "+rounds);
        } while ((!ready || !rightChar) && (rounds <= 3));
        
      } catch (IOException e) {
        logger.error("Robot communication problem: setLED0()", e);
      
        
        try {
          answer.reset();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        return 0;		
      }				
    } // end synchronized
    
    long stop = System.currentTimeMillis();
    if(debug) logger.info("LED0: "+(stop-start)+" ms");		
    
    if (LED0.equals("2")) {
      if (this.led0.equals("0"))
        this.led0 = "1";
      else
        this.led0 = "0";
    } else
      this.led0 = LED0;
    
    if(debug) logger.debug("LED0 geschafft!");
//    else System.out.println("LED0 geschafft!");
    return 1;
  }
  */
  
  /**
   * @param LED1 (0=off, 1=on, 2=change).
   * @return Returns if operation worked well.
   */
  public int setLED1(String LED1) {
    
    
    long start = System.currentTimeMillis();
      
      str = "";
      int i = -3;
      boolean ready = false;
      boolean rightChar = false;
      int rounds = 0;
      int j = 0;
      int not_ready = 0;
      
    synchronized(lock){
      
      
       
        try {
            try{
              i = Integer.parseInt(LED1);
            } catch(NumberFormatException e){
              logger.error("(LED1) false numberFormat! left = "+speed_motor_left);
              return 0;
            }
            
            if ( !((i==0) || (i==1) || (i==2)) ){
              if(debug) logger.error("(LED1) false value (just 0,1,2)! number = "+LED1);
              return 0;
            }
     
   
                
                try {command.write("L,1," + LED1 + "\n");
                }catch (IOException e) {
                    logger.error("Robot communication problem: write() in setLED1()",e);
                    e.printStackTrace();
                    return 0;
                }
                
                try {
                    flush2();
                } catch (IOException e) {
                    logger.error("(LED1) flush2() IOException!!",e);
                    e.printStackTrace();
                    return 0;
                } 
                
                
                for (i=0;i<ROUNDS;i++){     
                	try {
                		if(answer.ready()) {
                			++j;
                			wait(5);
                			try {
                				if((cbuf[0] = (char)answer.read())==108){ //first letter "o"?
                					if((cbuf[1] = (char)answer.read())==13){ //2nd letter '\r'?
                						if (cbuf[2]==10){ //last char '\n'?
                							break;
                						}
                					}       
                					try {
                						str = String.valueOf(cbuf,0,2);
                					} catch (RuntimeException e) {
                						logger.debug("(LED1) String.valueOf(cbuf,0,2) failed! (="+str+")");
                						return 0;
                					}
                					if(debug) logger.debug("(LED1) "+str);
                					
                					wait(1);
                					break;
                				}
                				
                			} catch (IOException e1) {
                				e1.printStackTrace();
                			}
                		}
                		else {
                			++not_ready;
                			wait(1);
                		}
                	} catch (IOException e) {
                		e.printStackTrace();
                	}
                }
        } catch (RuntimeException e) {
        	logger.error("(LED1) failed!",e);
            e.printStackTrace();
            return 0;
        }
         
      } //end synchronized
        
    
    if (LED1.equals("2")) {
      if (this.led1.equals("0"))
        this.led1 = "1";
      else
        this.led1 = "0";
    } else
      this.led1 = LED1;
    
    if(debug) logger.debug("LED1 geschafft!");
   
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

  public String getPositionX() {
    return positionX;
  }

  public void setPositionX(String positionX) {
    this.positionX = positionX;
  }

  public String getPositionY() {
    return positionY;
  }

  public void setPositionY(String positionY) {
    this.positionY = positionY;
  }

  public String getOrientation() {
	return orientation;
  }

  public void setOrientation(String orientation) {
	this.orientation = orientation;
  }
  
  
  public int restart(){
      
      logger.debug("Trying khepera restart...");
      
      synchronized(lock){
          
          try {command.write("restart\n");
          }catch (IOException e) {
              logger.error("Robot communication problem: write() in restart()",e);
              e.printStackTrace();
              return 0;
          }
          
          try {
              flush2();
          } catch (IOException e) {
              logger.error("(restart) flush2() IOException!!",e);
              e.printStackTrace();
              return 0;
          }
      } //end synchronized
      logger.debug("...khepera restarted!");
      
      
      char c;
      
      try {
    	  while((c = (char)answer.read())!= -1){//10){
    		  logger.debug("(restart) read()="+c);
    	  }
      } catch (IOException e) {
    	  logger.error("(restart) read() IOException!!",e);
    	  e.printStackTrace();
      }
      
      return 1;
  }


}
