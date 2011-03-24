/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NativeModule.java,v 1.9 2006/06/27 19:37:02 rvuine Exp $
 */
package org.micropsi.nodenet;

/**
 * NativeModules are entities just like nodespaces or nodes, but their slots and
 * gates are controlled by a custom implementation in java. NativeModules
 * provide an easy way to pass control seamlessly between nodescripts and java
 * sourcecode: You can link any entity to the NativeModule's slots, do
 * calculations inside and propagate the results via the NativeModule's gates.
 */
public final class NativeModule extends Module {

	private AbstractNativeModuleImpl implementation;
	private String implementationName;
	private boolean implementationIsBad = false;
	private boolean hasLostLinks = false;
	private String badMessage = null;
	private Gate[] implGates;
	private Slot[] implSlots;
	private GateManipulator manipulator;
	private UserInteractionManager interaction;
	public NetPropertiesIF netProperties;
	
	/**
	 * Constructs the NativeModule, setting the implementation of
	 * AbstractNativeModuleImpl to be used with this instance of NativeModule.
	 * The AbstractNativeModuleImpl contains the inner logic of the
	 * NativeModule.<br/> The other parameters are standard parameters for an
	 * entity's constructor. (Except the "defiant" parameter: If the
	 * NativeModule is defiant, it will have its calculate()-Method called every
	 * netstep, not only when activation was propagated to the slots.)
	 * @param id the entity's ID
	 * @param parent the module's parent
	 * @param implementationName the classname of the implementation
	 * @param manager the entity manager to be used
	 * @param moduleManager the module manager to be used
	 * @param defiant whether the module is to be defiant
	 */
	protected NativeModule(String id, String parent, String implementationName, NetEntityManager manager, UserInteractionManager interaction, ModuleManager moduleManager, NetPropertiesIF netProperties, boolean defiant) { 
		super(id,parent,manager,moduleManager);
		this.interaction = interaction;
		this.implementationName = implementationName;
		this.netProperties = netProperties;
		if(defiant) manager.reportDefiantEntity(id);
	}
	
	private void reportBadModule(Throwable e) {
		implementationIsBad = true;
		badMessage = e.getMessage()+"@"+e.getStackTrace()[0].getMethodName()+":"+e.getStackTrace()[0].getLineNumber();
			
		entityManager.getLogger().warn(
			"Bad native module: "+
			this+
			" Reason: "+
			badMessage,e
		);
			
	}
	
	public String toString() {
		return getImplementationClassName()+" in "+
			super.toString()+
			" ("+getID()+")";
	}
	
	/**
	 * Initializes the NativeModule. This creates the slots and gates according
	 * to the data provided by the AbstractNativeModuleImpl used.
	 * @throws NetIntegrityException if the data provided by the
	 * AbstractNativeModuleImpl in getGateTypes()/getSlotTypes() is bogus.
	 * (Probably because the returned array of one of the methods contains the
	 * same number twice)
	 * @see Gate
	 * @see Slot
	 */
	protected void initialize() throws NetIntegrityException {
		
		if(implementation == null) return;
		
		try {
			
			int gateTypes[] = implementation.getGateTypes();
			int slotTypes[] = implementation.getSlotTypes();
			
			if(gateTypes == null) {
				gateTypes = new int[0];
			}
			if(slotTypes == null) {
				slotTypes = new int[0];
			}
			
			implGates = new Gate[gateTypes.length]; 
			for(int i=0;i<gateTypes.length;i++)
				implGates[i] = getGate(gateTypes[i]);
	
			implSlots = new Slot[slotTypes.length];
			for(int i=0;i<slotTypes.length;i++)
				implSlots[i] = getSlot(slotTypes[i]);
		
			manipulator = new GateManipulator(this);	
						
			implementation.initialize(entityManager,interaction,this);
			entityManager.reportChangedEntity(this);

			implementationIsBad = false;
			
		} catch (Throwable e) {
			reportBadModule(e);
		}
		
	}
		
