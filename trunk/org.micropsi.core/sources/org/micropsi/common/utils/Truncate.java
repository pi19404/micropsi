/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/utils/Truncate.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $ 
 */
package org.micropsi.common.utils;


public class Truncate {

	public static String trDouble(double dbl, int pcdigits) {
		String str = Double.toString(dbl);
		int comma = str.lastIndexOf('.');
		if(comma < 0) return str;		
		return str.substring(0, comma+1+pcdigits);
	}

}
