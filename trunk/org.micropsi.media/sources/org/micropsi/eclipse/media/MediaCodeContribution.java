package org.micropsi.eclipse.media;

import org.micropsi.eclipse.runtime.IRuntimeCodeContribution;
import org.osgi.framework.Bundle;

public class MediaCodeContribution implements IRuntimeCodeContribution {

	public Bundle getBundle() {
		try {
			return MediaPlugin.getDefault().getBundle();
		} catch (Exception e) {
			return null;
		}
	}

}
