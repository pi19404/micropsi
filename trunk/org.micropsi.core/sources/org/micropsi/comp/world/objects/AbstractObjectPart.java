/*
 * Created on 28.06.2004
 *
 */

package org.micropsi.comp.world.objects;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ChangeLogEntry;
import org.micropsi.comp.world.ObjectProperties;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.World;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.WorldMessageHandlerIF;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.w3c.dom.Element;

/**
 * @author Matthias
 *
 */
public class AbstractObjectPart implements VisualFeatureIF, WorldMessageHandlerIF {

	private static long nextObjectId = 0;
	protected static final NumberFormat sizeNumberFormat = createSizeNumberFormat();

	protected World world = null;
	protected AbstractObjectPart containingPart = null;
	protected Set<AbstractObjectPart> subParts = null;
	protected int partHierarchyLevel = 0;
	protected Collection<AbstractPropertyAccessor> requiredProperties = null;
	protected Collection<AbstractPropertyAccessor> optionalProperties = null;

	protected long id = -1;
	protected String objectClass;
	protected Position position;
	protected double orientationAngle = 0;

	protected double xSize;
	protected double ySize;
	protected double zSize;

	/**
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public AbstractObjectPart(String objectClass, Position pos) {
		id = getNextObjectId();
		this.objectClass = objectClass;
		position = pos;
		createPropertyCollection();
		initProperties();
		initObjectParameters();
		initSubobjects();
	}

	/**
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public AbstractObjectPart(Element configData, Logger logger) throws MicropsiException {
		id = getNextObjectId();
		createPropertyCollection();
		initProperties();
		
		ObjectProperties properties;
		try {
			properties = new ObjectProperties(XMLElementHelper
					.getElementByTagName(configData, "properties"));
		} catch (XMLElementNotFoundException e1) {
			throw new MicropsiException(10,
					"Object has no properties. At least 'name', 'class' and 'position' are required");
		}
		setRequiredProperties(properties);

		initObjectParameters();

		if (!setProperties(properties)) {
			logger.error(getObjectIdentification()
					+ ": unable to set these properties:\n" + properties);
		}

		initSubobjects();
	}

	private static NumberFormat createSizeNumberFormat() {
		DecimalFormatSymbols syms = new DecimalFormatSymbols();
		syms.setDecimalSeparator('.');
		syms.setGroupingSeparator(' ');
		
		DecimalFormat nFormat = new DecimalFormat();
		nFormat.setDecimalFormatSymbols(syms);
		nFormat.setMinimumFractionDigits(0);
		nFormat.setMaximumFractionDigits(2);
		return nFormat;
	}

	// Inititalize
	
	protected void initProperties() {
		addRequiredProperty(new AbstractPropertyAccessor("class", ObjectProperty.VTYPE_STRING, false) {
			protected boolean _setProperty(ObjectProperty prop) {
				setObjectClass(prop.getValue());
				return true;
			}
			protected String getValue() {
				return getObjectClass();
			}
		});
		addRequiredProperty(new AbstractPropertyAccessor("position", ObjectProperty.VTYPE_STRING) {
			protected boolean _setProperty(ObjectProperty prop) {
				String position = prop.getValue();
				if (getPosition() != null) {
					moveTo(new Position(position));
				} else {
					setPosition(new Position(position));
				}
				return true;
			}
			protected String getValue() {
				return getPosition().toString();
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("size", ObjectProperty.VTYPE_DOUBLE, true, true) {
			protected boolean _setProperty(ObjectProperty prop) {
				setSize(prop.getDoubleValue());
				return true;
			}

			protected String getValue() {
				return Double.toString(getSize());
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("size-x,y,z", ObjectProperty.VTYPE_STRING) {
			protected boolean _setProperty(ObjectProperty prop) {
				setDistinctSizes(prop.getValue());
				return true;
			}
			protected String getValue() {
				return getDistinctSizes();
			}
		});
		addOptionalProperty(new AbstractPropertyAccessor("orientation angle", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				rotateToAngle(prop.getDoubleValue());
				return true;
			}
			protected String getValue() {
				return Double.toString(getOrientationAngle());
			}
		});
	}
	
	/**
	 * 
	 */
	protected void initSubobjects() {
	}
	
