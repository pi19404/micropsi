package org.micropsi.eclipse.console.adminperspective;

import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.eclipse.console.controller.IViewControllerListener;
import org.micropsi.eclipse.runtime.RuntimePlugin;

public class LogView extends ViewPart implements IViewControllerListener {

	private Color backCol;
	private Color debugCol;
	private Color infoCol;
	private Color warnCol;
	private Color errorCol;
	private Color fatalCol;
	
	private boolean scroll = true;
	private boolean watch = true;
	
	private class AsyncAppend implements Runnable {
		LoggingEvent event;
		
		public void run() {
			
			if(folder == null) return;
			if(folder.isDisposed()) return;
			
			String ln = event.getLoggerName();
			
			if(!tabs.containsKey(ln)) {
				TabItem item = new TabItem(folder,SWT.NONE);
				item.setText(ln);	

				final StyledText text = new StyledText(folder, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
				text.setLayoutData(new GridData(GridData.FILL_BOTH));
				text.setEditable(false);
								
				Menu m = new Menu(text);
				MenuItem mitem = new MenuItem(m,SWT.CASCADE);
				mitem.setText("Clear");
				mitem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						text.setText("");
					}
				});

				mitem = new MenuItem(m,SWT.CASCADE);
				mitem.setText("Cut");
				mitem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						text.cut();
						text.insert("");
					}
				});
				
				mitem = new MenuItem(m,SWT.CASCADE);
				mitem.setText("Copy");
				mitem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						text.copy();
					}
				});
				
				final MenuItem toggle = new MenuItem(m,SWT.CHECK);
				toggle.setText("Scroll lock");
				toggle.addArmListener(new ArmListener() {
					public void widgetArmed(ArmEvent e) {
						toggle.setSelection(!scroll);
					}
				});
				toggle.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						scroll = !toggle.getSelection();
					}
				});
				
				text.setMenu(m);
				text.setBackground(backCol);
		
				item.setControl(text);
				tabs.put(ln, text);
			}
		
			String a = event.getRenderedMessage();
			if(!a.endsWith("\n")) a += "\n";
			StyledText text = tabs.get(ln);
			int start = text.getCharCount();
			if(!scroll) text.getVerticalBar().setEnabled(false);
			
			text.append(a);			
			
			if(!scroll) text.getVerticalBar().setEnabled(true);
			
			switch(event.getLevel().toInt()) {
				case Level.DEBUG_INT:
					text.setStyleRange(new StyleRange(start,a.length(),debugCol,backCol));
					break;
				case Level.INFO_INT:
					text.setStyleRange(new StyleRange(start,a.length(),infoCol,backCol));
					break;
				case Level.WARN_INT:
					text.setStyleRange(new StyleRange(start,a.length(),warnCol,backCol));
					break;
				case Level.ERROR_INT:
					text.setStyleRange(new StyleRange(start,a.length(),errorCol,backCol));
					break;
				case Level.FATAL_INT:
					text.setStyleRange(new StyleRange(start,a.length(),fatalCol,backCol));
					break;
			}
			
			String[] lines = text.getText().split("\n");
			if(lines.length > 1000) {
				for(int i=0;i<lines.length-1000;i++) {
					text.replaceTextRange(0,text.getText().indexOf('\n')+1,"");
				}				
			}
			
			if(scroll) {
				text.setSelection(start+1);
				text.showSelection();
			} 			
		}
	}

	private IStatusLineManager statusLineManager;

	private TabFolder folder;
	private HashMap<String,StyledText> tabs = new HashMap<String,StyledText>();
	
	public LogView() {
		LogController.getInstance().registerView(this);		
	}

	public void createPartControl(Composite parent) {

		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());
		
		folder = new TabFolder(topLevel,SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
	
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
				
		backCol = parent.getBackground();
		debugCol = new Color(null,0,0,0); 
		infoCol = new Color(null,0,140,0);
		warnCol = new Color(null,0,0,140);
		errorCol = new Color(null,150,0,0);
		fatalCol = new Color(null,200,0,0);

		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				debugCol.dispose();
				infoCol.dispose();
				warnCol.dispose();
				errorCol.dispose();
				fatalCol.dispose();
			}
		});

	}
	
	public void setFocus() {
		folder.setFocus();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}

	public void setDataBase(Object o) {
	}

	public void setData(Object o) {
		
		if(!watch) return;
				
		AsyncAppend appender = new AsyncAppend();
		appender.event = (LoggingEvent)o;
		
		if(RuntimePlugin.getDefault().getDisplay().isDisposed()) return;
		
		RuntimePlugin.getDefault().getDisplay().asyncExec(appender);
	}

	public boolean toggleWatch() {
		watch = !watch;
		folder.setEnabled(watch);
		return watch;
	}

}
