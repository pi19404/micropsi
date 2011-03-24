package org.micropsi.comp.console.worldconsole;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.agent.voronoi.*;

public class RegionOverlayRenderer implements IOverlayRenderer {

    protected Color lineColor = null;
    protected Color centerColor = null;
    protected Color waterColor = null;
    protected Color foodColor = null;
    protected Color healingColor = null;
    protected Color damageColor = null;
    protected Color obstacleColor = null;
    protected Color unknownColor = null;
    protected int ovalSize = 4;
    
    private boolean initialized = false;
    
    DelaunayTriangulation dt = null;
    
    //TODO set appropiate colours
    public RegionOverlayRenderer() {
    	lineColor = new Color(Display.getDefault(), 255, 255, 255);
		centerColor = new Color(Display.getDefault(), 50, 50, 50);
		unknownColor = new Color(Display.getDefault(), 255, 50, 50);
		foodColor = new Color(Display.getDefault(), 80, 120, 80);
		waterColor = new Color(Display.getDefault(), 0, 0, 180);
		healingColor = new Color(Display.getDefault(), 0, 180, 0);
		obstacleColor = new Color(Display.getDefault(), 80, 80, 80);
		damageColor = new Color(Display.getDefault(), 180, 0, 0);
    }
    
    public void init(LocalWorld world, EditSession editSession, IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo) {
        LocalAgentRegionInfo.getInstance().setEditSession(editSession);
        LocalAgentRegionInfo.getInstance().setChangeNotifier(changeNotifier);
        initialized = true;
    }
    
    public void init(IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo) {
    	LocalAgentRegionInfo.getInstance().setChangeNotifier(changeNotifier);
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
        	Iterator<Region> it;
			it = LocalAgentRegionInfo.getInstance().getRegions();
			List<Position> regions = new ArrayList<Position>();
			while(it.hasNext()) {
				WeakReference<Region> weakConnectionReference = new WeakReference<Region>(it.next());
				Region referenced = weakConnectionReference.get();
			    if(referenced != null) {
				    Region current = referenced;
				    regions.add(new Position(current.position));
				    int screenX = renderInfo.getScreenX(current.position.getX()) - offsetX;
					int screenY = renderInfo.getScreenY(current.position.getY()) - offsetY;
					if(current.isUnknown())
						gc.setBackground(unknownColor);
					else if(current.isType(Region.FOOD))
						gc.setBackground(foodColor);
					else if(current.isType(Region.WATER))
						gc.setBackground(waterColor);
					else if(current.isType(Region.HEALING))
						gc.setBackground(healingColor);
					else if(current.isType(Region.DAMAGE))
						gc.setBackground(damageColor);
					else if(current.isType(Region.IMPASSABLE))
						gc.setBackground(obstacleColor);
					else
						gc.setBackground(centerColor);
			        gc.fillOval(screenX - adjustValue, screenY - adjustValue, ovalSize, ovalSize);
			    } else {
			        it.remove();
			    }
			}
			// construct DelaunayTriangulation
			if(regions.size() >= 3) {
				Simplex<Pnt> tri = new Simplex<Pnt>(new Pnt(-10000.0, -10000.0), new Pnt(10000.0, -10000.0), new Pnt(0.0, 10000.0));
				dt = new DelaunayTriangulation(tri);
				for(int i = 0; i < regions.size(); i++) {
					dt.delaunayPlace(new Pnt(regions.get(i)));
				}
				for (Simplex<Pnt> triangle: dt) {
			        for (Simplex<Pnt> other: dt.neighbors(triangle)) {
			            Pnt p = Pnt.circumcenter(triangle.toArray(new Pnt[0]));
			            Pnt q = Pnt.circumcenter(other.toArray(new Pnt[0]));
			            drawLine(gc, p.getPosition(), q.getPosition(), offsetX, offsetY, renderInfo);
			        }
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
    	LocalAgentRegionInfo.getInstance().setOverlayEnabled(enabled);
    }

    public void dispose() {
    	if (lineColor != null) {
    		lineColor.dispose();
    	}
        if (centerColor != null) {
			centerColor.dispose();
		}
        if (unknownColor != null) {
			unknownColor.dispose();
		}   
    }
    
    private void drawLine(GC gc, Position from, Position to, int offsetX, int offsetY, IRenderInfo renderInfo) {
    	int startScreenX = renderInfo.getScreenX(from.getX()) - offsetX;
		int startScreenY = renderInfo.getScreenY(from.getY()) - offsetY;
		int endScreenX = renderInfo.getScreenX(to.getX()) - offsetX;
		int endScreenY = renderInfo.getScreenY(to.getY()) - offsetY;
		gc.setForeground(lineColor);
		gc.drawLine(startScreenX, startScreenY, endScreenX, endScreenY);
    }
}


