package org.micropsi.comp.console.mini.extensions;

import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.console.mini.AbstractConsoleExtension;
import org.micropsi.comp.messages.MAnswer;

/**
 * @author rvuine
 */
public class WorldManipulatorExtension extends AbstractConsoleExtension {

	private static WorldManipulatorExtension instance;

	public static WorldManipulatorExtension getInstance() {
		try {
			ComponentRunner.getInstance();
		} catch (ComponentRunnerException e) {
		}
		return instance;
	}

	/**
	 * WARNING! This is not meant to be called by anybody but java reflection within 
	 * ExtensibleConsoleComponent. If you even think of calling this method, you have
	 * not understood the extensible console component.
	 */
	public WorldManipulatorExtension() {
		instance = this;
	}

	public void receiveAnswer(MAnswer answer) {
	}
	
	public void resetWorld() {
		askQuestion("resetworld", "world", "");
	}
	
	public void resetAgent(String agentID) {
		//@todo Matthias: Please implement a "resetagent" question
		askQuestion("resetagent", "world", agentID);
	}

}
