package org.micropsi.eclipse.console.adminperspective;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.eclipse.console.ConsolePlugin;
import org.micropsi.eclipse.console.ConsoleRuntimeUser;
import org.micropsi.eclipse.console.IMonitorProvider;
import org.micropsi.eclipse.console.IParameterMonitor;
import org.micropsi.eclipse.console.controller.IViewControllerListener;
import org.micropsi.eclipse.console.dialogs.AddMonitorDialog;
import org.micropsi.eclipse.console.internal.MonitorProviderManager;
import org.micropsi.eclipse.console.internal.ParameterMonitorRegistry;
import org.micropsi.eclipse.console.internal.ParameterMonitorWrapper;
import org.micropsi.eclipse.console.widgets.DiagramWidget;
import org.micropsi.eclipse.console.widgets.Monitor;

/**
 *
 *
 *
 */
public class ParameterView extends ViewPart implements IViewControllerListener {

	private static final String PARAMETERVIEW_STATE_MEMENTO_ROOT = "parameterview-state";
	
	private class CreatePair {
		public Color color;
		public IParameterMonitor monitor;
		public String display;
	}

	private IStatusLineManager statusLineManager;
	private DiagramWidget diagrams;
	TableViewer table;	
	
	boolean initialRealtime = true;
	private ArrayList<CreatePair> initialContent = new ArrayList<CreatePair>(10);
	
	public ParameterView() {
		ParameterController.getInstance().registerView(this);
	}

	public void createPartControl(final Composite parent) {
		
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());

		diagrams = new DiagramWidget(topLevel,SWT.NONE);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		diagrams.setLayoutData(data);
		diagrams.setRealtime(initialRealtime);		

		table = new TableViewer(topLevel);
		data = new GridData(GridData.FILL_VERTICAL);
		data.widthHint = 180;
		data.horizontalSpan = 1;
		table.getTable().setLayoutData(data);
		table.getTable().setLinesVisible(true);
		
		new TableColumn(table.getTable(),SWT.NONE).setWidth(120);
		new TableColumn(table.getTable(),SWT.NONE).setWidth(60);
					
