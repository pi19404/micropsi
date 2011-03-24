/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.world.mars/src/org/micropsi/comp/world/objects/MarsianAgentObject.java,v 1.1 2006/01/23 15:10:46 fuessel Exp $
 */
package org.micropsi.comp.world.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MPercept;
import org.micropsi.comp.messages.MPerceptionResp;
import org.micropsi.comp.messages.MPerceptionValue;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.ActionTypesIF;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.ConsumeResponseMessage;
import org.micropsi.comp.world.messages.TickMessage;
import org.micropsi.comp.world.messages.WorldMessage;
import org.w3c.dom.Element;

/**
 *  Object class for micropsi agents.
 *
 */
public class MarsianAgentObject extends AbstractAgentObject {
	
	public abstract class AbstractAction {
		public MAction originalAction;
		
		public AbstractAction(MAction action) {
			originalAction = action;
		}
		
		public abstract String getType();
		
		public void start() {
		}
		
		public void finish(double success) {
			MActionResponse actionResp = new MActionResponse(
					getAgentName(),
					success,
					originalAction.getTicket()
			);
			addBodyPropertyChanges(actionResp);
			addActionAnswer(actionResp);
			updateActionStatistics(originalAction.getActionType(), success > 0);
			if (currentAction == this) {
				currentAction = null;
				setState("Action", null);
			}
		}
		
		public void abort() {
			finish(0);
		}
		
		public void tick(long simStep) {
		}
		
		public void execute() {
		}
	}
	
	public class MoveAction extends AbstractAction {
		protected WorldVector effortVec;
		protected Position targetPos;
		protected Position startPos;
		protected long lastSimstep;
		
		public MoveAction(MAction action) {
			super(action);
			effortVec =
				new WorldVector(
						action.getParameter(0)
						+ ","
						+ action.getParameter(1)
						+ ","
						+ action.getParameter(2));
			startPos = new Position(getPosition());
			targetPos = new Position(getPosition());
			targetPos.add(effortVec);
			effortVec.scaleBy(0.08);
			lastSimstep = world.getSimStep();
			
		}
		
		public String getType() {
			return "moveaction";
		}
		
		public void tick(long simStep) {
			for (long i = lastSimstep;i < simStep; i++) {
				WorldVector res = world.getEffectiveMoveVector(MarsianAgentObject.this, effortVec);
				if (res.getLength() >= getPosition().getDifferenceVector(targetPos).getLength()) {
					moveTo(targetPos);
					finish(1);
					break;
				} else if (res.getLength() < 0.001) {
					moveTo(startPos);
					finish(-1);
					break;
				} else {
					moveBy(res);
				}
			}
			lastSimstep = simStep;
		}

		public void finish(double success) {
			world.getHighResolutionTimer().unsubscribe(MarsianAgentObject.this);
			super.finish(success);
			if (success > 0) {
				world.getLogger().debug(world.getSimStep()
						+ ": Agent "
						+ MarsianAgentObject.this.getObjectName()
						+ " has moved to position "
						+ targetPos);
			} else {
				world.getLogger().debug(world.getSimStep()
						+ ": Agent "
						+ MarsianAgentObject.this.getObjectName()
						+ " FAILED trying to move to position "
						+ targetPos);
			}
		}

		public void start() {
			super.start();
			setState("Action", "Move");
			rotateToAngle(effortVec.getAngle());
			world.getHighResolutionTimer().subscribe(MarsianAgentObject.this);
		}
	}
	
	public class EatAction extends AbstractAction {
		private AbstractObjectPart targetObject;
		public EatAction(MAction action, AbstractObjectPart targetObject) {
			super(action);
			this.targetObject = targetObject;
		}

		public String getType() {
			return "eataction";
		}

		public void execute() {
			double success;
			
			if (targetObject.getPosition().distance2D(getPosition()) > getVisionRange()) {
				getLogger().info("Agent " + getObjectName() + " tried to eat " + targetObject.getObjectIdentification() + " which was out of reach.");
				finish(-1);
			}
			AbstractWorldMessage m = new WorldMessage("AGENTACTION", "eat", MarsianAgentObject.this);
			m.setCollectAnswers(true);
			world.getPostOffice().send(m, targetObject);

			//look if response has been received
			List answers = m.getAnswers();
			if (answers == null) {
				success = -1;
			} else {
				Iterator it = answers.iterator();
				success = -1;
				while (it.hasNext()) {
					AbstractWorldMessage answer = (AbstractWorldMessage) it.next();
					if (answer instanceof ConsumeResponseMessage) {
						if (success < 1
								&& (((ConsumeResponseMessage) answer)
								.getEatenKiloJoules()
								>= 0
								|| ((ConsumeResponseMessage) answer)
								.getDrunkLiters()
								>= 0)) {
							success = 1;
						}
					}
					handleMessage(answer);
				}
			}

			world.getLogger().debug(
					world.getSimStep()
					+ ": Agent "
					+ getObjectName()
					+ " eats Object "
					+ targetObject.getId());
			finish(success);
		}

