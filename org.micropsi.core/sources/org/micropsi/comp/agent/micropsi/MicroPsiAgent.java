/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/MicroPsiAgent.java,v 1.24 2005/11/21 20:12:22 vuine Exp $
 */
package org.micropsi.comp.agent.micropsi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.FileSystemClassLoader;
import org.micropsi.common.utils.InteractiveClassLoaderIF;
import org.micropsi.common.utils.MultiPassInputStream;
import org.micropsi.comp.agent.AgentFrameworkComponent;
import org.micropsi.comp.agent.AgentIF;
import org.micropsi.comp.agent.AgentTypesIF;
import org.micropsi.comp.agent.micropsi.conserv.QTypeChangeDebugSource;
import org.micropsi.comp.agent.micropsi.conserv.QTypeChangeInnerState;
import org.micropsi.comp.agent.micropsi.conserv.QTypeCycleSuspendedNet;
import org.micropsi.comp.agent.micropsi.conserv.QTypeDeleteAgentState;
import org.micropsi.comp.agent.micropsi.conserv.QTypeDie;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetActorValues;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetAgentStateMetadataPath;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetAgentStates;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetCurrentAgentState;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetCycleLength;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetInnerStates;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetLocalNet;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetScriptingManager;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetSituation;
import org.micropsi.comp.agent.micropsi.conserv.QTypeLoadAgentState;
import org.micropsi.comp.agent.micropsi.conserv.QTypeRenameAgentState;
import org.micropsi.comp.agent.micropsi.conserv.QTypeResumeNet;
import org.micropsi.comp.agent.micropsi.conserv.QTypeSaveAgentState;
import org.micropsi.comp.agent.micropsi.conserv.QTypeSetCycleLength;
import org.micropsi.comp.agent.micropsi.conserv.QTypeSuspendNet;
import org.micropsi.comp.agent.micropsi.scripting.AgentScriptingManager;
import org.micropsi.comp.agent.micropsi.urges.PhysicalStateListenerIF;
import org.micropsi.nodenet.ActorDataTargetIF;
import org.micropsi.nodenet.CycleObserverIF;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.SensorDataSourceIF;
import org.micropsi.nodenet.agent.AgentInformationProviderIF;
import org.micropsi.nodenet.agent.MicroPSICore;
import org.micropsi.nodenet.agent.MicroPSIFactory;
import org.micropsi.nodenet.agent.MicroPSINetProperties;
import org.micropsi.nodenet.agent.Situation;

public class MicroPsiAgent implements AgentIF, PhysicalStateListenerIF {

	private class ConstantDataSource implements SensorDataSourceIF {

		private final String type;
		private final double strength;

		public ConstantDataSource(double strength) {
			this.strength = strength;
			type = "constant_"+strength;
		}

		public String getDataType() {
			return type;
		}

		public double getSignalStrength() {
			return strength;
		}	
	}

	private class ZoomedDataSource implements SensorDataSourceIF {

		private final Situation situation;
		private final String type;

		public ZoomedDataSource(Situation situation) {
			this.situation = situation;
			type = "zoomed";
		}

		public String getDataType() {
			return type;
		}

		public double getSignalStrength() {
			return (situation.isZoomed() ? 1 : 0);
		}	
	}
	
	private class FoveaResetDataTarget implements ActorDataTargetIF,CycleObserverIF {

		private boolean wasSuccess = false;
		private Situation situation;
		private double val;
		private long successStep;
		
		public FoveaResetDataTarget(Situation situation) {
			this.situation = situation;
		}
		
		public String getDataType() {
			return "fovea_reset";
		}

		public void addSignalStrength(double value) {
			val += value;
		}

		public double getExecutionSuccess() {
			return wasSuccess ? 1 : -1;
		}

		public void startCycle(long netStep) {
			if(netStep > successStep) {
				wasSuccess = false;
			}
			if(val > 0) {
				situation.lookAtHomePosition();
				wasSuccess = true;
				successStep = netStep;
			}
			val = 0;
		}

