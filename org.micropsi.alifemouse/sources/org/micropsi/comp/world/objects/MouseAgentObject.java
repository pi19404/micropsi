/*
 * Created on 09.04.2005
 *
 */
package org.micropsi.comp.world.objects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.Functions;
import org.micropsi.comp.agent.MouseAgentManager;
import org.micropsi.comp.agent.RandomGenerator;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MPercept;
import org.micropsi.comp.messages.MPerceptionResp;
import org.micropsi.comp.messages.MPerceptionValue;
import org.micropsi.comp.world.World;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.messages.WorldMessage;

/**
 * @author Markus
 *
 */
public class MouseAgentObject extends SteamVehicleAgentObject {
    private int obstacle = 0;
    private int poison = 0;
    private int food = 0;
    private int water = 0;
    private int healing = 0;
    private int collision = 0;
    
    private Position lastMovedPosition = null;
    
    int smiledAt = 0;
    int smiled = 0;
    private int smilerRGB[];
    int gotBitten = 0;
    private int biterRGB[];
    
    private List<int[]> smilerList = new ArrayList<int[]>();
    private List<int[]> biterList = new ArrayList<int[]>();
    
    private WorldVector orientationVector = new WorldVector(0.0, 1.0, 0.0);
    private double speedLimit = 1;
	private double wheelDistance = 1;
    
    public MouseAgentObject(String objectName, String objectClass, Position pos) {
        super(objectName, objectClass, new Position(45.0, 35.0, 0.0));
    }
    
    public MouseAgentObject(String objectName, Position pos) {
		this(objectName, "MouseAgent", pos);
	}
    
    // set visionRange
	protected void initObjectParameters() {
		super.initObjectParameters();
		visionRange = ConstantValues.PERCEPTION_RANGE;
		smilerRGB = new int[3];
		biterRGB = new int[3];
		for(int i = 0; i < 3; i++) {
	        smilerRGB[i] = 0;
	        biterRGB[i] = 0;
	    }
	}
	
	
	public void init(World world) {
		super.init(world);
		
		try {
	        Position tempPosition = MouseAgentManager.getInstance().getCurrentPosition();
	        
	        if(tempPosition != null && world.getGroundType(tempPosition).isAgentAllowed())
	        	position = tempPosition;
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public MPerceptionResp getPerception() {
		MPerceptionResp perception = new MPerceptionResp();
		
		Iterator it = getVisibleObjects().iterator();
		while (it.hasNext()) {
		    AbstractObject object = (AbstractObject) it.next();
		    if(object.getId() != this.getId() && object.getPosition().distance2D(this.position) < ConstantValues.PERCEPTION_RANGE) {
				MPercept percept = new MPercept("OBJECT");
				percept.addParameter("ID", Long.toString(object.getId()));
				percept.addParameter("CLASS", object.getObjectClass());
	
				Position p = new Position(object.getPosition());
				/*
				p.subtract(this.getPosition());
				WorldVector temp = new WorldVector(p.getX(), p.getY(), p.getZ());
				double relativeAngle = Functions.getAngle(orientationVector);
				temp.rotate(relativeAngle);
				p = new Position(temp.getX(), temp.getY(), temp.getZ());
				*/
				percept.addParameter("POSITION", p.toString());
				
				percept.addParameter("AGENTPOSITION", this.position.toString());
				percept.addParameter("AGENTID", Long.toString(this.id));
				
				perception.addPercept(percept);
		    }
		}
		return perception;
	}
	
	protected void addBodyPropertyChanges(MActionResponse actionResp) {
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("XPOSITION", Double.toString(this.getPosition().getX())));
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("YPOSITION", Double.toString(this.getPosition().getY())));
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("ORIENTATION", Double.toString(this.getOrientationAngle() / 360.0)));
		actionResp.addBodyPropertyChange(
				new MPerceptionValue("OBSTACLE", Integer.toString(obstacle)));
		actionResp.addBodyPropertyChange(
				new MPerceptionValue("POISON", Integer.toString(poison)));
		actionResp.addBodyPropertyChange(
				new MPerceptionValue("FOOD", Integer.toString(food)));
		actionResp.addBodyPropertyChange(
				new MPerceptionValue("WATER", Integer.toString(water)));
		actionResp.addBodyPropertyChange(
				new MPerceptionValue("HEALING", Integer.toString(healing)));
		actionResp.addBodyPropertyChange(
				new MPerceptionValue("COLLISION", Integer.toString(collision)));
		
		resetBodyProperties();
		if(!smilerList.isEmpty()) {
		    for(int i = 0; i < smilerList.size(); i++) {
		        if(smilerList.get(i) != null) {
		            smiledAt = 1;
		            smilerRGB = (int[])smilerList.get(i);
		            smilerList.remove(i);
		            break;
		        }
		    }	    
		} else {
			smiledAt = 0;
		}
		
