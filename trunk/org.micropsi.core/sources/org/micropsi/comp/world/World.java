/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/World.java,v 1.10 2006/01/22 11:00:55 fuessel Exp $
 */
package org.micropsi.comp.world;

import java.lang.reflect.Constructor;
import java.util.*;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Area2D;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.objects.AbstractAgentObject;
import org.micropsi.comp.world.objects.AbstractObject;
import org.micropsi.comp.world.objects.AbstractObjectPart;
import org.micropsi.comp.world.objects.AgentObjectIF;
import org.micropsi.comp.world.objects.GroundType;

/**
 * @author Matthias
 *
 */
public class World {
	private String fileName = null;
	private String worldType = null;
	
	private Map<Long,AbstractObjectPart> objectParts = new HashMap<Long,AbstractObjectPart>(400);
	private Set<AbstractObject> objects = new HashSet<AbstractObject>(150);
	
	private GroundMap groundMap = null;
	private Area2D visibleArea;
	private int versionOfGlobalData = 1;
	private List<Position> spawnLocations = null;
	
	private PostOffice postOffice = null;
	private ChangeLog changeLog = null;
	private ObjectCreator objectCreator = null;
	private ObjectNameGenerator objectNameGenerator = new ObjectNameGenerator();
	
	private int dynamicLevel = 0;

	private WorldComponent worldComponent;
	private long simStep;
	
	private TimerTickGenerator highResolutionTimer;
	private TimerTickGenerator mediumResolutionTimer;
	private TimerTickGenerator lowResolutionTimer;
	
	/**
	 * Constructor for World.
	 */
	public World(WorldComponent worldComponent) {
		this.worldComponent = worldComponent;
		postOffice = new PostOffice();
		changeLog = new ChangeLog(100, this);
		objectCreator = new ObjectCreator(this);
		visibleArea = new Area2D(new Position(0, 0), new Position(30, 30));
		spawnLocations = new ArrayList<Position>(5);
		
		highResolutionTimer = new TimerTickGenerator(1, "highresolutiontick", this);
		mediumResolutionTimer = new TimerTickGenerator(50, "mediumresolutiontick", this);
		highResolutionTimer.subscribe(mediumResolutionTimer);
		lowResolutionTimer = new TimerTickGenerator(250, "lowresolutiontick", this);
		mediumResolutionTimer.subscribe(lowResolutionTimer);
	}

	public int getNumberOfObjects() {
		return objects.size();
	}

	public void addObjectPart(AbstractObjectPart thing) {
		objectParts.put(new Long(thing.getId()), thing);
		thing.init(this);
	}
	
	public void addObject(AbstractObject thing) {
		addObjectPart(thing);
		objects.add(thing);
	}
	
	public long createObject(String className, String objectName, String objectClass, Position pos) throws MicropsiException {
		return createObject(className, objectName, objectClass, pos, null);
	}

	public long createObject(String className, String objectName, String objectClass, Position pos, ObjectProperties properties) throws MicropsiException {
		Class[] parameters = new Class[3];
		try {
			parameters[0] = Class.forName("java.lang.String"); // object name
			parameters[1] = Class.forName("java.lang.String"); // object class
			parameters[2] = Class.forName("org.micropsi.common.coordinates.Position"); // position
		} catch (ClassNotFoundException e) {
			throw new MicropsiException(10, "Error creating world object: required class not found", e);
		}
		Object[] parameterValues = new Object[3];
		parameterValues[0] = objectName;
		parameterValues[1] = objectClass;
		parameterValues[2] = pos;
		
		AbstractObject newObject;
		try {
			Constructor myConstr = Class.forName(className).getConstructor(parameters);
			newObject = (AbstractObject) myConstr.newInstance(parameterValues);
		} catch (ClassNotFoundException e) {
			throw new MicropsiException(
				10,
				"Error creating world object: class not found: " + className,
				e);
		} catch (NoSuchMethodException e) {
			throw new MicropsiException(
				10,
				"Error creating world object: class "
					+ className
					+ " has no matching constructor",
				e);
		} catch (InstantiationException e) {
			throw new MicropsiException(
				10,
				"Error creating world object: Error setting up class " + className,
				e);
		} catch (IllegalAccessException e) {
			throw new MicropsiException(
				10,
				"Error creating world object: Error setting up class " + className,
				e);
		} catch (java.lang.reflect.InvocationTargetException e) {
			Exception f = (Exception) e.getCause();
			throw new MicropsiException(
				10,
				"Error creating world object: Exception (see below) in constructor for class "
					+ className,
				f == null ? e : f);
		}
		
		if (properties != null) {
			newObject.setProperties(properties);
		}

		addObject(newObject);
		return newObject.getId();
	}

