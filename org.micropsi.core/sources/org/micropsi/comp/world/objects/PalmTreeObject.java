/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/PalmTreeObject.java,v 1.2 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.world.objects;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.WorldMessage;
import org.w3c.dom.Element;

/**
 *  $Header $
 *  @author henning
 *
 */
public class PalmTreeObject extends PlantObject {

	protected TreeTrunkObject treeTrunk = null;
	protected CrownObject crown = null;
	private Set<BananaObject> bananas = null;
	private int bananaCount = 0;
	private int leafCount = 0;

	/**
	 * Constructor for BananaTreeObject.
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public PalmTreeObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
	}

	public PalmTreeObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		waterContent = 2;
		maxHeight = 12;
		maxWaterContent = 50;
		growRate = 0.2;
		setSize(2, 2, 8);
		weight = 500;
		leafCount = (int) Math.round(5 + Math.random()*5);
		bananaCount = (int) Math.round(3 + Math.random()*3);
	}

	protected void initObjectState() {
		super.initObjectState();
	}

	/**
	 * @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(WorldMessage)
	 */
	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);

		if (m.isContent("hit")) {
			takeDamage(10, m.getMessageContent());
			m.answer(new WorldMessage("AGENTACTION_RESPONSE", "hit", this));
		}

/*		
		if (m.getMessageClass() == "lowresolutiontick") {
			if (((this.actHeight >= 4.0) && (this.bondingObjects.size() < 1))
				|| ((this.actHeight >= 8.0) && (this.bondingObjects.size() < 3))) {

				this.bondingObjects.add(
					new BondingObject(
						(AbstractCommonObject) this,
						new BananaObject("Banana" + this.world.getNumberOfObjects(),
							"Banana",
							(Position) this.fruitPosition.get(this.bondingObjects.size() + 1)),
						5.0));

				world.getLogger().debug(
					"object"
						+ this.getObjectName()
						+ "has a new fruit:"
						+ ((BondingObject) this.bondingObjects.get(this.bondingObjects.size() - 1))
							.getOtherObject(this)
							.toString());
			}
		}
*/
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractHierarchicObject#initSubobjects()
	 */
	protected void initSubobjects() {
		super.initSubobjects();
		double size = getHeight() / 3;
		
		Position pos = new Position(getPosition());
		treeTrunk = new TreeTrunkObject(getObjectClass() + ".treetrunk", pos);
		treeTrunk.setSize(0.3, 0.3, getHeight() - size/2);
		addSubPart(treeTrunk);

		pos = new Position(getPosition());
		pos.setZ(pos.getZ() + getHeight() - size/2);
		crown = new CrownObject(getObjectClass() + ".crown", pos);
		crown.setSize(size);
		addSubPart(crown);
		
		crown.addLeaves(getObjectClass() + ".leaf", leafCount);
		
		bananas = new HashSet<BananaObject>(bananaCount);
		for (int i = 0; i < bananaCount; i++) {
			BananaObject banana = new BananaObject(getObjectName() + ".banana", "banana", crown.getRandomSubposition());
			banana.setAge((int) Math.round(Math.random()*100));
			crown.addSubPart(banana);
			bananas.add(banana);
		}
	}
}