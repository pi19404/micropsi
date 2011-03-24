/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/LibraryWidget.java,v 1.4 2005/08/12 17:56:02 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.library.LibraryManager;

public class LibraryWidget extends Composite {

	private LibraryManager lib;
	private Composite parent;

	public LibraryWidget(Composite parent, int style, final LibraryManager lib) throws MicropsiException {
		super(parent, style);
		this.parent = parent;
		this.lib = lib;
		
		RowLayout layout = new RowLayout();
		layout.spacing = 10;
		layout.marginTop = 10;
		layout.marginLeft = 10;
		layout.pack = true;
		this.setLayout(layout);
		this.setFont(parent.getFont());
		
		final Color white = new Color(null,255,255,255);
		this.setBackground(white);
		
		Menu popUpMenu = new Menu(this.getShell(),SWT.POP_UP);
		this.setMenu(popUpMenu);
		
		MenuItem item = new MenuItem(popUpMenu,SWT.CASCADE);
		item.setText("Import...");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(getShell(),SWT.OPEN);
				dlg.setFilterExtensions(new String[] {"*.mlb"});
				String filename = dlg.open();
				if(filename == null) return;
				String entryname = getValidatedName();
				if(entryname == null) return;
				try {
					lib.importEntry(entryname, new File(filename));
					addLibraryWidget(entryname);
					setSize(computeSize(0, 0));
					layout();
				} catch (Exception ex) {
					MindPlugin.getDefault().handleException(ex);
				}
			}
		});

		
		List<String> entries = lib.getEntries();
		for(int i=0;i<entries.size();i++) {
			addLibraryWidget(entries.get(i));
		}
		
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				white.dispose();
			}
		});
		
		parent.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				setSize(computeSize(0, 0));
				layout();
			}	
		});

		
		DropTarget dropTarget = new DropTarget(this,DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] {TextTransfer.getInstance(),FileTransfer.getInstance()});
		dropTarget.addDropListener(new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
				if(FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
/*					String[] files = (String[])event.data;
					boolean found = false;
					for(int i=0;i<files.length;i++)
						if(files[i].endsWith(".mlb")) found = true; 
					if(!found) event.detail = DND.DROP_NONE;*/
				}
			}

			public void drop(DropTargetEvent event) {
				if(TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
					try {
						List<EntityTransferData> d = EntityTransferData.textToList((String)event.data);
						dropEntities(d);
					} catch (Exception e) {
						MindPlugin.getDefault().handleException(e);
					}					
				} else if(FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
					try {
						String[] files = (String[])event.data;
						for(int i=0;i<files.length;i++) {
							if(!files[i].endsWith(".mlb")) continue; 
					
							String entryname = getValidatedName();
							if(entryname == null) return;

							lib.importEntry(entryname, new File(files[i]));
							addLibraryWidget(entryname);
							setSize(computeSize(0, 0));
							layout();
						}						
					} catch (Exception ex) {
						MindPlugin.getDefault().handleException(ex);
					}

					
				}
				
			}

			public void dropAccept(DropTargetEvent event) {
			}
			
		});
		
	}

	private void addLibraryWidget(String id) {
		LibraryEntryWidget lew = new LibraryEntryWidget(this, SWT.NONE, id, lib);
		RowData data = new RowData();
		data.height = 50;
		data.width = 50;
		lew.setLayoutData(data);
		setSize(computeSize(0, 0));
		layout();
		redraw();
	}
	
	private String getValidatedName() {
		
		IInputValidator val = new IInputValidator() {
			public String isValid(String newText) {
				
				boolean illegal = false;
				for(int i=0;i<newText.length();i++) {
					if(!Character.isLetterOrDigit(newText.charAt(i))) {
						illegal = true;
						break;
					}
				}
				
				try {					
					if(illegal) {
						return "Illegal character";
					} else if(lib.getEntries().contains(newText)) {
						return "There already is an entry with that name";
					} else if(newText.length() < 3) {
						return "The name is to short";
					} else if(newText.length() > 15) {
						return "The name is too long";
					} else if(newText.equals("LibraryRoot")) {
						return "The name is reserved";
					} else if(newText.equals("foo")) {
						return "The name is too boring";
					} else 
						return null;
				} catch(Exception e) {
					return MindPlugin.getDefault().handleException(e);
				}
			}			
		};
		
		InputDialog inp = new InputDialog(
			getShell(),
			"Entry name",
			"Please enter a name for the entry",
			"default",
			val
		);
		
		inp.open();
		if(inp.getReturnCode() != InputDialog.OK) return null;
		
		return inp.getValue();

	}
	
	private void dropEntities(List<EntityTransferData> toDrop) {
				
		String newID = getValidatedName();
		if(newID == null) return;
		
		try {
			lib.createEntry(newID,toDrop);
			addLibraryWidget(newID);
		} catch (Exception e) {
			MindPlugin.getDefault().handleException(e);
		}		
	}
	
	public Point computeSize(int xhint, int yhint, boolean b) {		
					
		int perHor = (parent.getClientArea().width / 50) - 1;
		if(perHor < 1) perHor = 1;
		int ver = (getChildren().length / perHor);
		int y = ver * 50;

		Point toReturn = new Point(parent.getSize().x,y);
		if(toReturn.x < parent.getSize().x) toReturn.x = parent.getSize().x + 20;
		if(toReturn.y < parent.getSize().y) toReturn.y = parent.getSize().y;

		if(toReturn.x < 70) toReturn.x = 70;
		if(toReturn.y < 70) toReturn.y = 70;
		
		return toReturn;
	}
	
	

	protected void deleteEntry(String entryName) {	
		try {
			lib.deleteEntry(entryName);
//			lib.saveLibrary();
			setSize(computeSize(0, 0));
			layout();
		} catch (Exception e) {
			MindPlugin.getDefault().handleException(e);
		}		
	}

}