	public AbstractObject getObject(String objectClass, String objectName) throws MicropsiException {

		//search all objectParts -> right now this is just a linear search, which is bad
		Iterator iter = getObjects().iterator();
		while (iter.hasNext()) {
			AbstractObject o = (AbstractObject) iter.next();
			if (o.getObjectClass().equals(objectClass) && o.getObjectName().equals(objectName)) {
				return o;
			}
		}
		throw new MicropsiException(10, "world object not found: " + objectClass + ", " + objectName);
	}

	public Collection<AbstractObject> getObjects() {
		return objects;
	}
	
	public Collection<AbstractObjectPart> getObjectParts() {
		return objectParts.values();
	}
	
	public AbstractObjectPart getObjectPart(long l) {
		return objectParts.get(new Long(l));
	}
	
	public AbstractObject getObject(long l) {
		AbstractObjectPart result = getObjectPart(l);
		if (result instanceof AbstractObject) {
			return (AbstractObject) result;
		} else {
			return null;
		}
	}

	public void moveObject(AbstractObject thing, Position newPosition) {
		thing.moveTo(newPosition);
	}

	public void removeObject(AbstractObjectPart object) {
		getHighResolutionTimer().unsubscribe(object);
		getMediumResolutionTimer().unsubscribe(object);
		getLowResolutionTimer().unsubscribe(object);
		if (object.isHighLevelObject()) {
			objects.remove(object);
		}
		if (object instanceof AgentObjectIF) {
			worldComponent.getAgents().remove(((AgentObjectIF) object).getAgentName());
		}
		objectParts.remove(new Long(object.getId()));
		object._remove();
	}
	
	public void removeObject(long id) throws MicropsiException {
		AbstractObjectPart object = objectParts.get(new Long(id));
		removeObject(object);
	}
	
	public Set<AbstractObject> getObjectsByPosition(Position pos, double radius) {
		Set<AbstractObject> objects = new HashSet<AbstractObject>(50);
		Collection allObjects = getObjects();
		radius = radius * radius;
		Iterator it = allObjects.iterator();
		while (it.hasNext()) {
			AbstractObject obj = (AbstractObject) it.next();
			if (obj.getPosition().sqrDistance2D(pos) <= radius) {
				objects.add(obj);
			}
		}
		return objects;
	}
	
	public Set<AbstractObjectPart> getObjectPartsByPosition(Position pos, double radius) {
		Set<AbstractObjectPart> objects = new HashSet<AbstractObjectPart>(50);
		Collection allObjects = getObjectParts();
		radius = radius * radius;
		Iterator it = allObjects.iterator();
		while (it.hasNext()) {
			AbstractObject obj = (AbstractObject) it.next();
			if (obj.getPosition().sqrDistance2D(pos) <= radius) {
				objects.add(obj);
			}
		}
		return objects;
	}
	
	public String toString() {
		if (objectParts == null) {
			return "empty";
		} else {
			return objectParts.toString();
		}
	}

	/**
	 * 
	 */
	public Logger getLogger() {
		return worldComponent.getLogger();
		
	}

	/**
	 * @return
	 */
	public WorldComponent getWorldComponent() {
		return worldComponent;
	}

	/**
	 * @param component
	 */
	public void setWorldComponent(WorldComponent component) {
		worldComponent = component;
	}
	
