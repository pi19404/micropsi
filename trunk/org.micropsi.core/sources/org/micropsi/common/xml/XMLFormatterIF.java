/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/xml/XMLFormatterIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.xml;

public interface XMLFormatterIF {

	public String getBreak();
	public String getTab();
	
	public int getIndentation();
	
	public boolean breakValues();
	public boolean breakTags();

	public boolean surroundTextWithSpaces();
}
