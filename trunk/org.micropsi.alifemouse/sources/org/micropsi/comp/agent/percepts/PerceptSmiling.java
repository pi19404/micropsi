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
public class PerceptSmiling {
    private MouseMicroPsiAgent agent;
	private PerceptProperty smiledAt;
	private PerceptProperty smilerRed;
	private PerceptProperty smilerGreen;
	private PerceptProperty smilerBlue;
	
	public PerceptSmiling(MouseMicroPsiAgent micropsi) {
	    smiledAt = new PerceptProperty(micropsi, "smiledAt");
	    smilerRed = new PerceptProperty(micropsi, "smiler-red");
	    smilerGreen = new PerceptProperty(micropsi, "smiler-green");
	    smilerBlue = new PerceptProperty(micropsi, "smiler-blue");
	    
		this.agent = micropsi;
		
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(smiledAt);
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(smilerRed);
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(smilerGreen);
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(smilerBlue);
	}
	
	public void setSignalStrength(double signal, int[] RGB) {
	    smiledAt.setSignalStrength(signal);
	    smilerRed.setSignalStrength(RGB[0]);
	    smilerGreen.setSignalStrength(RGB[1]);
	    smilerBlue.setSignalStrength(RGB[2]);
	}
}

