/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetPersistencyManager.java,v 1.18 2006/06/27 19:37:02 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.common.utils.MultiPassInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The persistency manager saves and loads nets to/from XML streams.<br/><br/>
 * The implementation is an ugly hell of nested loops. Do not care for how it
 * does what it does. Do not read the source. Do not ask questions.
 */
public class NetPersistencyManager {
		
	class PreparationHandler extends DefaultHandler {
		
		private int c = 0;
		
		public void startElement (String uri, String localName, String qName, Attributes attr) throws SAXException {
			if("Entity".equals(qName)) {
				c++;
			} else if("Entities".equals(qName)) {
				entityManager.netstep = Long.parseLong(attr.getValue("netstep"));	
			}
		}
		
		public int getNumberOfEntities() {
			return c;
		}
		
	}
	
	class EntityCreationHandler extends DefaultHandler {
		
		private NetEntity stackEntity = null;
		private Gate stackGate = null;
		private ClassLoader l;
		
		private int progCount = 0;
		private int progMax = 0;
		
		public EntityCreationHandler(ClassLoader l, int maxProgress) {
			this.l = l;
			this.progMax = maxProgress;
		}
		
		public void endElement(String uri, String localName, String qName) {
			if("Entity".equals(qName)) {
				stackEntity = null;
			} else if("Gate".equals(qName)) {
				stackGate = null;
			}
		}
		
