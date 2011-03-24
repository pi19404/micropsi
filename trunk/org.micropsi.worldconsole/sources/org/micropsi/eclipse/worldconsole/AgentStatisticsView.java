/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 06.04.2003
 *
 */
package org.micropsi.eclipse.worldconsole;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.console.command.AnswerQueue;
import org.micropsi.eclipse.console.command.IConsoleWorkbenchPart;

/**
 * @author matthias
 *
 * Viewpart for watching properties of a selected world object and changing them.
 */
public class AgentStatisticsView extends ViewPart implements IConsoleWorkbenchPart, ModifyListener {
	
	private AnswerQueue answerQueue;
	
	private boolean disposed = false;

	private Combo agentSelectCombo;
	private Table statisticsTable;
	
	private Map<String,String> agents = new HashMap<String,String>(5);
	private long currentAgentId = -1;


	/**
	 * 
	 */
	public AgentStatisticsView() {
		super();
		answerQueue = new AnswerQueue(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		Composite myComposite = new Composite(parent, SWT.NONE);
		GridLayout globalLayout = new GridLayout(1, false);
		myComposite.setLayout(globalLayout);
		
		Composite agentSelectRow = new Composite(myComposite, SWT.NONE);
		RowLayout agentSelectLayout = new RowLayout(SWT.HORIZONTAL);
		agentSelectRow.setLayout(agentSelectLayout);
		
		Label label = new Label(agentSelectRow, SWT.NONE);
		label.setText("Agent:");
		agentSelectCombo = new Combo(agentSelectRow, SWT.DROP_DOWN | SWT.READ_ONLY);
		agentSelectCombo.addModifyListener(this);
		statisticsTable = new Table(myComposite, SWT.SINGLE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		statisticsTable.setLayoutData(gridData);
		statisticsTable.setHeaderVisible(false);
		TableColumn col = new TableColumn(statisticsTable, SWT.LEFT);
		col.setWidth(150);
		col = new TableColumn(statisticsTable, SWT.LEFT);
		col.setWidth(150);
		
		subscribeAgentList();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}
	
	protected void processStatisticsUpdate(MTreeNode update) {
		int i = 0;
		for (Iterator<MTreeNode> children = update.children(); children.hasNext(); ) {
			MTreeNode node = children.next();
			TableItem item;
			if (i >= statisticsTable.getItemCount()) {
				item = new TableItem(statisticsTable, SWT.NONE);
			} else {
				item = statisticsTable.getItem(i);
			}
			String key = node.getName();
			String value = node.getValue();
			if (!key.equals(item.getText(0))) {
				item.setText(0, key);
			}
			if (!value.equals(item.getText(1))) {
				item.setText(1, value);
			}
			i++;
		}
		while (statisticsTable.getItemCount() > i) {
			statisticsTable.remove(statisticsTable.getItemCount() - 1);
		}
	}
	
	/**
	 * 
	 */
	private void unsubscribeStatistics() {
		if (currentAgentId >= 0) {
			WorldPlugin.getDefault().getConsole().unsubscribe(
				"world",
				"getobjectproperties",
				Long.toString(currentAgentId),
				answerQueue);
		}
	}

	/**
	 * 
	 */
	private void subscribeStatistics() {
		if (currentAgentId >= 0) {
			WorldPlugin.getDefault().getConsole().subscribe(
				50,
				"world",
				"getagentstatistics",
				Long.toString(currentAgentId),
				answerQueue);
		}
	}
	
	private void subscribeAgentList() {
		WorldPlugin.getDefault().getConsole().subscribe(100, "world", "getagentlist", "", answerQueue);
	}

//	private void askStatistics(long id) {
//		WorldPlugin.getDefault().getConsole().getInformation(
//			0,
//			"world",
//			"getagentstatistics",
//			Long.toString(id),
//			answerQueue);
//	}
	
	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.ConsoleWorkbenchPartIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
	 */
	public void handleAnswer(AnswerIF answer) {
		if (disposed) {
			return;
		}
		if (answer.getAnsweredQuestion().getQuestionName().equals("getagentlist")) {
			if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
				processAgentListUpdate((MTreeNode) answer.getContent());
			}
			//@todo3 error handling
		}
		if (answer.getAnsweredQuestion().getQuestionName().equals("getagentstatistics")) {
			if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
				try {
					MTreeNode node = (MTreeNode) answer.getContent();
					if (node.getValue() != null) {
						double id = Double.parseDouble(node.getValue());
						if (id == currentAgentId) {
							processStatisticsUpdate((MTreeNode) answer.getContent());
						}
					}
				} catch (NumberFormatException e) {
					//@todo3 error handling
				}
			}
			//@todo3 error handling
		}
	}

	/**
	 * @param content
	 */
	protected void processAgentListUpdate(MTreeNode content) {
		agents.clear();
		int selectionIndex = agentSelectCombo.getSelectionIndex();
		String selectedAgent;
		if (selectionIndex >= 0) {
			selectedAgent = agentSelectCombo.getItem(selectionIndex);
		} else {
			selectedAgent = null;
		}
		agentSelectCombo.removeAll();
		for (Iterator<MTreeNode> i = content.children(); i.hasNext(); ) {
			MTreeNode agentNode = i.next();
			agents.put(agentNode.getName(), agentNode.getValue());
			agentSelectCombo.add(agentNode.getName());
		}
		if (selectedAgent != null) {
			int itemToSelect;
			for (itemToSelect = 0; itemToSelect < agentSelectCombo.getItemCount() - 1; itemToSelect++) {
				if (agentSelectCombo.getItem(itemToSelect).equals(selectedAgent)) {
					break;
				}
			}
			if (itemToSelect < agentSelectCombo.getItemCount()) {
				agentSelectCombo.select(itemToSelect);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		WorldPlugin.getDefault().getConsole().unsubscribeAll(answerQueue);
		disposed = true;
		super.dispose();
	}

	/* @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)*/
	public void modifyText(ModifyEvent event) {
		String  agentString = agentSelectCombo.getText();
		long agentId;
		if (agentString == "") {
			agentId = -1;
		} else {
			String idString = agents.get(agentString);
			if (idString == null) {
				agentId = -1;
			} else {
				try {
					agentId = Long.parseLong(idString);
				} catch (NumberFormatException e) {
					agentId = -1;
				}
			}
		}
		if (agentId == -1 && agentString.length() > 0) {
			agentSelectCombo.setText("");
		}
		setCurrentAgentId(agentId);
	}

	/**
	 * @param agentId
	 */
	protected void setCurrentAgentId(long agentId) {
		if (currentAgentId != agentId) {
			if (currentAgentId >= 0) {
				unsubscribeStatistics();
			}
			currentAgentId = agentId;
			if (currentAgentId >= 0) {
				subscribeStatistics();
			}
		}
	}


}
