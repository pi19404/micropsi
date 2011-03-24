/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/internal/RuntimeCodeContributionManager.java,v 1.3 2005/07/12 12:52:14 vuine Exp $ 
 */
package org.micropsi.eclipse.runtime.internal;

import java.util.ArrayList;
import java.util.List;

import org.micropsi.eclipse.runtime.IRuntimeCodeContribution;


public class RuntimeCodeContributionManager {

	private static RuntimeCodeContributionManager instance;
	
	private RuntimeCodeContributionManager() {};
	
	private ArrayList<IRuntimeCodeContribution> contributions = new ArrayList<IRuntimeCodeContribution>();
	
	public static RuntimeCodeContributionManager getInstance() {
		if(instance == null) instance = new RuntimeCodeContributionManager();
		return instance;
	}
	
	public void registerRuntimeCodeContribution(IRuntimeCodeContribution cont) {
		contributions.add(cont);
	}
	
	public List<IRuntimeCodeContribution> getContributions() {
		return contributions;
	}
}