		public void startElement (String uri, String localName, String qName, Attributes attr) throws SAXException {
			
			try {
			
			if("Entity".equals(qName)) {
				int entityType = Integer.parseInt(attr.getValue("type"));
				String entityID = attr.getValue("id"); 
				String entityName = attr.getValue("name");
				NetEntity entity = null;
				
				try {
				
					switch(entityType) {
						case NetEntityTypesIF.ET_NODE:
							int nodeType = Integer.parseInt(attr.getValue("nodeType"));
							Node n = NetEntityFactory.getInstance().createNode(
								nodeType, 
								entityID, 
								entityName.equals("null") ? null : entityName, 
								entityManager, 
								sensReg);
							switch(nodeType) {
								case NodeFunctionalTypesIF.NT_SENSOR:
									String dataType = attr.getValue("dataType");
									if(!dataType.equals("null"))	
										((SensorNode)n).connectSensor(dataType);
									break;
								case NodeFunctionalTypesIF.NT_ACTOR:
									dataType = attr.getValue("dataType");
									if(!dataType.equals("null"))	
										((ActorNode)n).connectActor(dataType);
									break;
								case NodeFunctionalTypesIF.NT_CHUNK:
									int state = Integer.parseInt(attr.getValue("state"));
									((ChunkNode)n).setState(state);
									break;
							}
								
							entity = n;						
							break;
						case NetEntityTypesIF.ET_MODULE_NODESPACE:
							NodeSpaceModule space = NetEntityFactory.getInstance().createNodeSpace(
								entityID, 
								null, 
								moduleManager, 
								entityManager,
								sensReg);
							space.DISSOCIATIONCONST = Double.parseDouble(attr.getValue("dissoconstant")); 
							space.LEARNINGCONST = Double.parseDouble(attr.getValue("learningconst"));
							space.STRENGTHENINGCONST = Double.parseDouble(attr.getValue("strengthconst"));

							// TODO: For backwards compability only. Remove in later versions. (0.6.11)
							String a = attr.getValue("decayallowed");
							if(a == null) {
								space.DECAYALLOWED = true;
							} else {
								space.DECAYALLOWED = Boolean.parseBoolean(attr.getValue("decayallowed"));
							}							
							
							entity = space;
													
							String parent = attr.getValue("parent");
							if(parent.equals("null")) parent = null;
							((Module)entity).changeParent(parent);							
							break;
						case NetEntityTypesIF.ET_MODULE_NATIVE:
							String classname = attr.getValue("impl");
							boolean isDefiant = true;
							try {
								 isDefiant = attr.getValue("defiant").equalsIgnoreCase("true");
							} catch (Exception e) {
								// ok. make it defiant by default.
							}
							if(classname.equals("null")) classname = null;
							NativeModule mod = NetEntityFactory.getInstance().createNativeModuleAndInstance(
								entityID, 
								classname,
								l, 
								null, 
								isDefiant, 
								moduleManager, 
								entityManager,
								interactionManager,
								netProperties);
						
							mod.initialize();						
							entity = mod;
							
							parent = attr.getValue("parent");
							if(parent.equals("null")) parent = null;
							((Module)entity).changeParent(parent);							
							break;		
					}
					entity.setEntityName(entityName.equals("null") ? null : entityName);
					stackEntity = entity;
				
				} catch(NetIntegrityException e) {
					entityManager.getLogger().warn("Could not load entity "+entityID,e);
					stackEntity = null;
				}
				
				progCount++;
				notifyLoadMonitorsOfProgress(progCount, progMax, "creating "+entityID);
				Thread.yield();
	
			} else if("Slot".equals(qName)) {
				if(stackEntity == null) return;
				
				int key = Integer.parseInt(attr.getValue("key"));
				
				if(stackEntity instanceof NodeSpaceModule) {
					try {
						((NodeSpaceModule)stackEntity).createSlot(key);
					} catch(NetIntegrityException e) {
						entityManager.getLogger().warn("Could not load slot at entity"+stackEntity.getID(),e);
					}
				}
				
				Slot slot = stackEntity.getSlot(key);
				if(slot == null) {
					entityManager.getLogger().warn("Slot missing at entity "+stackEntity.getID());
					if(stackEntity instanceof NativeModule) {
						((NativeModule)stackEntity).setHasLostLinks(true);
					}
					return;
				}
				
				
				String val = attr.getValue("act");
				slot.killActivation();
				slot.putActivation(Double.parseDouble(val));
			} else if("Gate".equals(qName)) {
				if(stackEntity == null) return;
				
				int key = Integer.parseInt(attr.getValue("key"));
				
				if(stackEntity instanceof NodeSpaceModule) {
					try {
						((NodeSpaceModule)stackEntity).createGate(key);
					} catch(NetIntegrityException e) {
						entityManager.getLogger().warn("Could not load gate at entity"+stackEntity.getID(),e);
					}
				}
					
				Gate gate = stackEntity.getGate(key);
				if(gate == null) {
					entityManager.getLogger().warn("Gate missing at entity "+stackEntity.getID());
					if(stackEntity instanceof NativeModule) {
						((NativeModule)stackEntity).setHasLostLinks(true);
					}
					return;
				}
					
				gate.setConfirmedActivation(Double.parseDouble(attr.getValue("act")));
				gate.setAmpfactor(Double.parseDouble(attr.getValue("amp")));
				gate.setDecayCalculatorType(Integer.parseInt(attr.getValue("decaytype")));
				gate.setGateFactor(Double.parseDouble(attr.getValue("fact")));
				gate.setLastDecayCalculation(Long.parseLong(attr.getValue("lastdecay")));
				gate.setMaximum(Double.parseDouble(attr.getValue("max")));
				gate.setMinimum(Double.parseDouble(attr.getValue("min")));
				
				// for backwards compatibility
				// TODO: remove
				// ---------------------------------------------------------------------------------
				if(attr.getValue("outputf") != null) {
					try {
						double theta = Double.parseDouble(attr.getValue("theta"));
						int oldType = Integer.parseInt(attr.getValue("outputf"));
					
						String classname = GateOutputFunctions.typeToClassName(oldType);
						gate.setOutputFunction(GateOutputFunctions.getOutputFunction(classname));
						
						try {
							gate.setOutputFunctionParameter("theta", theta);
						} catch(IllegalArgumentException e) {
							entityManager.getLogger().warn("Unable to migrate theta as output function "+classname+" does not know theta");
						}
						
					} catch (Exception e) {
						entityManager.getLogger().warn("Unable to migrate old gate output function at gate "+gate.getType()+" at entity "+stackEntity.getID(),e);
					}					
				} else {
				// -----------------------------------------------------------------------------------
				
					String classname = attr.getValue("of");
					try {
						gate.setOutputFunction(GateOutputFunctions.getOutputFunction(classname));
					} catch (ClassNotFoundException e) {
						entityManager.getLogger().warn("OutputFunction "+classname+" not found. Falling back to default output function.");
					}
					
				}
				stackGate = gate;
			} else if("OutputFunction".equals(qName)) {
				if(stackGate == null) return;
				
				for(int i=0;i<attr.getLength();i++) {
					String name = null;
					try {
						name = attr.getQName(i);
						double value = Double.parseDouble(attr.getValue(i));
						stackGate.setOutputFunctionParameter(name, value);
					} catch(Exception e) {
						entityManager.getLogger().warn("Unable to set output function parameter "+name+". Output function: "+stackGate.getOutputFunction());
					}
				}
				
			} else if("Implementation".equals(qName)) {
				if(stackEntity == null) return;

				if(stackEntity instanceof NativeModule) {
					NativeModule mod = (NativeModule)stackEntity;
					HashMap<String, String> tmpHash = new HashMap<String, String>();
					for(int j=0;j<attr.getLength();j++)
						tmpHash.put(attr.getQName(j),attr.getValue(j));
					
					if(mod.getImplementation() != null)
						mod.getImplementation().getInnerStates().setMap(tmpHash);
				}
			}
		} catch (Throwable e) {
			entityManager.getLogger().fatal("Net is unparseable (entity pass). Cause: "+e,e);
		} 
			
		}
		
	}
	
