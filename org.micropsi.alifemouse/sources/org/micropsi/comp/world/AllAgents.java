/*
 * Created on 09.04.2005
 *
 */
package org.micropsi.comp.world;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.world.objects.AbstractObject;
import org.micropsi.comp.world.objects.MouseAgentObject;

/**
 * @author Markus
 *
 */
public class AllAgents {
    private static HashMap<Long, MouseAgentObject> agents;
    private static AllAgents instance = null;
    private static WorldComponent worldComponent;
    
    private AllAgents() {
        try {
            worldComponent = (WorldComponent)ComponentRunner.getInstance().getComponent("world");
        } catch (ComponentRunnerException e) {
            System.err.println("ComponentRunner not loaded");           
        }
        agents = new HashMap<Long, MouseAgentObject>(ConstantValues.MAX_AGENT_COUNT);
    }
    
    public synchronized static AllAgents getInstance() {
        if(instance == null)
            instance  = new AllAgents();
        
    	return instance;
    }
    
    public void addAgent(MouseAgentObject agent) {
        if(!agents.containsValue(agent)) {
            agents.put(new Long(agent.getId()), agent);
        }
    }
    
    public MouseAgentObject getAgent(long id) {
        collectAgents();
        if(agents.containsKey(new Long(id))) {
            return (MouseAgentObject)agents.get(new Long(id));
        } else
            return null;
    }
    
    private void collectAgents() {
        Iterator it = worldComponent.getWorld().getObjects().iterator();
        AbstractObject current;
        
        while(it.hasNext()) {
            try {
                current = (AbstractObject)it.next();
                if(current.getObjectClass().equals("MouseAgent")) {
                    addAgent((MouseAgentObject)current);
                }
            } catch (ConcurrentModificationException e) {
                it = worldComponent.getWorld().getObjects().iterator();
            }
        }
    }
}