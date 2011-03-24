/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/VisualSituationWidget.java,v 1.8 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.util.Iterator;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.common.model.IAgentChangeListener;
import org.micropsi.eclipse.console.command.AnswerQueue;
import org.micropsi.eclipse.mindconsole.MindConsole;

public class VisualSituationWidget extends Composite implements AnswerHandlerIF {

	private String agentName;
	private Color bgColor;
	private Color drawColor;
	private Color foveaColor;
	private Color attentionColor;
	private ConsoleFacadeIF console;
	private AnswerQueueIF answerQueue;
	
	private MTreeNode situation;

	public VisualSituationWidget(Composite parent, int style, String agentName) {
		super(parent, style);
		this.agentName = agentName;
		
		console = MindConsole.getInstance().getConsole();
		
		RowLayout layout = new RowLayout();
		layout.spacing = 10;
		layout.marginTop = 10;
		layout.marginLeft = 10;
		layout.pack = true;
		this.setLayout(layout);
		this.setFont(parent.getFont());
		
		foveaColor = new Color(null,255,0,0);
		attentionColor = new Color(null,0,0,255);
		drawColor = new Color(null,255,255,255);
		bgColor = new Color(null,0,0,0);
		this.setBackground(bgColor);
		this.setSize(100, 100);
				
		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				paintSituation(e);
			}
		});		
		
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				bgColor.dispose();
				drawColor.dispose();
				foveaColor.dispose();
			}
		});
		
		agentName = AgentManager.getInstance().getCurrentAgent();
		answerQueue = new AnswerQueue(this);
		
		console.subscribe(
			5,
			agentName,
			"getsituation", "", 
			answerQueue);
		
		AgentManager.getInstance().addAgentChangeListener(new IAgentChangeListener() {

			public void agentSwitched(String newAgentID) {
				console.unsubscribe(
					getCurrentAgentName(),
					"getsituation", "", 
					answerQueue);
				
				setCurrentAgentName(newAgentID);

				console.subscribe(
					5,
					getCurrentAgentName(),
					"getsituation", "", 
					answerQueue);
			}

			public void agentDeleted(String deletedAgentID) {
				if(deletedAgentID.equals(getCurrentAgentName())) {
					console.unsubscribe(
						getCurrentAgentName(),
						"getsituation", "", 
						answerQueue);					
					situation = null;
				}
			}
			
		});
			
	}
	
	private void setCurrentAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	private String getCurrentAgentName() {
		return agentName;
	}

	protected void paintSituation(PaintEvent e) {		
		
		e.gc.setBackground(bgColor);
		e.gc.setForeground(drawColor);
		
		if(situation == null) return;
		
		int scale = 20;
		float TOLERANCE = 0.5f;
		
		int selfx = e.width / 2;
		int selfy = e.height / 2;
		
		e.gc.fillRectangle(0,0,e.width,e.height);
		
		e.gc.drawLine(selfx-2, selfy, selfx+2, selfy);
		e.gc.drawLine(selfx, selfy-2, selfx, selfy+2);
		
		MTreeNode elementsNode = situation.searchChild("elements");
		Iterator<MTreeNode> children = elementsNode.children();
		if(children != null) {
			while(children.hasNext()) {
				MTreeNode element = children.next();
				int x = (int)Math.round((Double.parseDouble(element.searchChild("x").getValue())*scale))+selfx;
				int y = -(int)Math.round((Double.parseDouble(element.searchChild("y").getValue())*scale))+selfy;
				int size = Math.round(TOLERANCE * scale);
			
				e.gc.drawRectangle(x-(size/2), y-(size/2), size, size);
				e.gc.drawString(element.getName(), x+size+2, y-(size/2));
			}
		}
		
		MTreeNode foveaNode = situation.searchChild("fovea");
		int x = (int)Math.round((Double.parseDouble(foveaNode.searchChild("x").getValue())*scale))+selfx;
		int y = -(int)Math.round((Double.parseDouble(foveaNode.searchChild("y").getValue())*scale))+selfy;		

		e.gc.setForeground(foveaColor);
		e.gc.drawLine(x-2, y, x+2, y);
		e.gc.drawLine(x, y-2, x, y+2);

		MTreeNode attentionNode = situation.searchChild("attention");
		x = (int)Math.round((Double.parseDouble(attentionNode.searchChild("x").getValue())*scale))+selfx;
		y = -(int)Math.round((Double.parseDouble(attentionNode.searchChild("y").getValue())*scale))+selfy;		

		e.gc.setForeground(attentionColor);
		e.gc.drawOval(x-3, y-3, 5, 5);

	}

	public void handleAnswer(AnswerIF a) {
		this.situation = (MTreeNode)a.getContent();
		
		//Display.findDisplay(uiThread).syncExec(createCommitter);
		if(!isDisposed())
			redraw();
	}
}