	/**
	 * @see org.micropsi.nodenet.NetEntity#calculateGates()
	 */
	protected void calculateGates() throws NetIntegrityException {
		try {
			implementation.calculate(implSlots, manipulator, entityManager.getNetstep());
		} catch (Throwable e) {
			reportBadModule(e);
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetEntity#getEntityType()
	 */
	public int getEntityType() {
		return NetEntityTypesIF.ET_MODULE_NATIVE;
	}
	
	/**
	 * Returns the full qualifying class name of the used
	 * AbstractNavtiveModuleImpl or null if there isn't yet an implementation
	 * @return String the class name
	 */
	public String getImplementationClassName() {
		return (implementation != null ? implementation.getClass().getName() : implementationName);
	}
	
	/**
	 * Returns the classname of the implementation (without package path)
	 * @return String the classname
	 */
	public String getImplementationName() {
		String s = getImplementationClassName();
		return s.substring(s.lastIndexOf(".")+1);
	}
	
	/**
	 * returns the used AbstractNativeModuleImpl or null
	 * @return AbstractNativeModuleImpl the implementation
	 */
	public AbstractNativeModuleImpl getImplementation() {
		return implementation;
	}
	
	/**
	 * Replaces the current implementation with a new one. The method will try
	 * to preserve existing gates and slots where possible (slots and gates that
	 * are required by both the new and the old implementation will survive).
	 * Slots and gates that don't exist in the new implementation will be
	 * unlinked and deleted. The new implementation tries to load the inner
	 * states of the old one. The slots and gates of the newImplementation WILL
	 * NOT BE initialized (initializeGates won't be called). <br><br> You can
	 * replace the old implementation by "null". All slots and gates will be
	 * dropped and the module will be reset to uninitialized state.
	 * @param newImplementation
	 * @throws NetIntegrityException
	 */
	protected void replaceImplementation(AbstractNativeModuleImpl newImplementation) throws NetIntegrityException{
				
		if(newImplementation == null) {
			unlinkCompletely();
			manipulator = null;
			slots.clear();
			gates.clear();
			implementation = null;
			return;
		}
		
		try {
					
			int oldGateTypes[] = (implementation != null) ? implementation.getGateTypes() : new int[0];
			int oldSlotTypes[] = (implementation != null) ? implementation.getSlotTypes() : new int[0];
			
			int newGateTypes[] = newImplementation.getGateTypes();
			int newSlotTypes[] = newImplementation.getSlotTypes();
			
			// look for deleted gates
			for(int i=0;i<oldGateTypes.length;i++) {
				boolean found = false;
				for(int j=0;j<newGateTypes.length;j++)
					if(oldGateTypes[i] == newGateTypes[j]) found = true;
				if(!found) deleteGate(oldGateTypes[i]);		 
			}
			
			// look for deleted slots
			for(int i=0;i<oldSlotTypes.length;i++) {
				boolean found = false;
				for(int j=0;j<newSlotTypes.length;j++)
					if(oldSlotTypes[i] == newSlotTypes[j]) found = true;
				if(!found) deleteSlot(oldSlotTypes[i]);
			}
	
			// look for new gates
			for(int i=0;i<newGateTypes.length;i++) {
				boolean found = false;
				for(int j=0;j<oldGateTypes.length;j++)
					if(newGateTypes[i] == oldGateTypes[j]) found = true;
				if(!found) {
					Gate newGate = new Gate(newGateTypes[i],this,-1);
					addGate(newGate);
				}
			}
	
			// look for new slots
			for(int i=0;i<newSlotTypes.length;i++) {
				boolean found = false;
				for(int j=0;j<oldSlotTypes.length;j++)
					if(newSlotTypes[i] == oldSlotTypes[j]) found = true;
				if(!found) {
					Slot newSlot = new Slot(newSlotTypes[i],this,this.entityManager);
					addSlot(newSlot);
				}
			}
		} catch (Throwable e) {
			reportBadModule(e);
		}
		
		try {
			if(implementation != null) {
				newImplementation.getInnerStates().setMap(
					implementation.getInnerStates().getMap()
				);
			}
		} catch (Exception e) {
			entityManager.getLogger().warn("Transmission of inner states failed when replacing " +
				implementation+				
				" by "+
				newImplementation+". Message: "+e.getMessage());			
		}

		implementation = newImplementation;
		initialize();
	}
	
	/**
	 * Returns the inner state container for this module
	 * @return InnerStateContainer
	 */
	protected InnerStateContainer getInnerStates() {
		return implementation.getInnerStates();
	}
	
	/**
	 * @see org.micropsi.nodenet.Module#notifyObservers()
	 */
	public void notifyObservers() {}
	
	/**
	 * @see org.micropsi.nodenet.Module#reportEntityDeletion(java.lang.String)
	 */
	public void reportEntityDeletion(String id) {}

	/**
	 * Returns if the implementation produced errors
	 * @return boolean true if the implementation produced errors
	 */
	public boolean isImplementationBad() {
		return implementationIsBad;
	}
	
	/**
	 * Checks if the module is marked "defiant"
	 * @return boolean true if the module is defiant
	 */
	public boolean isDefiant() {
		return entityManager.isDefiant(this.getID());
	}
	
	/**
	 * Returns an error string for erroneous implementations
	 * @return a description of the error
	 */
	public String getBadMessage() {
		if(implementationIsBad)
			return badMessage;
		else
			return "";
	}
	
	/**
	 * Native modules lose their links if there is no appropriate implementation
	 * when they are created or loaded. This flag indicates if this has happened.
	 * @return true if this module has lost links
	 */
	public boolean hasLostLinks() {
		return hasLostLinks;
	}
	
	/**
	 * Native modules lose their links if there is no appropriate implementation
	 * when they are created or loaded. The persistency manager calls this method
	 * to indicate a problem with the loading of this module's links.
	 * @param b
	 */
	protected void setHasLostLinks(boolean b) {
		hasLostLinks = b;
	}
	
	public void destroy() {
		if(implementation != null)
			implementation.destroy();
		badMessage = null;
		implGates = null;
		implSlots = null;
		manipulator = null;
		super.destroy();
	}
}
