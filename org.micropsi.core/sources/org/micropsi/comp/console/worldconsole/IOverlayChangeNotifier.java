/*
 * Created on 21.06.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

public interface IOverlayChangeNotifier {
	
	/**Requests complete redraw of the render target, if possible
	 */
	void redraw();
	
	/**Requests redraw of the sprecified area of the render target, if possible
	 * @param x - upper left x
	 * @param y - upper left y
	 * @param width - width
	 * @param height - height
	 */
	void redraw(int x, int y, int width, int height);
}
