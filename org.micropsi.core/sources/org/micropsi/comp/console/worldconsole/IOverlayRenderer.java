/*
 * Created on 14.06.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import org.eclipse.swt.graphics.GC;

public interface IOverlayRenderer {
	/**Callback to initialise and tell the IOverlayRenderer the world modell / edit session to be
	 * used for the resulting image.<br/><br/>
	 * Only one of the two init method will be called, depending on wheter world and edit session
	 * are available or not. Overlay renderers that absolutely need the world and edit session
	 * objects, but are not initialized with them, should just quietly do nothing.
	 * 
	 * @param world - access to the world model of the main renderer
	 * @param editSession - access to the edit session of the main renderer. May be null it it does not use an edit session.
	 * @param changeNotifier - can be used to request redrawing of areas of the render target
	 * @param renderInfo - coordinate transformation
	 */
	void init(LocalWorld world, EditSession editSession, IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo);

	/**Callback for initialisation.<br/><br/>
	 * Only one of the two init method will be called, depending on wheter world and edit session
	 * are available or not. Overlay renderers that absolutely need the world and edit session
	 * objects, but are not initialized with them, should just quietly do nothing.
	 * @param world - access to the world model of the main renderer
	 * @param editSession - access to the edit session of the main renderer. May be null it it does not use an edit session.
	 * @param changeNotifier - can be used to request redrawing of areas of the render target
	 * @param renderInfo - coordinate transformation
	 */
	void init(IOverlayChangeNotifier changeNotifier, IRenderInfo renderInfo);
	
	/**Should paint an area of the overlay starting at (offsetX, offsetY) of the size
	 * (gc.getClipping().width, .height) in screen coodinates on the given GC, starting at (0, 0).
	 * (Clipping will be done automatically if necessary.)
	 * @param gc - GC to paint to
	 * @param offsetX - upper left corner of the overlay area
	 * @param offsetY - upper left corner of the overlay area
	 * @param renderInfo - class for coordinate transformation
	 */
	void paintOverlay(GC gc, int offsetX, int offsetY, IRenderInfo renderInfo);
	
	/**Callback function called when the resolution (pixel per world coordinate) changes.
	 * If you cache resolution-dependant information, it should be recalculated now.
	 * @param scaleX - x pixel per world coordinate
	 * @param scaleY - y pixel per world coordinate
	 */
	void setScale(double scaleX, double scaleY);
	
	/**Callback function to tell the overlay renderer whether it is enabled or not.
	 * A disabled overlay renderer should never request redrawing.
	 * 
	 * @param enabled - the new state
	 */
	void setEnabled(boolean enabled);

	/**Tells the renderer to release all requested resources.
	 */
	void dispose();
}