	protected void initObjectParameters() {
		orientationAngle = 0;
	}

	public void init(World world) {
		this.world = world;
		if (subParts != null) {
			Iterator it = subParts.iterator();
			while (it.hasNext()) {
				world.addObjectPart((AbstractObjectPart) it.next());
			}
		}
	}
	
	// External
	
	
	public boolean isAlive() {
		return (world != null);
	}

	/**
	 * Returns the objectClass.
	 * @return String
	 */
	public String getObjectClass() {
		return objectClass;
	}

	/**
	 * Returns the position.
	 * @return Position
	 */
	public Position getPosition() {
		return position;
	}

	public ObjectProperties getProperties() {
		ObjectProperties res = new ObjectProperties(id);
		Iterator it = requiredProperties.iterator();
		while (it.hasNext()) {
			res.addProperty(((AbstractPropertyAccessor) it.next()).getProperty());
		}
		it = optionalProperties.iterator();
		while (it.hasNext()) {
			res.addProperty(((AbstractPropertyAccessor) it.next()).getProperty());
		}
		return res;
	}
	
	/**
	 * @return all optional, none-redundant properties.
	 */
	public ObjectProperties getOptionalProperties() {
		ObjectProperties res = new ObjectProperties(id);
		Iterator it = optionalProperties.iterator();
		while (it.hasNext()) {
			ObjectProperty property = ((AbstractPropertyAccessor) it.next()).getProperty();
			if (!property.isRedundantProperty()) {
				res.addProperty(property);
			}
		}
		return  res;
	}

	public double getSize() {
		return Math.sqrt(xSize*xSize + ySize*ySize + zSize*zSize);
	}

	public double getXSize() {
		return xSize;
	}

	public double getYSize() {
		return ySize;
	}

	public double getZSize() {
		return zSize;
	}

	public boolean isLowLevelPart() {
		return !(hasSubParts());
	}

	public final void handleMessage(AbstractWorldMessage m) {
		if (isAlive()) {
			try {
				_handleMessage(m);
			} catch (Exception e) {
				getLogger().error("Exception while handling message " + m + " for " + getObjectIdentification() + ".", e);
			}
		} else {
			getLogger().warn(getObjectIdentification() + " received message " + m + " while not being part of the world.");
		}
	}

	/**
	 * Returns the id.
	 * @return long
	 */
	public long getId() {
		return id;
	}

	public void moveBy(WorldVector vec) {
		Position pos = new Position(getPosition());
		pos.add(vec);
		moveTo(new Position(pos));
	}

	public void moveTo(Position newPosition) {
		WorldVector vec = newPosition.getDifferenceVector(getPosition());
		setPosition(newPosition);
		if (subParts != null) {
			Iterator it = subParts.iterator();
			while (it.hasNext()) {
				AbstractObjectPart obj = (AbstractObjectPart) it.next();
				obj.moveBy(vec);
			}
		}
	}

	public void setSize(double x, double y, double z) {
		double xFactor = getXSize() != 0 ? x / getXSize() : 1;
		double yFactor = getYSize() != 0 ? y / getYSize() : 1;
		double zFactor = getZSize() != 0 ? z / getZSize() : 1;
		_setSize(x, y, z);
		scaleSubpartsBy(xFactor, yFactor, zFactor);
	}

	public void scaleBy(double xFactor, double yFactor, double zFactor) {
		xSize*=xFactor;
		ySize*=yFactor;
		zSize*=zFactor;
		scaleSubpartsBy(xFactor, yFactor, zFactor);
	}

	public boolean setProperties(ObjectProperties properties) {
		boolean res = true;
		Iterator it = properties.iterator();
		while (it.hasNext()) {
			ObjectProperty prop = (ObjectProperty) it.next();
			if (setProperty(prop)) {
				if (prop.getComment() == null) {
					it.remove();
				}
			} else {
				res = false;
			}
		}
		return res;
	}

