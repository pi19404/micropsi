/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/autoalign/IAutoAlignment.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole.autoalign;

import org.micropsi.common.exception.MicropsiException;


public interface IAutoAlignment {

	public String getName();
	
	public String getDisplayName();
	
	public void align(IAutoAligner toAlign) throws MicropsiException;
	
}