	class EntityStructureHandler extends DefaultHandler {
		
		NetEntity stackEntity = null;
		Gate stackGate = null;
		NodeSpaceModule stackSpace = null;
		
		private int progCount = 0;
		private int progMax = 0;
		
		public EntityStructureHandler(int maxProgress, int offset) {
			this.progCount = offset;
			this.progMax = maxProgress;
		}
		
		public void startElement (String uri, String localName, String qName, Attributes attr) throws SAXException {
			
			try {
			
			if("Entity".equals(qName)) {
				String entityID = attr.getValue("id");
				try {
					stackEntity = entityManager.getEntity(entityID);
					
					if(stackEntity instanceof NodeSpaceModule) {
						stackSpace = (NodeSpaceModule)stackEntity;
					}
					
				} catch(NetIntegrityException e) {
					// just skip
				}
				
				progCount++;
				notifyLoadMonitorsOfProgress(progCount, progMax, "linking "+entityID);
				Thread.yield();

			} else if("Gate".equals(qName)) {
				int key = Integer.parseInt(attr.getValue("key"));
				if(stackEntity != null) {
					stackGate = stackEntity.getGate(key);
				} // else: just skip
			} else if("Link".equals(qName)) {

				if(stackEntity == null || stackGate == null) return;
				
				int linkType = Integer.parseInt(attr.getValue("type"));
				String linked = attr.getValue("linked");
				int slot = Integer.parseInt(attr.getValue("slot"));
				double weight = Double.parseDouble(attr.getValue("weight"));  
				double conf = Double.parseDouble(attr.getValue("conf"));
				boolean used = attr.getValue("conf").equals("true"); 
				
				if(!entityManager.entityExists(linked)) {
					entityManager.getLogger().warn("Entity "+linked+" does not exist - skipping link creation at "+stackEntity.getID());
					return;
				}
				
				Link l = LinkFactory.getInstance().createLink(
					linkType, 
					entityManager, 
					stackGate, 
					linked, 
					slot, 
					weight, 
					conf);
				l.setUsed(used);
				
				if(linkType == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL) {
					LinkST stlink = (LinkST)l;
					stlink.setX(Double.parseDouble(attr.getValue("x")));
					stlink.setY(Double.parseDouble(attr.getValue("y")));
					stlink.setZ(Double.parseDouble(attr.getValue("z")));
					stlink.setT(Integer.parseInt(attr.getValue("t")));					
				}
			
				try {	
					if(l.getLinkedSlot() != null) {
						stackGate.addLink(l);
						l.getLinkedSlot().attachIncomingLink(l);
					} else {
						entityManager.getLogger().warn("Entity slot missing: "+slot+" at "+l.getLinkedEntityID()+": Cannot create link from "+stackEntity.getID());	
					}
				} catch (NetIntegrityException e) {
					entityManager.getLogger().warn("NetIntegrity problem. Cannot create link from "+stackEntity.getID(),e);
					return;					
				}
				
			} else if("Contains".equals(qName)) {
				String id = attr.getValue("id");
				
				if(!entityManager.entityExists(id)) {
					entityManager.getLogger().warn("Content missing: "+id+" (at "+stackEntity.getID()+")");
				} else {
					try {
						stackSpace.attachEntity(id,true);
					} catch (NetIntegrityException e) {
						entityManager.getLogger().warn("NetIntegrity problem. Cannot create containment in "+stackEntity.getID(),e);	
					}
				}
	
			}
			
			} catch (Throwable e) {
				entityManager.getLogger().fatal("Net is unparseable (structure pass). Cause: "+e,e);
			} 

		}
		
