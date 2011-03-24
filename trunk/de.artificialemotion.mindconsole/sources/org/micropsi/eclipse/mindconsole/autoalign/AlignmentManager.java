/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/autoalign/AlignmentManager.java,v 1.3 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.autoalign;

import java.util.HashMap;
import java.util.Iterator;


public class AlignmentManager {

	private static AlignmentManager instance;

	public static AlignmentManager getInstance() {
		if(instance == null) instance = new AlignmentManager();
		return instance;
	}
	
	
	private HashMap<String,IAutoAlignment> strategies = new HashMap<String,IAutoAlignment>(5);
	
	public void registerAlignmentStrategy(IAutoAlignment strategy) {
		strategies.put(strategy.getName(), strategy);
	}
	
	public Iterator getAlignmentStrategies() {
		return strategies.values().iterator();
	}
	
}