		public void endCycle(long netStep) {
		}
		
	}
	
	private class FoveaDataTarget implements ActorDataTargetIF,CycleObserverIF {

		public static final int HORIZONTAL = 0;
		public static final int VERTICAL = 1;
		
		private double val = 0;
		private boolean wasSuccess = false;
		private int orientation;
		private Situation situation;
		private ActorValueCache avc;
		private String type;
		private long successStep;

		public FoveaDataTarget(int orientation, Situation situation, ActorValueCache avc) {
			this.orientation = orientation;
			this.situation = situation;
			this.avc = avc;
			switch(orientation) {
				case HORIZONTAL:
					type = "fovea_h";
					break;
				case VERTICAL:
					type = "fovea_v";
					break;
			}
		}

		public String getDataType() {
			return type;
		}

		public void addSignalStrength(double value) {
			val += value;
		}

		public double getExecutionSuccess() {
			return wasSuccess ? 1 : -1;
		}

		public void startCycle(long netStep) {
			if(netStep > successStep) {
				wasSuccess = false;
			}
			if(val != 0) {
				switch(orientation) {
					case HORIZONTAL:
						situation.foveaRight(val);
						break;
					case VERTICAL:
						situation.foveaDown(val);
						break;
				}
				wasSuccess = true;
				successStep = netStep;
				
				avc.reportValue(type, val);
			}
			val = 0;
		}

		public void endCycle(long netStep) {
		}
		
	}
	
	private class ZoomDataTarget implements ActorDataTargetIF,CycleObserverIF {
		
		private boolean wasSuccess = false;
		private Situation situation;
		private double val;
		
		public ZoomDataTarget(Situation situation) {
			this.situation = situation;
		}
		
		public String getDataType() {
			return "zoom";
		}

		public void addSignalStrength(double value) {
			val += value;
		}

		public double getExecutionSuccess() {
			return wasSuccess ? 1 : -1;
		}

		public void startCycle(long netStep) {
			if(val > 0) {
				situation.zoom();
				zoomedFlag = true;
				wasSuccess = true;
			}
			val = 0;
		}

		public void endCycle(long netStep) {
		}
		
	}

	private class UnzoomDataTarget implements ActorDataTargetIF,CycleObserverIF {
		
		private boolean wasSuccess = false;
		private Situation situation;
		private double val;
		
		public UnzoomDataTarget(Situation situation) {
			this.situation = situation;
		}
		
		public String getDataType() {
			return "unzoom";
		}

		public void addSignalStrength(double value) {
			val += value;
		}

		public double getExecutionSuccess() {
			return wasSuccess ? 1 : -1;
		}

		public void startCycle(long netStep) {
			if(val > 0) {
				situation.unzoom();
				unzoomedFlag = true;
				wasSuccess = true;
			}
			val = 0;
		}

		public void endCycle(long netStep) {
		}
		
	}


	protected AgentStateRepository agentStates;
	protected ActorValueCache actorValueCache;

	protected MicroPSICore micropsi;
	protected MicroPSINetProperties netProperties;
	protected boolean pause = true;
	protected String currentAgentStateID;
	protected AgentFrameworkComponent tecLayer;
	
	protected AgentScriptingManager scriptingManager;
	
	protected DebugDataSource debugSource;
	protected FoveaDataTarget hTarget; 
	protected FoveaDataTarget vTarget;
	protected FoveaResetDataTarget rTarget;
	protected ZoomDataTarget zoomTarget;
	protected UnzoomDataTarget unzoomTarget;

	protected int netDelayMs = 5;

	protected boolean operate = false;

