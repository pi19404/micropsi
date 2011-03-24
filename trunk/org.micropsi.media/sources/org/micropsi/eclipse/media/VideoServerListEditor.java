package org.micropsi.eclipse.media;

import java.util.StringTokenizer;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.micropsi.eclipse.media.wizards.NewVideoServerWizard;

public class VideoServerListEditor extends ListEditor {

	private IWorkbench workbench;
	
	public VideoServerListEditor(IWorkbench workbench, String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		this.workbench = workbench;
	}

	protected String getNewInputObject() {
		NewVideoServerWizard serverWizard = new NewVideoServerWizard();		
		WizardDialog dlg = new WizardDialog(workbench.getDisplay().getActiveShell(),serverWizard);
		dlg.open();
		
		if(dlg.getReturnCode() == WizardDialog.CANCEL) {
			return null;
		}
		
		return serverWizard.getVideoServerDescriptor();
	}
	
	protected String createList(String[] items) {
		if(items == null) return "";
		String toReturn = "";
		for(int i=0;i<items.length;i++) toReturn += items[i]+"\n";
		return toReturn;
	}

	protected String[] parseString(String stringList) {
		StringTokenizer tokener = new StringTokenizer(stringList,"\n");
		String[] toReturn = new String[tokener.countTokens()]; 
		
		int i=0;
		while(tokener.hasMoreTokens()) {
			String next = tokener.nextToken();
			if(next.equals("")) continue;
			toReturn[i] = next;
			i++;
		}
		return toReturn;
	}
	
}