		public void endElement(String uri, String localName, String qName) {
			if("Entity".equals(qName)) {
				stackEntity = null;
				stackSpace = null;
			} else if("Gate".equals(qName)) {
				stackGate = null;
			}
		}

				
	}

	
	
	private NetPropertiesIF netProperties;
	private NetEntityManager entityManager;
	private ModuleManager moduleManager;
	private UserInteractionManager interactionManager;
	private SensActRegistry sensReg;
	private ArrayList<ProgressMonitorIF> loadprogresslisteners = new ArrayList<ProgressMonitorIF>();
	private ArrayList<ProgressMonitorIF> saveprogresslisteners = new ArrayList<ProgressMonitorIF>();
	
	public NetPersistencyManager(NetEntityManager nodeManager, ModuleManager spaceManager, UserInteractionManager interactionManager, NetPropertiesIF netProperties, SensActRegistry sensReg) {
		this.netProperties = netProperties;
		this.entityManager = nodeManager;
		this.moduleManager = spaceManager;
		this.interactionManager = interactionManager;
		this.sensReg = sensReg;
	}
				
	public void loadNet(MultiPassInputStream inp, ClassLoader classloader) throws MicropsiException {

		if(inp == null) {
			emptyLoad();
			return;
		}

		notifyLoadMonitorsOfBegin("Loading net...");
		
		try {
		
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			PreparationHandler ph = new PreparationHandler();
			parser.parse(inp, ph);
		
			int numberOfEntities = ph.getNumberOfEntities();
			
			entityManager.prepareLoad(numberOfEntities);
			
			int progMax = numberOfEntities * 3;
			notifyLoadMonitorsOfProgress(0, progMax, null);

			EntityCreationHandler ech = new EntityCreationHandler(classloader,progMax);

			inp.reset();
			parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(inp, ech);
		
			EntityStructureHandler esh = new EntityStructureHandler(progMax,numberOfEntities);

			inp.reset();
			parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(inp, esh);

		} catch (IOException e) {
			throw new MicropsiException(101,e);
		} catch (ParserConfigurationException e) {
			throw new MicropsiException(101,e);
		} catch (SAXException e) {
			inp.dump();
			throw new MicropsiException(101,e);
		}

		// after loading: build the list of active entities
		entityManager.buildActiveEntitiesList();
		
		notifyLoadMonitorsOfEnd();	
	}
		

	private void emptyLoad() {
		notifyLoadMonitorsOfBegin("Loading net...");
		notifyLoadMonitorsOfEnd();
	}