	public boolean setProperty(ObjectProperty prop) {
		try {
			return _setProperty(prop);
		} catch (NumberFormatException e) {
			prop.setComment("invalid data format");
			return false;
		}
	}

	public void scaleBy(double factor) {
		scaleBy(factor, factor, factor);
	}

	public MTreeNode toMTreeNode() {
		MTreeNode res = new MTreeNode("object", "", null);
		res.addChild("id", Long.toString(getId()));
		res.addChild("position", getPosition().toString());
		res.addChild("orientation", Double.toString(getOrientationAngle()));
		res.addChild("objectclass", getObjectClass());
		if (hasSubParts()) {
			MTreeNode subObjectNode = new MTreeNode("subobjects", "", null);
			addSubPartsToMTreeNode(subObjectNode);
			res.addChild(subObjectNode);
		}
		
		return res;
	}

	public String toString() {
		return "class: " + objectClass +
			"\nposition: " + position + "\n";
	}

	public boolean isHighLevelObject() {
		return containingPart == null && this instanceof AbstractObject;
	}

	/**
	 * @return Returns the containingPart.
	 */
	public AbstractObjectPart getContainingPart() {
		return containingPart;
	}
	
	public AbstractObject getContainingObject() {
		if (isHighLevelObject()) {
			return (AbstractObject) this;
		} else {
			if (getContainingPart() != null) {
				return getContainingPart().getContainingObject();
			} else {
				return null;
			}
		}
	}

	public void addLowLevePartsTo(Collection<AbstractObjectPart> coll) {
		if (isLowLevelPart()) {
			coll.add(this);
		} else {
			if (subParts != null) {
				Iterator it = subParts.iterator();
				while (it.hasNext()) {
					AbstractObject obj = (AbstractObject) it.next();
					obj.addLowLevePartsTo(coll);
				}
			}
		}
	}

	public AbstractObjectPart addSubPart(AbstractObjectPart obj) {
		if (subParts == null) {
			subParts = new HashSet<AbstractObjectPart>(10);
		}
		subParts.add(obj);
		obj.setContainingPart(this);
		obj.setPartHierarchyLevel(getPartHierarchyLevel() + 1);
		if (isAlive()) {
			triggerSubPartChanged();
		}
		return obj;
	}
	
	public AbstractObjectPart addSubPartRelative(AbstractObjectPart obj) {
		AbstractObjectPart res = addSubPart(obj);
		Position pos = new Position(obj.getPosition().getX()
				* (getXSize() - obj.getXSize()) / 2 + getPosition().getX(), obj
				.getPosition().getY()
				* (getYSize() - obj.getYSize()) / 2 + getPosition().getY(), obj
				.getPosition().getZ()
				* (getZSize() - obj.getZSize()) + getPosition().getZ());
		obj.moveTo(pos);
		return res;
	}

	public void removeSubPart(AbstractObjectPart obj) {
		if (subParts != null) {
			subParts.remove(obj);
			obj.setContainingPart(null);
			if (isAlive()) {
				triggerSubPartChanged();
			}
		}
	}
	
	/**
	 * @return
	 */
	public boolean hasSubParts() {
		return subParts != null && subParts.size() > 0;
	}

	/**
	 * @return Returns the partHierarchyLevel.
	 */
	public int getPartHierarchyLevel() {
		return partHierarchyLevel;
	}

	/**
	 * @param d
	 */
	public void setSize(double newSize) {
		double size = getSize();
		if (size != 0) {
			scaleBy(newSize / size);
		} else {
			double newSizeX = Math.sqrt(newSize*newSize / 3);
			setSize(newSizeX, newSizeX, newSizeX);
		}
	}

	public boolean originatorsMatchSubParts(AbstractWorldMessage m) {
		return originatorsMatchSubParts(m, 0);
	}
	
	public boolean originatorsMatchSubParts(AbstractWorldMessage m, int allowMissing) {
		Collection coll = m.getCurrentRecipientOriginators();
		if (coll == null) {
			return true;
		}
		if (subParts == null) {
			return (coll == null) || (coll.size() == 0); 
		}
		if (allowMissing == 0) {
			return (coll.size() == subParts.size()) && (subParts.containsAll(coll));
		} else {
			Set<AbstractObjectPart> set = new HashSet<AbstractObjectPart>(subParts);
			set.retainAll(coll);
			return (subParts.size() - set.size() <= allowMissing) && (coll.size() == set.size());
		}
	}

