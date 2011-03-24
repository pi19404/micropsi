package org.micropsi.comp.robot.khepera6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

import org.apache.log4j.Logger;


public class KheperaManager {
  
  private static KheperaManager instance;
  
  public Khepera khepera;
  //public KheperaUpdateServer update;
  private Logger logger;
  private boolean debug = false; 
  
  //private BufferedWriter command;
  private OutputStreamWriter command;
  private BufferedReader answer;
  private SerialPort com1;	
  
  public KheperaManager() {
    debug = false;
    System.out.println("constructor KheperaManager (out)");
  };
  
  public KheperaManager(boolean debugging) {
    debug = debugging;
    System.out.println("constructor KheperaManager (out)");
  };
  
  public KheperaManager(Logger logger) {
    this.logger = logger;
    logger.debug("constructor KheperaManager mit logger");
  };
  
  public KheperaManager(Logger logger, boolean debugging) {
    this.logger = logger;
    debug = debugging;
    logger.debug("constructor KheperaManager mit logger");
  };
  
  
  public static KheperaManager getInstance(Logger logger){//KheperaActionExecutor kheperaNBPActionExecutorObject) {
    if(instance == null) {
      instance = new KheperaManager(logger);
      //instance.logger = kheperaNBPActionExecutorObject.getLogger();
      logger.debug("New manager instance with logger...");
      instance.initialize();
    }
    return instance;
  }
  
  public static KheperaManager getInstance(Logger logger, boolean debugging){//KheperaActionExecutor kheperaNBPActionExecutorObject) {
    if(instance == null) {
      instance = new KheperaManager(logger, debugging);
      //instance.logger = kheperaNBPActionExecutorObject.getLogger();
      logger.debug("New manager instance with logger...");
      instance.initialize();
    }
    return instance;
  }
  
  public static KheperaManager getInstance(){//KheperaActionExecutor kheperaNBPActionExecutorObject) {
    if(instance == null) {
      instance = new KheperaManager();
      System.out.println("KheperaManager.getInstance()");
      instance.initialize();
      System.out.println("KheperaManager.initialize()");
    }
    return instance;
  }
  
  
  private void initialize() {
    
    Enumeration pList = CommPortIdentifier.getPortIdentifiers();
    System.out.println("(Manager) initialize()...");
    
    while(pList.hasMoreElements()){
      CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
      
      if (cpi.getName().equals("COM1")){
        try {
          com1 = (SerialPort)cpi.open("KHEPERA_COM1",1000);
          try {
            com1.setSerialPortParams(115200,
                com1.DATABITS_8,
                com1.STOPBITS_2,
                com1.PARITY_NONE);
            com1.setFlowControlMode(com1.FLOWCONTROL_NONE
            );
            
            /*
             try {
             outputStream.write(messageString.getBytes());
             } catch (IOException e) {
             e.printStackTrace();
             }
             */
            
          } catch (UnsupportedCommOperationException e1) {
            if(debug) logger.debug("(Manager) UnsupportedCommOperation: "+e1);
            else System.out.println("(Manager) UnsupportedCommOperation: "+e1);
          }
          if(debug) logger.debug("(Manager)established COM1");
          else System.out.println("(Manager)established COM1");
          
          //command = new BufferedWriter(new OutputStreamWriter(com1.getOutputStream()));//,15);
          command = new OutputStreamWriter(com1.getOutputStream());//,15);
          answer = new BufferedReader(new InputStreamReader(com1.getInputStream()));//,50);
          //answer = new InputStreamReader(com1.getInputStream());//,50);
        } catch (PortInUseException e) {
          if(debug) logger.debug("(Manager) Port in use: "+e);
          else System.out.println("(Manager) Port in use: "+e);
        } catch (IOException e) {
          if(debug) logger.debug("IOException while initialising COM1: "+e);
          else System.out.println("IOException while initialising COM1: "+e);
        }
        break;
      }
    }
    /*
     try {
     command.write("restart\n");
     } catch (IOException e) {
     System.out.println("restart failed");
     e.printStackTrace();
     }
     */
    if(debug) logger.debug("new Khepera created...");
    else System.out.println("(Manager)initialize() finished");
    
    
    
    if(debug) khepera = new Khepera(command, answer, logger);
    else khepera = new Khepera(command, answer, debug);
    
    if(debug) logger.debug("new Khepera created...");
    else System.out.println("new Khepera created...");
    
    if(debug) logger.debug("getLED0(): "+khepera.getLED0());
    else System.out.println("getLED0(): "+khepera.getLED0());
    
    
    //update = new KheperaUpdateServer(khepera, logger);
    //update.start();
    
    //logger.debug(update.toString());
    
  }
  
  public void shutdown(){
    khepera.setLED0("0");
    khepera.setLED1("0");
    khepera.stop();
    try {
      command.write("restart\n");
      //command.newLine();
      command.flush();
    } catch (IOException e2) {
      e2.printStackTrace();
    }
    try {
      command.close();
      if(debug) logger.debug("command.close(): OK");
    } catch (IOException e) {
      if(debug) logger.error("Shutdown Exc: ",e);
    }
    try {
      answer.close();
    } catch (IOException e1) {
      if(debug) logger.error("Shutdown Exc: ",e1);
    }
  }
  
}
