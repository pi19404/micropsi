/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/model/net/NetModel.java,v 1.4 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.model.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NetObserverIF;


/**
 * 
 * This is a preliminar implementation of what is meant to be a complete model
 * of the net at a later stage, using a remote implementation of NetFacadeIF. 
 * 
 * Currently, it's only used to store positions and 
 * some more metadata.
 *  
 */

public class NetModel {

	protected NetObserverIF modelDeleteObserver = new NetObserverIF() {

		public void updateEntities(Iterator changedKeys, long netstep) {
		}

		public void createEntities(Iterator newKeys, long netstep) {
		}

		public void deleteEntities(Iterator deletedKeys, long netstep) {
			while(deletedKeys.hasNext())
				models.remove(deletedKeys.next());
		}
	}; 
	
	protected NetFacadeIF net;
	protected Map<String,EntityModel> models;
		
	public NetFacadeIF getNet() {
		return net;
	}
	
	public EntityModel getModel(String id) throws MicropsiException {
		if(models.containsKey(id)) {
			return models.get(id);
		} else {
			NetEntity unmodelledEntity = net.getEntity(id);
			EntityModel newModel = new EntityModel(this, unmodelledEntity);
			models.put(id, newModel);
			return newModel;
		}
	}
		
	public void loadModels(InputStream metadata, ProgressMonitorIF progress) {
		if(metadata == null) {
			models = new HashMap<String,EntityModel>();
			return;
		}

		if(models != null) {
			Iterator iter = models.values().iterator();
			while(iter.hasNext()) {
				EntityModel n = (EntityModel)iter.next();
				n.destroy();
			}
		}
		
		this.models = ModelPersistencyManager.loadModels(metadata,this,progress);
	}
	
	public void initFromNet(NetFacadeIF net, InputStream metadata, ProgressMonitorIF progress) {
		
		if(this.net != null) {
			this.net.unregisterNetObserver(modelDeleteObserver);
		}
		
		this.net = net;
		
		loadModels(metadata,progress);

		net.registerNetObserver(modelDeleteObserver);
		
	}
	
	public void saveModels(OutputStream out) throws MicropsiException,IOException {
		ModelPersistencyManager.saveModels(this, out);
	}
			
}
