package org.micropsi.comp.console.worldconsole;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.micropsi.comp.agent.RandomGenerator;
import org.micropsi.comp.agent.voronoi.*;

public class HierarchyOverlayRenderer implements IOverlayRenderer {

    protected int ovalSize = 8;
    
    private boolean initialized = false;
    
    //TODO set appropiate colours
    public HierarchyOverlayRenderer() {
    }
    
    public void init(LocalWorld world, EditSession editSession, IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo) {
    	LocalAgentRegionHierarchyInfo.getInstance().setEditSession(editSession);
    	LocalAgentRegionHierarchyInfo.getInstance().setChangeNotifier(changeNotifier);
        initialized = true;
    }
    
    public void init(IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo) {
    	LocalAgentRegionHierarchyInfo.getInstance().setChangeNotifier(changeNotifier);
    	initialized = false;
    }

    public void paintOverlay(GC gc, int offsetX, int offsetY, IRenderInfo renderInfo) {
    	if(!initialized)
    		return;
        try {
        	// these are for later optimization
        	/*
	        int minX = gc.getClipping().x - 1;
			int minY = gc.getClipping().y - 1;
			int maxX = minX + gc.getClipping().width + 2;
			int maxY = minY + gc.getClipping().height + 2;
			
			Position minPos = renderInfo.getWorldPosition(minX + offsetX, maxY + offsetY);
			Position maxPos = renderInfo.getWorldPosition(maxX + offsetX, minY + offsetY);
	        */
			
			int adjustValue = ovalSize / 2;
			
			//TODO draw Voronoi-Diagram
        	Iterator<ArrayList<Region>> it;
			it = LocalAgentRegionHierarchyInfo.getInstance().getRegions();
			while(it.hasNext()) {
				WeakReference<ArrayList<Region>> weakConnectionReference = new WeakReference<ArrayList<Region>>(it.next());
				ArrayList<Region> referenced = weakConnectionReference.get();
			    if(referenced != null) {
			    	ArrayList<Region> current = referenced;
			    	Color random = new Color(Display.getDefault(), Math.abs(RandomGenerator.generator.nextInt() % 256), 
							   Math.abs(RandomGenerator.generator.nextInt() % 256), 
							   Math.abs(RandomGenerator.generator.nextInt() % 256));
			    	gc.setBackground(random);
			    	for(int i = 0; i < current.size(); i++) {
			    		Region region = current.get(i);
					    int screenX = renderInfo.getScreenX(region.position.getX()) - offsetX;
						int screenY = renderInfo.getScreenY(region.position.getY()) - offsetY;

						gc.fillOval(screenX - adjustValue, screenY - adjustValue, ovalSize, ovalSize);
			    	}
			    	random.dispose();
			    } else {
			        it.remove();
			    }
			}
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setScale(double scaleX, double scaleY) {
        // TODO Auto-generated method stub
        
    }

    public void setEnabled(boolean enabled) {
    	LocalAgentRegionHierarchyInfo.getInstance().setOverlayEnabled(enabled);
    }

    public void dispose() {  
    }
}