	protected boolean zoomedFlag = false;
	protected boolean unzoomedFlag = false;
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.agent.AgentIF#initialize(org.micropsi.comp.agent.AgentFrameworkComponent, java.lang.String, org.micropsi.common.config.ConfigurationReaderIF)
	 */
	public ConsoleQuestionTypeIF[] initialize(AgentFrameworkComponent teclayer, String configroot, ConfigurationReaderIF reader) throws MicropsiException {
		
		this.tecLayer = teclayer;
		
		try {
			String additionalcp = reader.getConfigValue(configroot+".additionalcp");
			ClassLoader p = getClass().getClassLoader();
			if(p instanceof InteractiveClassLoaderIF) p = p.getParent(); 			
			FileSystemClassLoader fscl = new FileSystemClassLoader(additionalcp,p);
			LocalNetFacade.setInitialClassLoader(fscl);
		} catch (Exception e) {
			tecLayer.getLogger().warn("Unable to set additional classpath for micropsi agent",e);
		}
		
		agentStates = new AgentStateRepository(
			reader.getConfigValue(configroot+".agentstates"),
			reader.getConfigValue(configroot+".initialstate")
		);
		
		actorValueCache = new ActorValueCache();
			
		debugSource = new DebugDataSource(1.0);
		
		currentAgentStateID = agentStates.getDefaultAgentState();
				
		pause = true;
						
		File agentStateFile = null;
		if(currentAgentStateID != null)
			agentStateFile = new File(agentStates.getAgentStateDataPath(currentAgentStateID));
	
		netProperties = new MicroPSINetProperties(new AgentInformationProviderIF() {
			public String getAgentName() {
				return tecLayer.getComponentID();
			}
		});
		
		micropsi = MicroPSIFactory.getInstance().createMicroPSI(tecLayer.getLogger(),agentStateFile,netProperties);
		micropsi.block();

		LocalNetFacade net = micropsi.getNet();
		teclayer.getLogger().info("MicroPsi agent "+teclayer.getComponentID()+" initialized with net "+net.getNetKey());		
		
		scriptingManager = new AgentScriptingManager(net,this,tecLayer.getLogger());
		
		
		ConsoleQuestionTypeIF[] toReturn = new ConsoleQuestionTypeIF[20];
		
		toReturn[0] = new QTypeGetLocalNet(this);
		toReturn[1] = new QTypeSaveAgentState(this,tecLayer.getExproc());
		toReturn[2] = new QTypeSuspendNet(this);
		toReturn[3] = new QTypeResumeNet(this);
		toReturn[4] = new QTypeCycleSuspendedNet(this,tecLayer.getExproc());
		toReturn[5] = new QTypeLoadAgentState(this,tecLayer.getExproc());
		toReturn[6] = new QTypeGetAgentStates(agentStates);
		toReturn[7] = new QTypeGetAgentStateMetadataPath(agentStates);
		toReturn[8] = new QTypeGetCurrentAgentState(this);
		toReturn[9] = new QTypeDeleteAgentState(agentStates);
		toReturn[10] = new QTypeRenameAgentState(agentStates);
		toReturn[11] = new QTypeGetInnerStates(this,tecLayer.getExproc());
		toReturn[12] = new QTypeChangeInnerState(this,tecLayer.getExproc());
		toReturn[13] = new QTypeGetCycleLength(this);
		toReturn[14] = new QTypeSetCycleLength(this,tecLayer.getExproc());
		toReturn[15] = new QTypeGetSituation(this);
		toReturn[16] = new QTypeChangeDebugSource(debugSource);
		toReturn[17] = new QTypeDie(this);
		toReturn[18] = new QTypeGetScriptingManager(scriptingManager);
		toReturn[19] = new QTypeGetActorValues(this);
		
		// provide debug data sources
		net.getSensorRegistry().registerSensorDataProvider(
			debugSource
		);
		
		net.getSensorRegistry().registerSensorDataProvider(
			new ConstantDataSource(1.0)
		);
		
		net.getSensorRegistry().registerSensorDataProvider(
			new ZoomedDataSource(getSituation())
		);
	
		getSituation().lookAtHomePosition();
		
		// Provide actor data targets for fovea movement
		hTarget = new FoveaDataTarget(FoveaDataTarget.HORIZONTAL,getSituation(),actorValueCache); 
		vTarget = new FoveaDataTarget(FoveaDataTarget.VERTICAL,getSituation(),actorValueCache);
		rTarget = new FoveaResetDataTarget(getSituation());
		zoomTarget = new ZoomDataTarget(getSituation());
		unzoomTarget = new UnzoomDataTarget(getSituation());
		net.getSensorRegistry().registerActuatorDataTarget(hTarget);
		net.getSensorRegistry().registerActuatorDataTarget(vTarget);
		net.getSensorRegistry().registerActuatorDataTarget(rTarget);
		net.getSensorRegistry().registerActuatorDataTarget(zoomTarget);
		net.getSensorRegistry().registerActuatorDataTarget(unzoomTarget);
		
		net.getCycle().registerCycleObserver(hTarget);
		net.getCycle().registerCycleObserver(vTarget);
		net.getCycle().registerCycleObserver(rTarget);
		net.getCycle().registerCycleObserver(zoomTarget);
		net.getCycle().registerCycleObserver(unzoomTarget);
				
		operate = true;
		pause = false;
		
		micropsi.suspend();
		micropsi.unblock();
		
//		final Logger l = teclayer.getLogger();
		
//		try {
//			Thread.sleep(1000);
//		} catch (Exception e) {};
		
//		try {
//			final org.micropsi.media.VideoServer detector = org.micropsi.media.VideoServerRegistry.getInstance().getServer("detector");
//			if(detector != null) {
//				new Thread(new Runnable() {
//					public void run() {
//						try {
//							System.err.println("found detector");
//							MultiHypothesisCameraTester.doTheJob(l,detector);
//						} catch (Exception e) {
//							l.error("Detector failure",e);
//						}
//					}
//				}).start();
//			}
//		} catch (Throwable e) {			
//		}
		
		return toReturn;
	}

