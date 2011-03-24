/*
 * Created on Dec 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.amouse;


import org.micropsi.comp.agent.amouse.percept.LightUrges;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Daniel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RegistryNode {
	
	public RegistryNode(){
	}
	
	
	public void regestry(LightUrges type, MicroPsiAgent agent){
		((LocalNetFacade)agent.getNet()).getSensorRegistry().registerSensorDataProvider(type);
				
	}

}
