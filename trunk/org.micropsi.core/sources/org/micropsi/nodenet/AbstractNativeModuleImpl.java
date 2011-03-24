/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/AbstractNativeModuleImpl.java,v 1.10 2006/08/03 15:40:37 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;

/**
 * Implementations of this abstract class can be plugged into NativeModules and
 * act as inner logic of some NetEntity. The main job for any implementation
 * will be the calculation of a set of gates from a set of slots. You can wrap
 * any logics into a AbstractNativeModuleImpl, as long you mind some
 * constraints: First, NativeModules must be linkable with the net. So, every IO
 * has to be done in doubles via Slots and Gates. Second, NativeModule
 * implementations must stay within the net's heartbeat: Any calculation
 * necessarily terminates within the current netstep. So, some calculation that
 * takes more than one net cycle will have to use some sort of internal program
 * counter.
 */
public abstract class AbstractNativeModuleImpl {

	protected StructureModificator structure;
	protected UserInteraction userinteraction;
	protected NetStepLogger logger;

	protected InnerStateContainer innerstate = new InnerStateContainer();

	protected final class NetStepLogger {

		private Logger realLogger;
		
		public NetStepLogger(Logger realLogger) {
			this.realLogger = realLogger;
		}

		public void debug(Object data) {
			realLogger.debug(structure.entityManager.getNetstep()+": "+data);
		}

		public void debug(Object data, Throwable cause) {
			realLogger.debug(structure.entityManager.getNetstep()+": "+data, cause);
		}

		public void error(Object data) {
			realLogger.error(structure.entityManager.getNetstep()+": "+data);
		}

		public void error(Object data, Throwable cause) {
			realLogger.error(structure.entityManager.getNetstep()+": "+data, cause);
		}

		public void fatal(Object data) {
			realLogger.fatal(structure.entityManager.getNetstep()+": "+data);
		}

		public void fatal(Object data, Throwable cause) {
			realLogger.fatal(structure.entityManager.getNetstep()+": "+data, cause);
		}

		public String getName() {
			return realLogger.getName();
		}

		public Category getParent() {
			return realLogger.getParent();
		}

		public void info(Object data) {
			realLogger.info(structure.entityManager.getNetstep()+": "+data);
		}

		public void info(Object data, Throwable cause) {
			realLogger.info(structure.entityManager.getNetstep()+": "+data, cause);
		}

		public void warn(Object data) {
			realLogger.warn(structure.entityManager.getNetstep()+": "+data);
		}

		public void warn(Object data, Throwable cause) {
			realLogger.warn(structure.entityManager.getNetstep()+": "+data, cause);
		}

	}

	protected final class UserInteraction {

		protected UserInteractionManager interaction;
		
		protected UserInteraction(UserInteractionManager interaction) {
			this.interaction = interaction;
		}
		
		/**
		 * Allows the user to select a subset of Strings from a set of strings
		 * @param alternatives The set of strings to select from
		 * @return the subset of strings that were selected
		 */
		public String[] selectFromAlternatives(String[] alternatives) {
			return interaction.selectFromAlternatives(alternatives);
		}

		/**
		 * Allows the user to enter a string
		 * @param prompt The text to prompt the user with 
		 * @return the string entered by the user
		 */
		public String askUser(String prompt) {
			return interaction.askUser(prompt);
		}

		/**
		 * Displays some text
		 * @param information
		 */
		public void displayInformation(String information) {
			interaction.displayInformation(information);
		}
			
	}

	protected final class StructureModificator {
		
		private NetEntityManager entityManager;
		private Module module;
		
		private StructureModificator(NetEntityManager manager, Module correspondingModule) {
			this.entityManager = manager;
			this.module = correspondingModule;
		}
		
		/**
		 * Finds an entity object by the given ID
		 * @param entityID the id of the entity
		 * @return the NetEntity or null if there is no such entity
		 */
		public NetEntity findEntity(String entityID) {
			try {
				return entityManager.getEntity(entityID);
			} catch (NetIntegrityException e) {
				return null;
			}	
		}
		
