/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/autoalign/CascadeAlignment.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole.autoalign;

import java.util.Iterator;

import org.micropsi.eclipse.model.net.EntityModel;

public class CascadeAlignment implements IAutoAlignment {

	private static final String name = "cascade";
	private static final String desc = "Cascade all";
	
	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return desc;
	}

	public void align(IAutoAligner toAlign) {
		int i = 0;
		Iterator iter = toAlign.getElements().iterator();
		while(iter.hasNext()) {
			i++;
			EntityModel e = (EntityModel)iter.next();
			e.setX(i*5);
			e.setY(i*5);
			toAlign.moveElement(e);
		}
	}

}
