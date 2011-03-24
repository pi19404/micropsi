/*
 * Created on 25.04.2005
 *
 */

package org.micropsi.eclipse.worldconsole;

import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.console.worldconsole.EditSession;
import org.micropsi.comp.console.worldconsole.LocalWorld;
import org.micropsi.comp.console.worldconsole.RemoteWorld;
import org.micropsi.comp.console.worldconsole.SWTAwareAnswerQueue;
import org.micropsi.comp.console.worldconsole.WorldMetaDataController;
import org.micropsi.eclipse.worldconsole.actions.CreateObjectAction;
import org.micropsi.eclipse.worldconsole.actions.LoadWorldAction;
import org.micropsi.eclipse.worldconsole.actions.RemoveObjectAction;
import org.micropsi.eclipse.worldconsole.actions.SaveWorldAction;

/**
 * @author Matthias
 */
public class GlobalData {

	private LocalWorld localWorld;
	private RemoteWorld remoteWorld;
	private EditSession editSession;
	private WorldMetaDataController worldMetaData;
	private MenuManager menuManager;
	
	/**

	 * 
	 */
	public GlobalData(ConsoleFacadeIF console) {
		String configFileUrl = null;
		try {
			configFileUrl = Platform.asLocalURL(WorldPlugin.getDefault().find(new Path("config/worldviewconfig.xml"))).getFile();
		} catch (IOException e) {
			WorldPlugin.getDefault().handleException(e);
		}
		
		try {
			localWorld = new LocalWorld(configFileUrl, console, SWTAwareAnswerQueue.class);
			localWorld.setQuestionErrorHandler(QuestionErrorHandler.getInstance());
			remoteWorld = new RemoteWorld("world", console, SWTAwareAnswerQueue.class);
			remoteWorld.setQuestionErrorHandler(QuestionErrorHandler.getInstance());
			editSession = new EditSession(localWorld);
			worldMetaData = new WorldMetaDataController(console, SWTAwareAnswerQueue.class);
			worldMetaData.setQuestionErrorHandler(QuestionErrorHandler.getInstance());
		} catch (MicropsiException e) {
			WorldPlugin.getDefault().getConsole().getExproc().handleException(e);
		}
		menuManager = new MenuManager();
		menuManager.add(new CreateObjectAction());
		menuManager.add(new RemoveObjectAction());
		menuManager.add(new Separator());
		menuManager.add(new LoadWorldAction());
		menuManager.add(new SaveWorldAction());
	}

	/**
	 * @return Returns the editSession.
	 */
	public EditSession getEditSession() {
		return editSession;
	}
	/**
	 * @return Returns the localWorld.
	 */
	public LocalWorld getLocalWorld() {
		return localWorld;
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	/**
	 * @return Returns the worldMetaData.
	 */
	public WorldMetaDataController getWorldMetaData() {
		return worldMetaData;
	}

	/**
	 * @return Returns the remoteWorld.
	 */
	public RemoteWorld getRemoteWorld() {
		return remoteWorld;
	}
	/**
	 * @param remoteWorld The remoteWorld to set.
	 */
	public void setRemoteWorld(RemoteWorld remoteWorld) {
		this.remoteWorld = remoteWorld;
	}
}
