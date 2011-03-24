/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/LibraryEntryWidget.java,v 1.7 2005/08/12 17:56:02 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.micropsi.eclipse.common.ProgressDialog;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.dialogs.LibEntryAboutDialog;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.mindconsole.library.LibraryManager;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntityTypesIF;

public class LibraryEntryWidget extends Canvas {
	
	private static Image libImage;
	private static Color white;
	
	private String entryName;
	private LibraryManager lib;
	private LibraryWidget lw;
	
	private File tempfile;

	public LibraryEntryWidget(LibraryWidget lw, int style, final String entryName, final LibraryManager lib) {
		super(lw, style);
		
		this.entryName = entryName;
		this.lib = lib;
		this.lw = lw;
		
		try {
			if(libImage == null) {
				Path p = new Path("icons/libentry.gif");
				String f = Platform.asLocalURL(MindPlugin.getDefault().find(p)).getFile();
				libImage = new Image(lw.getDisplay(),f);
			}
		} catch (IOException e) {
			MindPlugin.getDefault().handleException(e);
		}
			
		if(white == null)
			white = new Color(null,255,255,255);
		
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				drawLibraryEntry(e);
			}
		});
		
		Menu popUpMenu = new Menu(this.getShell(),SWT.POP_UP);
		this.setMenu(popUpMenu);
		
		MenuItem item = new MenuItem(popUpMenu,SWT.CASCADE);
		item.setText("Delete");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				LibraryEntryWidget.this.dispose();
				LibraryEntryWidget.this.lw.deleteEntry(LibraryEntryWidget.this.entryName);
			}
		});

		item = new MenuItem(popUpMenu,SWT.CASCADE);
		item.setText("About...");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new LibEntryAboutDialog(
					getShell(),
					LibraryEntryWidget.this.lib,
					LibraryEntryWidget.this.entryName
				).open();
			}
		});
		
		item = new MenuItem(popUpMenu,SWT.CASCADE);
		item.setText("Export...");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					FileDialog dlg = new FileDialog(getShell(),SWT.SAVE);
					dlg.setFilterExtensions(new String[] {"*.mlb"});
					dlg.setFileName(entryName+".mlb");
					String filename = dlg.open();
					if(filename != null && filename != "") {
						lib.exportLibraryEntry(entryName, new File(filename));
					}
				} catch (Exception ex) {
					MindPlugin.getDefault().handleException(ex);
				}
			}
		});

		
		DragSource dragSource = new DragSource(this,DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] {TextTransfer.getInstance(),FileTransfer.getInstance()});
		dragSource.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
			}

			public void dragSetData(DragSourceEvent event) {							
				if(TextTransfer.getInstance().isSupportedType(event.dataType)) {
					try {
						HashMap<String,String> toInject = new HashMap<String,String>();
						Iterator<EntityModel> iter = LibraryEntryWidget.this.lib.getElementsOfEntry(LibraryEntryWidget.this.entryName).iterator();
						while(iter.hasNext()) {
							EntityModel model = iter.next();	
							if(model.getEntity().getEntityType() == NetEntityTypesIF.ET_MODULE_NATIVE) {
								NativeModule m = (NativeModule)model.getEntity(); 
								String cn = m.getImplementationClassName();
								String source = lib.getElementSourceCode(entryName, cn);
								IType type = ModuleJavaManager.getInstance().findType(cn);
										
								if(	(m.getImplementation() == null) ||
									//(m.hasLostLinks()) ||
									(type == null) ||
									(!type.getCompilationUnit().getSource().equals(source))) {
										toInject.put(cn,source);
									}											
							}
						}					
						if(toInject.size() > 0) {
							ModuleJavaManager.getInstance().injectSourceFiles(getShell(), toInject, new ProgressDialog("Injecting sources...",getShell()));
						}
							
						iter = LibraryEntryWidget.this.lib.getElementsOfEntry(LibraryEntryWidget.this.entryName).iterator();
						ArrayList<EntityTransferData> toTransfer = new ArrayList<EntityTransferData>();
						while(iter.hasNext()) {
							EntityModel model = iter.next(); 
							EntityTransferData data = new EntityTransferData();
							data.net = (LocalNetFacade)model.getUnderlyingNetModel().getNet();
							data.entity = model.getEntity();
							data.x = model.getX();
							data.y = model.getY();
										
							toTransfer.add(data);								
						}
																
						event.data = EntityTransferData.listToText(toTransfer);
					} catch (Exception e) {
						MindPlugin.getDefault().handleException(e);
						event.data = EntityTransferData.listToText(new ArrayList());
						event.doit = false;
					}
				} else if(FileTransfer.getInstance().isSupportedType(event.dataType)) {
					try {
						tempfile = File.createTempFile(entryName, ".mlb");
						File otherfile = new File(tempfile.getParent(),entryName+".mlb");
						tempfile.renameTo(otherfile);
						tempfile = otherfile;
						lib.exportLibraryEntry(entryName, tempfile);
						event.data = new String[] {
							tempfile.getAbsolutePath()	
						};
					} catch (Exception e) {
						MindPlugin.getDefault().handleException(e);
					}				
				}
			}

			public void dragFinished(DragSourceEvent event) {
				if(tempfile != null) {
					if(tempfile.exists()) tempfile.delete();
					tempfile = null;
				}
			}
			
		}); 

	}
	
	protected void drawLibraryEntry(PaintEvent e) {
		e.gc.setBackground(white);
		e.gc.fillRectangle(0, 0, 50, 50);
		libImage.setBackground(white);
		e.gc.drawImage(libImage, 0, 0, 40, 35, 5, 0, 40, 35);
		e.gc.drawText(entryName,8,35);
	}

	public Point computeSize(int hintx, int hinty) {
		 return new Point(50,50);
	}

}
