/*
 * Created on 28.05.2005
 *
 */
package org.micropsi.comp.agent;

/**
 * @author Markus
 *
 */
public interface MouseAgentIF {
    public void createNet();
    
    public long getID();
    
    public void deleteAgentFromKnownAgents(long id);
    
    public void runNet();
}
