/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/console/mini/extensions/ReferenceScannerExtension.java,v 1.1 2005/01/20 23:24:56 vuine Exp $ 
 */
package org.micropsi.comp.console.mini.extensions;

import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.console.mini.AbstractConsoleExtension;
import org.micropsi.comp.messages.MAnswer;

import com.jb2works.reference.HttpScanner;


public class ReferenceScannerExtension extends AbstractConsoleExtension {

	private static ReferenceScannerExtension instance;

	public static ReferenceScannerExtension getInstance() {
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
	public ReferenceScannerExtension() {
		instance = this;
		try {
			HttpScanner.start(ComponentRunner.getInstance());
		} catch (ComponentRunnerException e) {
		}
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.AbstractConsoleExtension#receiveAnswer(org.micropsi.comp.messages.MAnswer)
	 */
	protected void receiveAnswer(MAnswer answer) {
	}

}
