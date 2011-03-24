package org.micropsi.eclipse.mindconsole;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.mindconsole.widgets.EntityTransferData;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;


/**
 * 
 * 
 * 
 */
public class MindNavigatorCallback extends AbstractMindCallback {

	public MindNavigatorCallback(Shell shell, ModuleJavaManager javaManager) {
		super(shell, javaManager);
	}

	public void selectEntity(String entityID) {	
		EntityEditController.getInstance().setData(entityID);
		
	}
	
	public void openEntity(String entityID) {
		try {
			NetEntity e = netmodel.getNet().getEntity(entityID);
			switch(e.getEntityType()) {
				case NetEntityTypesIF.ET_MODULE_NATIVE:
					javaManager.openInEditor(((NativeModule)e).getImplementationClassName()); 
					break;
				case NetEntityTypesIF.ET_MODULE_NODESPACE:					
					MindEditController.getInstance().setData(entityID);
					break;
				default:
					MindEditController.getInstance().lookUpNode(entityID);
			}
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}		
	}


	public void dropEntities(String where, List<EntityTransferData> items) {
		MindEditController.getInstance().drop(where, items);
	}
}