		if(!biterList.isEmpty()) {
		    for(int i = 0; i < biterList.size(); i++) {
		        if(biterList.get(i) != null) {
		            gotBitten = 1;
		            biterRGB = (int[])biterList.get(i);
		            biterList.remove(i);
		            break;
		        }
		    }	    
		} else {
			gotBitten = 0;
		}
		
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("SMILEDAT", Integer.toString(smiledAt)));
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("SMILERRED", Integer.toString(smilerRGB[0])));
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("SMILERGREEN", Integer.toString(smilerRGB[1])));
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("SMILERBLUE", Integer.toString(smilerRGB[2])));
		
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("GOTBITTEN", Integer.toString(gotBitten)));
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("BITERRED", Integer.toString(biterRGB[0])));
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("BITERGREEN", Integer.toString(biterRGB[1])));
		actionResp.addBodyPropertyChange(
		        new MPerceptionValue("BITERBLUE", Integer.toString(biterRGB[2])));
		
		actionResp.addBodyPropertyChange(
				new MPerceptionValue("SMILED", Integer.toString(smiled)));
	}
	
	public void handleAction(MAction action, AbstractObjectPart targetObject) {
	    super.handleAction(action, targetObject);
	    smiled = 0;
	    if (action.getActionType().equals("WALK")) {
	        double direction = new Double(action.getParameter(0)).doubleValue();
	        double forward = new Double(action.getParameter(1)).doubleValue();
	        double success = 0;
	        
	        WorldVector newOrientationVector = new WorldVector(0.0, 1.0, 0.0);
	        newOrientationVector.rotate(direction * 360);
	        
	        orientationVector = newOrientationVector;
	        
	        WorldVector effortVec = new WorldVector(orientationVector.getX(),orientationVector.getY(),orientationVector.getZ());
	        effortVec.scaleBy(forward);
	        
	        if(!world.getGroundType(getPosition()).isAgentAllowed())
	        	System.err.println("agent is on invalid position");
	        
	        // sensor infos
	        Position testPosition = new Position(this.position);
	        testPosition.add(effortVec);
	        if(world.getGroundType(testPosition).isAgentAllowed())
	            obstacle = 0;
	        else
	            obstacle = 1;
	        
	        if(world.getGroundType(testPosition).getName().equals("swamp"))
	            poison = 1;
	        else
	            poison = 0;
	        
	        WorldVector moveVec = world.getEffectiveMoveVector(this, effortVec);
			if (Math.abs(effortVec.getLength() - moveVec.getLength()) < 0.001) {
				// condition temporary: agent should for now only get to discreet positions
				moveBy(effortVec); // should be moveVec later
				success = 1.0;
			} else {
				success = -1.0;
			}
			
			if(world.getGroundType(testPosition).getName().equals("grass"))
	            food = 1;
	        else
	            food = 0;
			if(world.getGroundType(testPosition).getName().equals("shallowwater"))
	            water = 1;
	        else
	            water = 0;
			if(world.getGroundType(testPosition).getName().equals("darkgrass"))
	            healing = 1;
	        else
	            healing = 0;
			
	        MActionResponse actionResp = new MActionResponse(getAgentName(), success, action.getTicket());
			addBodyPropertyChanges(actionResp);		
			addActionAnswer(actionResp); 
			return;
	    }
	    
	    double success = -1;
	    if (action.getActionType().equals("MOVEBY")) {
	        double speedLeftWheel = Double.parseDouble(action.getParameter(0));
			double speedRightWheel = Double.parseDouble(action.getParameter(1));
			
			if (speedLeftWheel + speedRightWheel > 2*speedLimit) { // speed limit ;-)
				double fak = 2*speedLimit / (speedLeftWheel + speedRightWheel);
				speedLeftWheel *= fak;
				speedRightWheel *= fak;
			}
			moveAStep(speedLeftWheel, speedRightWheel);
			/*
			if(lastMovedPosition != null && collision == 0) {
				if(obstacle == 0 && this.position.equals(lastMovedPosition) && !(speedLeftWheel == 0 && speedRightWheel == 0) && (speedLeftWheel == speedRightWheel)) {
					obstacle = 1;
					//System.err.println("had to readjust obstacle-value");
				}
			}
			*/
			lastMovedPosition = this.position;
			success = 1;	  
			
			MActionResponse actionResp = new MActionResponse(
					getAgentName(),
					success,
					action.getTicket()				
			);
			addBodyPropertyChanges(actionResp);
			
			addActionAnswer(actionResp);
	    }
	    if(action.getActionType().equals("SMILE")) {
	    	//System.out.println("try smiling");
	        AbstractObjectPart targetAgent = world.getObjectPart(action.getTargetObject());
	        /*
	        if(targetAgent == null) {
	        	System.out.println("agent has different ID");
	        } else {
	        	System.out.println(targetAgent.objectClass + targetAgent.id);
	        }
	        */
	        AbstractWorldMessage m = new WorldMessage("AGENTINTERACTION", "SMILE", this);
	        world.getPostOffice().send(m, targetAgent);
	        smiled = 1;
	    }
	    if(action.getActionType().equals("BITE")) {
	        AbstractObjectPart targetAgent = world.getObjectPart(action.getTargetObject());
	        AbstractWorldMessage m = new WorldMessage("AGENTINTERACTION", "BITE", this);
	        world.getPostOffice().send(m, targetAgent);
	    }
	}
	
	public void _handleMessage(AbstractWorldMessage message) {
		//System.out.println("got message " + message.getMessageClass());
	    super._handleMessage(message);
	    if (message.isClass("AGENTINTERACTION")) {
	    	//System.out.println("agents interacted");
	        if (message.isContent("SMILE")) {
	          message.answer(new WorldMessage("ACTIONRESPONSE", "SMILE_RECEIVED", this));
	          smilerList.add(Functions.getRGB(message.getSender().getId()));
	          //System.out.println("received smile");
	          /*
	          smiledAt = 1;
	          smilerRGB = Functions.getRGB(message.getSender().getId());
	          */
	        } else if (message.isContent("BITE")){
	            message.answer(new WorldMessage("ACTIONRESPONSE", "GOT_BITTEN", this));
	            biterList.add(Functions.getRGB(message.getSender().getId()));
	            /*
	            gotBitten = 1;
		        biterRGB = Functions.getRGB(message.getSender().getId());
		        */
	        }
	    }
	    
	    if(message.isClass("ACTIONRESPONSE")) {
	        if(message.isContent("SMILE_RECEIVED")) {
	            
	        } else if(message.isContent("GOT_BITTEN")) {
	            
	        }
	    }
	}
	
	protected void moveAStep(double speedLeftWheel, double speedRightWheel) {
		double rotationAngle = Math.toDegrees(speedLeftWheel - speedRightWheel) / wheelDistance;
		WorldVector effortVec = new WorldVector(0, (speedRightWheel + speedLeftWheel) / 2, 0);
		effortVec.rotate(getOrientationAngle() + rotationAngle / 2);
		
		Position testPosition = new Position(this.position);
        testPosition.add(effortVec);
        
		WorldVector resultVector =  world.getEffectiveMoveVector(this, effortVec);
            
        boolean collision = false;
        this.collision = 0;
        for (AbstractObject obj : world.getObjectsByPosition(testPosition, this.getSize())) {
           if (obj.getId() != this.getId() && obj instanceof AgentObjectIF) { 
             collision = true; // oder sogar das Objekt merken
             this.collision = 1;
             if(this.getPosition().distance2D(obj.getPosition()) < (this.getSize()/* * 2*/)) {
            	 WorldVector orientation = new WorldVector(this.getPosition().getX() - obj.getPosition().getX(),  this.getPosition().getY() - obj.getPosition().getY());
            	 if(orientation.getX() == 0 && orientation.getY() == 0) {
            		 orientation = new WorldVector(1.0, 0.0, 0.0);
            		 orientation.rotate(RandomGenerator.generator.nextDouble() * 360.0);
            	 }
            		 
            	 orientation.setLength(this.getSize()/* * 2*/ - this.getPosition().distance2D(obj.getPosition()) + 0.01);
            	 Position newPos = new Position(this.getPosition());
            	 newPos.add(orientation);
            	 
            	 boolean newPosOK = true;
            	 for (AbstractObject agentObj : world.getObjectsByPosition(newPos, this.getSize())) {
                     if (agentObj.getId() != this.getId() && agentObj instanceof AgentObjectIF) {
                    	 newPosOK = false;
                    	 break;
                     }
            	 }
            	 
            	 if(newPosOK)
            		 this.setPosition(newPos);
             }
             break;
           }
        }
        
        // do not move
        if (collision) {
        	return;
        }
        
        
        if(/*world.getGroundType(testPosition).isAgentAllowed() || */Math.abs(effortVec.getLength() - resultVector.getLength()) < 0.02)
            obstacle = 0;
        else
            obstacle = 1;
        if(world.getGroundType(testPosition).getName().equals("swamp"))
            poison = 1;
        else
            poison = 0;
		moveBy(resultVector);
		rotateBy(rotationAngle);
		if(world.getGroundType(testPosition).getName().equals("grass"))
            food = 1;
        else
            food = 0;
		if(world.getGroundType(testPosition).getName().equals("shallowwater"))
            water = 1;
        else
            water = 0;
		if(world.getGroundType(testPosition).getName().equals("darkgrass"))
            healing = 1;
        else
            healing = 0;
	}
	
	/*
	public double getOrientationAngle() {
		return Functions.getAngle(orientationVector);
	}
	*/
	/*
	private void rotateRadians(double radians) {
		double oldx = orientationVector.getX();
		double oldy = orientationVector.getY();
		double x = oldx*Math.cos(radians) + oldy*Math.sin(radians);
		double y = -oldx*Math.sin(radians) + oldy*Math.cos(radians);
		orientationVector.set(x, y, orientationVector.getZ());
	}
	*/
	
	private void resetBodyProperties() {
	    smiledAt = 0;
	    smilerRGB = new int[3];
	    gotBitten = 0;
	    biterRGB = new int[3];
	   
	    for(int i = 0; i < 3; i++) {
	        smilerRGB[i] = 0;
	        biterRGB[i] = 0;
	    }
	}
}