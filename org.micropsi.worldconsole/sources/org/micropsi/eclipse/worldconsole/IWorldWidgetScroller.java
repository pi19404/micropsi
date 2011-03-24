/*
 * Created on 31.05.2005
 *
 */

package org.micropsi.eclipse.worldconsole;

import org.eclipse.swt.graphics.Point;

/**
 * @author Matthias
 */
public interface IWorldWidgetScroller {
	void setScrollOffset(int x, int y);
	Point getScrollOffset();

}
