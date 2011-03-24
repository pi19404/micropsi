/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.tomcat.internal;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Realm;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.logger.FileLogger;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Embedded;
import org.apache.coyote.tomcat4.CoyoteConnector;
import org.apache.coyote.tomcat4.CoyoteServerSocketFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.help.internal.appserver.IWebappServer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.tomcat.internal.extensions.IRealmFactory;

/**
 * Single engine, single host, single connector Tomcat Application Server.
 */
public class TomcatAppServer implements IWebappServer {
    /**
     * Specify this reserved value for the SSL port # to indicate that SSL
     * should not be used
     */
    public final static int SSL_DISABLED = -1;

    private String hostAddress;

    private int port;

    private int sslPort = SSL_DISABLED;

    // false until an attempt to start Tomcat
    private boolean isStarted = false;

    // true after started without problems
    private boolean running = false;

    private Embedded embedded = null;

    private Engine engine = null;

    private Host host = null;

    private Connector httpConnector = null;
    private Connector sslConnector = null;
    // Con
    private ArrayList contexts = new ArrayList();

    /**
     * Constructs this class, but does not instantiates or start Tomcat classes
     * until webapp are added.
     */
    public TomcatAppServer() {
    }

    /**
     * @see org.eclipse.help.internal.appserver.IWebappServer#start(int,
     *      java.lang.String)
     */
    public synchronized void start(int port, String hostAddress) throws CoreException {
        this.hostAddress = hostAddress;
        this.port = port;

        if (isStarted) {
            return;
        }
        isStarted = true;
        try {
            FileLogger logger = new FileLogger();
            logger.setDirectory(TomcatPlugin.getDefault().getStateLocation()
                    .toOSString());
            embedded = new Embedded(logger, new MemoryRealm());
            embedded.setDebug(0);
            embedded.setLogger(logger);
            URL installURL = TomcatPlugin.getDefault().getBundle()
                    .getEntry("/"); //$NON-NLS-1$
            URL resolvedURL = FileLocator.resolve(installURL);
            String home = FileLocator.toFileURL(resolvedURL).getFile();
            System.setProperty("catalina.home", home); //$NON-NLS-1$
            String base = home;
            System.setProperty("catalina.base", base); //$NON-NLS-1$

            // Set up realm if one found
            if (TomcatPlugin.getDefault().getPluginPreferences().getInt(
                    TomcatPlugin.PREF_SSL_PORT) >= 0) {
                Realm realm = getRealm();
                embedded.setRealm(realm);
            }

            // start now, and then add all the contexts..
            embedded.start();

            // Create a very basic container hierarchy
            engine = embedded.createEngine();

            host = embedded.createHost("localhost", home + "/webapps"); //$NON-NLS-1$ //$NON-NLS-2$

            // all request go to our only host
            engine.setDefaultHost(host.getName());

            if (host instanceof StandardHost) {
                ((StandardHost) host)
                        .setErrorReportValveClass("org.eclipse.tomcat.internal.EclipseErrorReportValve"); //$NON-NLS-1$
            }
            engine.addChild(host);

            // Install the assembled container hierarchy
            PrintStream sysOut = System.out;
            // reassign standard output to prevent Tomcat from writing
            // its version message there.
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            try {
                embedded.addEngine(engine);
            } finally {
                System.setOut(sysOut);
            }

            // Root context
            Context root = embedded.createContext("", home + "/webapps/ROOT"); //$NON-NLS-1$ //$NON-NLS-2$
            // this line should be replaced once tomcat provides support
            // for setting the working directory
            if (root instanceof StandardContext) {
                ((StandardContext) root)
                        .setWorkDir(getWorkingDirectory("ROOT")); //$NON-NLS-1$
            }
            root.setLoader(embedded.createLoader(this.getClass()
                    .getClassLoader()));
            contexts.add(root);
            host.addChild(root);

            InetAddress iAddress = null;
            if (this.hostAddress != null) {
                try {
                    iAddress = InetAddress.getByName(this.hostAddress);
                } catch (UnknownHostException uhe) {
                    // will default to all interfaces
                }
            }
            updateSslPort(iAddress);
            if (this.port == 0) {
                this.port = SocketUtil.findUnusedLocalPort(iAddress); 
                if (this.port == -1) {
                    throw new CoreException(
                            new Status(
                                    IStatus.ERROR,
                                    TomcatPlugin.PLUGIN_ID,
                                    IStatus.OK,
                                    TomcatResources.TomcatAppServer_start_CannotObtainPort, 
                                    null));
                }
            }

            // Create Connector
            Connector connector = embedded.createConnector(null, this.port,
                    false);
            // Override defaults on CoyoteConnector
            if (connector instanceof CoyoteConnector) {
                CoyoteConnector connectorImpl = (CoyoteConnector) connector;
                if (iAddress != null) {
                    // bug in Embedded that incorrectly sets host on connector.
                    // pass null when creating connector, and set host here if
                    // it is specified
                    connectorImpl.setAddress(iAddress.getHostAddress());
                }
                Preferences pref = TomcatPlugin.getDefault()
                        .getPluginPreferences();
                int acceptCount = pref.getInt(TomcatPlugin.PREF_ACCEPT_COUNT);
                if (acceptCount > 0) {
                    connectorImpl.setAcceptCount(acceptCount);
                }
                int maxProcessors = pref
                        .getInt(TomcatPlugin.PREF_MAX_PROCESSORS);
                if (maxProcessors > 0) {
                    connectorImpl.setMaxProcessors(maxProcessors);
                }
                int minProcessors = pref
                        .getInt(TomcatPlugin.PREF_MIN_PROCESSORS);
                if (minProcessors > 0) {
                    connectorImpl.setMinProcessors(minProcessors);
                }
                if (this.sslPort > 0) {
                    connectorImpl.setRedirectPort(this.sslPort);
                    connectorImpl.setEnableLookups(true);
                    connectorImpl.setConnectionTimeout(20000);
                    connectorImpl.setUseURIValidationHack(false);
                    connectorImpl.setDisableUploadTimeout(true);
                }
                // connectorImpl.setDebug(0);
                // If there is problem in embedded.addConnector()
                // there is no exception, so add a listener
                connectorImpl.addLifecycleListener(new LifecycleListener() {
                    public void lifecycleEvent(LifecycleEvent event) {
                        if ("start".equals(event.getType())) //$NON-NLS-1$
                            running = true;
                    }
                });
            }

            // add Connector to Tomcat
            PrintStream sysErr = System.err;
            // reassign standard error to prevent Coyote from writing
            // its version message there.
            System.setErr(new PrintStream(new ByteArrayOutputStream()));
            try {
                embedded.addConnector(connector);
                httpConnector = connector;
            } finally {
                System.setErr(sysErr);
            }

            if (this.sslPort > 0) {
                createSSLConnector(iAddress, this.sslPort);
            }

            // if null passed for hostAddress, use local host
            if (this.hostAddress == null) {
                this.hostAddress = "127.0.0.1"; //$NON-NLS-1$
            }

            // running = true;
            TomcatPlugin.getDefault().setAppserver(this);

        } catch (Exception exc) {
            TomcatPlugin
                    .logError(
                            "Exception occurred starting the embedded application server.", //$NON-NLS-1$
                            exc);
            if (exc instanceof CoreException) {
                throw (CoreException) exc;
            }
            throw new CoreException(new Status(IStatus.ERROR,
            		TomcatPlugin.PLUGIN_ID, IStatus.OK,
            		TomcatResources.TomcatAppServer_start, 
            		exc));
        }
        if (!running) {
            throw new CoreException(new Status(IStatus.ERROR,
                    TomcatPlugin.PLUGIN_ID, IStatus.OK,
                    TomcatResources.TomcatAppServer_start, null)); 
        }
    }

