/*
 * Created on 08.08.2005
 *
 */
package org.micropsi.eclipse.alifemousemanager;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.micropsi.eclipse.runtime.IRuntimeCodeContribution;

/**
 * @author Markus
 *
 */
public class AlifemousemanagerCodeContribution implements IRuntimeCodeContribution {
    public Bundle getBundle() {
		return Platform.getBundle("org.micropsi.alifemousemanager");
	}
}
