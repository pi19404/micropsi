package org.micropsi.eclipse.mindconsole.widgets;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.nodenet.Link;

/**
 * 
 * 
 * 
 */
public interface IMindEditCallback {

	public void handleException(Throwable e);

	public void createSlot(String moduleID);

	public void createGate(String moduleID);

	public void createNativeModule(String parentID);

	public void openEntity(String entityID);
	
	public void selectEntity(String entityID);
	
	public void selectLink(Link link);

	public void connectSensAct(String nodeID);

	public void lookUp(Shell shell, String currentSpace);

	public void setAutoUpdate(Shell shell, String entityID, boolean newState);

	public void openInspector(Shell shell, String entityID);
	
	public void openLinkDialog(Shell shell, Link link);
	
	public void createLink(Shell shell, String entityID);
	
	public void editSpaceProperties(Shell shell, String entityID);

	public void dropEntities(String where, List<EntityTransferData> itens);

	public ProgressMonitorIF createProgressMonitor(String text);
}