	public synchronized void saveNet(OutputStream outp) throws MicropsiException {
		
		BufferedOutputStream o = new BufferedOutputStream(outp);
		OutputStreamWriter w = new OutputStreamWriter(o);
		
		notifySaveMonitorsOfBegin("Saving net...");
		int progCount = 0;
		int progMax = entityManager.accessEntityMap().size() * 2;
		
		try {
		
		w.write("<MicroPsiNet>");
		w.write("<Entities netstep=\""+Long.toString(entityManager.getNetstep())+"\">");
				
		// create the entity entries
		Iterator keys = entityManager.accessEntityMap().keySet().iterator();
		while(keys.hasNext()) {
			
			String nextKey = (String)keys.next();
			
			progCount++;
			notifySaveMonitorsOfProgress(progCount, progMax, "Saving "+nextKey);
			
			try {
			
				NetEntity entity = entityManager.getEntity(nextKey); 
	 
				StringBuffer b = new StringBuffer();
				b	.append("<Entity id=\"")
					.append(entity.getID())
					.append("\" name=\"")
					.append(entity.hasName() ? entity.getEntityName() : "null")
					.append("\" type=\"")
					.append(Integer.toString(entity.getEntityType()))
					.append("\"");
				
				switch(entity.getEntityType()) {
					case NetEntityTypesIF.ET_NODE:
						Node node = (Node)entity;
						b	.append(" nodeType=\"")
							.append(Integer.toString(node.getType()))
							.append("\"");
						switch(node.getType()) {
							case NodeFunctionalTypesIF.NT_SENSOR:
								SensorNode snode = (SensorNode)node;
								b	.append(" dataType=\"")
									.append(snode.isConnected() ? snode.getDataType() : "null")
									.append("\"");
								break; 	
							case NodeFunctionalTypesIF.NT_ACTOR:
								ActorNode anode = (ActorNode)node;
								b	.append(" dataType=\"")
									.append(anode.isConnected() ? anode.getDataType() : "null")
									.append("\"");
								break;
							case NodeFunctionalTypesIF.NT_CHUNK:
								ChunkNode chunk = (ChunkNode)node;
								b	.append(" state=\"")
									.append(Integer.toString(chunk.getState()))
									.append("\"");
								break;
						}
						break;
					case NetEntityTypesIF.ET_MODULE_NODESPACE:
						NodeSpaceModule mod = (NodeSpaceModule)entity;
						b	.append(" dissoconstant=\"")
							.append(Double.toString(mod.getDissociationConstant()))
							.append("\" learningconst=\"")
							.append(Double.toString(mod.getLearningConstant()))
							.append("\" strengthconst=\"")
							.append(Double.toString(mod.getStrengtheningConstant()))
							.append("\" decayallowed=\"")
							.append(Boolean.toString(mod.isDecayAllowed()))
							.append("\" parent=\"")
							.append(mod.getParent() != null ? mod.getParent().getID() : "null")
							.append("\"");							
						break;
					case NetEntityTypesIF.ET_MODULE_NATIVE:
						NativeModule nmod = (NativeModule)entity;
						boolean isDefiant = entityManager.accessDefiantEntitiesIDList().contains(entity.getID());
						b	.append(" impl=\"")
							.append(nmod.getImplementationClassName())
							.append("\" parent=\"")
							.append(nmod.getParent() != null ? nmod.getParent().getID() : "null")
							.append("\" defiant=\"")
							.append(isDefiant ? "true" : "false")
							.append("\"");
						break;
				}
				
				b.append(">");
				
				w.write(b.toString());

				// create the slot entries
				w.write("<Slots>");
				Iterator slots = entity.getSlots();
				while(slots.hasNext()) {
					Slot slot = (Slot)slots.next();
					b = new StringBuffer();
					b	.append("<Slot key=\"")
						.append(Integer.toString(slot.getType()))
						.append("\" act=\"")
						.append(Double.toString(slot.getIncomingActivation()))
						.append("\"/>");
					w.write(b.toString());
				}
				w.write("</Slots>");

				// create the gate entries with links
				w.write("<Gates>");
				Iterator gates = entity.getGates();
				while(gates.hasNext()) {
					Gate gate = (Gate)gates.next();
					b = new StringBuffer();
					b	.append("<Gate key=\"")
						.append(Integer.toString(gate.getType()))
						.append("\" act=\"")
						.append(Double.toString(gate.getConfirmedActivation()))
						.append("\" fact=\"")
						.append(Double.toString(gate.getGateFactor()))
						.append("\" amp=\"")
						.append(Double.toString(gate.getAmpfactor()))
						.append("\" max=\"")
						.append(Double.toString(gate.getMaximum()))
						.append("\" min=\"")
						.append(Double.toString(gate.getMinimum()))
						.append("\" decaytype=\"")
						.append(Integer.toString(gate.getDecayCalculatorType()))
						.append("\" of=\"")
						.append(gate.getOutputFunction().getClass().getName())
						.append("\" lastdecay=\"")
						.append(Long.toString(gate.getLastDecayCalculation()))
						.append("\">");
					
					w.write(b.toString());
					
					// create the output function parameter entry
					b = new StringBuffer();
					b	.append("<OutputFunction ");
					OutputFunctionParameter[] params = gate.getCurrentOutputFunctionParameters();
					for(int i=0;i<params.length;i++) {
						
						String name = params[i].getName();
						if(name == null || name.length()<1) {
							continue;
						}
						
						b	.append(params[i].getName())
							.append("=\"")
							.append(params[i].getValue())
							.append("\" ");
					}
					b	.append("/>");
					w.write(b.toString());
					
					// create the link entries
					Iterator links = gate.getLinks();
					while(links.hasNext()) {
						Link l = (Link)links.next();
						b = new StringBuffer();
						b	.append("<Link type=\"")
							.append(Integer.toString(l.getType()))
							.append("\" linked=\"")
							.append(l.getLinkedEntityID())
							.append("\" slot=\"")
							.append(Integer.toString(l.getLinkedSlot().getType()))
							.append("\" weight=\"")
							.append(Double.toString(l.getWeight()))
							.append("\" conf=\"")
							.append(Double.toString(l.getConfidence()))
							.append("\" used=\"")
							.append(l.wasUsed() ? "true" : "false")
							.append("\"");
						
						if(l.getType() == LinkTypesIF.LINKTYPE_SPACIOTEMPORAL) {
							LinkST lst = (LinkST)l;
							b	.append(" x=\"")
								.append(Double.toString(lst.getX()))
								.append("\" y=\"")
								.append(Double.toString(lst.getY()))
								.append("\" z=\"")
								.append(Double.toString(lst.getZ()))
								.append("\" t=\"")
								.append(Integer.toString(lst.getT()))
								.append("\"");
						}
						
						b.append("/>");
						w.write(b.toString());
					}
					
					w.write("</Gate>");
				}
				w.write("</Gates>");
			
				switch(entity.getEntityType()) {
					case NetEntityTypesIF.ET_NODE:
						break;
					case NetEntityTypesIF.ET_MODULE_NODESPACE:

						w.write("<Content>");
					
						NodeSpaceModule mod = (NodeSpaceModule)entity;
						Iterator contains = mod.getAllLevelOneEntities();
						
						while(contains.hasNext()) {
							NetEntity cent = (NetEntity)contains.next();
							w.write("<Contains id=\""+cent.getID()+"\"/>");							
						}
						
						w.write("</Content>");
						
						break;
					case NetEntityTypesIF.ET_MODULE_NATIVE:
						NativeModule nmod = (NativeModule)entity;

						w.write("<Implementation");

						try {
							
							HashMap<String,String> tmpHash = nmod.getImplementation().getInnerStates().getMap();
							if(tmpHash == null) {
								w.write("/>");
								break;
							}
						
							Iterator iter = tmpHash.keySet().iterator();
							while(iter.hasNext()) {
								String k = (String)iter.next();
								
								if(k == null || "".equals(k)) {
									continue;
								}
								
								String v = tmpHash.get(k);
								k.replace('<', '_');
								k.replace('>', '_');
								v.replace('<', '_');
								v.replace('>', '_');
								
								w.write(" "+k+"=\""+v+"\"");
							}
						} catch (Exception e){
							// do nothing
						}
	
						w.write("/>");
						break;
				}
				
				w.write("</Entity>");
				
			} catch (Exception e) {
				entityManager.getLogger().error("Could not save entity: "+nextKey+". This will probably cause trouble when reloading.",e);				
			}
		}
		
		w.write("</Entities>");
		
		w.write("</MicroPsiNet>");

		w.flush();
		o.flush();
		outp.flush();
		
		} catch (IOException e) {
			throw new MicropsiException(15,e);
		}
		
		notifySaveMonitorsOfEnd();		
	}
	
