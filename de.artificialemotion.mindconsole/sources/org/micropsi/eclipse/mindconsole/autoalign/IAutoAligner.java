/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/autoalign/IAutoAligner.java,v 1.4 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.autoalign;

import java.util.List;

import org.micropsi.eclipse.model.net.EntityModel;


public interface IAutoAligner {

	public void moveElement(EntityModel model);
	
	public List<EntityModel> getElements();
	public boolean isSelected(String key);

}