		/**
		 * Create a new link
		 * @param from	the entity where to link from
		 * @param gate the origin entity's gate
		 * @param to the entity to be linked
		 * @param slot the linked entity's slot
		 * @param weight the initial weight
		 * @param confidence the initial confidence
		 * @throws NetIntegrityException if there is no such entity, gate or
		 * slot
		 */
		public void createLink(String from, int gate, String to, int slot, double weight, double confidence) throws NetIntegrityException {
			NetEntity frome;
			frome = entityManager.getEntity(from);
			frome.createLinkTo(to, slot, frome.getGate(gate), weight, confidence);
			
		}
		
		/**
		 * Creatre a new ST link
		 * @param from	the entity where to link from
		 * @param gate the origin entity's gate
		 * @param to the entity to be linked
		 * @param slot the linked entity's slot
		 * @param weight the initial weight
		 * @param confidence the initial confidence
		 * @param x the x value of the new ST link
		 * @param y the y value of the new ST link
		 * @param z the z value of the new ST link
		 * @param t the t value of the new ST link
		 * @throws NetIntegrityException
		 */
		public void createLink(String from, int gate, String to, int slot, double weight, double confidence, double x, double y, double z, int t) throws NetIntegrityException {
			NetEntity frome;
			frome = entityManager.getEntity(from);
			frome.createLinkTo(to, slot, frome.getGate(gate), weight, confidence,x,y,z,t);
		}
		
		/**
		 * Deletes a link.
		 * @param from the origin of the link
		 * @param gate the origin of the link
		 * @param to the target of the link
		 * @param slot the target of the link
		 * @throws NetIntegrityException if any of the given entities does not
		 * exist or there is no such link.
		 */
		public void deleteLink(String from, int gate, String to, int slot) throws NetIntegrityException {
			NetEntity frome = entityManager.getEntity(from);
			NetEntity toe = entityManager.getEntity(to);
			
			NodeSpaceModule fromParent = (NodeSpaceModule)entityManager.getEntity(frome.getParentID());
			fromParent.setHasDeletedLinks(true);
			NodeSpaceModule toParent = (NodeSpaceModule)entityManager.getEntity(toe.getParentID());
			toParent.setHasDeletedLinks(true);

			frome.deleteLink(gate, to, slot);
		}
		
		/**
		 * Changes the parameters of a link.
		 * @param l The link whose parameters are to be changed
		 * @param weight the new weight for the link
		 * @param confidence the new confidence for the link
		 */
		public void changeLinkParameters(Link l, double weight, double confidence) {
			l.setWeight(weight);
			l.setConfidence(confidence);
		}

		/**
		 * Changes the parameters of an st link.
		 * @param l The link whose parameters are to be changed
		 * @param weight the new weight for the link
		 * @param confidence the new confidence for the link
		 * @param t the new t value
		 * @param x the new x value
		 * @param y the new y value
		 * @param z the new z value
		 */
		public void changeLinkParameters(LinkST l, double weight, double confidence, int t, double x, double y, double z) {
			l.setWeight(weight);
			l.setConfidence(confidence);
			l.setT(t);
			l.setX(x);
			l.setY(y);
			l.setZ(z);
		}
		
		/**
		 * Creates a new Chunk within the parent NodeSpace of the
		 * NativeModule and returns the ID of the newly created node.
		 * @return String the ID of the new chunk
		 */
		public String createChunkNode(String name) throws NetIntegrityException {
			Node newNode = NetEntityFactory.getInstance().createNode(
				NodeFunctionalTypesIF.NT_CHUNK,
				(NodeSpaceModule)module.getParent(), 
				entityManager, 
				null);
			newNode.setEntityName(name);			
			return newNode.getID();
		}

