package org.micropsi.eclipse.emotion3d;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.console.IConsolePart;
import org.micropsi.eclipse.runtime.IBasicServices;

public class Emotion3DConsole implements IConsolePart {

	private static Emotion3DConsole instance; 

	private IBasicServices bserv;
	private ConsoleFacadeIF console;

	public static Emotion3DConsole getInstance() {
		return instance;
	}

	public void initialize(ConsoleFacadeIF console, IBasicServices bserv) {
		this.bserv = bserv;
		this.console = console;
		instance = this;
	}
	
	public IBasicServices getBserv() {
		return bserv;
	}

	public ConsoleFacadeIF getConsole() {
		return console;
	}	

}
