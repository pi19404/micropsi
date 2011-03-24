/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/model/net/EntityModel.java,v 1.5 2005/10/20 14:03:24 vuine Exp $ 
 */
package org.micropsi.eclipse.model.net;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.nodenet.NetEntity;


public class EntityModel {
	
	private NetModel net;
	private int x = 0;
	private int y = 0;
	private int z = 0;
	
	private String comment = "";
	
	private NetEntity entity; 
	
	protected EntityModel(NetModel net, NetEntity entity) {
		this.net = net;
		this.entity = entity;
	}
	
	public NetModel getUnderlyingNetModel() {
		return net;
	}

	/**
	 * @return String
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return NetEntity
	 */
	public NetEntity getEntity() {
		return entity;
	}

	/**
	 * Use 100 for the true value.
	 * @param scalingInPercent the scale value
	 * @return int scaled x value
	 */
	public int getX(int scalingInPercent) {
		return x*scalingInPercent/100;
	}

	/**
	 * @return int 
	 */
	public int getX() {
		return x;
	}

	/**
	 * Use 100 for the true value.
	 * @param scalingInPercent the scale value
	 * @return int scaled y value
	 */
	public int getY(int scalingInPercent) {
		return y*scalingInPercent/100;
	}

	/**
	 * @return int 
	 */

	public int getY() {
		return y;
	}

	/**
	 * @return int
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Sets the comment.
	 * @param comment The comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Sets the x. Because a different value might be used for display,
	 * scaling applies here. (Use 100 for the true value.)
	 * @param x The x to set
	 * @param scalingInPercent scaling value
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Sets the x. 
	 * @param x The x to set
	 */

	public void setX(int x, int scalingInPercent) {
		if (scalingInPercent < 1) scalingInPercent = 1;
		this.x = x * 100 / scalingInPercent;
	}

	/**
	 * Sets the y. Because a different value might be used for display,
	 * scaling applies here. (Use 100 for the true value.)
	 * @param y The y to set
	 * @param scalingInPercent scaling value
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Sets the y. 
	 * @param y The y to set
	 */
	public void setY(int y, int scalingInPercent) {
		if (scalingInPercent < 1) scalingInPercent = 1;
		this.y = y * 100 / scalingInPercent;
	}

	/**
	 * Sets the z.
	 * @param z The z to set
	 */
	public void setZ(int z) {
		this.z = z;
	}

	public void destroy() {
//		entity = null;
//		comment = null;
//		net = null;
	}

	public MTreeNode getInnerStates() {
				
		MQuestion q = new MQuestion();
		q.setQuestionName("getinnerstates");
		q.setDestination(AgentManager.getInstance().getCurrentAgent());
		q.setParameters(new String[]{entity.getID()});
		
		try {
			AnswerIF answer = MindPlugin.getDefault().getConsole().askBlockingQuestion(q);
			MTreeNode innerstates = (MTreeNode)answer.getContent(); 
			return innerstates;
		} catch (MicropsiException e) {
			return null;
		}		

	}


	public void changeInnerState(String key, String value) {
		MindPlugin.getDefault().getConsole().sendCommand(
			ConsoleFacadeIF.ZERO_TOLERANCE, 
			AgentManager.getInstance().getCurrentAgent(), 
			"changeinnerstate", 
			entity.getID()+" "+key+" "+value,
			false);
			
	}
}
