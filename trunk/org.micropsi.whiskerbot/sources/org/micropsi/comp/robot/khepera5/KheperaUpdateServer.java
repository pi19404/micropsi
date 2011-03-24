package org.micropsi.comp.robot.khepera5;

import org.apache.log4j.Logger;



public class KheperaUpdateServer extends Thread {

	private Khepera khepera;
	private boolean suspended = false;
	private boolean paused = false;
	private Logger logger;

	public KheperaUpdateServer(Khepera khepera, Logger logger) {
		super("KheperaUpdateServer");
		this.khepera = khepera;
		this.logger = logger;
	}
	
	public void run() {

		// initialise and blink once:
		khepera.setLED0(khepera.getLED0());
		khepera.setLED1(khepera.getLED1());
		khepera.setLED0("1");
		khepera.setLED1("1");
		khepera.setLED0("0");
		khepera.setLED1("0");

		boolean nextAmbient = false;

		while (!suspended) {

		if(!paused){
			if (!nextAmbient) {
				khepera.getProximitySensors();
			} else {
				khepera.getAmbientLightSensors();
			}
			nextAmbient = !nextAmbient; //alternating update of prox/ambient.
			Thread.yield(); //smoother movements!
		}
			
		}

	}
	
	public void suspended() {
		suspended = true;
		//resumed = false;
	}
	
	public void pause(boolean pause) {
		paused = pause;
	}
	
}
