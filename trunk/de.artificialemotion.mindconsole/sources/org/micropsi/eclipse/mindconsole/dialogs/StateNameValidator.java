/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/dialogs/StateNameValidator.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IInputValidator;


public class StateNameValidator implements IInputValidator {
	
	private ArrayList existing;
		
	public StateNameValidator(ArrayList existing) {
		this.existing = existing;
	}
		
	public String isValid(String newText) {
		if(newText.length() < 3)
			return "State names must not be shorter than 3 characters.";
		
		if(newText.length() > 20)
			return "State names must not be longer than 20 characters.";
			
		if(existing != null && existing.contains(newText))
			return "A state with that name already exists.";
		
		if(newText.indexOf(".") > 0)
			return "State names must not contain dots.";
			
		if(	newText.indexOf("\\") > 0 	||
			newText.indexOf("/") > 0 	||
			newText.indexOf(":") > 0	||
			newText.indexOf("?") > 0	||
			newText.indexOf("*") > 0	||
			newText.indexOf(" ") > 0	||
			newText.indexOf("&") > 0	||
			newText.indexOf("$") > 0)
			return "State names must not such characters.";

			
		if(newText.length() > 20)
			return "State names must not be 'null'";
							
		return null;
	}
}