		public void start() {
			if (targetObject == null) {
				getLogger().warn("SteamVehicleAgent: Action '"+getType()+"' has no/invalid target object. Action fails.");
				finish(-1);
			} else {
				WorldMessage m = new WorldMessage("ACTIONMANAGEMENT", "executeaction", MarsianAgentObject.this);
				m.addParameter(this);
				world.getPostOffice().send(m, MarsianAgentObject.this, world.getSimStep() + 10);
				setState("Action", "Eat");
			}
		}
	}

	public class DrinkAction extends AbstractAction {
		private AbstractObjectPart targetObject;
		public DrinkAction(MAction action, AbstractObjectPart targetObject) {
			super(action);
			this.targetObject = targetObject;
		}

		public String getType() {
			return "drinkaction";
		}

		public void execute() {
			double success;
			if (targetObject.getPosition().distance2D(getPosition()) > getVisionRange()) {
				getLogger().info("Agent " + getObjectName() + " tried to drink object " + targetObject.getObjectIdentification() + " which was out of reach.");
				finish(-1);
			}
			
			AbstractWorldMessage m = new WorldMessage("AGENTACTION", "drink", MarsianAgentObject.this);
			m.setCollectAnswers(true);
			world.getPostOffice().send(m, targetObject);

			//look if response has been received
			List answers = m.getAnswers();
			if (answers == null) {
				success = -1;
			} else {
				Iterator it = answers.iterator();
				success = -1;
				while (it.hasNext()) {
					AbstractWorldMessage answer = (AbstractWorldMessage) it.next();
					if (answer instanceof ConsumeResponseMessage) {
						if (success < 1
								&& (((ConsumeResponseMessage) answer)
								.getEatenKiloJoules()
								>= 0
								|| ((ConsumeResponseMessage) answer)
								.getDrunkLiters()
								>= 0)) {
							success = 1;
						}
					}
					handleMessage(answer);
				}
			}

			world.getLogger().debug(
					world.getSimStep()
					+ ": Agent "
					+ getObjectName()
					+ " drinks from Object "
					+ targetObject.getId());
			finish(success);
		}

		public void start() {
			if (targetObject == null) {
				getLogger().warn("SteamVehicleAgent: Action '"+getType()+"' has no/invalid target object. Action fails.");
				finish(-1);
			} else {
				WorldMessage m = new WorldMessage("ACTIONMANAGEMENT", "executeaction", MarsianAgentObject.this);
				m.addParameter(this);
				world.getPostOffice().send(m, MarsianAgentObject.this, world.getSimStep() + 10);
				setState("Action", "Drink");
			}
		}
	}

	private class ActionStatistics implements Comparable {
		public String action;
		public long successful = 0;
		public long failed = 0;
		
		public ActionStatistics(String action) {
			this.action = action;
		}

		/* @see java.lang.Comparable#compareTo(java.lang.Object)*/
		public int compareTo(Object arg0) {
			if (arg0 instanceof ActionStatistics) {
				ActionStatistics other = (ActionStatistics) arg0;
				if (other.successful + other.failed > successful + failed) {
					return 1;
				} else if (other.successful + other.failed < successful + failed) {
					return -1;
				} else {
					if (other.action.compareToIgnoreCase(action) != 0) {
						return other.action.compareToIgnoreCase(action);
					}
				}
			}
			return hashCode() - arg0.hashCode();
		}
	}
	
	protected Map<String,ActionStatistics> actionStatistics = new HashMap<String,ActionStatistics>(10);
	
	private Position gridBasePosition;
	
	private AbstractAction currentAction = null;
	private double receivedDamage = 0;
	private double receivedFood = 0;
	private double receivedWater = 0;
	
	/**
	 * Some statistics...
	 */
	public double aggregatedWater = 0;
	/**
	 * Some statistics...
	 */
	public double aggregatedFood = 0;
	/**
	 * Some statistics...
	 */
	public double aggregatedDamage = 0;
	/**
	 * Some statistics...
	 */
	public double aggregatedHealing = 0;
	
