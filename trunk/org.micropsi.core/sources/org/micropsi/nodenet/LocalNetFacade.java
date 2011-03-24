/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/LocalNetFacade.java,v 1.9 2006/06/27 19:37:02 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.io.OutputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.common.utils.MultiPassInputStream;
import org.micropsi.nodenet.ext.StandardDecays;

/**
 * The implementation of the NetFacadeIF for local nets. Intended for use
 * within agent components.
 */
public class LocalNetFacade implements NetFacadeIF {

	private static ClassLoader initialClassLoader = ClassLoader.getSystemClassLoader();
	
	public static void setInitialClassLoader(ClassLoader loader) {
		initialClassLoader = loader;
	}
	
	private NetPropertiesIF netProperties;
	private NetEntityManager entityManager;
	private ModuleManager moduleManager;
	private SensActRegistry sensorReg;
	private UserInteractionManager userInteractionManager;
	private NetCycle cycle;
	private NetPersistencyManager persistencyManager;
	private Logger logger;
	private String netKey;

	/**
	 * Constructor, sets the logger.
	 * @param logger The logger to be used
	 * @param netProperties agent implementation dependent net properties
	 */
	public LocalNetFacade(Logger logger, NetPropertiesIF netProperties) {
		this.logger = logger;
		this.netProperties = netProperties;
		entityManager = new NetEntityManager(logger,this);
		moduleManager = new ModuleManager(entityManager);
		userInteractionManager = new UserInteractionManager(logger);
		sensorReg = new SensActRegistry();
		cycle = new NetCycle(entityManager,moduleManager,logger);
		persistencyManager = new NetPersistencyManager(entityManager,moduleManager,userInteractionManager,netProperties,sensorReg);
		
		DecayCalculator.setDecay(new StandardDecays());
		
		netKey = LocalNetRegistry.getInstance().registerNewNet(this);	
	}
	
