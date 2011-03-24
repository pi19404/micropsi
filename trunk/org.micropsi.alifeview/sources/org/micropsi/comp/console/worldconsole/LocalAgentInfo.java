/*
 * Created on 21.06.2005
 *
 */
package org.micropsi.comp.console.worldconsole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.console.worldconsole.model.AbstractWorldObject;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.alifeview.ViewConsole;

/**
 * @author Markus
 *
 */
public class LocalAgentInfo implements IEditSessionListener, AnswerHandlerIF {
    
    private static LocalAgentInfo instance = null;
    
    private AnswerQueueIF answerQueue;
	private ConsoleFacadeIF console = null;
	private IOverlayChangeNotifier changeNotifier = null;
//	private AnswerHandlerIF questionErrorHandler = null;
    private boolean overlayEnabled;
    
    private List<ConnectionLine> connections;
    private List<Position> foodWaypoints;
    private List<Position> waterWaypoints;
    private List<Position> healingWaypoints;
    private List<Position> obstaclePoints;
    private List<Position> damagePoints;
    
    private long lastSelected = -1;
    private String currentAgent = null;
    private boolean initialized = false;
    
    public static LocalAgentInfo getInstance() {
        if(instance == null)
            instance = new LocalAgentInfo();
        return instance;
    }
    
    private LocalAgentInfo() {
        connections = new ArrayList<ConnectionLine>();
        foodWaypoints = new ArrayList<Position>();
        waterWaypoints = new ArrayList<Position>();
        healingWaypoints = new ArrayList<Position>();
        obstaclePoints = new ArrayList<Position>();
        damagePoints = new ArrayList<Position>();
        
        initializeConsole();
    }

    private void initializeConsole() {
        if(ViewConsole.getInstance() != null) {
            console = ViewConsole.getInstance().getConsole();
            answerQueue = new SWTAwareAnswerQueue(this);
            initialized = true;
        }
    }
    
    public void setEditSession(EditSession editSession) {
        editSession.registerView(this);
    }
    
    public void onSelectionChanged(EditSession editSession, Collection changeList) {
        //System.out.println("selection changed: " + changeList.size());
        if(!changeList.isEmpty()) {
            Iterator it = changeList.iterator();
            AbstractWorldObject object;
            while(it.hasNext()) {             
    			object = (AbstractWorldObject)it.next();
    			if(object.getId() == lastSelected) {
    			    lastSelected = -1;
    			    unsubscripeFromAgentWaypoints();
    			    clearLists();
                } else {
                    if(object.getObjectClass().equals("MouseAgent")) {
                        unsubscripeFromAgentWaypoints();
                        lastSelected = object.getId();
                        currentAgent = object.getObjectName();
                        subscribeForAgentWaypoints();
                        break;
                    }
                }  
            }
        }
        redraw();
    }

    public void subscribeForAgentWaypoints() {
        if(!initialized)
            initializeConsole();
        if(currentAgent != null)
            getConsole().subscribe(100, currentAgent, "getwaypoints", "", answerQueue);
    }
    
    public void unsubscripeFromAgentWaypoints() {
        if(!initialized)
            initializeConsole();
        if(currentAgent != null)
            getConsole().unsubscribe(currentAgent, "getwaypoints", "", answerQueue);
    }
    
