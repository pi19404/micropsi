/*
 * Created on 05.04.2005
 *
 */
package org.micropsi.comp.agent.urges;

import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Markus
 *
 */
public class WaterUrge implements UrgeCreatorIF, SensorDataSourceIF {

    private String type = "WaterUrge";
    private final MouseBodySimulator bodySimulator;

    public WaterUrge(MouseBodySimulator bodySimulator, LocalNetFacade net) {
        this.bodySimulator = bodySimulator;
        net.getSensorRegistry().registerSensorDataProvider(this);
    }
    
    /* (non-Javadoc)
     * @see org.micropsi.comp.agent.aaa.UrgeCreatorIF#notifyOfPerception()
     */
    public void notifyOfPerception() {
    }

    /* (non-Javadoc)
     * @see org.micropsi.comp.agent.aaa.UrgeCreatorIF#notifyOfBodyPropertyChanges()
     */
    public void notifyOfBodyPropertyChanges() {
    }

    /* (non-Javadoc)
     * @see org.micropsi.comp.agent.aaa.UrgeCreatorIF#shutdown()
     */
    public void shutdown() {
    }

    /* (non-Javadoc)
     * @see org.micropsi.nodenet.SensorDataSourceIF#getDataType()
     */
    public String getDataType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.micropsi.nodenet.SensorDataSourceIF#getSignalStrength()
     */
    public double getSignalStrength() {
        return bodySimulator.getWaterUrge();
    }

}

