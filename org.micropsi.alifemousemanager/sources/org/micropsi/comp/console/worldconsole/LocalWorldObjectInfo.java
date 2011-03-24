package org.micropsi.comp.console.worldconsole;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.comp.console.AlifeManagerConsole;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.messages.MTreeNode;

public class LocalWorldObjectInfo implements AnswerHandlerIF {
	private static LocalWorldObjectInfo instance = null;
	    
	private AnswerQueueIF answerQueue;
	private ConsoleFacadeIF console = null;
	
	private boolean initialized = false;
	private int agentCount = 0;
	
	public static LocalWorldObjectInfo getInstance() {
        if(instance == null)
            instance = new LocalWorldObjectInfo();
        return instance;
    }
	
	private LocalWorldObjectInfo() {
		initializeConsole();
		subscribeForAgentList();
	}
	
	private void initializeConsole() {
        if(AlifeManagerConsole.getInstance() != null) {
            console = AlifeManagerConsole.getInstance().getConsole();
            answerQueue = new SWTAwareAnswerQueue(this);
            initialized = true;
        }
    }
	
	public void subscribeForAgentList() {
        if(!initialized)
            initializeConsole();
        console.subscribe(100, "world", "getagentlist", "", answerQueue);
    }
	
	public void handleAnswer(AnswerIF answer) {
		if (answer.getAnsweredQuestion().getQuestionName().equals("getagentlist")) {
            if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
				MTreeNode node = (MTreeNode) answer.getContent();
				if (node != null && node.getName() != null && node.getName().equals("agents")) {
					agentCount = node.getChildren().length;
				}
            }
        }
	}

	public int getAgentCount() {
		return agentCount;
	}
}