	public GroundMap getGroundMap() {
		return groundMap;
	}
	
	public void setGroundMap(GroundMap groundMap) {
		this.groundMap = groundMap;
	}
	
	protected void tick(long simStep) {
		this.simStep = simStep;
//		this.physics.treadObjects();
		if (getChangeLog() != null) {
			getChangeLog().updateTime(simStep);
		}
		postOffice.tick(simStep);
		highResolutionTimer.tick(simStep);
	}

	/**
	 * Returns the current simStep.
	 * @return long
	 */
	public long getSimStep() {
		return simStep;
	}

	/**
	 * @return
	 */
	public PostOffice getPostOffice() {
		return postOffice;
	}

	/**
	 * @return
	 */
	public TimerTickGenerator getHighResolutionTimer() {
		return highResolutionTimer;
	}

	/**
	 * @return
	 */
	public TimerTickGenerator getLowResolutionTimer() {
		return lowResolutionTimer;
	}

	/**
	 * @return
	 */
	public TimerTickGenerator getMediumResolutionTimer() {
		return mediumResolutionTimer;
	}

	/**
	 * @param agent
	 * @param vec
	 * @return
	 */
	public WorldVector getEffectiveMoveVector(
		AbstractAgentObject agent,
		WorldVector vec) {
		return groundMap.getEffectiveMoveVector(agent, vec);
	}

	/**
	 * @param pos
	 * @return
	 */
	public GroundType getGroundType(Position pos) {
		return groundMap.getGroundType(pos);
	}

	/**
	 * @return
	 */
	public ChangeLog getChangeLog() {
		return changeLog;
	}

	/**
	 * @return Returns the dynamicLevel.
	 * 
	 * 0: no dynamics
	 * 1: agent action may change/remove target objectParts
	 * 2: agent action may cause general changes
	 * 10: all types of dynamics
	 */
	public int getDynamicLevel() {
		return dynamicLevel;
	}
	/**
	 * @param dynamicLevel The dynamicLevel to set.
	 */
	public void setDynamicLevel(int dynamicLevel) {
		this.dynamicLevel = dynamicLevel;
	}
	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName The fileName to set.
	 */
	public void setFileName(String worldFileName) {
		this.fileName = worldFileName;
	}
	/**
	 * @return Returns the worldType.
	 */
	public String getWorldType() {
		return worldType;
	}
	/**
	 * @param worldType The worldType to set.
	 */
	public void setWorldType(String worldType) {
		this.worldType = worldType;
	}
	/**
	 * @return Returns the versionOfGlobalData.
	 */
	public int getVersionOfGlobalData() {
		return versionOfGlobalData;
	}
	public void globalDataChanged() {
		versionOfGlobalData = (versionOfGlobalData + 1) % (Integer.MAX_VALUE - 1);
	}
	/**
	 * @return Returns the visibleArea.
	 */
	public Area2D getVisibleArea() {
		return visibleArea;
	}
	/**
	 * @param visibleArea The visibleArea to set.
	 */
	public void setVisibleArea(Area2D visibleArea) {
		this.visibleArea = visibleArea;
	}
	/**
	 * @return Returns the objectCreator.
	 */
	public ObjectCreator getObjectCreator() {
		return objectCreator;
	}
	/**
	 * @return Returns the objectNameGenerator.
	 */
	public ObjectNameGenerator getObjectNameGenerator() {
		return objectNameGenerator;
	}
	
	public Position getSpawnLocation() {
		if (!spawnLocations.isEmpty()) {
			return spawnLocations.get((int) (Math.random()*spawnLocations.size()));
		} else {
			getLogger().error("no spawn location present in world.");
			return new Position(0, 0, 0);
		}
	}
	
	public void addSpawnLocation(Position pos) {
		spawnLocations.add(pos);
	}

	/**
	 * @return Returns the spawnLocations.
	 */
	public List<Position> getSpawnLocations() {
		return spawnLocations;
	}
}