    /**
     * Update {@link sslPort} to refer an appropriate port. If the user set
     * sslPort to 0, an arbitrary free port will be used.
     * 
     * @param iAddress
     *            {@link InetAddress} object representing the machine hosting
     *            the help system
     * @return port number of use for the SSL connection
     * @throws CoreException
     */
    private int updateSslPort(InetAddress iAddress) throws CoreException {
        this.sslPort = TomcatPlugin.getDefault().getPluginPreferences().getInt(
                TomcatPlugin.PREF_SSL_PORT);
        if (this.sslPort == 0) {
            this.sslPort = SocketUtil.findUnusedLocalPort(iAddress); 
            if (this.sslPort == -1) {
                throw new CoreException(new Status(IStatus.ERROR,
                        TomcatPlugin.PLUGIN_ID, IStatus.OK,
                        TomcatResources.TomcatAppServer_start_CannotObtainPort, 
                        null));
            }
        }
        return this.sslPort;
    }

    /**
     * @param iAddress
     *            InetAddress representing the machine hosting the help system.
     * @param sslport
     *            port # to use for the SSL connection
     * @throws CoreException
     */
    private void createSSLConnector(InetAddress iAddress, int sslport)
            throws CoreException {
        // Create Connector
        this.sslConnector = embedded.createConnector(null, sslport, false);
        // Override defaults on CoyoteConnector
        if (this.sslConnector instanceof CoyoteConnector) {
            CoyoteConnector connectorImpl = (CoyoteConnector) this.sslConnector;
            if (iAddress != null) {
                // bug in Embedded that incorrectly sets host on connector.
                // pass null when creating connector, and set host here if
                // it is specified
                connectorImpl.setAddress(iAddress.getHostAddress());
            }
            Preferences pref = TomcatPlugin.getDefault().getPluginPreferences();
            int acceptCount = pref.getInt(TomcatPlugin.PREF_ACCEPT_COUNT);
            if (acceptCount > 0) {
                connectorImpl.setAcceptCount(acceptCount);
            }
            int maxProcessors = pref.getInt(TomcatPlugin.PREF_MAX_PROCESSORS);
            if (maxProcessors > 0) {
                connectorImpl.setMaxProcessors(maxProcessors);
            }
            int minProcessors = pref.getInt(TomcatPlugin.PREF_MIN_PROCESSORS);
            if (minProcessors > 0) {
                connectorImpl.setMinProcessors(minProcessors);
            }
            connectorImpl.setUseURIValidationHack(false);
            connectorImpl.setDisableUploadTimeout(true);
            connectorImpl.setSecure(true);
            String scheme = pref.getString(TomcatPlugin.PREF_SSL_SCHEME);
            if ((scheme != null) && (!("".equals(scheme.trim())))) { //$NON-NLS-1$
                connectorImpl.setScheme(scheme);
            }
            connectorImpl.setEnableLookups(true);
            CoyoteServerSocketFactory factory = new CoyoteServerSocketFactory();
            factory.setClientAuth(false);
            String protocol = pref.getString(TomcatPlugin.PREF_SSL_PROTOCOL);
            if ((protocol != null) && (!("".equals(protocol.trim())))) { //$NON-NLS-1$
                factory.setProtocol(protocol);
            }
            String algorithm = pref.getString(TomcatPlugin.PREF_SSL_ALGORITHM);
            if ((algorithm != null) && (!("".equals(algorithm.trim())))) { //$NON-NLS-1$
                factory.setAlgorithm(algorithm);
            }
            String keyStoreFile = pref
                    .getString(TomcatPlugin.PREF_KEY_STORE_FILE);
            if ((keyStoreFile != null) && (!("".equals(keyStoreFile.trim())))) { //$NON-NLS-1$
                factory.setKeystoreFile(keyStoreFile);
            }
            String keyStorePassword = pref
                    .getString(TomcatPlugin.PREF_KEY_STORE_PASSWORD);
            if ((keyStorePassword != null)
                    && (!("".equals(keyStorePassword.trim())))) { //$NON-NLS-1$
                factory.setKeystorePass(keyStorePassword);
            }
            connectorImpl.setFactory(factory);
            connectorImpl.setDebug(0);
            // If there is problem in embedded.addConnector()
            // there is no exception, so add a listener
            connectorImpl.addLifecycleListener(new LifecycleListener() {
                public void lifecycleEvent(LifecycleEvent event) {
                    if ("start".equals(event.getType())) //$NON-NLS-1$
                        running = true;
                }
            });
        }

        // add Connector to Tomcat
        PrintStream sysErr = System.err;
        // reassign standard error to prevent Coyote from writing
        // its version message there.
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
        try {
            embedded.addConnector(this.sslConnector);
        } finally {
            System.setErr(sysErr);
        }
    }