    /* (non-Javadoc)
     * @see org.micropsi.comp.console.worldconsole.IViewControllerListener#setDataBase(java.lang.Object)
     */
    public void setDataBase(Object arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.micropsi.comp.console.worldconsole.IViewControllerListener#setData(java.lang.Object)
     */
    public void setData(Object arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.micropsi.comp.console.AnswerHandlerIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
     */
    public void handleAnswer(AnswerIF answer) {
        if (answer.getAnsweredQuestion().getQuestionName().equals("getwaypoints")) {
            if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
				MTreeNode node = (MTreeNode) answer.getContent();
				if (node != null && node.getName() != null && node.getName().equals("rootNode")) {
					processPointListUpdate(node);
					redraw();
				}
            }
        }
    }

    private void processPointListUpdate(MTreeNode node) {
        clearLists();
        MTreeNode food = node.searchChild("food");
        MTreeNode water = node.searchChild("water");
        MTreeNode healing = node.searchChild("healing");
        MTreeNode obstacle = node.searchChild("obstacle");
        MTreeNode damage = node.searchChild("damage");
        
        MTreeNode[] buffer;
        int i;
        Position currentNodePosition;
        ConnectionLine currentLine;
        
        // food        
        buffer = food.getChildren();
        if(buffer != null) {
	        for(i = 0; i < buffer.length; i++) {
	            currentNodePosition = getPositionFromTreeNode(buffer[i]);
	            if(currentNodePosition != null) {
	                foodWaypoints.add(currentNodePosition);
	            }
	            currentLine = getConnectionFromTreeNode(buffer[i]);
	            if(currentLine != null) {
	                connections.add(currentLine);
	            }
	        }
        }
        
        // water
        buffer = water.getChildren();
        if(buffer != null) {
	        for(i = 0; i < buffer.length; i++) {
	            currentNodePosition = getPositionFromTreeNode(buffer[i]);
	            if(currentNodePosition != null)
	                waterWaypoints.add(currentNodePosition);
	            currentLine = getConnectionFromTreeNode(buffer[i]);
	            if(currentLine != null)
	                connections.add(currentLine);
	        }
        }
    
        // healing
        buffer = healing.getChildren();
        if(buffer != null) {
	        for(i = 0; i < buffer.length; i++) {
	            currentNodePosition = getPositionFromTreeNode(buffer[i]);
	            if(currentNodePosition != null)
	                healingWaypoints.add(currentNodePosition);
	            currentLine = getConnectionFromTreeNode(buffer[i]);
	            if(currentLine != null)
	                connections.add(currentLine);
	        }
        }
        
        // obstacles
        buffer = obstacle.getChildren();
        if(buffer != null) {
	        for(i = 0; i < buffer.length; i++) {
	            currentNodePosition = getPositionFromTreeNode(buffer[i]);
	            if(currentNodePosition != null)
	                obstaclePoints.add(currentNodePosition);
	        }
        }
        
        // damage
        buffer = damage.getChildren();
        if(buffer != null) {
	        for(i = 0; i < buffer.length; i++) {
	            currentNodePosition = getPositionFromTreeNode(buffer[i]);
	            if(currentNodePosition != null)
	                damagePoints.add(currentNodePosition);
	        }
        }
    }

    private Position getPositionFromTreeNode(MTreeNode node) {
        MTreeNode x = node.searchChild("x");
        MTreeNode y = node.searchChild("y");
        
        if(x != null && y != null)
            return new Position(Double.parseDouble(x.getValue()), Double.parseDouble(y.getValue()));
        else
            return null;
    }
    
    private ConnectionLine getConnectionFromTreeNode(MTreeNode node) {
        MTreeNode leadsTo = node.searchChild("leadsTo");
        Position start = getPositionFromTreeNode(node);
        if(leadsTo != null && start != null) {
            MTreeNode x = leadsTo.searchChild("x");
            MTreeNode y = leadsTo.searchChild("y");
            if(x != null && y != null) {
                Position end = new Position(Double.parseDouble(x.getValue()), Double.parseDouble(y.getValue()));
                return new ConnectionLine(start, end);
            }
        }
        
        return null;
    }
    
    private void clearLists() {
        connections.clear();
        foodWaypoints.clear();
        waterWaypoints.clear();
        healingWaypoints.clear();
        obstaclePoints.clear();
        damagePoints.clear();
    }
    
    public Iterator getConnections() {
        return connections.iterator();
    }
    
    public Iterator getFoodPoints() {
        return foodWaypoints.iterator();
    }
    
    public Iterator getWaterPoints() {
        return waterWaypoints.iterator();
    }
    
    public Iterator getHealingPoints() {
        return healingWaypoints.iterator();
    }
    
    public Iterator getObstaclePoints() {
        return obstaclePoints.iterator();
    }
    
    public Iterator getDamagePoints() {
        return damagePoints.iterator();
    }
    
    /**
     * @return Returns the console.
     */
    public ConsoleFacadeIF getConsole() {
        return console;
    }
    
    /**
     * @param changeNotifier The changeNotifier to set.
     */
    public void setChangeNotifier(IOverlayChangeNotifier changeNotifier) {
        this.changeNotifier = changeNotifier;
    }
    
    /**
     * @param overlayEnabled The overlayEnabled to set.
     */
    public void setOverlayEnabled(boolean overlayEnabled) {
        this.overlayEnabled = overlayEnabled;
    }
    
    private void redraw() {
        if(overlayEnabled)
            changeNotifier.redraw();
    }
}
