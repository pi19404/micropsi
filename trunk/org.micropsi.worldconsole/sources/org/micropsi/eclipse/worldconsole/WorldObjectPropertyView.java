/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 06.04.2003
 *
 */
package org.micropsi.eclipse.worldconsole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.comp.console.worldconsole.EditSession;
import org.micropsi.comp.console.worldconsole.IEditSessionListener;
import org.micropsi.comp.console.worldconsole.LocalWorld;
import org.micropsi.comp.console.worldconsole.model.AbstractWorldObject;
import org.micropsi.comp.console.worldconsole.model.WorldObject;
import org.micropsi.comp.console.worldconsole.model.WorldObjectProperties;
import org.micropsi.comp.console.worldconsole.model.WorldObjectProperty;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.console.command.AnswerQueue;
import org.micropsi.eclipse.console.command.IConsoleWorkbenchPart;

/**
 * @author matthias
 *
 * Viewpart for watching properties of a selected world object and changing them.
 */
public class WorldObjectPropertyView extends ViewPart implements IConsoleWorkbenchPart, IEditSessionListener {
	
	private static final String DEFAULT_TEXT_ID = "(none)";
	private static int updateID = 0;
	
	protected EditSession editSession = null;
	private AnswerQueue answerQueue;
	
	private long currentObjectId = -1;
	private WorldObjectProperties currentProperties;
	private Label objectIDLabel;
	private TableViewer propertyTable;
	private boolean disposed = false;


