package org.micropsi.comp.console.mini;

import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MQuestion;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public abstract class AbstractConsoleExtension {

	private ExtensibleConsoleComponent component;

	protected void setComponent(ExtensibleConsoleComponent component) {
		this.component = component;
	}

	protected void askQuestion(MQuestion question) {
		question.setOrigin(component.getComponentID());
		component.sendQuestion(question);
	}

	protected void askQuestion(String question, String destination, String parameters) {
		MQuestion q = new MQuestion();
		q.setDestination(destination);
		q.setQuestionName(question);
		q.setOrigin(component.getComponentID());
		StringTokenizer tokener = new StringTokenizer(parameters, " ");
		String[] p = new String[tokener.countTokens()];
		for(int i=0;i<p.length;i++) 
			p[i] = tokener.nextToken();
		
		askQuestion(q);
	}
	
	public Logger getLogger() {
		return component.getLogger();
	}
	
	public ExceptionProcessor getExproc() {
		return component.getExproc();
	}
	
	public String getConsoleComponentID() {
		return component.getComponentID();
	}
	
	protected abstract void receiveAnswer(MAnswer answer);
}
