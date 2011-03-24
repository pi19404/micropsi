/*
 * Created on 21.04.2005
 *
 */
package org.micropsi.comp.agent;

import java.io.File;

import org.micropsi.common.config.ConfigurationReaderFactory;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;

/**
 * @author Markus
 *
 */
public class MouseAgentManager {
    private static MouseAgentManager instance = null;
    
    private MouseAgentIF[] agents;
    private MouseAgentIF lastAgent;
    
    private Position currentPosition = null;
    
    public static synchronized MouseAgentManager getInstance() {
        if(instance == null) {
            instance = new MouseAgentManager();
            String tempPath = ComponentRunner.getGlobalVariable("MICROPSI_HOME");
    		tempPath = tempPath.substring(1, tempPath.length() - 1) + "../org.micropsi.alifemouse/config/";
    		
    		ConstantValues.setPATH(tempPath);
        }
        return instance;
    }
    
    private MouseAgentManager() {
        agents = new MouseMicroPsiAgent[ConstantValues.MAX_AGENT_COUNT];
        for(int i = 0; i < ConstantValues.MAX_AGENT_COUNT; i++) {
            agents[i] = null;
        }
    }
    
    public void createAgent(Position startPosition) {
        currentPosition = startPosition;
        ConfigurationReaderIF reader = null;
	    String filename = "agentconfig.xml";
        try {
        	reader = ConfigurationReaderFactory.getConfigReader(
        			new File(ConstantValues.PATH+filename).getAbsolutePath(),
					ComponentRunner.getInstance().getGlobalVariables(),
					ConfigurationReaderFactory.CONFIG_XML
				 );
        	
        } catch (MicropsiException e) {
            System.err.println("error reading xml-file");
        } catch (ComponentRunnerException e) {
            System.err.println("configurationfile " + filename + " not found");
        }
        try {
            ComponentRunner.getInstance().createComponent("agent", reader, null, true);
        } catch (ComponentRunnerException e1) {          
            e1.printStackTrace();
            System.err.println("error creating agent");
        }
        
        lastAgent.createNet();
    }
    
    public void createAgent() {
    	double x = Math.abs(RandomGenerator.generator.nextInt()) % ConstantValues.WORLDMAXX;
	    double y = Math.abs(RandomGenerator.generator.nextInt()) % ConstantValues.WORLDMAXY;
	    
	    createAgent(new Position(x, y));
    }

    public void addAgent(MouseAgentIF agent) {
        boolean inserted = false;
        for(int i = 0; i < ConstantValues.MAX_AGENT_COUNT; i++) {
            if(agents[i] == null) {
                agents[i] = agent;
                inserted = true;
                lastAgent = agent;
                break;
            }
        }
        
        if(!inserted)
            System.err.println("AgentManager: could not insert agent; array is full");
    }
    
    
    public void runAgents() {
	    for(int i = 0; i < agents.length; i++) {
	    	if(agents[i] != null) {
	    		((MouseAgentIF)agents[i]).runNet();
	    	}
		}    
	}
    
    public void deleteAgent(MouseAgentIF agent) {
        long ID = agent.getID();
        for(int i = 0; i < ConstantValues.MAX_AGENT_COUNT; i++) {
            if(agents[i] != null) {
	            if(agents[i].getID() == ID)
	                agents[i] = null;
	            else {
	                agents[i].deleteAgentFromKnownAgents(ID);
	            }
            }
        }
    }

	public Position getCurrentPosition() {
		return currentPosition;
	}
}
