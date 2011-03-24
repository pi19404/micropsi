/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NodeSpaceObserverIF.java,v 1.3 2006/06/27 19:37:02 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;
/**
 * Implementations of this interface can be registered with the net to observe
 * changes within one NodeSpace while the net is edited or run. Remember that
 * NodeSpaces are structured hierarchical, so the higher you register an
 * observer the more load it will cause.
 */
public interface NodeSpaceObserverIF {
	
	/**
	 * Will be called at the end of a netstep (aka cycle). The iterator contains
	 * the IDs of all entities that have changes. <i> Every </i> change in the
	 * net will be reported, including the transport of activation
	 * @param changedKeys the keys (Strings) of the changed entites
	 * @param netstep the current netstep
	 */
	public void updateEntities(Iterator<String> changedKeys, long netstep);
	
	/**
	 * Will be called at the end of a netstep (aka cycle). The iterator contains
	 * the IDs of all newly created entities. (New Slots, Gates and Links are
	 * considered to be <i>changes</i> and will not have any influence here.
	 * @param newKeys the keys od the new entities
	 * @param netstep the current netstep
	 */
	public void createEntities(Iterator<String> newKeys, long netstep);
	
	/**
	 * Will be called at the end of a netstep (aka cycle). The iterator contains
	 * the IDs of all deleted entities. Not that, as the entities have been
	 * deleted, it is a bad idea to try to retrieve them from the net. By the
	 * time this method is called, the entities are gone already.
	 * @param deletedKeys the keys of the deleted entities
	 * @param netstep the current netstep
	 */	
	public void deleteEntities(Iterator<String> deletedKeys, long netstep);

}
