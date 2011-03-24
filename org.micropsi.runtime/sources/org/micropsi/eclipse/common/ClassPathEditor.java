package org.micropsi.eclipse.common;

import java.util.StringTokenizer;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * 
 * 
 * 
 */
public class ClassPathEditor extends ListEditor {

	public ClassPathEditor(String name,Composite parent) {
		super(name,"Classpath",parent);
	}

	protected String createList(String[] items) {
		if(items == null) return "";
		String toReturn = "";
		for(int i=0;i<items.length;i++) toReturn += items[i]+",";
		return toReturn;
	}

	protected String getNewInputObject() {
		FileDialog dlg = new FileDialog(getShell());
		dlg.setFilterExtensions(new String[] {"*.jar"});
		dlg.open();
		return dlg.getFilterPath()+System.getProperty("file.separator")+dlg.getFileName();
	}

	protected String[] parseString(String stringList) {
		StringTokenizer tokener = new StringTokenizer(stringList,",");
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
