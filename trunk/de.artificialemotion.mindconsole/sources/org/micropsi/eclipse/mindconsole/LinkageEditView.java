package org.micropsi.eclipse.mindconsole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.console.controller.IViewControllerListener;
import org.micropsi.eclipse.mindconsole.dialogs.LinkDialog;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.GateOutputFunctions;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.NetParametersIF;
import org.micropsi.nodenet.OutputFunctionIF;
import org.micropsi.nodenet.OutputFunctionParameter;
import org.micropsi.nodenet.agent.TypeStrings;

/**
 *
 *
 *
 */
public class LinkageEditView extends ViewPart implements IViewControllerListener {
	
	private static String[] getOFTable() {
		List ofs = GateOutputFunctions.getOutputFunctions();
		String[] displayNames = new String[ofs.size()];
		for(int i=0;i<displayNames.length;i++) {
			displayNames[i] = ((OutputFunctionIF)ofs.get(i)).getDisplayName();
		}
		return displayNames;
	}
	
	class LinkContentProvider implements IStructuredContentProvider {

		Gate gate;

		public Object[] getElements(Object inputElement) {
			if(gate == null) return new Object[0];
			
			Object[] toReturn = new Object[gate.getNumberOfLinks()];
			Iterator iter = gate.getLinks();
			int i = 0;
			while(iter.hasNext()) {
				toReturn[i] = iter.next();
				i++;
			}
			
			return toReturn;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.gate = (Gate)newInput;
		}
	}


	class GateTableContent {
		
		static final int ENTITY = 0;
		static final int TYPE = 1;
		static final int ACTIVATION = 2;
		static final int AMPF = 3;
		static final int GATEF = 4;
		static final int MAX = 5;
		static final int MIN = 6;
		static final int DECAY = 7;
		static final int OUTPUTF = 8;
		
		static final int OTHER = 9;

		int row;
		Gate g;
		String param = null;

		public GateTableContent(Gate g, int row) {
			this.row = row;
			this.g = g;
		}
		
		public GateTableContent(Gate g, String param) {
			this.row = OTHER;
			this.g = g;
			this.param = param;
		}		
		
	}

