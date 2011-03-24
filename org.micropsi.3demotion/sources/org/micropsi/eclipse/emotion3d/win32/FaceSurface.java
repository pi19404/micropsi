/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3demotion/sources/org/micropsi/eclipse/emotion3d/win32/FaceSurface.java,v 1.2 2005/11/15 16:40:06 vuine Exp $ 
 */
package org.micropsi.eclipse.emotion3d.win32;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class FaceSurface extends Composite {

	static {
		
		try {
			new LibLoaderClassLoader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.loadLibrary("3demotion");
	}

	public FaceSurface(Composite parent, String cmd) {	
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
	
	public native int updatebones(char[] names, char[] values);
	
	protected native int shutdown();

}