	public MarsianAgentObject(String objectName, String objectClass, Position pos) {
		super(objectName, objectClass, pos);
		gridBasePosition = pos;
	}
	
	public MarsianAgentObject(String objectName, Position pos) {
		this(objectName, "MarsianAgent", pos);
	}

	public MarsianAgentObject(Element configData, Logger logger) throws MicropsiException {
		super(configData, logger);
		gridBasePosition = getPosition();
	}

	/**
	 * @see org.micropsi.comp.world.WorldMessageHandlerIF#handleMessage(WorldMessage)
	 */
	public void _handleMessage(AbstractWorldMessage m) {
		super._handleMessage(m);

		if (m.isContent("CONSUMERESPONSE")) {
			ConsumeResponseMessage consume = (ConsumeResponseMessage) m;
			if (consume.getEatenKiloJoules() > 0) {
				takeFood(consume.getEatenKiloJoules());
			}
			if (consume.getDrunkLiters() > 0) {
				takeWater(consume.getDrunkLiters());
			}
			if (consume.getReceivedDamage() > 0) {
				takeDamage(consume.getReceivedDamage());
			}
		}
		
		if (m.isClass("ENVIRONMENT_EVENT")) {
			if (m.isContent("REPAIR")) {
				takeDamage(0 - (Integer) m.getParameter(0));
			}
			if (m.isContent("DAMAGE")) {
				takeDamage(0 - (Integer) m.getParameter(0));
			}
		}

		if (m.isContent("hit")) {
			float damage;
			if (m.getParameter(0) != null) {
				damage = ((Integer) m.getParameter(0)).floatValue();
			} else {
				damage = 10;
			}
			takeDamage(damage);
		}
		
		if (m.isClass("TICK")) {
			if (currentAction != null) {
				currentAction.tick(((TickMessage) m).getSimStep());
			}
		}
		
		if (m.isClass("ACTIONMANAGEMENT")) {
			if (m.isContent("executeaction")) {
				if (currentAction == m.getParameter(0)) {
					currentAction.execute();
				}
			}
		}

	}

	public void handleAction(MAction action, AbstractObjectPart targetObject) {
		double success = 0;
		
		int actionType = 0;
		if(action.getActionType().equals("NOOP"))
			actionType = ActionTypesIF.ACTION_NOOP;
		else if(action.getActionType().equals("EAT"))
			actionType = ActionTypesIF.ACTION_EAT;
		else if(action.getActionType().equals("DRINK"))
			actionType = ActionTypesIF.ACTION_DRINK;
		else if(action.getActionType().equals("MOVE"))
			actionType = ActionTypesIF.ACTION_MOVE;
		else if(action.getActionType().equals("FOCUS"))
			actionType = ActionTypesIF.ACTION_FOCUS;

		switch (actionType) {
			case ActionTypesIF.ACTION_NOOP :
				break;
			case ActionTypesIF.ACTION_MOVE :
				success = -500;
				startAction(new MoveAction(action));
				break;

			case ActionTypesIF.ACTION_EAT : {
				success = -500;
				startAction(new EatAction(action, targetObject));
				break;
			}
			case ActionTypesIF.ACTION_DRINK : {
				success = -500;
				startAction(new DrinkAction(action, targetObject));
				break;
			}
//			case ActionTypesIF.ACTION_DIE :
//				break;
			case ActionTypesIF.ACTION_FOCUS : {
				if (targetObject != null) {
					rotateToAngle(targetObject.getPosition().getDifferenceVector(getPosition()).getAngle());
				}
				success = 1;
				break;
			}
			default :
				world.getLogger().info("unknown action type: " + action.getActionType());
		}

		// @todo Hack!
		if (success != -500) {
			MActionResponse actionResp = new MActionResponse(
					getAgentName(),
					success,
					action.getTicket()
			);
			addBodyPropertyChanges(actionResp);
			addActionAnswer(actionResp);
		}
	}

	/**
	 * @param action
	 */
	protected void startAction(AbstractAction action) {
		if (currentAction != null) {
			currentAction.abort();
		}
		currentAction = action;
		action.start();
	}

	protected void initObjectParameters() {
		super.initObjectParameters();
		maxDamage = 100;
		setSize(0.8, 0.8, 1.5);
		weight = 40;
	}
	
