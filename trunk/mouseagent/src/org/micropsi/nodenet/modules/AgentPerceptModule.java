/*
 * Created on 22.05.2005
 */
package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.micropsi.comp.Functions;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.Situation;
import org.micropsi.nodenet.agent.SituationElement;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

/**
 * @author Markus
 */
public class AgentPerceptModule extends AbstractNativeModuleImpl {
	//slots
	private static final int GOTBITTEN   = 18500;
	private static final int ENEMYRED    = 18501;
	private static final int ENEMYGREEN  = 18502;
	private static final int ENEMYBLUE   = 18503;
	private static final int SMILEDAT    = 18504;
	private static final int FRIENDRED   = 18505;
	private static final int FRIENDGREEN = 18506;
	private static final int FRIENDBLUE  = 18507;
	
	//gates
	private static final int KNOWNAGENTSCONCEPT    = 18500;
	private static final int NEARAGENTSCONCEPT     = 18501;
	private static final int SMILEACTIONCOMPETENCE = 18502;
	private static final int BITEACTIONCOMPETENCE  = 18505;
	
	private boolean firsttime = true;
	private String knownAgentsConcept = null; 
	private String nearAgentsConcept = null;
	
	private Slot gotBitten;
	private Slot enemyRed;
	private Slot enemyGreen;
	private Slot enemyBlue;
	private Slot smiledAt;
	private Slot friendRed;
	private Slot friendGreen;
	private Slot friendBlue;
	
	private ArrayList knownAgents = new ArrayList();
	
	private final int[] gateTypes = {
			KNOWNAGENTSCONCEPT,
			NEARAGENTSCONCEPT,
			SMILEACTIONCOMPETENCE,
			BITEACTIONCOMPETENCE
	};
	
	private final int[] slotTypes = {
			GOTBITTEN,
			ENEMYRED,
			ENEMYGREEN,
			ENEMYBLUE,
			SMILEDAT,
			FRIENDRED,
			FRIENDGREEN,
			FRIENDBLUE
	};
	
	public AgentPerceptModule() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "agents";

			public String getExtensionID() {
				return id;
			}

			public String slotType(int type) {
				switch(type) {
					case GOTBITTEN: return "got-bitten";
					case ENEMYRED: return "biter-red";
					case ENEMYGREEN: return "biter-green";
					case ENEMYBLUE: return "biter-blue";
					case SMILEDAT: return "smiledAt";
					case FRIENDRED: return "smiler-red";
					case FRIENDGREEN: return "smiler-green";
					case FRIENDBLUE: return "smiler-blue";
					default: return null;
				}
			}
			
