/*
 * Created on 03.03.2003
 *
 */
package org.micropsi.comp.console;

/**
 * @author daniel
 */
public class Utilities {

	public static final int DEBUG_STATE_DEBUG = 0;
	public static final int DEBUG_STATE_NO_DEBUG = 1;

	public static int debugState = DEBUG_STATE_NO_DEBUG;

	public static boolean equiv(boolean a, boolean b) {
		return (a && b) || (!a && !b);
	}
    
    public static boolean equiv(Object a, Object b){
        return (a == null && b == null) || a.equals(b);
    }
    
    public static int key(Object o){
        return (o==null?0:o.hashCode());
    }

}