    /**
     * Creates a {@link Realm}object using the information contained in
     * extensions of the type org.eclipse.tomcat.realmfactory in the plugin
     * registry.
     * 
     * @return the {@link Realm}object created
     */
    private Realm getRealm() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = registry.getExtensionPoint(
                TomcatPlugin.PLUGIN_ID, "realmfactory"); //$NON-NLS-1$
        Realm realm = null;
        if (extensionPoint != null) {
            IExtension[] extensions = extensionPoint.getExtensions();
            if ((extensions != null) && (extensions.length == 1)) {
                IConfigurationElement[] factoryElements = extensions[0]
                        .getConfigurationElements();
                if ((factoryElements != null) && (factoryElements.length == 1)) {
                    try {
                        IRealmFactory realmFactory = (IRealmFactory) factoryElements[0]
                                .createExecutableExtension("class"); //$NON-NLS-1$
                        realm = realmFactory.createRealm();
                    } catch (CoreException e) {
                        logError(
                                TomcatResources.TomcatAppServer_getRealmFactoryFailed,
                                e);
                    }
                } else {
                    if ((factoryElements == null)
                            || (factoryElements.length == 0)) {
                        logError(TomcatResources.TomcatAppServer_missingFactoryElement);
                    } else {
                        logError(TomcatResources.TomcatAppServer_multipleFactoryElements);
                    }
                }
            } else {
                if ((extensions == null) || (extensions.length == 0)) {
                    logError(TomcatResources.TomcatAppServer_missingRealmExtension);
                } else {
                    logError(TomcatResources.TomcatAppServer_multipleRealmExtensions);
                }
            }
        } else {
            logError(TomcatResources.TomcatAppServer_missingRealmExtensionPoint);
        }
        return realm;
    }

    /**
     * Create an error entry in the log
     * 
     * @param msg
     *            error message
     */
    private void logError(String msg) {
        logError(msg, null);
    }

    /**
     * Create an error entry in the log
     * 
     * @param msg
     *            error message
     * @param cause
     *            {@link Throwable} associated with this error message
     */
    private void logError(String msg, Throwable cause) {
        TomcatPlugin.logError(msg, cause);
    }

    /**
     * @see org.eclipse.help.internal.appserver.IWebappServer#start(java.lang.String,
     *      org.eclipse.core.runtime.IPath, java.lang.ClassLoader)
     */
    public synchronized void start(String webappName, IPath path, ClassLoader customLoader)
            throws CoreException {

        if (!isStarted) {
            start(port, hostAddress);
        }
        if (!running) {
            throw new CoreException(new Status(IStatus.ERROR,
                    TomcatPlugin.PLUGIN_ID, IStatus.OK, NLS.bind(
                            TomcatResources.TomcatAppServer_addingWebapp,
                            webappName, path.toOSString()), null));
        }

        String contextPath = webappName;
        if (!contextPath.startsWith("/")) { //$NON-NLS-1$
            contextPath = "/" + contextPath; //$NON-NLS-1$
        }
        try {
            Context context = embedded.createContext(contextPath, path
                    .toOSString());
            if (context instanceof StandardContext) {
                ((StandardContext) context)
                        .setWorkDir(getWorkingDirectory(webappName));
            }

            WebAppClassLoader webappLoader = new WebAppClassLoader(customLoader);
            context.setLoader(embedded.createLoader(webappLoader));

            host.addChild(context);
            contexts.add(context);
        } catch (Exception exc) {
            throw new CoreException(new Status(IStatus.ERROR,
                    TomcatPlugin.PLUGIN_ID, IStatus.OK, NLS.bind(
                            "TomcatAppServer.addingWebapp", webappName, path //$NON-NLS-1$
                                    .toOSString()), exc));
        }
    }

    /**
     * @see org.eclipse.help.internal.appserver.IWebappServer#stop(java.lang.String)
     */
    public synchronized void stop(String webappName) throws CoreException {
        if (!running) {
            return;
        }
        Context context = (Context) host.findChild("/" + webappName); //$NON-NLS-1$
        if (context != null) {
        	contexts.remove(context);
            embedded.removeContext(context);
        }
    }

    /**
     * @see org.eclipse.help.internal.appserver.IWebappServer#getHost()
     */
    public String getHost() {
        if (!running) {
            return null;
        }
        return hostAddress;
    }

    /**
     * @see org.eclipse.help.internal.appserver.IWebappServer#getPort()
     */
    public int getPort() {
        if (!running) {
            return 0;
        }
        return port;
    }

    /**
     * @see org.eclipse.help.internal.appserver.IWebappServer#isRunning()
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @see org.eclipse.help.internal.appserver.IWebappServer#stop()
     */
    public synchronized void stop() throws CoreException {
        if (!running) {
            return;
        }
        running = false;
        // Remove all contexts
        for(int i = 0; i< contexts.size(); i++){
            embedded.removeContext((Context)contexts.get(i));
           	contexts.remove(contexts.get(i));
        }

        // Remove the sslConnector, if present.
        try {
            if (sslConnector != null) {
                embedded.removeConnector(this.sslConnector);
            }
        } catch (Exception exc) {
            throw new CoreException(new Status(IStatus.ERROR,
                    TomcatPlugin.PLUGIN_ID, IStatus.OK,
                    TomcatResources.TomcatAppServer_sslConnectorRemove, 
                    exc));
        }

        // Remove the HTTP Connector, if present.
        try {
            if (httpConnector != null) {
                embedded.removeConnector(this.httpConnector);
            }
        } catch (Exception exc) {
            throw new CoreException(new Status(IStatus.ERROR,
                    TomcatPlugin.PLUGIN_ID, IStatus.OK,
                    TomcatResources.TomcatAppServer_httpConnectorRemove, 
                    exc));
        }

        // Remove the engine (which should trigger removing the connector)
        try {
            embedded.removeEngine(engine);
        } catch (Exception exc) {
            throw new CoreException(new Status(IStatus.ERROR,
                    TomcatPlugin.PLUGIN_ID, IStatus.OK,
                    TomcatResources.TomcatAppServer_engineRemove, 
                    exc));
        }
        // Shut down this tomcat server (should have nothing left to do)
        try {
            embedded.stop();
        } catch (LifecycleException e) {
            throw new CoreException(new Status(IStatus.ERROR,
                    TomcatPlugin.PLUGIN_ID, IStatus.OK,
                    TomcatResources.TomcatAppServer_embeddedStop, 
                    e));
        }
        isStarted = false;
    }

    private String getWorkingDirectory(String webApp) {
        return TomcatPlugin.getDefault().getStateLocation().append(webApp)
                .toOSString();
    }
}