		/**
		 * Creates a new ConceptNode within the parent NodeSpace of the
		 * NativeModule and returns the ID of the newly created node.
		 * @return String the ID of the new concept node
		 */
		public String createConceptNode(String name) throws NetIntegrityException {
			Node newNode = NetEntityFactory.getInstance().createNode(
				NodeFunctionalTypesIF.NT_CONCEPT,
				(NodeSpaceModule)module.getParent(), 
				entityManager, 
				null);
			newNode.setEntityName(name);			
			return newNode.getID();
		}

		/**
		 * Creates a new TopoNode within the parent NodeSpace of the
		 * NativeModule and returns the ID of the newly created node.
		 * @return String the ID of the new topo node
		 */
		public String createTopoNode(String name) throws NetIntegrityException {
			Node newNode = NetEntityFactory.getInstance().createNode(
				NodeFunctionalTypesIF.NT_TOPO,
				(NodeSpaceModule)module.getParent(), 
				entityManager, 
				null);
			newNode.setEntityName(name);			
			return newNode.getID();
		}
		
		/**
		 * Creates a new RegisterNode within the parent NodeSpace of the
		 * NativeModule and returns the ID of the newly created node.
		 * @return String the ID of the new concept node
		 */
		public String createRegisterNode(String name) throws NetIntegrityException {
			Node newNode = NetEntityFactory.getInstance().createNode(
				NodeFunctionalTypesIF.NT_REGISTER,
				(NodeSpaceModule)module.getParent(), 
				entityManager, 
				null);
			newNode.setEntityName(name);			
			return newNode.getID();
		}

		/**
		 * Deletes an entity
		 * @param ID the ID of the node to be deleted
		 */
		public void deleteEntity(String ID) throws NetIntegrityException {
			entityManager.deleteEntity(ID);
			module.moduleManager.reportNodeDeletion(ID);
		}
		
		/**
		 * Creates a new gate manipulator for the given entity. 
		 * @param ID the ID of the entity
		 * @return GateManipulator the new manipulator
		 * @throws NetIntegrityException if there is no such entity
		 */
		public GateManipulator getGateManipulator(String ID) throws NetIntegrityException {			
			return new GateManipulator(entityManager.getEntity(ID));
		}
		
		/**
		 * Puts activation into the GEN slot of the given node
		 * @param ID the id of the node
		 * @param activation the activation
		 * @throws NetIntegrityException if there is no entity with that ID or
		 * the entity does not have a GEN slot.
		 */
		public void activateNode(String ID, double activation) throws NetIntegrityException {
			activateEntity(ID,SlotTypesIF.ST_GEN,activation);
		}
		
		/**
		 * Puts activation into a slot at some entity.
		 * @param ID the id of the entity
		 * @param slot the slot
		 * @param activation the activation
		 * @throws NetIntegrityException if there is no such entity or the
		 * entity doesn't have a slot of the given type
		 */
		public void activateEntity(String ID, int slot, double activation) throws NetIntegrityException {
			NetEntity entity = entityManager.getEntity(ID);
			entity.getSlot(slot).putActivation(activation);
		}
		
		/**
		 * Returns the parent space of the structure object's corresponding module.
		 * @return NodeSpaceModule the parent space
		 * @throws NetIntegrityException if the parent can't be found (this shouldn't 
		 * happen unless your net is severely messed up)
		 */
		public NodeSpaceModule getSpace() throws NetIntegrityException {
			return (NodeSpaceModule)module.getParent();
		}
				
		/**
		 * Unlinks a gate completely
		 * @param node The entity id
		 * @param gate the gate
		 * @throws NetIntegrityException if there's a bad link at the node or the
		 * node does not exist
		 * @throws NullPointerException if there's no such gate
		 */
		public void unlinkGate(String entity, int gate) throws NetIntegrityException {
			NetEntity e = entityManager.getEntity(entity);
			
			NodeSpaceModule parent = (NodeSpaceModule)entityManager.getEntity(e.getParentID());
			parent.setHasDeletedLinks(true);
			
			Iterator links = e.getGate(gate).getLinks();
			while(links.hasNext()) {
				Link l = (Link)links.next();
				NodeSpaceModule linkedparent = (NodeSpaceModule)entityManager.getEntity(l.getLinkedEntity().getParentID());
				linkedparent.setHasDeletedLinks(true);
			}
			
			e.getGate(gate).unlinkCompletely();
		}
		