		table.setLabelProvider(new ITableLabelProvider() {

			HashMap<Color,Image> images = new HashMap<Color,Image>(20);

			public Image getColumnImage(Object element, int columnIndex) {
				if(columnIndex != 0) return null;
				
				Monitor m = (Monitor)element;
				if(images.containsKey(m.getColor())) {
					return images.get(m.getColor());
				} else {
					Image img = new Image(getSite().getShell().getDisplay(),16,16);
					GC gc = new GC(img);
					gc.setBackground(m.getColor());
					gc.fillRectangle(0, 0, 16, 16); 
					gc.dispose();
					images.put(m.getColor(), img);
					return img;
				}
			}

			public String getColumnText(Object element, int columnIndex) {
				Monitor m = (Monitor)element;
				switch(columnIndex) {
					case 0:
						String str = m.getDisplayName();
						if(m.isInvalid()) str = "invalid ("+str+")";
						return str;
					case 1:
						return Double.toString(m.getCurrentValue());
					default:
						return "";
				}				
			}

			public void addListener(ILabelProviderListener listener) {
			}
			public void removeListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}		
		});
		
		updateTables(initialContent);
		initialContent.clear();
		initialContent = null;

	}
	
	private void updateTables(ArrayList content) {
			
		diagrams.removeAllParameterMonitors();
		for(int i=0;i<content.size();i++) {
			CreatePair p = (CreatePair)content.get(i);
			diagrams.addParameterMonitor(p.monitor, p.color, p.display);
		}
		
		table.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				HashMap m = (HashMap)inputElement;
				if(m == null) return new Object[0];				
				return m.values().toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}			
		});
		
		table.setInput(diagrams.accessParameterMonitors());
		
		Menu menu = new Menu(this.getSite().getShell(),SWT.POP_UP);
		table.getTable().setMenu(menu);
		
		MenuItem item = new MenuItem(menu,SWT.CASCADE);
		item.setText("Add");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AddMonitorDialog dlg = new AddMonitorDialog(table.getTable().getShell(), ParameterMonitorRegistry.getInstance());
				dlg.setBlockOnOpen(true);
				dlg.open();
				if(	dlg.getReturnCode() == AddMonitorDialog.OK &&
					dlg.getSelMon() != null) {
					diagrams.addParameterMonitor(
						dlg.getSelMon(), 
						dlg.getSelCol(),
						dlg.getSelDis()
					);
					table.setInput(diagrams.accessParameterMonitors());
					diagrams.redraw();			
				}
			}
		});
		
		item = new MenuItem(menu,SWT.CASCADE);
		item.setText("Remove");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = table.getTable().getSelectionIndex();
				if(i < 0) return;
				Monitor m = (Monitor)table.getElementAt(i);
				diagrams.removeParameterMonitor(m.getID());
				table.setInput(diagrams.accessParameterMonitors());
				diagrams.redraw();
			}
		});
 
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();

	}
	
	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}

	public void setDataBase(Object o) {
	}

	public void setData(Object o) {
	}

	boolean drawing = false;
	
	public void tick(final long step) {
		Runnable ticker = new Runnable() {
			public void run() {
				if(!drawing) {
					drawing = true;
					diagrams.tick(step);
					table.setInput(diagrams.accessParameterMonitors());
					drawing = false;
				}
			}
		};
		if(!drawing) {
			ParameterView.this.getSite().getShell().getDisplay().asyncExec(ticker);
			Thread.yield();
			ParameterView.this.getSite().getShell().getDisplay().wake();
		}
	}
	
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site,memento);
		loadMonitorState(memento);
	}
	
	private void loadMonitorState(IMemento memento) {
		
		initialContent = new ArrayList<CreatePair>();
		
		try {
			int r = memento.getInteger("realtime").intValue();
			initialRealtime = (r == 1); 
		} catch (Exception e) {
			// that's ok
		}
		
		if(memento == null) return;
		IMemento[] monitors = memento.getChildren("mon");
		if(monitors == null) return;		
		
		ParameterMonitorRegistry.getInstance().unregisterAllParameterMonitors();
		
		for(int i=0;i<monitors.length;i++) {
			try {
				String c = monitors[i].getString("java_class");
				String prov = monitors[i].getString("provided_by");
				
				IMonitorProvider provider = MonitorProviderManager.getInstance().getProvider(prov);
				IParameterMonitor rest = provider.createMonitor(c);
								
				Color color = new Color(
					null,
					monitors[i].getInteger("col_r").intValue(),
					monitors[i].getInteger("col_g").intValue(),
					monitors[i].getInteger("col_b").intValue()
				);
				
				rest.restoreFromMemento(monitors[i]);
				ParameterMonitorWrapper w = new ParameterMonitorWrapper(rest,prov);
				
				ParameterMonitorRegistry.getInstance().registerParameterMonitor(w);
				
				CreatePair p = new CreatePair();
				p.color = color;
				p.monitor = w;
				p.display = monitors[i].getString("display");
				
				initialContent.add(p);
				
			} catch (Exception e) {
				ConsoleRuntimeUser.getInstance().getLogger().warn("Could not restore monitor: ",e);
			}			
		}		
	}

	public void saveState(IMemento memento) {
		memento.putInteger("realtime", (diagrams.isRealtime() ? 1 : 0));
		
		Iterator iter = diagrams.accessParameterMonitors().values().iterator();
		while(iter.hasNext()) {
			Monitor mon = (Monitor)iter.next();	
			try {
				ParameterMonitorWrapper rest = (ParameterMonitorWrapper) mon.accessParameterMonitor();
				IMemento mem = memento.createChild("mon");
				mem.putString("java_class", rest.getWrapped().getClass().getName());
				mem.putString("provided_by",rest.getProvider());
				mem.putInteger("col_r", mon.getColor().getRed());
				mem.putInteger("col_g", mon.getColor().getGreen());
				mem.putInteger("col_b", mon.getColor().getBlue());
				mem.putString("display",mon.getDisplayName());
				rest.saveToMemento(mem);
			} catch (Exception e) {
				ConsoleRuntimeUser.getInstance().getLogger().error("Could not save parameter monitor: "+mon.getID()+" ("+mon.getDisplayName()+")",e);
			}
		}
	}
	
	public void saveState(String stateName) {
		IPath statePath = Platform.getPluginStateLocation(ConsolePlugin.getDefault());
		
		File statefile = new File(statePath.toFile().getAbsolutePath(),stateName);
		
		try {
			if(!statefile.exists()) {
				statefile.createNewFile();
			}

			XMLMemento memento = XMLMemento.createWriteRoot(PARAMETERVIEW_STATE_MEMENTO_ROOT);
			saveState(memento);
		
			FileWriter writer = new FileWriter(statefile);
			memento.save(writer);

		} catch (IOException e) {
			ConsoleRuntimeUser.getInstance().handleException(e);
		}
	}

	public void loadState(String stateName) {
		IPath statePath = Platform.getPluginStateLocation(ConsolePlugin.getDefault());
		
		File statefile = new File(statePath.toFile().getAbsolutePath(),stateName);
		
		if(!statefile.exists()) {
			return;
		}
		
		try {
			FileReader reader = new FileReader(statefile); 
			XMLMemento memento = XMLMemento.createReadRoot(reader);
			
			IMemento theData = memento;//memento.getChild(PARAMETERVIEW_STATE_MEMENTO_ROOT);
			
			loadMonitorState(theData);
			
			table.getTable().getShell().getDisplay().syncExec(new Runnable() {
				public void run() {
					table.setInput(diagrams.accessParameterMonitors());
					updateTables(initialContent);					
					diagrams.redraw();	
				}
			});
			
			initialContent.clear();
			initialContent = null;
			
		} catch (Exception e) {
			ConsoleRuntimeUser.getInstance().handleException(e);
		}

	}


	public void addParameterMonitor(IParameterMonitor mon, Color col, String display) {
		diagrams.addParameterMonitor(
			mon, 
			col,
			display
		);
		table.setInput(diagrams.accessParameterMonitors());
		diagrams.redraw();			
	}

	public void clear() {
		diagrams.clear();
	}
}
