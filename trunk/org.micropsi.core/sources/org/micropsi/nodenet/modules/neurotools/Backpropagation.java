package org.micropsi.nodenet.modules.neurotools;

import java.util.HashMap;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;
import org.micropsi.nodenet.outputfunctions.OFLogistic;

public class Backpropagation extends AbstractNativeModuleImpl {
	
	// slots
	private static final int ST_TRIGGER			=	10101;
	
	
	// gates
	private static final int GT_OUTPUTNEURONS	=	10100;
	private static final int GT_GLOBALERROR		=	10101;
	private static final int GT_TARGETS			= 	10102;

	boolean firsttime = true;
	private Slot trigger;
	
	protected int[] getGateTypes() {
		return new int[] {GT_OUTPUTNEURONS,GT_TARGETS,GT_GLOBALERROR};
	}

	protected int[] getSlotTypes() {
		return new int[] {ST_TRIGGER};
	}
	
	public Backpropagation() {

		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			
			public String getExtensionID() {
				return "backpropagation";
			}
			
			public String gateType(int type) {
				switch(type) {
					case GT_OUTPUTNEURONS:		return "OutpNeurons";
					case GT_GLOBALERROR:		return "GlobalError";
					case GT_TARGETS:			return "TargetSens";
					default:					return null;
				}				
			}
			
			public String slotType(int type) {
				switch(type) {
					case ST_TRIGGER:	return "Trigger";
					default: 			return null;
				}
			}
		});
	}
	
	private void catchSlots(Slot[] slots) {
		for(int i=0;i<slots.length;i++) {
			switch(slots[i].getType()) {
				case ST_TRIGGER:
					trigger = slots[i];
					break;

			}
		}		
	}
	
	double globalError = 0;
	HashMap<Node,Double> errors = new HashMap<Node,Double>();
	
	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
		if(firsttime) { 
			catchSlots(slots);
			firsttime = false;
		}
		
		/*
		 * This is meant to be a general backpropagation module for fully connected feed forward nets
		 * with any number of hidden layers and a single output neuron. (as there is only one target slot -- this should
		 * be easy to adapt for nets with more output neurons)
		 * Output function is assumed to be OFLogistic.  
		 */
				
		if(trigger.getIncomingActivation() != 0) {
			
			double[] targets = new double[manipulator.getGate(GT_TARGETS).getNumberOfLinks()];
			for(int i=0;i<targets.length;i++) {
				targets[i] = manipulator.getGate(GT_TARGETS).getLinkAt(i).getLinkedEntity().getGate(GateTypesIF.GT_GEN).getConfirmedActivation();
			}
					
//			logger.info("Backpropagation in step "+netstep+". Target value: "+target.getIncomingActivation());
//			logger.info("h value: "+structure.findEntity("61-050915/170536").getGate(GateTypesIF.GT_GEN).getConfirmedActivation());
//			logger.info("o value: "+structure.findEntity("9-050916/183648").getGate(GateTypesIF.GT_GEN).getConfirmedActivation());
			
			// calculate the errors for the output layer
			int i = 0;
			Iterator<Link> allONLinks = manipulator.getGate(GT_OUTPUTNEURONS).getLinks();
			Node someOutputNeuron = null;
			while(allONLinks.hasNext()) {
				Node outputNeuron = (Node)allONLinks.next().getLinkedEntity();
				
				double tgt = targets[i];
				i++;
				double value = outputNeuron.getGenActivation();				
				double delta = value * (1-value) * (tgt-value);
					
				if(errors.get(outputNeuron) != null) {
					delta += errors.get(outputNeuron).doubleValue();
				}
				
				errors.put(outputNeuron,new Double(delta));

				globalError += (tgt-value);
				
				someOutputNeuron = outputNeuron;
			}
						
			Node someCurrentLayerNeuron = someOutputNeuron;
			boolean isInputLayer = false;
			
			// calculate the errors for all hidden layers
			do {
				
				NetEntity somePrevLayerEntity = someCurrentLayerNeuron.getSlot(SlotTypesIF.ST_GEN).getIncomingLinkAt(0).getLinkingEntity();
				i=0;
				while(somePrevLayerEntity.getEntityType() != NetEntityTypesIF.ET_NODE) {
					i++;
					somePrevLayerEntity = someCurrentLayerNeuron.getSlot(SlotTypesIF.ST_GEN).getIncomingLinkAt(i).getLinkingEntity();
					// the NullPointerException is ok if there are no nodes (which does not make any sense)
				}
				
				Node somePrevLayerNeuron = (Node)somePrevLayerEntity;
								
				isInputLayer = isInputLayer(somePrevLayerNeuron);
				
				// calculate the errors for this layer
				Iterator<Link> linksFromPrevLayer = someCurrentLayerNeuron.getSlot(SlotTypesIF.ST_GEN).getIncomingLinks();
				while(linksFromPrevLayer.hasNext()) {
					NetEntity entity = linksFromPrevLayer.next().getLinkingEntity();
					
					if(entity.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
					
					Node layerNode = (Node)entity;
										
					double value = layerNode.getGenActivation();
					double sum = 0;
					Iterator<Link> linksToNextLayer = layerNode.getGate(GateTypesIF.GT_GEN).getLinks();
					while(linksToNextLayer.hasNext()) {
						Link l = linksToNextLayer.next();
						double nextLayerNodeError = errors.get(l.getLinkedEntity()).doubleValue();
						sum += (nextLayerNodeError * l.getWeight());
					}
					
					double delta = value * (1-value) * sum;
										
					if(errors.get(layerNode) != null) {
						delta += errors.get(layerNode).doubleValue();
					}

					errors.put(layerNode,new Double(delta));
				}
		
				someCurrentLayerNeuron = somePrevLayerNeuron;
				
			} while (!isInputLayer);
			 	
		}
		
		if(trigger.getIncomingActivation() > 0) {
						
			double learningConstant = structure.getSpace().getStrengtheningConstant();
			
			// adjust link weights and thetas (apply delta rule)
			Iterator<Node> allNodes = errors.keySet().iterator();
			while(allNodes.hasNext()) {
				Node next = allNodes.next();
				double error = errors.get(next).doubleValue();
								
//				if(!isInputLayer(next)) {
				
					// ------------- theta adjustment -----------------
					GateManipulator g = structure.getGateManipulator(next.getID());
					if(g.getGate(GateTypesIF.GT_GEN).getOutputFunction().getClass() == OFLogistic.class) {
						double newTheta = g.getGate(GateTypesIF.GT_GEN).getOutputFunctionParameter("theta") - (learningConstant * error);
						g.setOutputFunctionParameter(GateTypesIF.GT_GEN,"theta",newTheta);
					}
										
					// ------------- w adjustment -------------------
					Iterator<Link> allLinks = next.getSlot(SlotTypesIF.ST_GEN).getIncomingLinks();
					while(allLinks.hasNext()) {
						Link l = allLinks.next();
						if(l.getLinkingEntity().getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			
						double newWeight = l.getWeight() + (learningConstant * error * ((Node)l.getLinkingEntity()).getGenActivation());									
						structure.changeLinkParameters(
							l,
							newWeight,
							l.getConfidence()
						);	
					}
				}
//			}

			manipulator.setGateActivation(GT_GLOBALERROR,globalError);			
			
			errors.clear();
			globalError = 0;
		}
		
	}
	
	private boolean isInputLayer(Node n) {
		
		boolean isInputLayer = false;
		
		// check if the neuron belongs to the input layer
		if(!n.getSlot(SlotTypesIF.ST_GEN).hasIncomingLinks()) {
			isInputLayer = true;
		} else {	
			isInputLayer = true;
			Iterator<Link> links = n.getSlot(SlotTypesIF.ST_GEN).getIncomingLinks();
			while(links.hasNext()) {
				Link l = links.next();
				
				NetEntity e = l.getLinkingEntity(); 
				if(e.getEntityType() == NetEntityTypesIF.ET_NODE) {
					isInputLayer = false;
					break;
				}
			}	
		}
		
		return isInputLayer;
	}
		

}
