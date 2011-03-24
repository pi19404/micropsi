/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/ModuleManager.java,v 1.4 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.nodenet;

import java.util.HashMap;
import java.util.Iterator;

/**
 * The ModuleManager maintains the tree hierarchy of a net's modules and allows
 * the observation of modules.
 */
public class ModuleManager {
	
	protected HashMap<String,Module> modules = new HashMap<String,Module>();
	protected NetEntityManager entityManager;
	private NodeSpaceModule rootModule;
	
	/**
	 * Constructs the moduleManager, setting the corresponding NetEntityManager
	 * @param entityManager
	 */
	protected ModuleManager(NetEntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	/**
	 * Adds a module to the manager - this only makes the manager aware of the
	 * module, it has to be created and registered with the entityManager just
	 * like a Node.
	 * @param module the module to be added
	 * @throws NetIntegrityException if the module was already added before
	 */
	protected void addModule(Module module) throws NetIntegrityException {
		if(modules.containsKey(module.getID())) throw new NetIntegrityException(NetIntegrityIF.DUPLICATE_KEY,module.getID());
		modules.put(module.getID(), module);
		reportModuleParentChange(module);
	}
	
	/**
	 * Notifies the ModuleManager of some change in the tree structure
	 * @param module the module that changed it's parent
	 */
	protected void reportModuleParentChange(Module module) {	
		if(rootModule == null || !rootModule.isRoot()) {
			if(module.getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE)
				rootModule = (NodeSpaceModule)module;
		}	
	} 
	
	/**
	 * Retrieves a module from the manager.
	 * @param id the module's ID.
	 * @return Module the module
	 * @throws NetIntegrityException if there is no such module
	 */	
	protected Module getModule(String id) throws NetIntegrityException {
		if(!modules.containsKey(id)) throw new NetIntegrityException(NetIntegrityIF.UNKNOWN_MODULE,id);
		return (NodeSpaceModule)modules.get(id);
	}
	
	/**
	 * Returns the root module or null if there is no root module.
	 * @return Module the root module
	 */
	protected NodeSpaceModule getRootModule() {
		return rootModule;
	}
	
	/**
	 * Returns an iterator with instances of Module. Don't call remove().
	 * @return Iterator the modules
	 */
	public Iterator<Module> getModules() {
		return modules.values().iterator();
	}
	
	/**
	 * Checks if some entity is a module.
	 * @param entityID the ID of the entity to be checked
	 * @return boolean true if the entity is a module.
	 */
	public boolean isModule(String entityID) {
		return modules.containsKey(entityID);
	}
	
	/**
	 * Notifies all observers of all modules
	 */
	protected void notifyObservers() {
		Iterator iter = modules.values().iterator();
		while(iter.hasNext()) ((Module)iter.next()).notifyObservers();
	}
	
	/**
	 * Reportes a node's deletion to all modules
	 * @param ID the deleted node's ID
	 */
	protected void reportNodeDeletion(String ID) {
		Iterator iter = modules.values().iterator();
		while(iter.hasNext()) ((Module)iter.next()).reportEntityDeletion(ID);
	}

	/**
	 * Resets the module manager, but leaves it intact for reuse
	 */
	protected void reset() {
		modules.clear();
		rootModule = null;
	}

	/**
	 * Resets the hasDeletedLink flags on all node space modules to false
	 */
	protected void resetDeletedLinksFlags() {
		Iterator m = modules.values().iterator();
		while(m.hasNext()) {
			Module module = (Module)m.next();
			if(module.getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE)
				((NodeSpaceModule)module).setHasDeletedLinks(false);
		}
	}
	
			
}
