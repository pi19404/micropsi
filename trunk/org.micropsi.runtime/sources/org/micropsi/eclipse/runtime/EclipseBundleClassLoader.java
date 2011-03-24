/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/EclipseBundleClassLoader.java,v 1.7 2005/08/25 14:44:12 vuine Exp $ 
 */
package org.micropsi.eclipse.runtime;

import java.util.List;

import org.micropsi.eclipse.runtime.internal.RuntimeCodeContributionManager;
import org.osgi.framework.Bundle;

public class EclipseBundleClassLoader extends ClassLoader {

	public EclipseBundleClassLoader() {
	}

	public Class<?> findClass(String className) throws ClassNotFoundException {

		List l = RuntimeCodeContributionManager.getInstance().getContributions();
		for (int i = 0; i < l.size(); i++) {
			IRuntimeCodeContribution contribution = (IRuntimeCodeContribution) l.get(i);
			
			Bundle bundle = contribution.getBundle();
			if (bundle == null) {
				throw new ClassNotFoundException("Bundle not found from contribution " + contribution);
			}
			try {
				Class c = bundle.loadClass(className);
				return c;
			} catch (ClassNotFoundException e) {
			} catch (Throwable e) {
				System.err.println("Warning -- Unexpected bundle class loading problem.");
				e.printStackTrace();
			}
		}

		throw new ClassNotFoundException();
	}

}