		/**
		 * Clones a list of entities and returns a String-to-String mapping hash
		 * between the original and cloned entity ids. Valid values of the preserveMode
		 * variable are: 
		 * <ol>
		 * <li> NetWeaver.PM_PRESERVE_ALL</li>
		 * <li> NetWeaver.PM_PRESERVE_INTER</li>
		 * <li> NetWeaver.PM_PRESERVE_NONE</li>
		 * </ol>
		 * For an explanation of what these mean, @see NetWeaver
		 * @param entities A list of entity objects to be cloned.
		 * @param space The nodespace where the clones are to be created
		 * @param preserveMode The preserve mode.
		 * @return the clone mappings
		 * @throws NetIntegrityException if the target space does not exist.
		 * @throws MicropsiException if the net is really badly messed up
		 */
		public Map<String,String> cloneEntities(List<NetEntity> entities, String space, int preserveMode) throws NetIntegrityException, MicropsiException {
		
			HashMap<String,String> cloneMappings = new HashMap<String,String>(entities.size());
			
			NetWeaver.insertEntities(
				entityManager.getCorrespondingFacade(), 
				entities,
				space, 
				preserveMode, 
				cloneMappings,
				null
			);
			
			return cloneMappings;
		}
		
		/**
		 * Changes the name of an entity.
		 * @param entityID the id of the entity
		 * @param newName the entity's new name
		 * @throws NetIntegrityException if the entity does not exist.
		 */
		public void changeEntityName(String entityID, String newName) throws NetIntegrityException {
			entityManager.getEntity(entityID).setEntityName(newName);
		}
		
		/**
		 * Destroys the instance for less gc load
		 */
		protected void destroy() {
			entityManager = null;
			module = null;			
		}
		
	}
	
	/**
	 * Returns the types of gates to create for the module. This method is the
	 * first to be called when the module is set up.
	 * @return int[] An array of types
	 */
	protected abstract int[] getGateTypes();
	 
	/**
	 * Returns the types of slots to create for the module. This method is the
	 * second to be called during module setup.
	 * @return int[]
	 */
	protected abstract int[] getSlotTypes();
	
	/**
	 * The module implemented by this abstract implementation
	 */
	private NativeModule module;
	
	/**
	 * Used by NativeModule to set the entity manager. THIS METHOD IS FOR
	 * INTERNAT USE ONLY. DO NOT CALL.
	 * @param manager
	 */
	protected final void initialize(NetEntityManager manager, UserInteractionManager interaction, NativeModule module) {
		this.module = module;
		structure = new StructureModificator(manager,module);
		userinteraction = new UserInteraction(interaction);
		logger = new NetStepLogger(manager.getLogger());
	}
	
	/**
	 * Returns the innerstate object of the module
	 * @return InnerStateContainer
	 */
	protected InnerStateContainer getInnerStates() {
		return innerstate;
	}
	
	/**
	 * Returns the net properties object which typically contains information
	 * about the agent's name and the like. What information exacly is in the 
	 * properties object is, however, dependent on the agent implementation.
	 * @return the net property object of this net
	 */
	protected NetPropertiesIF getNetProperties() {
		return module.netProperties;
	}
		
	/**
	 * This method is called whenever the module needs to calculate it's gates.
	 * @param slots The module's slots
	 * @param manipulator The manipulator to be used for changing the gates
	 */
	public abstract void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException;

	/**
	 * Destroys the instance for less gc load
	 */
	protected void destroy() {
		structure.destroy();
		structure = null;
		innerstate = null;
		userinteraction = null;
		logger = null;
	}
	
}
