/*
 * Created on 21.06.2005
 *
 */
package org.micropsi.comp.console.worldconsole;

import java.util.Iterator;

import java.lang.ref.WeakReference;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.coordinates.Position;

/**
 * @author Markus
 *
 */
public class WaypointOverlayRenderer implements IOverlayRenderer {

    protected Color lineColor = null;
    protected Color foodColor = null;
    protected Color waterColor = null;
    protected Color healingColor = null;
    protected Color obstacleColor = null;
    protected Color damageColor = null;
    protected int ovalSize = 4;
    
    private boolean initialized = false;
    
    //TODO set appropiate colours
    public WaypointOverlayRenderer() {
        lineColor = new Color(Display.getDefault(), 255, 255, 255);
        foodColor = new Color(Display.getDefault(), 80, 100, 80);
		waterColor = new Color(Display.getDefault(), 0, 0, 255);
		healingColor = new Color(Display.getDefault(), 0, 255, 0);
		obstacleColor = new Color(Display.getDefault(), 50, 50, 50);
		damageColor = new Color(Display.getDefault(), 255, 0, 0);
    }
    
    public void init(LocalWorld world, EditSession editSession, IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo) {
        LocalAgentInfo.getInstance().setEditSession(editSession);
        LocalAgentInfo.getInstance().setChangeNotifier(changeNotifier);
        initialized = true;
    }
    
    public void init(IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo) {
    	LocalAgentInfo.getInstance().setChangeNotifier(changeNotifier);
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
        	
        	Iterator it;
			
			int adjustValue = ovalSize / 2;
			
			// draw lines between wayPoints
			it = LocalAgentInfo.getInstance().getConnections();
			while(it.hasNext()) {
				WeakReference<ConnectionLine> weakConnectionReference = new WeakReference<ConnectionLine>((ConnectionLine)it.next());
				ConnectionLine referenced = weakConnectionReference.get();
			    if(referenced != null) {
				    ConnectionLine current = referenced;
				    int startScreenX = renderInfo.getScreenX(current.getStart().getX()) - offsetX;
					int startScreenY = renderInfo.getScreenY(current.getStart().getY()) - offsetY;
					int endScreenX = renderInfo.getScreenX(current.getEnd().getX()) - offsetX;
					int endScreenY = renderInfo.getScreenY(current.getEnd().getY()) - offsetY;
					gc.setForeground(lineColor);
					gc.drawLine(startScreenX, startScreenY, endScreenX, endScreenY);
			    } else {
			        it.remove();
			    }
			}
			
			// draw obstacles
			it = LocalAgentInfo.getInstance().getObstaclePoints();
			while(it.hasNext()) {
				WeakReference<Position> weakObstacleReference = new WeakReference<Position>((Position)it.next());
				Position referenced = weakObstacleReference.get();
			    if(referenced != null) {
				    Position current = referenced;
				    int screenX = renderInfo.getScreenX(current.getX()) - offsetX;
					int screenY = renderInfo.getScreenY(current.getY()) - offsetY;
			        gc.setBackground(obstacleColor);
			        gc.fillOval(screenX - adjustValue, screenY - adjustValue, ovalSize, ovalSize);
			    } else {
			        it.remove();
			    }
			}
			
			// draw damage spots
			it = LocalAgentInfo.getInstance().getDamagePoints();
			while(it.hasNext()) {
				WeakReference<Position> weakDamageReference = new WeakReference<Position>((Position)it.next());
				Position referenced = weakDamageReference.get();
			    if(referenced != null) {
				    Position current = referenced;
				    int screenX = renderInfo.getScreenX(current.getX()) - offsetX;
					int screenY = renderInfo.getScreenY(current.getY()) - offsetY;
			        gc.setBackground(damageColor);
			        gc.fillOval(screenX - adjustValue, screenY - adjustValue, ovalSize, ovalSize);
			    } else {
			        it.remove();
			    }
			}
			
			// draw food spots
			it = LocalAgentInfo.getInstance().getFoodPoints();
			while(it.hasNext()) {
				WeakReference<Position> weakFoodReference = new WeakReference<Position>((Position)it.next());
				Position referenced = weakFoodReference.get();
			    if(referenced != null) {
				    Position current = referenced;
				    int screenX = renderInfo.getScreenX(current.getX()) - offsetX;
					int screenY = renderInfo.getScreenY(current.getY()) - offsetY;
			        gc.setBackground(foodColor);
			        gc.fillOval(screenX - adjustValue, screenY - adjustValue, ovalSize, ovalSize);
			    } else {
			        it.remove();
			    }
			}
			
			// draw water spots
			it = LocalAgentInfo.getInstance().getWaterPoints();
			while(it.hasNext()) {
				WeakReference<Position> weakWaterReference = new WeakReference<Position>((Position)it.next());
				Position referenced = weakWaterReference.get();
			    if(referenced != null) {
				    Position current = referenced;
				    int screenX = renderInfo.getScreenX(current.getX()) - offsetX;
					int screenY = renderInfo.getScreenY(current.getY()) - offsetY;
			        gc.setBackground(waterColor);
			        gc.fillOval(screenX - adjustValue, screenY - adjustValue, ovalSize, ovalSize);
			    } else {
			        it.remove();
			    }
			}
			
			// draw healing spots
			it = LocalAgentInfo.getInstance().getHealingPoints();
			while(it.hasNext()) {
				WeakReference<Position> weakHealingReference = new WeakReference<Position>((Position)it.next());
			    Position referenced = weakHealingReference.get();
			    if(referenced != null) {
				    Position current = referenced;
				    int screenX = renderInfo.getScreenX(current.getX()) - offsetX;
					int screenY = renderInfo.getScreenY(current.getY()) - offsetY;
			        gc.setBackground(healingColor);
			        gc.fillOval(screenX - adjustValue, screenY - adjustValue, ovalSize, ovalSize);
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
        LocalAgentInfo.getInstance().setOverlayEnabled(enabled);
    }

    public void dispose() {
    	if (lineColor != null) {
    		lineColor.dispose();
    	}
        if (foodColor != null) {
			foodColor.dispose();
		}
        if (waterColor != null) {
			waterColor.dispose();
		}
        if (healingColor != null) {
			healingColor.dispose();
		}
        if (obstacleColor != null) {
			obstacleColor.dispose();
		}
        if (damageColor != null) {
			damageColor.dispose();
		}      
    }

}

