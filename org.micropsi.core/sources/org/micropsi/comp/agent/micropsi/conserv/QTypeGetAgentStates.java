package org.micropsi.comp.agent.micropsi.conserv;

import java.util.List;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.AgentStateRepository;
import org.micropsi.comp.messages.MTreeNode;

/**
 * 
 * 
 * 
 */
public class QTypeGetAgentStates implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getstates";
	private AgentStateRepository states;
	
	public QTypeGetAgentStates(AgentStateRepository states) {
		this.states = states;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		MTreeNode response = new MTreeNode("states","",null);
		
		List<String> slist = states.getAgentStates();
		for(int i=0;i<slist.size();i++) {
			String s = slist.get(i);
			response.addChild("state",s);
		}

		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,response,step);
	}

}
