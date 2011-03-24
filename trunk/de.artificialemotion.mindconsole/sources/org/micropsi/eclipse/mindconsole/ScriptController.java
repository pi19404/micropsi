/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/ScriptController.java,v 1.2 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import java.lang.ref.WeakReference;

import org.micropsi.eclipse.console.controller.AbstractController;


public class ScriptController extends AbstractController {

	private static ScriptController instance;
	
	public static ScriptController getInstance() {
		if(instance == null) instance = new ScriptController();
		return instance;
	}

	public void run() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((ScriptView)referenced).runScript();
			}
		}		
	}

	public void stop() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((ScriptView)referenced).terminateScript(false);
			}
		}		
	}

	public void kill() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((ScriptView)referenced).terminateScript(true);
			}
		}		
	}
	
}
