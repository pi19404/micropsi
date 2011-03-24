/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/widgets/DiagramWidget.java,v 1.10 2005/09/15 22:57:37 vuine Exp $ 
 */
package org.micropsi.eclipse.console.widgets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.micropsi.common.utils.Truncate;
import org.micropsi.eclipse.console.IParameterMonitor;


public class DiagramWidget extends Canvas {

	private class DiagramPaintListener implements PaintListener {

		private final Color bgColor = new Color(null,0,0,0);
		private final Color lnColor = new Color(null,160,160,160);

		private double maxValue = 1;
		private double minValue = 1;
		
		private int zerolevel = 10;
		
		boolean normalize = false;
		boolean force = false;
		
		private Image screenImage;
		private GC gc;

		
		public DiagramPaintListener() {
			addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					bgColor.dispose();
					lnColor.dispose();
					if(screenImage != null) {
						gc.dispose();
						screenImage.dispose();
					}
				}
			});
		}
				
		private int scale(int height, double val, double normalize) {
			if(val >= 0) {
				double onepixel = (height-zerolevel) / (maxValue + (maxValue*0.1));			 
				return (height-zerolevel) - (int)Math.round((val * onepixel * normalize));
			} else {
				double onepixel = zerolevel / (minValue + (minValue*0.1));			 
				return (height-zerolevel) + (int)Math.round((val * onepixel * normalize));				
			}	
		}
				
		public void paintControl(PaintEvent evt) {			
			
			Point size = DiagramWidget.this.getSize();
			
			if(screenImage == null) {
				screenImage = new Image(evt.display,size.x,size.y);
				gc = new GC(screenImage);
			}	
			
			boolean extremumChange = false;
			Iterator monitors = parameterMonitors.values().iterator();
			while(monitors.hasNext()) {
				Monitor mon = (Monitor)monitors.next();
				double val = mon.getCurrentValue();
				if(val > maxValue) {
					maxValue = val;
					extremumChange = true;
				} 
				if(val < minValue) {
					minValue = val;
					extremumChange = true;
				}
			}
			
			if(normalize && (extremumChange || force)) {
				monitors = parameterMonitors.values().iterator();
				while(monitors.hasNext()) {
					Monitor mon = (Monitor)monitors.next();
					double val = mon.getCurrentValue();
					mon.setNormFactor(maxValue / val);
				}
				force = false;
			}
			
			zerolevel = (int)Math.round(size.y - ((maxValue / (maxValue - minValue)) * size.y));
			
			gc.setBackground(bgColor);
			gc.fillRectangle(0, 0, size.x, size.y);

			gc.setForeground(lnColor);
			gc.drawLine(0, size.y-zerolevel, size.x, size.y-zerolevel);
			
			gc.drawLine(5,0,5,size.y);
			
			gc.drawLine(4,scale(size.y,maxValue,1),6,scale(size.y,maxValue,1));
			gc.drawLine(4,scale(size.y,minValue,1),6,scale(size.y,minValue,1));
			
			if(!normalize) {
				gc.drawText(Truncate.trDouble(maxValue,1), 8, scale(size.y,maxValue,1)-3);
				gc.drawText(Truncate.trDouble(minValue,1), 8, scale(size.y,minValue,1)-3);
			}
			
			if(!realtime) {
				String txt = Long.toString(time);
				gc.drawText(txt,(size.x-10)-gc.stringExtent(txt).x,(size.y-zerolevel)-25);
			}
			
			int st = (size.x / Monitor.BACKLIST_LENGTH)+1;

			for(int i=0;i<Monitor.BACKLIST_LENGTH-1;i++) {
				gc.drawLine(
					size.x-(i*st), 
					size.y-4, 
					size.x-(i*st), 
					size.y
				);
			}
			
			int color = 1;
			monitors = parameterMonitors.values().iterator();
			gc.setLineStyle(SWT.LINE_SOLID);
			gc.setLineWidth(1);

			while(monitors.hasNext()) {
				color++;
				Monitor mon = (Monitor)monitors.next();
				gc.setForeground(mon.getColor());
				double norm = mon.getNormFactor();				
				for(int i=0;i<Monitor.BACKLIST_LENGTH-1;i++) {
					double val = mon.getPreviousValue(i);
					double prev = mon.getPreviousValue(i+1);
					gc.drawLine(
						size.x-(i*st), 
						scale(size.y,val,norm), 
						size.x-((i+1)*st), 
						scale(size.y,prev,norm)
					);
				}
			}
			evt.gc.drawImage(screenImage,0,0,size.x,size.y,0,0,size.x,size.y);
		}

		/**
		 * @return
		 */
		public boolean isNormalize() {
			return normalize;
		}

		/**
		 * @param b
		 */
		public void setNormalize(boolean b) {
			normalize = b;
			if(!b) {
				Iterator monitors = parameterMonitors.values().iterator();
				while(monitors.hasNext()) {
					Monitor mon = (Monitor)monitors.next();
					mon.setNormFactor(1);
				}			
			} else {
				force = true;
			}
		}
		
		public void reset() {
			maxValue = 1;
			minValue = 1;
		
			zerolevel = 10;
		
			normalize = false;
			force = false;	
			
			Iterator monitors = parameterMonitors.values().iterator();
			while(monitors.hasNext()) {
				Monitor mon = (Monitor)monitors.next();
				mon.setNormFactor(1);
				mon.clearBacklist();
			}
		}

		public void refreshSize() {
			if(screenImage != null) {
				gc.dispose();
				screenImage.dispose();
			}
			screenImage = null;
			gc = null;
			
			DiagramWidget.this.update();
		}
	}

	private DiagramPaintListener diagramPainter;
	private Thread uiThread;
	private HashMap<String,Monitor> parameterMonitors = new HashMap<String,Monitor>(20);
	private Timer redrawTimer;
	
	boolean realtime = false;
	long time = 0;
	
	public DiagramWidget(Composite parent, int style) {
		super(parent,style | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE);
		
		uiThread = Thread.currentThread();
		
		diagramPainter = new DiagramPaintListener();
							
		this.addPaintListener(diagramPainter);
		this.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				diagramPainter.refreshSize();
			}			
		});
		
		Menu menu = new Menu(this.getShell(),SWT.POP_UP);
		this.setMenu(menu);
		
		final MenuItem m = new MenuItem(menu, SWT.CHECK);
		m.setText("Normalize");
		m.addArmListener(new ArmListener() {
			public void widgetArmed(ArmEvent e) {
				m.setSelection(isNormalize());
			}
		});
		m.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				setNormalize(m.getSelection());	
			}
			
		});

		final MenuItem m3 = new MenuItem(menu, SWT.CHECK);
		m3.setText("Real-Time");
		m3.addArmListener(new ArmListener() {
			public void widgetArmed(ArmEvent e) {
				m3.setSelection(isRealtime());
			}
		});
		m3.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				setRealtime(m3.getSelection());	
			}
			
		});
		
		MenuItem m2 = new MenuItem(menu, SWT.CASCADE);
		m2.setText("Reset");
		m2.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				clear();
			}
			
		});

	}
	
	public void clear() {
		diagramPainter.reset();	
		redraw();
	}
	
	public void addParameterMonitor(IParameterMonitor newMonitor, Color color, String display) {
		parameterMonitors.put(newMonitor.getID(),new Monitor(newMonitor,color,display));
		if(redrawTimer != null) redrawTimer.cancel();
		redrawTimer = new Timer(true);
		recalcTimer();
	}

	public Iterator getParameterMonitorIDs() {
		return parameterMonitors.keySet().iterator();
	}

	public void removeParameterMonitor(String id) {
		parameterMonitors.remove(id);
		if(redrawTimer != null) redrawTimer.cancel();
		redrawTimer = new Timer(true);
		recalcTimer();
	}
	
	public void removeAllParameterMonitors() {
		parameterMonitors.clear();
		if(redrawTimer != null) redrawTimer.cancel();
		redrawTimer = new Timer(true);
		recalcTimer();		
	}
	
	public HashMap accessParameterMonitors() {
		return parameterMonitors; 
	}
	
	private void recalcTimer() {
		
		if(!realtime) return;
		
		long shortestdelay = Long.MAX_VALUE;
		long current = System.currentTimeMillis();
		Iterator iter = parameterMonitors.values().iterator();
		while(iter.hasNext()) {
			Monitor mon = (Monitor)iter.next();
			long delay = mon.getRedrawDelay(current); 
			if(delay < shortestdelay)
				shortestdelay = delay;
		}
		if(shortestdelay < 10) shortestdelay = 10;
		
		if(parameterMonitors.size() < 1) return;
		
		redrawTimer.schedule(new TimerTask() {
			public void run() {
				Display d = Display.findDisplay(uiThread);
				if(d == null) return; 
				d.syncExec(new Runnable() {
					public void run() {
						tickNow(System.currentTimeMillis());
					}
				});
				recalcTimer();			
			}			
		}, shortestdelay);
	}

	public boolean isNormalize() {
		return diagramPainter.isNormalize();
	}

	public void setNormalize(boolean b) {
		diagramPainter.setNormalize(b);
	}

	/**
	 * @return
	 */
	public boolean isRealtime() {
		return realtime;
	}

	/**
	 * @param b
	 */
	public void setRealtime(boolean b) {
		realtime = b;
	}

	public void tick(long timeindex) {
		if(realtime) return;		
		tickNow(timeindex);
	}
	
	private void tickNow(long timeindex) {
		time = timeindex;
		if(!isDisposed())
			redraw();
		Iterator monitors = parameterMonitors.values().iterator();
		while(monitors.hasNext()) {
			Monitor mon = (Monitor)monitors.next();
			mon.wasRedrawn(timeindex);
		}
	}

}
