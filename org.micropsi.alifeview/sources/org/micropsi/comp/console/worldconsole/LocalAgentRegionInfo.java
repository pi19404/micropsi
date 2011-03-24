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
import org.micropsi.comp.agent.voronoi.Region;
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
public class LocalAgentRegionInfo implements IEditSessionListener, AnswerHandlerIF {
    
    private static LocalAgentRegionInfo instance = null;
    
    private AnswerQueueIF answerQueue;
	private ConsoleFacadeIF console = null;
	private IOverlayChangeNotifier changeNotifier = null;
//	private AnswerHandlerIF questionErrorHandler = null;
    private boolean overlayEnabled;
    
    private List<Region> regions;
    
    private long lastSelected = -1;
    private String currentAgent = null;
    private boolean initialized = false;
    
    public static LocalAgentRegionInfo getInstance() {
        if(instance == null)
            instance = new LocalAgentRegionInfo();
        return instance;
    }
    
    private LocalAgentRegionInfo() {
        regions = new ArrayList<Region>();
        
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
    			    unsubscripeFromAgentRegions();
    			    clearLists();
                } else {
                    if(object.getObjectClass().equals("MouseAgent")) {
                        unsubscripeFromAgentRegions();
                        lastSelected = object.getId();
                        currentAgent = object.getObjectName();
                        subscribeForAgentRegions();
                        break;
                    }
                }  
            }
        }
        redraw();
    }

    public void subscribeForAgentRegions() {
        if(!initialized)
            initializeConsole();
        if(currentAgent != null)
            getConsole().subscribe(4000, currentAgent, "getregions", "", answerQueue);
    }
    
    public void unsubscripeFromAgentRegions() {
        if(!initialized)
            initializeConsole();
        if(currentAgent != null)
            getConsole().unsubscribe(currentAgent, "getregions", "", answerQueue);
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
        if (answer.getAnsweredQuestion().getQuestionName().equals("getregions")) {
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
        
        MTreeNode[] buffer;
        int i;
        Position currentNodePosition;
        
        // food        
        buffer = node.getChildren();
        if(buffer != null) {
	        for(i = 0; i < buffer.length; i++) {
	            currentNodePosition = getPositionFromTreeNode(buffer[i]);
	            MTreeNode explored = buffer[i].searchChild("explored");
	            
	            MTreeNode food = buffer[i].searchChild("food");
	            MTreeNode water = buffer[i].searchChild("water");
	            MTreeNode healing = buffer[i].searchChild("healing");
	            MTreeNode damage = buffer[i].searchChild("damage");
	            MTreeNode impassable = buffer[i].searchChild("impassable");
	            
	            Region region = new Region(currentNodePosition);
	            if(explored != null && Boolean.parseBoolean(explored.getValue()))
	            	region.known();
	            
	            if(food != null && Boolean.parseBoolean(food.getValue()))
	            	region.addType(Region.FOOD);
	            if(water != null && Boolean.parseBoolean(water.getValue()))
	            	region.addType(Region.WATER);
	            if(healing != null && Boolean.parseBoolean(healing.getValue()))
	            	region.addType(Region.HEALING);
	            if(damage != null && Boolean.parseBoolean(damage.getValue()))
	            	region.addType(Region.DAMAGE);
	            if(impassable != null && Boolean.parseBoolean(impassable.getValue()))
	            	region.addType(Region.IMPASSABLE);
	            
	            if(currentNodePosition != null) {
		            regions.add(region);
	            }
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
    
    private void clearLists() {
        regions.clear();
    }
    
    public Iterator<Region> getRegions() {
        return regions.iterator();
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