			public String gateType(int type) {
				switch(type) {
					case KNOWNAGENTSCONCEPT: return "known-agents";
					case NEARAGENTSCONCEPT: return "near-agents";
					case SMILEACTIONCOMPETENCE: return "smile-competence";
					case BITEACTIONCOMPETENCE: return "bite-competence";
					default: return null;
				}
			}
		});		
	}
	
	protected int[] getGateTypes() {
        return gateTypes;
    }
	
	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case GOTBITTEN: 
					gotBitten = slots[i];
					break;
				case ENEMYRED:
					enemyRed = slots[i];
					break;
				case ENEMYGREEN:
					enemyGreen = slots[i];
					break;
				case ENEMYBLUE:
					enemyBlue = slots[i];
					break;
				case SMILEDAT: 
					smiledAt = slots[i];
					break;
				case FRIENDRED:
					friendRed = slots[i];
					break;
				case FRIENDGREEN:
					friendGreen = slots[i];
					break;
				case FRIENDBLUE:
					friendBlue = slots[i];
					break;
			}
		}
	}
	
	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
        if (firsttime) {
			catchSlots(slots);
			firsttime = false;
			
			Link l = manipulator.getGate(KNOWNAGENTSCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to known agents - concept");
			else
				knownAgentsConcept = l.getLinkedEntityID();
			
			l = manipulator.getGate(NEARAGENTSCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to near agents - concept");
			else
				nearAgentsConcept = l.getLinkedEntityID();
			
			clearSituation();
		}
        
        //TODO react on other agents actions
        if(gotBitten.getIncomingActivation() > 0.5) {
        	int[] RGB = new int[3];
        	RGB[0] = (int)(enemyRed.getIncomingActivation() * 255);
        	RGB[1] = (int)(enemyGreen.getIncomingActivation() * 255);
        	RGB[2] = (int)(enemyBlue.getIncomingActivation() * 255);
        	
        	String agent = findAgent(RGB);
        	if(agent == null) {
        		createAgentNode(RGB, -0.1);
        	} else {
        		Link experienceLink = structure.findEntity(agent).getLink(GateTypesIF.GT_SUB, 3);
        		structure.changeLinkParameters(experienceLink, experienceLink.getWeight() - 0.1, 1.0);
        	}
        }
        if(smiledAt.getIncomingActivation() > 0.5) {
        	int[] RGB = new int[3];
        	RGB[0] = (int)(friendRed.getIncomingActivation() * 255);
        	RGB[1] = (int)(friendGreen.getIncomingActivation() * 255);
        	RGB[2] = (int)(friendBlue.getIncomingActivation() * 255);
        	
        	String agent = findAgent(RGB);
        	if(agent == null) {
        		createAgentNode(RGB, 0.1);
        	} else {
        		Link experienceLink = structure.findEntity(agent).getLink(GateTypesIF.GT_SUB, 3);
        		structure.changeLinkParameters(experienceLink, experienceLink.getWeight() + 0.1, 1.0);
        	}
        }

        manipulator.setGateActivation(NEARAGENTSCONCEPT, 1.0);
        
        // percept current situation
        Iterator<SituationElement> it = Situation.getInstance(getNetProperties().getProperty("agentName")).getWholeSituation();
        SituationElement current;
        double currentX;
        double currentY;
        int[] RGB;
        String agent = null;
        ArrayList<String> nearAgents = new ArrayList<String>(5);

        while(it.hasNext()) {
        	try {
	        	current = it.next();
	        	if(current.getType().equals("MouseAgent")) {
	        		currentX = current.getX();
	        		currentY = current.getY();
	        		RGB = Functions.getRGB(current.getWorldID());
	        		if(!knownAgents.contains(new Long(current.getWorldID()))) {
	        			knownAgents.add(new Long(current.getWorldID()));
	        			agent = createAgentNode(RGB, 0.0);
	        			structure.createLink(knownAgentsConcept, GateTypesIF.GT_SUB, agent, SlotTypesIF.ST_GEN, 1.0, 1.0);
	        		} else {
	        			agent = findAgent(RGB);
	        		}
	        		
	        		if(!nearAgents.contains(agent))
	        			nearAgents.add(agent);
	        	}
        	} catch(ConcurrentModificationException e) {
        		it = Situation.getInstance(getNetProperties().getProperty("agentName")).getWholeSituation();
        	}
        }
        
        Iterator<Link> linkIt = structure.findEntity(nearAgentsConcept).getGate(GateTypesIF.GT_SUB).getLinks();
        ArrayList<String> linksToDelete = new ArrayList<String>();
        while(linkIt.hasNext()) {
        	try {
	        	Link link = linkIt.next();
	        	if(nearAgents.contains(link.getLinkedEntityID())) {
	        		nearAgents.remove(link.getLinkedEntityID());
	        	} else if(!linksToDelete.contains(link.getLinkedEntityID())){
	        		linksToDelete.add(link.getLinkedEntityID());    		
	        	}
        	} catch(ConcurrentModificationException e) {
        		linkIt = structure.findEntity(nearAgentsConcept).getGate(GateTypesIF.GT_SUB).getLinks();
        	}
        }
        for(int i = 0; i < linksToDelete.size(); i++) {
        	structure.deleteLink(nearAgentsConcept, GateTypesIF.GT_SUB, linksToDelete.get(i), SlotTypesIF.ST_GEN);
        }
        for(int i = 0; i < nearAgents.size(); i++) {
			structure.createLink(nearAgentsConcept, GateTypesIF.GT_SUB, nearAgents.get(i), SlotTypesIF.ST_GEN, 1.0, 1.0);
        }
	}
	
	private String createAgentNode(int[] RGB, double experience) throws NetIntegrityException {
    	String agent = structure.createConceptNode("agent");
    	String rValue = structure.createConceptNode("r");
    	String gValue = structure.createConceptNode("g");
    	String bValue = structure.createConceptNode("b");
    	String expNode = structure.createConceptNode("experience");
    	
    	structure.createLink(agent, GateTypesIF.GT_SUB, rValue, SlotTypesIF.ST_GEN, (double)RGB[0] / 255.0, 1.0);
    	structure.createLink(agent, GateTypesIF.GT_SUB, gValue, SlotTypesIF.ST_GEN, (double)RGB[1] / 255.0, 1.0);
    	structure.createLink(agent, GateTypesIF.GT_SUB, bValue, SlotTypesIF.ST_GEN, (double)RGB[2] / 255.0, 1.0);
    	structure.createLink(agent, GateTypesIF.GT_SUB, expNode, SlotTypesIF.ST_GEN, experience, 1.0);
    	
    	return agent;  	
    }
	
	/**
	 * @param RGB RGB value of the agent to find
	 * @return ID of the agent or null if agent is not known
	 */
	private String findAgent(int[] RGB) throws NetIntegrityException {
		long ID = Functions.getID(RGB);
		Iterator it = structure.findEntity(knownAgentsConcept).getGate(GateTypesIF.GT_SUB).getLinks();
		long currentID;
		NetEntity current;
		while(it.hasNext()) {
			current = ((Link)it.next()).getLinkedEntity();
			currentID = Functions.getID(NodeFunctions.getRGB(current));
			if(ID == currentID)
				return current.getID();
		}
		
		return null;
	}
	
	/**
	 * deletes all links from near-agent-concept to agent-representations
	 */
	private void clearSituation() throws NetIntegrityException {
		while(structure.findEntity(nearAgentsConcept).getFirstLinkAt(GateTypesIF.GT_SUB) != null) {
			structure.deleteLink(nearAgentsConcept, GateTypesIF.GT_SUB, structure.findEntity(nearAgentsConcept).getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID(), SlotTypesIF.ST_GEN);
		}
	}
}
