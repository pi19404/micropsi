/*
 * Created on 07.09.2004
 *
 */
package org.micropsi.eclipse.alifeview;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.micropsi.eclipse.runtime.IRuntimeCodeContribution;

/**
 * @author Markus
 *
 */
public class AlifeMouseCodeContribution implements IRuntimeCodeContribution {
    public Bundle getBundle() {
		return Platform.getBundle("org.micropsi.alifeview");
	}
}