	public double getOrientationAngle() {
		return orientationAngle;
	}

	public void rotateToAngle(double angle) {
		rotateBy(angle - getOrientationAngle());
	}

	public void rotateBy(double angle) {
		orientationAngle += angle;
		if (orientationAngle >= 360) {
			orientationAngle -= 360;
		} else if (orientationAngle < 0) {
			orientationAngle += 360;
		}

		if (subParts != null) {
			Iterator it = subParts.iterator();
			while (it.hasNext()) {
				AbstractObject obj = (AbstractObject) it.next();
				obj.rotateBy(angle);
				WorldVector relativePos = obj.getPosition().getDifferenceVector(getPosition());
				relativePos.rotate(angle);
				Position newPos = new Position(getPosition());
				newPos.add(relativePos);
				obj.moveTo(newPos);
			}
		}
	}
	

	// Internal
	
	public void _remove() {
		if (getContainingPart() != null) {
			getContainingPart().removeSubPart(this);
		}
		if (isAlive()) {
			if (hasSubParts()) {
				Iterator<AbstractObjectPart> it = new HashSet<AbstractObjectPart>(subParts).iterator();
				while (it.hasNext()) {
					world.removeObject(it.next());
				}
			}
			this.world = null;
		}
	}

	protected String getObjectIdentification() {
		return "object part class " + getObjectClass() + "(" + getId() + ")";
	}
	
	/**
	 * @param m
	 */
	protected void _handleMessage(AbstractWorldMessage m) {
		if (m.isClass("AGENTACTION")) {
			handleMessageAgentAction(m);
		}
	}

	/**
	 * @param m
	 */
	protected void handleMessageAgentAction(AbstractWorldMessage m) {
		if (getContainingPart() != null && originatorsMatchSubParts(m)) {
			m.delegateToParent(this, getContainingPart());
		}
	}

	private boolean _setProperty(ObjectProperty prop) {
		String key = prop.getKey();
		Iterator it = requiredProperties.iterator();
		while (it.hasNext()) {
			AbstractPropertyAccessor propAcc = (AbstractPropertyAccessor) it.next();
			if (propAcc.matchesKey(key)) {
				boolean res = propAcc.setProperty(prop);
				if (!res) {
					if (prop.getComment() == null) {
						prop.setComment("could not set");
					}
				}
				return res;
			}
		}
		it = optionalProperties.iterator();
		while (it.hasNext()) {
			AbstractPropertyAccessor propAcc = (AbstractPropertyAccessor) it.next();
			if (propAcc.matchesKey(key)) {
				boolean res = propAcc.setProperty(prop);
				if (!res) {
					if (prop.getComment() == null) {
						prop.setComment("could not set");
					}
				}
				return res;
			}
		}
		prop.setComment("property unknown for object class " + getObjectClass());
		return false;
	}

	/**
	 * @return
	 */
	protected String getDistinctSizes() {
		return sizeNumberFormat.format(xSize) + ", " + sizeNumberFormat.format(ySize) + ", " + sizeNumberFormat.format(zSize);
	}

	/**
	 * @return
	 */
	protected Logger getLogger() {
		if (world != null) {
		return world.getLogger();
		} else {
			return WorldComponent.logger;
		}
	}

	protected long getNextObjectId() {
		return nextObjectId++;
	}

	/**
	 * @param string
	 */
	protected void setDistinctSizes(String s) {
		if (s.startsWith("(") && s.endsWith(")")) {
			s = s.substring(1, s.length()-1);
		}
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		double x, y, z;
		try {
			x = Double.parseDouble(tokenizer.nextToken());
			y = Double.parseDouble(tokenizer.nextToken());
			z = Double.parseDouble(tokenizer.nextToken());
		} catch (NoSuchElementException e) {
			throw new NumberFormatException();
		}
		if (tokenizer.hasMoreTokens()) {
			throw new NumberFormatException();
		}
		xSize = x; ySize = y; zSize = z;
	}