	public void registerAdditionalQuestionType(ConsoleQuestionTypeIF q) {
		tecLayer.registerAdditionalQuestionType(q);
	}
	
	/**
	 * Resets the agent. This means that:<br>
	 * - The world adapters will be reset
	 * - The initial net state will be loaded
	 * - Situation parameters will be reset
	 * @throws MicropsiException
	 */
	public void reset() throws MicropsiException {
		
		pause = true;
		
		//micropsi.suspend();
		micropsi.block();
		tecLayer.shutdownWorldAdapters();
		
		File agentStateFile = null;
		if(currentAgentStateID != null)
			agentStateFile = new File(agentStates.getAgentStateDataPath(currentAgentStateID));
		
		try {
			if(agentStateFile != null)
				micropsi.getNet().loadNet(new MultiPassInputStream(agentStateFile),true);
			else
				micropsi.getNet().loadNet(null,true);
		} catch (Exception e) {
			tecLayer.getExproc().handleException(e);
		}
			
		getSituation().lookAtHomePosition();		
		
		LocalNetFacade net = micropsi.getNet();
		
		// provide debug data sources
		net.getSensorRegistry().registerSensorDataProvider(
			debugSource
		);
		
		net.getSensorRegistry().registerSensorDataProvider(
			new ConstantDataSource(1.0)
		);
	
		net.getSensorRegistry().registerSensorDataProvider(
			new ZoomedDataSource(getSituation())
		);
		
		getSituation().lookAtHomePosition();
		
		// Provide actor data targets for fovea movement
		net.getSensorRegistry().registerActuatorDataTarget(hTarget);
		net.getSensorRegistry().registerActuatorDataTarget(vTarget);
		net.getSensorRegistry().registerActuatorDataTarget(rTarget);
		net.getSensorRegistry().registerActuatorDataTarget(zoomTarget);
		net.getSensorRegistry().registerActuatorDataTarget(unzoomTarget);
					
		tecLayer.initializeWorldAdapters();
		micropsi.unblock();			
		
		pause = false;
				
	}
	