	/**
	 * Class for mapping a WorldObjectProperty to text displayed in the table.
	 */
	public class PropertyLabelProvider implements ITableLabelProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			WorldObjectProperty prop = (WorldObjectProperty) element;
			if (prop == null) {
				return null;
			} else {
				return (columnIndex == 0)? prop.getKey() : 
					(columnIndex ==1 ) ? prop.getValue() : prop.getComment() == null? "" : prop.getComment();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
		}

	}

	/**
	 * Class delivers as rows for the table many WorldObjectProperty objects from a WorldObjectProperties
	 * object.
	 */
	public class PropertyContentProvider
		implements IStructuredContentProvider {
		
		/**
		 * 
		 */
		public PropertyContentProvider() {
			super();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			if (currentProperties == null) {
				return new Object[0];
			}
			Object[] result = new Object[currentProperties.getPropertyCount()];
			Iterator it = currentProperties.iterator();
			int i = 0;
			while(it.hasNext()) {
				result[i] = it.next();
				i++;
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			currentProperties = (WorldObjectProperties) newInput;
		}

	}
	
	
	/**
	 * Class handles modifying of a WorldObjectProperty object value within the table. It does this by
	 * calling appropriate methods in the enclosing class.
	 *
	 */
	public class PropertyCellModifier implements ICellModifier {

		/**
		 * 
		 */
		public PropertyCellModifier() {
			super();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
		 */
		public boolean canModify(Object element, String property) {
			return (element != null && property.equals("value") && ((WorldObjectProperty) element).isEditable());
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
		 */
		public Object getValue(Object element, String property) {
			if (element != null && property.equals("value")) {
				return ((WorldObjectProperty) element).getValue();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
		 */
		public void modify(Object element, String property, Object value) {
			WorldObjectProperty objectProp = (WorldObjectProperty) ((TableItem) element).getData();
			if (property != null && property.equals("value")) {
				triggerPropertyChangedByUser(objectProp, (String) value);
			}

		}

	}
	



	/**
	 * 
	 */
	public WorldObjectPropertyView() {
		super();
		answerQueue = new AnswerQueue(this);
		editSession = WorldConsole.getInstance().getGlobalData().getEditSession();
		currentProperties = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		Composite myComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		myComposite.setLayout(gridLayout);
		
		Label label = new Label(myComposite, SWT.NONE);
		label.setText("object id");
				
		objectIDLabel = new Label(myComposite, SWT.NONE);
		objectIDLabel.setText(DEFAULT_TEXT_ID);
		objectIDLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(myComposite, SWT.NONE);
		label.setText("Properites:");

		propertyTable = new TableViewer(myComposite);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 150;
		data.horizontalSpan = 2;
		propertyTable.getTable().setLayoutData(data);
		propertyTable.getTable().setLinesVisible(true);

		new TableColumn(propertyTable.getTable(),SWT.NONE).setWidth(80);
		new TableColumn(propertyTable.getTable(),SWT.NONE).setWidth(120);
		new TableColumn(propertyTable.getTable(),SWT.NONE).setWidth(120);
		String[] cprop = {"name", "value", "error"};
		propertyTable.setColumnProperties(cprop);
		CellEditor[] cellEditors = {null,new TextCellEditor(propertyTable.getTable()), null};
		propertyTable.setCellEditors(cellEditors);
		propertyTable.setCellModifier(new PropertyCellModifier());

		propertyTable.setCellEditors(cellEditors);

		propertyTable.setLabelProvider(new PropertyLabelProvider());
		propertyTable.setContentProvider(new PropertyContentProvider());
		
		editSession.registerView(this);
		setObject(-1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}
	
	private void triggerPropertyChangedByUser(WorldObjectProperty property, String newValue) {
		if (!property.getValue().equals(newValue)) {
			property.setValue(newValue);
			property.setComment("updating...");
			WorldObjectProperties changedProperties = new WorldObjectProperties(currentProperties.getObjectId());
			changedProperties.addProperty(property);
			setRemoteProperties(changedProperties);
			propertyTable.refresh();
		}
	}
	
	private void triggerPropertiesChanged(WorldObjectProperties properties) {
		if (disposed) {
			return;
		}
		if (currentProperties != properties || properties == null) {
			currentProperties = properties;
			propertyTable.setInput(properties);
			if (currentProperties !=  null) {
				currentProperties.clearUpdateState();
			}
		} else {
			if (currentProperties.isListStructureUpdated()) {
				propertyTable.refresh();
				currentProperties.clearUpdateState();
			} else {
				ArrayList<WorldObjectProperty> updatedProperties = new ArrayList<WorldObjectProperty>(currentProperties.getPropertyCount());
				Iterator it = currentProperties.iterator();
				while (it.hasNext()) {
					WorldObjectProperty p = (WorldObjectProperty) it.next();
					if (p.isUpdated()) {
						updatedProperties.add(p);
						p.setUpdated(false);
					}
				}
				if (!updatedProperties.isEmpty()) {
					propertyTable.update(updatedProperties.toArray(), null);
				}
			}
		}
	}
	
	public void setObject(long newId) {
		if (newId != currentObjectId) {
			unsubscribeRemoteProperties();
			currentObjectId = newId;
			if (currentObjectId < 0) {
				objectIDLabel.setText(DEFAULT_TEXT_ID);
			} else {
				objectIDLabel.setText(Long.toString(currentObjectId));
			}
		
			triggerPropertiesChanged(null);
//			askRemoteProperties(newId);
			subscribeRemoteProperties();
		}
	}
	
	/**
	 * 
	 */
	private void unsubscribeRemoteProperties() {
		if (currentObjectId >= 0) {
			WorldPlugin.getDefault().getConsole().unsubscribe(
				"world",
				"getobjectproperties",
				Long.toString(currentObjectId),
				answerQueue);
		}
		
	}

	/**
	 * 
	 */
	private void subscribeRemoteProperties() {
		if (currentObjectId >= 0) {
			WorldPlugin.getDefault().getConsole().subscribe(
				10,
				"world",
				"getobjectproperties",
				Long.toString(currentObjectId),
				answerQueue);
		}
		
	}

	private void askRemoteProperties(long id) {
		WorldPlugin.getDefault().getConsole().getInformation(
			0,
			"world",
			"getobjectproperties",
			Long.toString(id),
			answerQueue);
	}
	
	private void setRemoteProperties(WorldObjectProperties changedProperties) {
		Iterator it = changedProperties.iterator();
		while (it.hasNext()) {
			WorldObjectProperty p = (WorldObjectProperty) it.next();
			p.setCurrentUpdateId(updateID);
			p.setNoUpdate(true);
		}
		WorldPlugin.getDefault().getConsole().getInformation(
			0,
			"world",
			"changeobjectproperties",
			Long.toString(changedProperties.getObjectId())
				+ " "
				+ Integer.toString(updateID++),
			answerQueue,
			changedProperties.toMTreeNode());
		if (updateID > 1000000) {
			updateID = 0;
		}
	}
	

	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.ConsoleWorkbenchPartIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
	 */
	public void handleAnswer(AnswerIF answer) {
		if (answer.getAnsweredQuestion().getQuestionName().equals("getobjectproperties")) {
			if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE && !propertyTable.isCellEditorActive()) {
				MTreeNode node = (MTreeNode) answer.getContent();
				if (node.getName().equals("success")) {
					WorldObjectProperties newProperties = new WorldObjectProperties(node.getChildren()[0]);
					if (newProperties.getObjectId() == currentObjectId) {
						if (currentProperties == null) {
							triggerPropertiesChanged(newProperties);
						} else {
							currentProperties.updateDataBy(newProperties);
							triggerPropertiesChanged(currentProperties);
						}
					} // ignore properties for other objects
				}
				
			}
		} else if (answer.getAnsweredQuestion().getQuestionName().equals("changeobjectproperties")) {
			if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
				MTreeNode node = (MTreeNode) answer.getContent();
				if (node.getName().equals("error")) {
					WorldObjectProperties wrongProperties = new WorldObjectProperties(node.getChildren()[0]);
					if (wrongProperties.getObjectId() == currentObjectId && currentProperties != null) {
						Iterator it = wrongProperties.iterator();
						while (it.hasNext()) {
							WorldObjectProperty propRes = (WorldObjectProperty) it.next();
							WorldObjectProperty prop = currentProperties.getProperty(propRes.getKey());
							if (prop == null) {
								currentProperties.addProperty(propRes);
							} else {
								prop.setComment(propRes.getComment());
							}
						}
						propertyTable.refresh();
					} else { // changeobjectproperties failed, but selection has changed since then
						QuestionErrorHandler.getInstance().handleAnswer(answer);
					}
				}
			} else if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_OK) {
				String[] parameters = answer.getAnsweredQuestion().getParameters();
				if (parameters.length >= 2 && Long.parseLong(parameters[0]) == currentObjectId && currentProperties != null) {
					int questionId = Integer.parseInt(parameters[1]);
					Iterator it = currentProperties.iterator();
					while (it.hasNext()) {
						WorldObjectProperty p = (WorldObjectProperty) it.next();
						if (p.getCurrentUpdateId() == questionId) {
							p.setComment("");
							p.setNoUpdate(false);
						}
					}
					triggerPropertiesChanged(currentProperties);
				}
			} else {
				QuestionErrorHandler.getInstance().handleAnswer(answer);
			}
		} else {
			QuestionErrorHandler.getInstance().handleAnswer(answer);
		}

	}

	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.plugin.worldperspective.WorldSelectionChangeListenerIF#OnSelectionChanged(de.artificialemotion.comp.consoleapp.plugin.worldperspective.WorldObjectMgr)
	 */
	public void onSelectionChanged(EditSession session, Collection changeList) {
		if (!session.getSelectedObjectParts().isEmpty()) {
			AbstractWorldObject obj = session.getSelectedObjectPart();
			setObject(obj.getId());
		} else {
			setObject(-1);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		unsubscribeRemoteProperties();
		disposed = true;
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#OnObjChanged(org.micropsi.eclipse.worldconsole.LocalWorld, org.micropsi.eclipse.worldconsole.WorldObject, org.micropsi.eclipse.worldconsole.WorldObject)
	 */
	public void onObjectChanged(LocalWorld mgr, WorldObject object) {
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#OnObjListRefreshed(org.micropsi.eclipse.worldconsole.LocalWorld)
	 */
	public void onObjectListRefreshed(LocalWorld mrg) {
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.console.controller.IViewControllerListener#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.console.controller.IViewControllerListener#setData(java.lang.Object)
	 */
	public void setData(Object o) {
		setObject(-1);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#OnMultipleObjectsChanged(org.micropsi.eclipse.worldconsole.LocalWorld, java.util.List)
	 */
	public void onMultipleObjectsChanged(LocalWorld mgr, Collection changedObjects) {
	}

	/* @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#onGlobalsChanged()*/
	public void onGlobalsChanged() {
		// TODO Auto-generated method stub
		
	}

}
