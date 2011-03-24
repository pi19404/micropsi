/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/Module.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

/**
 * Module is the abstract ancestor of NodeSpaceModule and NativeModule. Modules
 * are NetEntities with more than one Slot and any number of gates. Additional,
 * they are organized as a tree.
 */
public abstract class Module extends NetEntity {

	/**
	 * The parent's ID
	 */
	protected String parent;
	
	/**
	 * The moduleManager that handles the tree hierarchy
	 */
	protected ModuleManager moduleManager;
     
     /**
      * Creates the module.
      * @param id The entity-ID of the module.
      * @param parent The parent's ID.
      * @param manager the NetEntityManager.
      * @param moduleManager the ModuleManager.
      */
     protected Module(String id, String parent, NetEntityManager manager, ModuleManager moduleManager) {
     	super(id,manager);
     	this.moduleManager = moduleManager;
     	this.parent = parent;
     }
     
     /**
      * Checks if this module is the root module.
      * @return boolean true if this module has no parent
      */
     public boolean isRoot() {
     	return (parent == null);
     }
     
     /**
      * Returns the parent module or null if the module is the root module.
      * @return Module the parent module.
      * @throws NetIntegrityException if the parent entity does not exist or is
      * not a module.
      */
     public Module getParent() throws NetIntegrityException {
     	if(isRoot()) return null;
     	return moduleManager.getModule(parent);
     }
     
     /**
      * Notifies all registered observers of changes within the module.
      */
     protected abstract void notifyObservers();
     
     /**
      * Called from the outside to notify the module of the fact that some
      * entity was deleted
      * @param id the deleted entity's ID.
      */
     protected abstract void reportEntityDeletion(String id);
     
	/**
	 * Changes this module's parent.
	 * @param id the ID of the new parent
	 */

	protected void changeParent(String id) {
		this.parent = id;
		moduleManager.reportModuleParentChange(this);
	} 
    
}