	public NetFacadeIF getNet() {
		if(micropsi == null) return null;
		return micropsi.getNet();
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.agent.AgentIF#startCycle(long)
	 */
	public void startCycle(long cyclecounter) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.agent.AgentIF#receiveCycleSignal(long, int)
	 */
	public void receiveCycleSignal(long cyclecounter, int state) {
	}

	/*
	 *  (non-Javadoc)
	 * @see org.micropsi.comp.agent.AgentIF#endCycle(long)
	 */
	public void endCycle(long cyclecounter) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.agent.AgentIF#stopEverything()
	 */
	public void stopEverything() throws MicropsiException {
		operate = false;
		micropsi.block();
		//net.saveNet();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		tecLayer.getLogger().debug(micropsi.getNet().getCycle()+": starting net");
		while(operate) {
			try {
				if(!pause) micropsi.nextCycle();
				switch(netDelayMs) {
					case 0: Thread.yield(); break;
					default: Thread.sleep(netDelayMs); break;
				} 
			} catch (Exception e) {
				tecLayer.getExproc().handleException(e);	
			}
		}
		tecLayer.getLogger().debug(micropsi.getNet().getCycle()+": MicroPsi agent stopped: "+tecLayer.getComponentID());
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.agent.AgentIF#isAlive()
	 */
	public boolean isAlive() {
		return operate;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.agent.AgentIF#getAgentType()
	 */
	public int getAgentType() {
		return AgentTypesIF.AGENT_MICROPSI_STANDARD;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.agent.micropsi.urges.PhysicalStateListenerIF#die(java.lang.String)
	 */
	public void die(String reason) {
		try {
			stopEverything();
		} catch (Exception e) {
			tecLayer.getLogger().error("Agent tried to die and couldn't: "+e.getMessage(),e);
		}
		tecLayer.getLogger().info(micropsi.getNet().getCycle()+": Agent has died: "+reason);
	}
	
	/**
	 * Saves this agent as a new state with the given name
	 * @param newstate the state's name
	 * @throws MicropsiException
	 * @throws IOException
	 */
	public void saveState(String newstate) throws MicropsiException, IOException {
		agentStates.createAgentState(newstate);
	
		FileOutputStream fout = new FileOutputStream(agentStates.getAgentStateDataPath(newstate));
		//FileLock lock = fout.getChannel().lock();
		micropsi.getNet().saveNet(fout);
		fout.flush();
		fout.close();
		//lock.release();
		
		tecLayer.getLogger().debug(micropsi.getNet().getCycle()+": Agent state saved: "+newstate+" File: "+agentStates.getAgentStateDataPath(newstate));
	}
	
	/**
	 * Loads the agent's state
	 * @param newState
	 * @throws MicropsiException
	 */
	public void setCurrentAgentState(String newState) throws MicropsiException {
		currentAgentStateID = newState;
		reset();
	}

	/**
	 * Returns the current agent's satet
	 * @return
	 */
	public String getCurrentAgentState() {
		return currentAgentStateID;
	}
	
	/**
	 * Returns the current situation object. Do NEVER, EVER hold the instance
	 * returned by this method, or you'll be out of sync with the world as soon
	 * as the agent gets renamed. If you need situations somewhere, pass the
	 * agent object and call getSituation() each time you need the situation!
	 * @return the current situation object
	 */
	public Situation getSituation() {
		return Situation.getInstance(tecLayer.getComponentID());
	}

	/**
	 * Returns the agent controller thread's current delay.
	 * @return the current delay in ms
	 */
	public int getCycleDelay() {
		return netDelayMs;
	}
	
	/**
	 * Sets the controller thread's delay (in ms)
	 * @param newDelay the new delay between two cycles
	 */
	public void setCycleDelay(int newDelay) {
		if(newDelay >= 0)
			this.netDelayMs = newDelay;
		else
			newDelay = 0;
	}

	/**
	 * 
	 * 
	 */
	public void resetZoomFlags() {
		zoomedFlag = false;
		unzoomedFlag = false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAnyZoomFlagSet() {
		return zoomedFlag || unzoomedFlag;
	}

	/**
	 * Returns the ActorValueCache. Typically, QuestionTypeIF implementations will need this
	 * to ask for actor values.
	 * @return the actor value cache, never null.
	 */
	public ActorValueCache getActorValueCache() {
		return actorValueCache;
	}

}
