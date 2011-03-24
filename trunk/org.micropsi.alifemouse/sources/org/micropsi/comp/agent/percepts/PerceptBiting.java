/*
 * Created on 14.06.2005
 *
 */
package org.micropsi.comp.agent.percepts;

import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Markus
 *
 */
public class PerceptBiting {
	private MouseMicroPsiAgent agent;
	private PerceptProperty gotBitten;
	private PerceptProperty biterRed;
	private PerceptProperty biterGreen;
	private PerceptProperty biterBlue;

	public PerceptBiting(MouseMicroPsiAgent micropsi) {
	    gotBitten = new PerceptProperty(micropsi, "gotBitten");
	    biterRed = new PerceptProperty(micropsi, "biter-red");
	    biterGreen = new PerceptProperty(micropsi, "biter-green");
	    biterBlue = new PerceptProperty(micropsi, "biter-blue");
	    
		this.agent = micropsi;
		
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(gotBitten);
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(biterRed);
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(biterGreen);
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(biterBlue);
	}
	
	public void setSignalStrength(double signal, int[] RGB) {
	    gotBitten.setSignalStrength(signal);
	    biterRed.setSignalStrength(RGB[0]);
	    biterGreen.setSignalStrength(RGB[1]);
	    biterBlue.setSignalStrength(RGB[2]);
	}
}