	class GateLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			GateTableContent c = (GateTableContent)element;
			switch(c.row) {
				case GateTableContent.ENTITY:
					return (columnIndex == 0) ? "Entity" : c.g.getNetEntity().getID();
				case GateTableContent.TYPE:
					return (columnIndex == 0) ? "GateType" : TypeStrings.gateType(c.g.getType());
				case GateTableContent.ACTIVATION:
					return (columnIndex == 0) ? "Activation" : Double.toString(c.g.getConfirmedActivation());
				case GateTableContent.MAX:
					return (columnIndex == 0) ? "Maximum" : Double.toString(c.g.getMaximum());
				case GateTableContent.MIN:
					return (columnIndex == 0) ? "Minimum" : Double.toString(c.g.getMinimum());
				case GateTableContent.AMPF:
					return (columnIndex == 0) ? "AmpFactor" : Double.toString(c.g.getAmpfactor());
				case GateTableContent.GATEF:
					return (columnIndex == 0) ? "GateFactor" : Double.toString(c.g.getGateFactor());
				case GateTableContent.DECAY:
					return (columnIndex == 0) ? "DecayType" : Integer.toString(c.g.getDecayCalculatorType());
				case GateTableContent.OUTPUTF:
					return (columnIndex == 0) ? "OutF" : c.g.getOutputFunction().getDisplayName();
				case GateTableContent.OTHER:
					return (columnIndex == 0) ? c.param : Double.toString(c.g.getOutputFunctionParameter(c.param));

				default: return ""; 
			}
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

	}

	class GateContentProvider implements IStructuredContentProvider {
		
		ArrayList<GateTableContent> defaultContent = new ArrayList<GateTableContent>();
		ArrayList<GateTableContent> currentContent = null;
		
		GateContentProvider() {
			
			defaultContent.add(new GateTableContent(null,GateTableContent.ENTITY));
			defaultContent.add(new GateTableContent(null,GateTableContent.TYPE));
			defaultContent.add(new GateTableContent(null,GateTableContent.ACTIVATION));
			defaultContent.add(new GateTableContent(null,GateTableContent.MAX));
			defaultContent.add(new GateTableContent(null,GateTableContent.MIN));
			defaultContent.add(new GateTableContent(null,GateTableContent.AMPF));
			defaultContent.add(new GateTableContent(null,GateTableContent.GATEF));
			defaultContent.add(new GateTableContent(null,GateTableContent.DECAY));
			defaultContent.add(new GateTableContent(null,GateTableContent.OUTPUTF));
				
		}
		
		public void dispose() {
		}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {						
		}
		
		public Object[] getElements(Object inputElement) {

			Gate g = (Gate)inputElement;
	
			currentContent = new ArrayList<GateTableContent>(defaultContent);
			
			OutputFunctionParameter[] currentOFParams = g.getCurrentOutputFunctionParameters();
			for(int i=0;i<currentOFParams.length;i++) {
				currentContent.add(new GateTableContent(g,currentOFParams[i].getName()));
			}
			
			for(int i=0;i<currentContent.size();i++) {
				GateTableContent c = currentContent.get(i);
				c.g = g;
			}		
						
			return currentContent.toArray();			
		}
	}

	private NetModel netmodel;
	private Gate currentGate;
	
	private Thread uiThread;
	private TableViewer gateProp;
	private ListViewer links;
	private IStatusLineManager statusLineManager;

	public static int LV_SLOT = 0;	
	public static int LV_GATE = 1;
	
	public LinkageEditView() {
		super();
		netmodel = AgentNetModelManager.getInstance().getNetModel();
		LinkageEditController.getInstance().registerView(this);
		uiThread = Thread.currentThread();
	}

	public void createPartControl(Composite parent) {

		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());

		gateProp = new TableViewer(topLevel);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 140;
		gateProp.getTable().setLayoutData(data);
		gateProp.getTable().setLinesVisible(true);
	
		final CellEditor[] outfCellEditors = {
			null,
			new ComboBoxCellEditor(gateProp.getTable(),getOFTable())
		};
		
		final CellEditor[] defaultCellEditors = {
			null,
			new TextCellEditor(gateProp.getTable())
		};
		
		gateProp.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				int sel = gateProp.getTable().getSelectionIndex(); 
				if(sel < 0) {
					gateProp.setCellEditors(defaultCellEditors);
					return;
				} 
				
				GateTableContent c = (GateTableContent)gateProp.getElementAt(sel);
				if(c.row == GateTableContent.OUTPUTF) {
					gateProp.setCellEditors(outfCellEditors);
				} else {
					gateProp.setCellEditors(defaultCellEditors);
				}
			}
		});	
		
		new TableColumn(gateProp.getTable(),SWT.NONE).setWidth(80);
		new TableColumn(gateProp.getTable(),SWT.NONE).setWidth(110);
			
		gateProp.setLabelProvider(new GateLabelProvider());
		gateProp.setContentProvider(new GateContentProvider());
		
		String[] cprop = {"eins","zwei"};
		gateProp.setColumnProperties(cprop);
			
		gateProp.setCellModifier(new ICellModifier() {
			
			public boolean canModify(Object element, String property) {				
				GateTableContent e = (GateTableContent)element;
				if(	e.row != GateTableContent.ENTITY &&
						e.row != GateTableContent.TYPE)
							return property.equals("zwei");
				else
					return false;
			}
			
			public Object getValue(Object element, String property) {	
				GateTableContent e = (GateTableContent)element;
				switch(e.row) {
					case GateTableContent.ACTIVATION:
						return Double.toString(e.g.getConfirmedActivation());
					case GateTableContent.MAX:
						return Double.toString(e.g.getMaximum());
					case GateTableContent.MIN:
						return Double.toString(e.g.getMinimum());
					case GateTableContent.AMPF:
						return Double.toString(e.g.getAmpfactor());
					case GateTableContent.GATEF:
						return Double.toString(e.g.getGateFactor());
					case GateTableContent.DECAY:
						return Integer.toString(e.g.getDecayCalculatorType());
					case GateTableContent.OUTPUTF:
						return new Integer(0); //Integer.toString(e.g.getOutputFunction());
					default: return "";
				}								
			}
			
			public void modify(Object element, String property, Object value) {
				triggerGateChange(((TableItem)element).getData(), value);				
			}
			
		});		
		
		links = new ListViewer(topLevel);
		links.getList().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		links.setContentProvider(new LinkContentProvider());
						
		links.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				Link l = (Link)element;
				String d = l.getLinkedEntityID();
				try {
					NetEntity e = l.getLinkedEntity();
					if(e.hasName()) d += " ("+e.getEntityName()+")";
				} catch (NetIntegrityException e) {
				} 
				return d;
			}
		});
		
		
		links.getList().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.DEL) {
					triggerLinkDelete(links.getList().getSelectionIndex());
				}
			}
		});
		
		links.getList().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				Link l = (Link)links.getElementAt(links.getList().getSelectionIndex());
				LinkDialog dlg = new LinkDialog(getSite().getShell(),netmodel,l);
				dlg.open();
			}
		});
		
		Menu popUp = new Menu(links.getList());
		
		MenuItem item = new MenuItem(popUp,SWT.CASCADE);
		item.setText("Edit link paramters");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(links.getList().getSelectionIndex() < 0) return;
				Link l = (Link)links.getElementAt(links.getList().getSelectionIndex());
				LinkDialog dlg = new LinkDialog(getSite().getShell(),netmodel,l);
				dlg.open();				
			}
		});
		
		item = new MenuItem(popUp,SWT.CASCADE);
		item.setText("Delete link");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(links.getList().getSelectionIndex() < 0) return;
				triggerLinkDelete(links.getList().getSelectionIndex());
			}
		});
		
		item = new MenuItem(popUp,SWT.CASCADE);
		item.setText("Look up linked node");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(links.getList().getSelectionIndex() < 0) return;
				Link l = (Link)links.getElementAt(links.getList().getSelectionIndex());
				try {
					triggerFollowLink(l.getLinkedEntity());
				} catch (NetIntegrityException exc) {
					MindPlugin.getDefault().handleException(exc);	
				}
			}
		});
		
		item = new MenuItem(popUp,SWT.CASCADE);
		item.setText("Linked node to front");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(links.getList().getSelectionIndex() < 0) return;
				Link l = (Link)links.getElementAt(links.getList().getSelectionIndex());
				triggerToFront(l.getLinkedEntityID());
			}
		});
		
	
		links.getList().setMenu(popUp);				
		
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}
	
	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}
	
	public void triggerLinkDelete(int i) {
		Link l = currentGate.getLinkAt(i);
		
		try {
			netmodel.getNet().deleteLink(
				currentGate.getNetEntity().getID(), 
				currentGate.getType(), 
				l.getLinkedEntityID(), 
				l.getLinkedSlot().getType());
		} catch (Exception e) {
			MindPlugin.getDefault().handleException(e);
		}
		
		MindEditController.getInstance().triggerRedraw();
		
		links.refresh();
		
		IncomingLinksController.getInstance().setData(null);
		
	}
	
	public void triggerFollowLink(NetEntity linked) {
		MindEditController.getInstance().lookUpNode(linked.getID());
	}

	public void triggerToFront(String id) {
		MindEditController.getInstance().bringToFront(id);
	}

	public void triggerGateChange(Object o, Object newValue) {
		GateTableContent gtc = (GateTableContent)o;
		
		int netParameter = -1;
		
		try {
			switch(gtc.row) {
				case GateTableContent.ACTIVATION:
					netParameter = NetParametersIF.PARM_ENTITY_GATE_ACTIVATION;
					Double.parseDouble((String)newValue);
					break;		
				case GateTableContent.MAX:
					netParameter = NetParametersIF.PARM_ENTITY_GATE_MAX;
					Double.parseDouble((String)newValue);
					break;
				case GateTableContent.MIN:
					netParameter = NetParametersIF.PARM_ENTITY_GATE_MIN;
					Double.parseDouble((String)newValue);
					break;
				case GateTableContent.AMPF:
					netParameter = NetParametersIF.PARM_ENTITY_GATE_AMP;
					Double.parseDouble((String)newValue);
					break;
				case GateTableContent.GATEF:
					netParameter = NetParametersIF.PARM_ENTITY_GATE_FACTOR;
					Double.parseDouble((String)newValue);
					break;
				case GateTableContent.DECAY:
					netParameter = NetParametersIF.PARM_ENTITY_GATE_DECAYTYPE;
					Integer.parseInt((String)newValue);
					break;
				case GateTableContent.OUTPUTF:
					netParameter = NetParametersIF.PARM_ENTITY_GATE_OUTPUTFUNCTION;
				
					int index = ((Integer)newValue).intValue();
					newValue = GateOutputFunctions.getOutputFunctions().get(index).getClass().getName();
					break;
				case GateTableContent.OTHER:
					netParameter = NetParametersIF.PARM_ENTITY_GATE_OUTPUTFUNCTION_PARAMETER;
					Double.parseDouble((String)newValue);
					newValue=gtc.param+"="+newValue;
					break;
		
			}
		} catch (NumberFormatException e) {
			return;
		}
		
		try {
			netmodel.getNet().changeParameter(
				netParameter, 
				gtc.g.getNetEntity().getID(), 
				gtc.g.getType(), 
				(String)newValue);
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}
		
		gateProp.refresh();
		
	}

	/**
	 * @see org.micropsi.comp.console.controller.ViewControllerListenerIF#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {
		this.netmodel = (NetModel)o;
		setData(null);
	}

	/**
	 * @see org.micropsi.comp.console.controller.ViewControllerListenerIF#setData(java.lang.Object)
	 */
	public void setData(final Object o) {		
		if(o == null) {
			Display.findDisplay(uiThread).asyncExec(new Runnable() {
				public void run() {
					currentGate = null;
					gateProp.setInput(null);
					links.setInput(null);
				}
			});
		} else {
			Display.findDisplay(uiThread).asyncExec(new Runnable() {
				public void run() {
					currentGate = (Gate)o;
					gateProp.setInput(currentGate);
					links.setInput(currentGate);
				}
			});

		}		
	}

	public void selectLink(final Link link) {

		Display.findDisplay(uiThread).asyncExec(new Runnable() {
			public void run() {
				if(currentGate == null) return;		
				for(int i=0;i<currentGate.getNumberOfLinks();i++) {
					if(currentGate.getLinkAt(i) == link) {
						links.getList().select(i);
						break;
					}
				}
				links.getList().setFocus();
			}
		});
		
		
	}

}
