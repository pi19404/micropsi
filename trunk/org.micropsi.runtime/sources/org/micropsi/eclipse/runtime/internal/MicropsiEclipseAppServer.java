/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/internal/MicropsiEclipseAppServer.java,v 1.3 2004/08/10 14:40:51 fuessel Exp $ 
 */
package org.micropsi.eclipse.runtime.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.help.internal.appserver.IWebappServer;
import org.eclipse.tomcat.internal.TomcatAppServer;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.runtime.IBasicServices;


public class MicropsiEclipseAppServer {

	private static IWebappServer server = null;

	public static void start(String fullpath, int port, ClassLoader loader, IBasicServices bserv) throws MicropsiException {
		
		try {
			server = (IWebappServer)loader.loadClass("org.eclipse.tomcat.internal.TomcatAppServer").newInstance();
		} catch (Exception e) {
			bserv.getLogger().error("Could not load TomcatAppServer",e);
		}	

		try {
			server.start(port, null);
		} catch (CoreException e) {
			bserv.getLogger().error("Could not start TomcatAppServer",e);
		}
		
		Path path = new Path(fullpath);	
		try {
			server.start("micropsi", path, loader);
		} catch (CoreException e) {
			bserv.getLogger().error("Could not start 'aep' web application",e);
		}		
		
		bserv.getLogger().info("Started application server for remote components: http://"+server.getHost()+":"+server.getPort()+"/micropsi/");
		
	}
	
	public static void stop(IBasicServices bserv) {
		try {
			if(server != null)
				server.stop();
		} catch (CoreException e) {
			bserv.getLogger().warn("Problems when stopping 'aep' web application",e);
		}
	}

}