	public MPerceptionResp getPerception() {
		MPerceptionResp perception = new MPerceptionResp();
		
		Iterator it = getVisibleObjects().iterator();
		while (it.hasNext()) {
			AbstractObject object = (AbstractObject) it.next();
			
			// don't add the agent to the list of visible objects
			if(object.getId() == this.getId()) continue;
			
			MPercept percept = new MPercept("OBJECT");

			percept.addParameter("ID", Long.toString(object.getId()));

			percept.addParameter("CLASS", object.getObjectClass());

			Position p = new Position(object.getPosition());
			p.subtract(this.getPosition());
			percept.addParameter("POSITION", p.toString());
			
			perception.addPercept(percept);
		}
		return perception;
	}
	
	protected void takeDamage(double damage) {
		receivedDamage += damage;
		if (damage > 0) {
			world.getLogger().debug("Agent: " + getAgentName() + " takes damage: " + damage);
		}
		if (damage > 0) {
			aggregatedDamage += damage;
		} else {
			aggregatedHealing += -damage;
		}
	}

	protected void takeFood(double food) {
		receivedFood += food;
		aggregatedFood += food;
		if (food > 0) {
			world.getLogger().debug("Agent: " + getAgentName() + " got food: " + food);
		}
	}

	protected void takeWater(double water) {
		receivedWater += water;
		aggregatedWater += water;
		if (water > 0) {
			world.getLogger().debug("Agent: " + getAgentName() + " got water: " + water);
		}
	}
	
	protected void addBodyPropertyChanges(MActionResponse actionResp) {
		if (receivedWater != 0) {
			actionResp.addBodyPropertyChange(
				new MPerceptionValue("ABSORBED_WATER", Double.toString(receivedWater)));
			receivedWater = 0;
		}
		if (receivedFood != 0) {
			actionResp.addBodyPropertyChange(
				new MPerceptionValue("ABSORBED_FOOD", Double.toString(receivedFood)));
			receivedFood = 0;
		}
		if (receivedDamage != 0) {
			actionResp.addBodyPropertyChange(
				new MPerceptionValue("SUFFERED_DAMAGE", Double.toString(receivedDamage)));
			receivedDamage = 0;
		}
	}
	
	/* @see org.micropsi.comp.world.objects.AbstractObjectPart#setProperty(org.micropsi.comp.world.ObjectProperty)*/
	public boolean setProperty(ObjectProperty prop) {
		if (prop.getKey().equals("position")) {
			try {
				Position pos = new Position(prop.getValue());
				if (makeValidPosition(pos)) {
					prop.setComment("modified to nearest valid agent position");
				}
				prop.setValue(pos.toString());
				
			} catch (NumberFormatException e) {
				// handled in super methode.
			}
		}
		return super.setProperty(prop);
	}
	
	/**
	 * Changes position to the nearest valid agent position.
	 * 
	 * @param pos Position that should be made valid
	 * @return true, if Position has been modified
	 */
	private boolean makeValidPosition(Position pos) {
		double oldX = pos.getX();
		double oldY = pos.getY();
		pos.setX(Math.round((pos.getX() - gridBasePosition.getX()) / 10)*10 + gridBasePosition.getX());
		pos.setY(Math.round((pos.getY() - gridBasePosition.getY()) / 10)*10 + gridBasePosition.getY());
		return oldX != pos.getX() || oldY != pos.getY();
	}
	
	/* @see org.micropsi.comp.world.objects.AbstractAgentObject#insertStatisticData(org.micropsi.comp.messages.MTreeNode)*/
	public void insertStatisticDataIn(MTreeNode node) {
		super.insertStatisticDataIn(node);
		node.addChild(new MTreeNode("water / food:", aggregatedWater + " / " + aggregatedFood, null));
		node.addChild(new MTreeNode("damage / healing:", aggregatedDamage + " / " + aggregatedHealing, null));
		SortedSet<ActionStatistics> actionTypes = new TreeSet<ActionStatistics>(actionStatistics.values());
		for (Iterator it = actionTypes.iterator(); it.hasNext(); ) {
			ActionStatistics actionStat = (ActionStatistics) it.next();
			String s = actionStat.successful + " / " + actionStat.failed;
			node.addChild(new MTreeNode(actionStat.action + " successful/failed:", s, null));
		}
	}
	
	protected void updateActionStatistics(String action, boolean success) {
		if (action != null && action != "") {
			ActionStatistics actionData = actionStatistics.get(action);
			if (actionData == null) {
				actionData = new ActionStatistics(action);
				actionStatistics.put(action, actionData);
			}
			if (success) {
				actionData.successful++;
			} else {
				actionData.failed++;
			}
		}
	}
}