	protected void registerLoadProgressListener(ProgressMonitorIF progress) {
		loadprogresslisteners.add(progress);
	}
	
	protected void unregisterLoadProgressListener(ProgressMonitorIF progress) {
		loadprogresslisteners.remove(progress);
	}
	
	private void notifyLoadMonitorsOfBegin(String ofwhat) {		
		for(int i=0;i<loadprogresslisteners.size();i++)
			loadprogresslisteners.get(i).beginTask(ofwhat);
	}
	
	private void notifyLoadMonitorsOfProgress(int progress, int of, String message) {
		for(int i=0;i<loadprogresslisteners.size();i++)
			loadprogresslisteners.get(i).reportProgress(progress, of, message);
	}

	private void notifyLoadMonitorsOfEnd() {
		for(int i=0;i<loadprogresslisteners.size();i++)
			loadprogresslisteners.get(i).endTask();
	}


	protected void registerSaveProgressListener(ProgressMonitorIF progress) {
		saveprogresslisteners.add(progress);
	}
	
	protected void unregisterSaveProgressListener(ProgressMonitorIF progress) {
		saveprogresslisteners.remove(progress);
	}
	
	private void notifySaveMonitorsOfBegin(String ofwhat) {		
		for(int i=0;i<saveprogresslisteners.size();i++)
			saveprogresslisteners.get(i).beginTask(ofwhat);
	}
	
	private void notifySaveMonitorsOfProgress(int progress, int of, String message) {
		for(int i=0;i<saveprogresslisteners.size();i++)
			saveprogresslisteners.get(i).reportProgress(progress, of, message);
	}

	private void notifySaveMonitorsOfEnd() {
		for(int i=0;i<saveprogresslisteners.size();i++)
			saveprogresslisteners.get(i).endTask();
	}

	
}