	// basic operations
	// -------------------------------------
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getNetstep()
	 */
	public long getNetstep() {
		return entityManager.getNetstep();
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getCycle()
	 */
	public NetCycleIF getCycle() {
		return cycle;
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#registerNetObserver(NetObserverIF)
	 */
	public void registerNetObserver(NetObserverIF newObserver) {
		entityManager.registerObserver(newObserver);
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#unregisterNetObserver(NetObserverIF)
	 */
	public void unregisterNetObserver(NetObserverIF observer) {
		entityManager.unregisterObserver(observer);
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#registerSpaceObserver(String, NodeSpaceObserverIF)
	 */
	public void registerSpaceObserver(String space, NodeSpaceObserverIF newObserver) throws MicropsiException{
		try {
			((NodeSpaceModule)moduleManager.getModule(space)).registerObserver(newObserver);
		} catch (NetIntegrityException e) {
			logger.error(e.getIntegrityReport());			
			throw new MicropsiException(2500,e.getMessage(),e);
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#unregisterSpaceObserver(String, NodeSpaceObserverIF)
	 */
	public void unregisterSpaceObserver(String space, NodeSpaceObserverIF observer) throws MicropsiException{
		try {
			((NodeSpaceModule)moduleManager.getModule(space)).unregisterObserver(observer);
		} catch (NetIntegrityException e) {
			logger.error(e.getIntegrityReport());			
			throw new MicropsiException(2500,e.getMessage(),e);
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#entityExists(String)
	 */
	public boolean entityExists(String id) {
		return entityManager.entityExists(id);	
	}

	// integrity operations
	// -------------------------------------
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#checkIntegrity() 
	 */
	public void checkIntegrity() throws NetIntegrityException {
		entityManager.checkIntegrity();
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getIntegrityStatus()
	 */
	public String getIntegrityStatus() {
		return entityManager.reportIntegrityStatus();
	}
	
	// reetreival operations
	// -------------------------------------
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getAllEntities()
	 */
	public Iterator<NetEntity> getAllEntities() {
		return entityManager.getAllEntities();
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getAllModules()
	 */
	public Iterator<Module> getAllModules() {
		return moduleManager.getModules();
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getEntity(String)
	 */
	public NetEntity getEntity(String id) throws MicropsiException {
		try {
			NetEntity entity = entityManager.getEntity(id);
			return entity;
		} catch (NetIntegrityException e) {
			logger.error(e.getIntegrityReport());			
			throw new MicropsiException(2500,e.getMessage(),e);
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getNodeSpaceModule(String)
	 */
	public NodeSpaceModule getNodeSpaceModule(String id) throws MicropsiException {
		try {
			return (NodeSpaceModule)moduleManager.getModule(id);
		} catch (ClassCastException e) {
			throw new MicropsiException(2503,e.getMessage(),e);
		}
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getRootNodeSpaceModule()
	 */
	public NodeSpaceModule getRootNodeSpaceModule() throws MicropsiException {
		try {
			return moduleManager.getRootModule();
		} catch (ClassCastException e) {
			throw new MicropsiException(2503,e.getMessage(),e);
		}			
	}

	// structure modification operations
	// -------------------------------------

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#createNode(int, String)
	 */		
	public Node createNode(int type, String space) throws MicropsiException {
		
		synchronized(cycle) {
			NodeSpaceModule nspace = getNodeSpaceModule(space);
			try {
				Node newNode = NetEntityFactory.getInstance().createNode(type, nspace, entityManager, sensorReg);
				entityManager.notifyObservers();
				moduleManager.notifyObservers();
				return newNode;
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,"could not create node",e);
			}
		}
		
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#createNodeSpace(String)
	 */
	public NodeSpaceModule createNodeSpace(String parent) throws MicropsiException {
		
		synchronized(cycle) {
			try {
				NodeSpaceModule space = NetEntityFactory.getInstance().createNodeSpace(
					parent, 
					moduleManager, 
					entityManager,
					sensorReg);
				entityManager.notifyObservers();
				moduleManager.notifyObservers();
				return space;		
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,"could not create nodespace",e);
			}
		}
		
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#createNodeSpaceSlot(String, int)
	 */
	public Slot createNodeSpaceSlot(String spaceID, int type) throws MicropsiException {
		
		synchronized(cycle) {
			NodeSpaceModule space = (NodeSpaceModule)moduleManager.getModule(spaceID);
			try {
				Slot s = space.createSlot(type); 
				entityManager.notifyObservers();
				moduleManager.notifyObservers();
				return s;
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,"could not create slot "+type+" at "+spaceID,e);			
			}
		}
		
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#createNodeSpaceGate(String, int)
	 */
	public Gate createNodeSpaceGate(String spaceID, int type) throws MicropsiException {
		
		synchronized(cycle) {
			NodeSpaceModule space = (NodeSpaceModule)moduleManager.getModule(spaceID);
			try {
				Gate g = space.createGate(type); 
				entityManager.notifyObservers();
				moduleManager.notifyObservers();
				return g;
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,"could not create gate "+type+" at "+spaceID,e);			
			}
		}
		
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#deleteNodeSpaceSlot(java.lang.String, int)
	 */
	public void deleteNodeSpaceSlot(String spaceID, int type) throws MicropsiException {
		
		synchronized(cycle) {
			NodeSpaceModule space = (NodeSpaceModule)moduleManager.getModule(spaceID);
			try {
				space.deleteSlot(type);
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,"could not delete slot "+type+" at "+spaceID,e);			
			}
		}
		
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#deleteNodeSpaceGate(java.lang.String, int)
	 */
	public void deleteNodeSpaceGate(String spaceID, int type) throws MicropsiException {
		
		synchronized(cycle) {
			NodeSpaceModule space = (NodeSpaceModule)moduleManager.getModule(spaceID);
			try {
				space.deleteGate(type);
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,"could not delete gate "+type+" at "+spaceID,e);			
			}
		}
	}

	/**
	 * Note: As this method is defined by NetFacadeIF and hence is not able to
	 * use a custom classloader, the used classloader will be the
	 * initialClassLoader of the LocalNetFacade
	 * @see org.micropsi.nodenet.NetFacadeIF#createNativeModule(String, String, boolean)
	 */
	public NativeModule createNativeModule(String classname, String parent, boolean defiant) throws MicropsiException {
		
		synchronized(cycle) {
			try {
				NativeModule mod = NetEntityFactory.getInstance().createNativeModuleAndInstance(classname, initialClassLoader, parent, defiant, moduleManager, entityManager, userInteractionManager, netProperties);
				mod.initialize();
				entityManager.notifyObservers();
				moduleManager.notifyObservers();			
				return mod;
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,"could not create nativemodule",e);						
			}
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#createLink(String, int, String, int, double, double, boolean)
	 */
	public Link createLink(String from,int gate,String to,int slot,double weight,double confidence, boolean st) throws MicropsiException {
		
		synchronized(cycle) {
			if(st) {
				return createSTLink(from, gate, to, slot, weight, confidence, 0, 0, 0, 0);
			}
			
			NetEntity frome;
			try {
				frome = entityManager.getEntity(from);
				return frome.createLinkTo(to, slot, frome.getGate(gate), weight, confidence);
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Creates a new ST-Link.
	 * 
	 */
	public Link createSTLink(String from,int gate,String to,int slot,double weight,double confidence, int t, double x, double y, double z) throws MicropsiException {
		
		synchronized(cycle) {
			NetEntity frome;
			try {
				frome = entityManager.getEntity(from);
				Slot s = entityManager.getEntity(to).getSlot(slot);
				if(s == null) throw new NetIntegrityException(NetIntegrityIF.BAD_SLOT,frome.getID(),null);
			
				LinkST l = (LinkST)LinkFactory.getInstance().createLink(
						LinkTypesIF.LINKTYPE_SPACIOTEMPORAL,
						entityManager,
						frome.getGate(gate),
						to,
						slot,
						weight,
						confidence);
						
				l.setT(t);
				l.setX(x);
				l.setY(y);
				l.setZ(z);
							
				frome.getGate(gate).addLink(l);
				s.attachIncomingLink(l);
				entityManager.reportChangedEntity(frome);
				return l;
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());
				throw new MicropsiException(2500,e.getMessage(),e);
			}
		} 
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#deleteLink(String, int, String, int)
	 */
	public void deleteLink(String from, int gate, String to, int slot) throws MicropsiException {
		
		synchronized(cycle) {
			NetEntity frome;
			try {
				frome = entityManager.getEntity(from);
				frome.deleteLink(gate, to, slot);
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());			
				throw new MicropsiException(2500,e.getMessage(),e);
			}
		}
	}
		
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#unlinkEntity(String)
	 */
	public void unlinkEntity(String id) throws MicropsiException {
		
		synchronized(cycle) {
			try {
				NetEntity entity = entityManager.getEntity(id);
				entity.unlinkCompletely();
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());			
				throw new MicropsiException(2500,e.getMessage(),e);
			}
		}
		
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#deleteEntity(String)
	 */
	public void deleteEntity(String id) throws MicropsiException {
		
		synchronized(cycle) {
			try {
				entityManager.deleteEntity(id);
				moduleManager.reportNodeDeletion(id);			
				entityManager.notifyObservers();
				moduleManager.notifyObservers();						
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());			
				throw new MicropsiException(2500,e.getMessage(),e);
			}
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#changeParameter(int, java.lang.String, int, java.lang.String)
	 */
	public void changeParameter(int parameterID, String entityID, int subID, String newValue) throws MicropsiException {
		
		synchronized(cycle) {
			try {
				entityManager.changeParameter(entityID, parameterID, subID, newValue);
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());			
				throw new MicropsiException(2500,e.getMessage(),e);
			}
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#changeLinkParameter(int, String, int, String, int, double)
	 */
	public void changeLinkParameter(int parameterID, String fromID, int gate, String toID, int slot, double newValue) throws MicropsiException {
		
		synchronized(cycle) {
		
			NetEntity origin = entityManager.getEntity(fromID); 
			Gate g = origin.getGate(gate);
			Link l = g.getLinkTo(toID, slot);
			
			switch(parameterID) {
				case Link.LINKPARAM_WEIGHT:
					l.setWeight(newValue);
					break;
				case Link.LINKPARAM_CONFIDENCE:
					l.setConfidence(newValue);
					break;	
			}
			
			if(l.getType() == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL) {
				
				LinkST stl = (LinkST)l;
				
				switch(parameterID) {
					case LinkST.LINKPARAM_T:
						stl.setT((int)Math.round(newValue));
						break;
					case LinkST.LINKPARAM_X:
						stl.setX(newValue);
						break;
					case LinkST.LINKPARAM_Y:
						stl.setY(newValue);
						break;
					case LinkST.LINKPARAM_Z:
						stl.setZ(newValue);
						break;
				}
			}
			
			entityManager.reportChangedEntity(origin);
		}
	}
	

	// sensor/actor operations
	// -------------------------------------

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getAvailableDataSources()
	 */
	public Iterator<String> getAvailableDataSources() {
		return sensorReg.getSensorDataProviderIDs();
	}
	
	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#getAvailableDataTargets()
	 */
	public Iterator<String> getAvailableDataTargets() {
		return sensorReg.getActuatorDataTargetIDs();
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#connectSensor(String, String)
	 */
	public void connectSensor(String sensorNodeID, String dataType) throws MicropsiException {
		
		synchronized(cycle) {
			if(!sensorReg.knowsSensorDataProvider(dataType)) throw new MicropsiException(2501,dataType);
			try {
				SensorNode snode = (SensorNode)entityManager.getEntity(sensorNodeID);
				snode.connectSensor(dataType);
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());			
				throw new MicropsiException(2500,e.getMessage(),e);
			} catch (ClassCastException e) {
				throw new MicropsiException(2502,sensorNodeID,e);
			}
		}
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#disconnectSensor(String)
	 */
	public void disconnectSensor(String sensorNodeID) throws MicropsiException {
		synchronized(cycle) {
			try {
				SensorNode snode = (SensorNode)entityManager.getEntity(sensorNodeID);
				snode.disconnectSensor();
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());			
				throw new MicropsiException(2500,e.getMessage(),e);
			} catch (ClassCastException e) {
				throw new MicropsiException(2502,sensorNodeID,e);
			}
		}
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#connectActor(java.lang.String, java.lang.String)
	 */
	public void connectActor(String actorNodeID, String dataType) throws MicropsiException {
		synchronized(cycle) {
			if(!sensorReg.knowsActuatorDataTarget(dataType)) throw new MicropsiException(2504,dataType);
			try {
				ActorNode anode = (ActorNode)entityManager.getEntity(actorNodeID);
				anode.connectActor(dataType);
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());			
				throw new MicropsiException(2500,e.getMessage(),e);
			} catch (ClassCastException e) {
				throw new MicropsiException(2502,actorNodeID,e);
			}
		}
	}

	/**
	 * @see org.micropsi.nodenet.NetFacadeIF#disconnectActor(java.lang.String)
	 */
	public void disconnectActor(String actorNodeID) throws MicropsiException {
		synchronized(cycle) {
			try {
				ActorNode anode = (ActorNode)entityManager.getEntity(actorNodeID);
				anode.disconnectActor();
			} catch (NetIntegrityException e) {
				logger.error(e.getIntegrityReport());			
				throw new MicropsiException(2500,e.getMessage(),e);
			} catch (ClassCastException e) {
				throw new MicropsiException(2502,actorNodeID,e);
			}
		}	
	}

	// additional operations
	// -------------------------------------

	/**
	 * Returns the registry
	 * @return the registry
	 */
	public SensActRegistry getSensorRegistry() {
		return sensorReg;
	}

	/**
	 * Saves the net to an OutputStream
	 * @param outp The OutputStream to save the net to
	 * @throws MicropsiException if there was a problem saving the net
	 */
	public void saveNet(OutputStream outp) throws MicropsiException {
		cycle.block();
		
		synchronized(cycle) {
			persistencyManager.saveNet(outp);
		}
		
		cycle.unblock();
	}

	/**
	 * Loads the net from an InputStream
	 * @param inp The InputStream to load the net from
	 * @param resetSensActRegistry - set this flag if you want to reset
	 * the Sensor/Actor registry and remove all sources and targets
	 * @throws MicropsiException if there was a problem loading the net
	 */
	public void loadNet(MultiPassInputStream inp, boolean resetSensActRegistry) throws MicropsiException {
		cycle.block();

		synchronized(cycle) {
			
			if(moduleManager != null)
				moduleManager.reset();
		
			if(entityManager != null)
				entityManager.reset();			
		
			if(resetSensActRegistry)
				sensorReg.clear();
			
			persistencyManager.loadNet(inp, initialClassLoader);
		}
		
		cycle.unblock();
	}

	/**
	 * Replaces the implementation of a native module. The classloader
	 * "classloader" will be used to load the given class. You can pass null as
	 * classloader parameter to use the system classloader. You can also pass
	 * null as classname to "empty" the native module.
	 * @param nativeModuleID the ID of the native module that will get a new
	 * implementation
	 * @param classLoader the classloader to be used
	 * @param classname the classname to be used (the class must be a subclass
	 * of AbstractNativeModuleImpl
	 * @throws MicropsiException if the class could not be found or bad links were
	 * found during replacement
	 */
	public void replaceNativeModuleImplementation(String nativeModuleID, ClassLoader classLoader, String classname) throws MicropsiException {
		
		synchronized(cycle) {
			NativeModule module;
			
			try {
				module = (NativeModule)entityManager.getEntity(nativeModuleID);
			} catch (ClassCastException e) {
				throw new MicropsiException(2503,e.getMessage(),e);
			} 
					
			Class implClass;
			try {
				implClass = classLoader.loadClass(classname);
			} catch (ClassNotFoundException e) {
				throw new MicropsiException(10,classname,e);
			}
			
			AbstractNativeModuleImpl impl;
			try {
				impl = (AbstractNativeModuleImpl)implClass.newInstance();
			} catch (InstantiationException e) {
				throw new MicropsiException(10,e.getMessage(),e);
			} catch (IllegalAccessException e) {
				throw new MicropsiException(10,e.getMessage(),e);
			}
			
			module.replaceImplementation(impl);
		}
		
		entityManager.notifyObservers();
		moduleManager.notifyObservers();
		
	}

	/**
	 * Returns a module inspector for a given module
	 * @return the inspector
	 */
	public InnerStateInspectorIF getModuleInspector(String nativeModuleID) throws MicropsiException {

		NativeModule module;
		
		try {
			module = (NativeModule)entityManager.getEntity(nativeModuleID);
		} catch (ClassCastException e) {
			throw new MicropsiException(2503,e.getMessage(),e);
		} 
				
		return module.getInnerStates();
		
	}
		
	/**
	 * Register a progress monitor here if you want to be noticed about load and
	 * save operations on this net and their progress.
	 * @param monitor the progress monitor to be registered
	 */
	public void registerLocalNetLoadMonitor(ProgressMonitorIF monitor) {
		persistencyManager.registerLoadProgressListener(monitor);
	}

	/**
	 * Unregisters a monitor previously registered with the facade. It's safe to
	 * unregister monitors that aren't registered, in which case this method will
	 * do nothing.
	 * @param monitor the monitor instance to unregister
	 */
	public void unregisterLocalNetLoadMonitor(ProgressMonitorIF monitor) {
		persistencyManager.unregisterLoadProgressListener(monitor);
	}

	/**
	 * Register a progress monitor here if you want to be noticed about load and
	 * save operations on this net and their progress.
	 * @param monitor the progress monitor to be registered
	 */
	public void registerLocalNetSaveMonitor(ProgressMonitorIF monitor) {
		persistencyManager.registerSaveProgressListener(monitor);
	}

	/**
	 * Unregisters a monitor previously registered with the facade. It's safe to
	 * unregister monitors that aren't registered, in which case this method will
	 * do nothing.
	 * @param monitor the monitor instance to unregister
	 */
	public void unregisterLocalNetSaveMonitor(ProgressMonitorIF monitor) {
		persistencyManager.unregisterSaveProgressListener(monitor);
	}
	
	/**
	 * Sets the user interaction facility. By default, there is no user
	 * interaction facility.
	 * 
	 * @param interaction the user interaction implementation.
	 */
	public void setUserInteractionImplementation(UserInteractionIF interaction) {
		userInteractionManager.setUserInteractionImplementation(interaction);
	}
	
	/**
	 * Returns the current user interaction facility. This can be null as there
	 * doesn't need to be a user interaction implementation in a node net.
	 * 
	 * @return an implementation of UserInteractionIF or null
	 */
	public UserInteractionIF getUserInteractionImplementation() {
		return userInteractionManager.getUserInteractionImplementation();
	}

	/**
	 * Returns the unique key of this net within its classloader realm.
	 * 
	 * @return a string net key. 
	 */
	public String getNetKey() {
		return netKey;
	}

	/**
	 * Destroy the net facade completely
	 */
	public void destroy() {
		entityManager.destroy();
		entityManager = null;
	}

}
