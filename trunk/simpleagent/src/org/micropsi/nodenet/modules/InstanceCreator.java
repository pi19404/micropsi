package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LinkST;
import org.micropsi.nodenet.LinkTypesIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.RegisterNode;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class InstanceCreator extends AbstractNativeModuleImpl {

	private class CloneRelation {
		
		public NetEntity original;
		public NetEntity clone;
		
	}


	// gate types
	private static final int ORIGREG		= 11000;
	private static final int COPYREG		= 11001;
	private static final int ORIGSTARTREG	= 11002;
	private static final int COPYSTARTREG	= 11003;
	private static final int DONE			= 11004; 
	
	// slot types
	private static final int TRIGGER		= 11000;


	private boolean firsttime = true;
	private Slot trigger;
	
	private ArrayList clones = new ArrayList();

	private final int[] gateTypes = { 
		ORIGREG,
		COPYREG,
		ORIGSTARTREG,
		COPYSTARTREG,
		DONE
	};

	private final int[] slotTypes = {
		TRIGGER
	};


	protected int[] getGateTypes() {
		return gateTypes;		
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}

	private void catchSlots(Slot[] slots) {	
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case TRIGGER:
					trigger = slots[i];
					break;
			}
		}
	}
	
	public InstanceCreator() {
				
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			
			private static final String id = "instancecreator";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case ORIGREG:		return "OrigReg";
					case COPYREG:		return "CopyReg";
					case ORIGSTARTREG:	return "OrigStartReg";
					case COPYSTARTREG:	return "CopyStartReg";
					case DONE:			return "Done";					
					default:			return null;
				}
			}

			public String slotType(int type) {
				switch(type) {
					case TRIGGER:		return "Trigger";
					default:			return null;
				}				
			}
		});
		
	}
	
	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			firsttime = false;			
		}
	
		if(trigger.getIncomingActivation() <= 0) return;
		
		clones.clear();
		
		Link origRegLink = gates.getGate(ORIGREG).getLinkAt(0);
		if(origRegLink == null) return;
		NetEntity origReg = origRegLink.getLinkedEntity();
		
		Link copyRegLink = gates.getGate(COPYREG).getLinkAt(0);
		if(copyRegLink == null) return;
		NetEntity copyReg = copyRegLink.getLinkedEntity();
				
		Link origStartRegLink = gates.getGate(ORIGSTARTREG).getLinkAt(0);
		if(origStartRegLink == null) return;
		RegisterNode origStartReg = (RegisterNode)origStartRegLink.getLinkedEntity();
		
		Link copyStartRegLink = gates.getGate(COPYSTARTREG).getLinkAt(0);
		if(copyStartRegLink == null) return;
		RegisterNode copyStartReg = (RegisterNode)copyStartRegLink.getLinkedEntity();
		
		Iterator iter = origReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(iter.hasNext()) {
			// create a clone of every linked entity
		
			Node origNode = (Node)((Link)iter.next()).getLinkedEntity();
			
			String copyNodeID = structure.createConceptNode("clone-"+origNode.getID());
			
			CloneRelation r = new CloneRelation();
			r.original = origNode;
			r.clone = structure.findEntity(copyNodeID);
			clones.add(r);
			
			structure.createLink(
				copyReg.getID(),
				GateTypesIF.GT_GEN,
				copyNodeID,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);
			
		}
		
		// create the new linkage
		for(int i=0;i<clones.size();i++) {
			
			NetEntity original = ((CloneRelation)clones.get(i)).original;
			NetEntity clone = ((CloneRelation)clones.get(i)).clone;
			
			Iterator gatelist = original.getGates();
			while(gatelist.hasNext()) {
				Gate g = (Gate)gatelist.next();
				Iterator linklist = g.getLinks();
				while(linklist.hasNext()) {
					Link l = (Link)linklist.next();
					 
					NetEntity entityToLink = null;
					
					entityToLink = findEntityToLink(l, clones);
							
					switch(l.getType()) {
						case LinkTypesIF.LINKTYPE_SIMPLEASSOCIATION:
							structure.createLink(
								clone.getID(),
								g.getType(),
								entityToLink.getID(),
								l.getLinkedSlot().getType(),
								l.getWeight(),
								l.getConfidence()
							);
							break;
						case LinkTypesIF.LINKTYPE_SPACIOTEMPORAL:
							LinkST stl = (LinkST)l;
							structure.createLink(
								clone.getID(),
								g.getType(),
								entityToLink.getID(),
								l.getLinkedSlot().getType(),
								l.getWeight(),
								l.getConfidence(),
								stl.getX(),
								stl.getY(),
								stl.getZ(),
								stl.getT()
							);
							break;	 	
					}
				}
			}
			
			Iterator slotlist = original.getSlots();
			while(slotlist.hasNext()) {
				Slot s = (Slot)slotlist.next();
				Iterator linklist = s.getIncomingLinks();
				while(linklist.hasNext()) {
					Link l = (Link)linklist.next();
					
					NetEntity entityToBeLinkedFrom = null;
					
					entityToBeLinkedFrom = findEntityToBeLinkedFrom(l, clones);
					
					if(entityToBeLinkedFrom == origReg || entityToBeLinkedFrom == origStartReg) continue;
							
					switch(l.getType()) {
						case LinkTypesIF.LINKTYPE_SIMPLEASSOCIATION:
							structure.createLink(
								entityToBeLinkedFrom.getID(),
								l.getLinkingGate().getType(),
								clone.getID(),
								s.getType(),
								l.getWeight(),
								l.getConfidence()
							);
							break;
						case LinkTypesIF.LINKTYPE_SPACIOTEMPORAL:
							LinkST stl = (LinkST)l;
							structure.createLink(
								entityToBeLinkedFrom.getID(),
								l.getLinkingGate().getType(),
								clone.getID(),
								s.getType(),
								l.getWeight(),
								stl.getConfidence(),
								stl.getX(),
								stl.getY(),
								stl.getZ(),
								stl.getT()
							);
							break;		
					}
					
				}
			}
		}	

		// finally, find and assign the clone of the node that was linked to the "start" register
				
		structure.unlinkGate(copyStartReg.getID(),GateTypesIF.GT_GEN);
		
		Link origStartLink = origStartReg.getGate(GateTypesIF.GT_GEN).getLinkAt(0);
		if(origStartLink == null) return;
		String origStartID = origStartLink.getLinkedEntityID();
				
		String copyStartID = null;
		for(int i=0;i<clones.size();i++) {
			CloneRelation r = (CloneRelation)clones.get(i);
			if(r.original.getID().equals(origStartID)) {
				copyStartID = r.clone.getID();
				break; 
			}
		}
		
		if(copyStartID == null) return;
		
		structure.createLink(
			copyStartReg.getID(),
			GateTypesIF.GT_GEN,
			copyStartID,
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
		
		//structure.unlinkGate(origStartReg.getID(),GateTypesIF.GT_GEN);
		gates.setGateActivation(DONE,1);
		
	}

	/**
	 * Finds the appropriate entity to be linked from. This is normally the entity that links the original, except
	 * if there's a clone for the linking node
	 * @param l
	 * @param clones
	 * @return NetEntity
	 */
	private NetEntity findEntityToBeLinkedFrom(Link l, ArrayList clones) {
		for(int i=0;i<clones.size();i++) {
			CloneRelation r = (CloneRelation)clones.get(i);
			
			// if there is a clone for the linked node, link the clone, not the
			// original
			if(r.original.getID().equals(l.getLinkingEntity().getID()))
				return r.clone;
		}
		
		return l.getLinkingEntity();
	}

	/**
	 * Finds the appropriate entity to link. This is normally the entity linked by the original, except if
	 * there's a clone for the linked node
	 * @param l The link
	 * @param clones the list of clones
	 * @return NetEntity
	 * @throws NetIntegrityException
	 */
	private NetEntity findEntityToLink(Link l,ArrayList clones) throws NetIntegrityException {
					
		for(int i=0;i<clones.size();i++) {
			CloneRelation r = (CloneRelation)clones.get(i);
			
			// if there is a clone for the linked node, link the clone, not the
			// original
			if(r.original.getID().equals(l.getLinkedEntityID()))
				return r.clone;
		}
		
		return l.getLinkedEntity();
	}

}
