/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3dviewer/sources/org/micropsi/eclipse/viewer3d/win32/NebulaSurface.java,v 1.2 2004/09/10 10:58:41 salz Exp $ 
 */
package org.micropsi.eclipse.viewer3d.win32;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class NebulaSurface extends Composite {

	static {
		
		try {
			new LibLoaderClassLoader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.loadLibrary("3dview2");
	}

	public NebulaSurface(Composite parent, String cmd) {	
		super(parent, SWT.NONE);
		
		initialize(this.handle,cmd.toCharArray());
			
		addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {	
			}

			public void controlResized(ControlEvent e) {
				Point p = getSize(); 		
				resize(p.x,p.y);
			}			
		});
		
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				shutdown();
			}
		});
	}
	
	protected native int initialize(int handle, char[] cmd);
	
	protected native int resize(int width, int height);
	
	protected native int shutdown();

}
