/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/urges/BodyDataProvider.java,v 1.3 2004/11/24 16:26:41 vuine Exp $
 */
package org.micropsi.comp.agent.micropsi.urges;

import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.SensorDataSourceIF;

public class BodyDataProvider implements UrgeCreatorIF,SensorDataSourceIF {

	private static final String integrityUrge = 	"integrityurge";
	private static final String foodUrge = 			"foodurge";
	private static final String waterUrge = 		"waterurge";

	public static final int UINTEGRITY = 0;
	public static final int UFOOD 		= 1;
	public static final int UWATER		= 2;
		
	private String type;
	private final int itype;
	private final BodySimulator simulator;
		
	public BodyDataProvider(int itype, BodySimulator simulator, LocalNetFacade net) {
		this.itype = itype;
		
		switch(itype) {
			case UINTEGRITY: type = integrityUrge; break;
			case UFOOD: type = foodUrge; break;
			case UWATER: type = waterUrge; break;
			default: type = "";
		}
		
		this.simulator = simulator;
		net.getSensorRegistry().registerSensorDataProvider(this);		
	}
		
	public String getDataType() {
		return type;
	}

	public double getSignalStrength() {
		switch(itype) {
			case UINTEGRITY: return simulator.getIntegrityUrge();
			case UFOOD: return simulator.getFoodUrge();
			case UWATER: return simulator.getWaterUrge();
		}
		return 0;
	}
	
	public void notifyOfPerception() {
		// body urges don't depend on perception
	}

	public void notifyOfBodyPropertyChanges() {
		// the body simulator already knows about the changes
	}

	public void shutdown() {
	}	

}