	/**
	 * Sets the objectClass.
	 * @param objectClass The objectClass to set
	 */
	protected void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
		triggerSubPartChanged();
	}

	/**
	 * Sets the position.
	 * @param position The position to set
	 */
	protected void setPosition(Position position) {
		this.position = position;
	}

	protected void _setSize(double x, double y, double z) {
		xSize = x;
		ySize = y;
		zSize = z;
	}

	/**
	 * @param xFactor
	 * @param yFactor
	 * @param zFactor
	 */
	protected void scaleSubpartsBy(double xFactor, double yFactor, double zFactor) {
		if (subParts != null) {
			Iterator it = subParts.iterator();
			while (it.hasNext()) {
				AbstractObject obj = (AbstractObject) it.next();
				obj.scaleBy(xFactor, yFactor, zFactor);
				Position newPos = new Position(obj.getPosition());
				newPos.subtract(getPosition());
				newPos.set(newPos.getX()*xFactor, newPos.getY()*yFactor, newPos.getZ()*zFactor);
				newPos.add(getPosition());
				obj.moveTo(newPos);
			}
		}
	}

	protected void addSubPartsToMTreeNode(MTreeNode node) {
		if (subParts != null) {
			Iterator it = subParts.iterator();
			while (it.hasNext()) {
				AbstractObjectPart subObject = (AbstractObjectPart) it.next();
				MTreeNode subNode = new MTreeNode(Long.toString(subObject.getId()), subObject.getObjectClass(), null);
				subObject.addSubPartsToMTreeNode(subNode);
				node.addChild(subNode);
			}
		}
	}

	/**
	 * @param containingPart The containingPart to set.
	 */
	protected void setContainingPart(AbstractObjectPart owningObject) {
		this.containingPart = owningObject;
	}

	/**Sets hierarchy level for object and all subobjects.
	 * (Root objects have hierarchy level 0)
	 * @param partHierarchyLevel The partHierarchyLevel to set.
	 */
	public void setPartHierarchyLevel(int hierarchyLevel) {
		this.partHierarchyLevel = hierarchyLevel;
		if (subParts != null) {
			Iterator it = subParts.iterator();
			while (it.hasNext()) {
				((AbstractObjectPart) it.next()).setPartHierarchyLevel(hierarchyLevel + 1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObjectPart#triggerSubobjectChanged(org.micropsi.comp.world.objects.AbstractObjectPart)
	 */
	protected void triggerSubPartChanged() {
		if (isAlive()) {
			AbstractObject containingObject = getContainingObject();
			if (containingObject != null) {
				containingObject.logChange(ChangeLogEntry.CT_CHANGE_SUBOBJECT);
			}
		}
	}
	
	protected void setRequiredProperties(ObjectProperties properties) throws MicropsiException {
		Iterator it = requiredProperties.iterator();
		while (it.hasNext()) {
			AbstractPropertyAccessor propAcc = (AbstractPropertyAccessor) it.next();
			ObjectProperty prop = properties.getProperty(propAcc.getKey());
			if (prop == null) {
				throw new MicropsiException(10, getObjectIdentification() + ": required property '" + propAcc.getKey() + "' is missing.");
			}
			if (!setProperty(prop)) {
				throw new MicropsiException(10, getObjectIdentification() + ": could not set required property '" + propAcc.getKey() + "'.");
			}
			properties.removeProperty(prop);
		}
	}
	
	/** Creates (empty) collections to store property accessors. 
	 *  Exists only to make it possible to overwrite initial sizes.
	 */
	protected void createPropertyCollection() {
		requiredProperties = new ArrayList<AbstractPropertyAccessor>(3);
		optionalProperties = new ArrayList<AbstractPropertyAccessor>(10);
	}
	
	protected void addRequiredProperty(AbstractPropertyAccessor prop) {
		requiredProperties.add(prop);
	}

	protected void addOptionalProperty(AbstractPropertyAccessor prop) {
		optionalProperties.add(prop);
	}

}
