package org.micropsi.comp.robot.khepera5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

    private BufferedReader answer;
	//private OutputStreamWriter command;
	public Khepera3 khepera;
	//public KheperaUpdateServer update;
	private Logger logger;
	private boolean debug = true; //switch logger on|off

	private BufferedWriter command;
	
	public KheperaManager() {
		debug = false;
		System.out.println("constructor KheperaManager (out)");
	};
	
	public KheperaManager(Logger logger) {
		this.logger = logger;
		logger.debug("constructor KheperaManager");
	};
	
	public static KheperaManager getInstance(Logger logger){//KheperaActionExecutor kheperaNBPActionExecutorObject) {
		if(instance == null) {
			instance = new KheperaManager(logger);
			//instance.logger = kheperaNBPActionExecutorObject.getLogger();
			instance.logger.debug("New manager instance...");
			logger.debug("2: New manager instance...");
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
		// Initialisieren, Konfigurieren, what have you.
		
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
		System.out.println("pList: "+pList.hasMoreElements());
		while(pList.hasMoreElements()){
			CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
			System.out.println("CommPortIdentifier = "+cpi.getName());
			if (cpi.getName().equals("COM1")){
				if(debug) logger.debug("khepera.initialize()");
				try {
					SerialPort com1 = (SerialPort)cpi.open("KHEPERA_COM1",1000);
					try {
						com1.setSerialPortParams(115200,8,2,0);
						System.out.println("khepera.initialize()...");
					} catch (UnsupportedCommOperationException e1) {
						if(debug) logger.error("Unsupported operation: ",e1);
					}
					command = new BufferedWriter(new OutputStreamWriter(com1.getOutputStream()));
					answer = new BufferedReader(new InputStreamReader(com1.getInputStream()));		
				} catch (PortInUseException e) {
					if(debug) logger.error("Port in use: ",e);
				} catch (IOException e) {
					if(debug) logger.error("IO problem in init: ",e);
				}
			}
		}
		//command2 = new BufferedWriter(command);
		if(debug)
			khepera = new Khepera3(command, answer, logger);
		else
			khepera = new Khepera3(command, answer, debug);
		
		System.out.println("new Khepera3");
		
		//update = new KheperaUpdateServer(khepera, logger);
		//update.start();
		
		//logger.debug(update.toString());

	}

	public void shutdown(){
		khepera.setLED0("0");
		khepera.setLED1("0");
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
