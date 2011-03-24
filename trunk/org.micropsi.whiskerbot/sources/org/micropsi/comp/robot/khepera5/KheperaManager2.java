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


public class KheperaManager2 {

	private static KheperaManager2 instance;

    private BufferedReader answer;
	private OutputStreamWriter command;
	private BufferedWriter command2;
	public Khepera2 khepera;
	public KheperaUpdateServer2 update;
	private Logger logger;
	
	public KheperaManager2(Logger logger) {
		this.logger = logger;
	};
	
	public static KheperaManager2 getInstance(Logger logger){//KheperaActionExecutor kheperaNBPActionExecutorObject) {
		if(instance == null) {
			instance = new KheperaManager2(logger);
			//instance.logger = kheperaNBPActionExecutorObject.getLogger();
			//instance.logger.debug("New manager instance...");
			instance.initialize();
		}
		return instance;
	}
	
	
	private void initialize() {
		// Initialisieren, Konfigurieren, what have you.
		
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
		while(pList.hasMoreElements()){
			CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
			if (cpi.getName().equals("COM1")){
				try {
					SerialPort com1 = (SerialPort)cpi.open("KHEPERA_COM1",1000);
					try {
						com1.setSerialPortParams(115200,8,2,0);
					} catch (UnsupportedCommOperationException e1) {
						logger.error("Unsupported operation: ",e1);
					}
					command = new OutputStreamWriter(com1.getOutputStream());
					answer = new BufferedReader(new InputStreamReader(com1.getInputStream()));		
				} catch (PortInUseException e) {
					logger.error("Port in use: ",e);
				} catch (IOException e) {
					logger.error("IO problem in init: ",e);
				}
			}
		}
		
		command2 = new BufferedWriter(command);
		khepera = new Khepera2(command2, answer, logger);
		
		//update = new KheperaUpdateServer2(khepera, logger);
		//update.start();
		
		//logger.debug(update.toString());

	}

	public void shutdown(){
		khepera.setLED0("0");
		khepera.setLED1("0");
		try {
			command.close();
			logger.debug("command.close(): OK");
		} catch (IOException e) {
			logger.error("Shutdown Exc: ",e);
		}
		try {
			answer.close();
		} catch (IOException e1) {
			logger.error("Shutdown Exc: ",e1);
		}
	}
	
}
