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
public class LocalAgentRegionHierarchyInfo implements IEditSessionListener, AnswerHandlerIF {
    
    private static LocalAgentRegionHierarchyInfo instance = null;
    
    private AnswerQueueIF answerQueue;
	private ConsoleFacadeIF console = null;
	private IOverlayChangeNotifier changeNotifier = null;
    private boolean overlayEnabled;
    
    private List<ArrayList<Region>> regions;
    
    private long lastSelected = -1;
    private String currentAgent = null;
    private boolean initialized = false;
    
    public static LocalAgentRegionHierarchyInfo getInstance() {
        if(instance == null)
            instance = new LocalAgentRegionHierarchyInfo();
        return instance;
    }
    
    private LocalAgentRegionHierarchyInfo() {
        regions = new ArrayList<ArrayList<Region>>();
        
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
    			    unsubscripeFromAgentHierarchy();
    			    clearLists();
                } else {
                    if(object.getObjectClass().equals("MouseAgent")) {
                        unsubscripeFromAgentHierarchy();
                        lastSelected = object.getId();
                        currentAgent = object.getObjectName();
                        subscribeForAgentHierarchy();
                        break;
                    }
                }  
            }
        }
        redraw();
    }

    public void subscribeForAgentHierarchy() {
        if(!initialized)
            initializeConsole();
        if(currentAgent != null)
            getConsole().subscribe(4000, currentAgent, "gethierarchy", "", answerQueue);
    }
    
    public void unsubscripeFromAgentHierarchy() {
        if(!initialized)
            initializeConsole();
        if(currentAgent != null)
            getConsole().unsubscribe(currentAgent, "gethierarchy", "", answerQueue);
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
        if (answer.getAnsweredQuestion().getQuestionName().equals("gethierarchy")) {
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
       
        buffer = node.getChildren();
        if(buffer != null) {
	        for(int i = 0; i < buffer.length; i++) {
	            processTree(buffer[i]);
	        }
        }
    }
    
    private void processTree(MTreeNode node) {
    	MTreeNode[] buffer;
    	Position currentNodePosition;
    	ArrayList<Region> hierarchyNode = new ArrayList<Region>();
    	
    	buffer = node.getChildren();
        if(buffer != null) {
        	if(buffer[0].getName().startsWith("region")) {
		        for(int i = 0; i < buffer.length; i++) {
		            currentNodePosition = getPositionFromTreeNode(buffer[i]);
	
		            Region region = new Region(currentNodePosition);
	
		            if(currentNodePosition != null) {
		            	hierarchyNode.add(region);
		            }
		        }
		        regions.add(hierarchyNode);
        	} else if(buffer[0].getName().startsWith("hierarchy")) {
        		for(int i = 0; i < buffer.length; i++) {
        			processTree(buffer[i]);
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
    
    public Iterator<ArrayList<Region>> getRegions() {
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
