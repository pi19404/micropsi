/*
 * Created on 22.06.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.coordinates.Position;

public class CoordinateGridRenderer implements IOverlayRenderer {

	protected Color gridColor = null;
	protected Color gridTextColor = null;
	protected Font gridTextFont = null;

	
	public CoordinateGridRenderer() {
		gridColor = new Color(Display.getDefault(), 80, 100, 80);
		gridTextColor = new Color(Display.getDefault(), 100, 120, 100);
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		gridTextFont = new Font (Display.getDefault(), fd);
		fd.setHeight(fd.getHeight() * 4/5);

	}

	public void init(LocalWorld world, EditSession editSession,
			IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo) {
		// don't need any of them
	}

	public void init(IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo) {
		// don't need any of them
	}
	
	public void paintOverlay(GC gc, int offsetX, int offsetY, IRenderInfo renderInfo) {
		
		gc.setForeground(gridColor);

		int minX = gc.getClipping().x - 1;
		int minY = gc.getClipping().y - 1;
		int maxX = minX + gc.getClipping().width + 2;
		int maxY = minY + gc.getClipping().height + 2;
		Position _minPos = renderInfo.getWorldPosition(minX + offsetX, maxY + offsetY);
		Position _maxPos = renderInfo.getWorldPosition(maxX + offsetX, minY + offsetY);
		
		Position minPos = new Position(Math.min(_minPos.getX(), _maxPos.getX()), Math.min(_minPos.getY(), _maxPos.getY()));
		Position maxPos = new Position(Math.max(_minPos.getX(), _maxPos.getX()), Math.max(_minPos.getY(), _maxPos.getY()));

		double startPosX = Math.ceil(minPos.getX() / 10)*10;
		for (double x = startPosX; x < maxPos.getX(); x += 10) {
			int screenX = renderInfo.getScreenX(x) - offsetX;
			gc.drawLine(screenX, minY, screenX, maxY);
		}
		double startPosY = Math.ceil(minPos.getY() / 10)*10;
		for (double y = startPosY; y < maxPos.getY(); y += 10) {
			int screenY = renderInfo.getScreenY(y) - offsetY;
			gc.drawLine(minX, screenY, maxX, screenY);
		}

		gc.setForeground(gridTextColor);
		gc.setFont(gridTextFont);

		gc.setTextAntialias(SWT.OFF);
		startPosX -= 10;
		startPosY -= 10;
		for (double x = startPosX; x < maxPos.getX() + 10; x += 10) {
			for (double y = startPosY; y < maxPos.getY() + 10; y += 10) {
				String s = (int) x + "," + (int) y;
				gc.drawText(s, renderInfo.getScreenX(x) + 1 - offsetX, renderInfo.getScreenY(y) + 1 - offsetY, true);
			}
		}
	}

	public void setScale(double scaleX, double scaleY) {
		// TODO Auto-generated method stub

	}

	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}
	
	public void dispose() {
		if (gridColor != null) {
			gridColor.dispose();
		}
		if (gridTextColor != null) {
			gridTextColor.dispose();
		}
		if (gridTextFont != null) {
			gridTextFont.dispose();
		}

	}

}
