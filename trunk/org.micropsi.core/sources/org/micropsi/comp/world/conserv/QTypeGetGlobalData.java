/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetGlobalData.java,v 1.2 2005/01/02 23:06:23 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.coordinates.Area2D;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.WorldComponent;

/**
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Handles questions with name 'getglobaldata'. Returns a MTreeNode containing visibility bounds,
 * groundmap bounds and groundmap filename.
 */
public class QTypeGetGlobalData implements ConsoleQuestionTypeIF {
	
	private WorldComponent worldComponent;
	
	public QTypeGetGlobalData(WorldComponent worldComponent) {
		this.worldComponent = worldComponent;
	}

	public String getQuestionName() {
		return "getglobaldata";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		MTreeNode toReturn = new MTreeNode("success", null, null);
		toReturn.addChild(new MTreeNode("filename", worldComponent.getWorld().getFileName(), null));
		MTreeNode groundMapNode = new MTreeNode("groundmap", null, null);
		toReturn.addChild(groundMapNode);
		groundMapNode.addChild(new Area2D(worldComponent.getWorld()
				.getGroundMap().getLowestCoords(), worldComponent.getWorld()
				.getGroundMap().getHighestCoords()).toMTreeNode("area"));
		groundMapNode.addChild("image filename", worldComponent.getWorld().getGroundMap().getImageFileName());

		MTreeNode visibleAreaNode = worldComponent.getWorld().getVisibleArea().toMTreeNode("visible area");
		toReturn.addChild(visibleAreaNode);
		
		toReturn.addChild(new MTreeNode("version", Integer.toString(worldComponent.getWorld().getVersionOfGlobalData()), null));
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
