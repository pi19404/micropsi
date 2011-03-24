/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/progress/ProgressMonitorIF.java,v 1.3 2005/08/12 17:54:16 vuine Exp $ 
 */
package org.micropsi.common.progress;


public interface ProgressMonitorIF {

	public static ProgressMonitorIF DUMMY = new ProgressMonitorIF() {
		public void beginTask(String message) {}
		public void endTask() {}
		public void reportProgress(int progress, int of, String details) {}
	};
	
	public void beginTask(String message);
	
	public void endTask();

	public void reportProgress(int progress, int of, String details);

}
