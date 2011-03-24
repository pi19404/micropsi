package org.micropsi.comp.console;

import org.micropsi.comp.messages.MQuestion;

/**
 * @author daniel
 *
 */
public class NonBlockingQuestion implements Runnable {

	protected MQuestion question;
	protected ConsoleComponent component;

	/**
	 * Constructor NonBlockingQuestion.
	 * @param q - The question.
	 */
	public NonBlockingQuestion(MQuestion q, ConsoleComponent component) {
		this.question = q;
		this.component = component;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		component.askRemoteQuestion(question);
	}
}
