/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetObserverIF.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;

/**
 * Implementations can be registered with the net to observe changes that take
 * place inside it while it is edited or run. Keep in mind that implementations
 * of this interface will observe the whole net. Registering and using them will
 * probably cause serious impact.
 */
public interface NetObserverIF {
	
	/**
	 * Will be called at the end of a netstep (aka cycle). The iterator contains
	 * the IDs of all entities that have changes. <i> Every </i> change in the
	 * net will be reported, including the transport of activation
	 * @param changedKeys the keys (Strings) of the changed entites 
	 * @param netstep the current netstep
	 */
	public void updateEntities(Iterator changedKeys, long netstep);
	
	/**
	 * Will be called at the end of a netstep (aka cycle). The iterator contains
	 * the IDs of all newly created entities. (New Slots, Gates and Links are
	 * considered to be <i>changes</i> and will not have any influence here.
	 * @param newKeys the keys od the new entities
	 * @param netstep the current netstep
	 */
	public void createEntities(Iterator newKeys, long netstep);
	
	/**
	 * Will be called at the end of a netstep (aka cycle). The iterator contains
	 * the IDs of all deleted entities. Not that, as the entities have been
	 * deleted, it is a bad idea to try to retrieve them from the net. By the
	 * time this method is called, the entities are gone already.
	 * @param deletedKeys the keys of the deleted entities
	 * @param netstep the current netstep
	 */
	public void deleteEntities(Iterator deletedKeys, long netstep);

}
