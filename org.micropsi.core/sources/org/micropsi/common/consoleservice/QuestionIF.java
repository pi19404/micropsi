/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/consoleservice/QuestionIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.consoleservice;

public interface QuestionIF {
	
	/**
	 * Execute the question, but do not return an answer.
	 */
	public static final int AM_DONT_ANSWER = 0;
	
	/**
	 * Do not execute the question and remove it from the list of stored answers (if there)
	 */
	public static final int AM_STOP_ANSWERING = 1;
	
	/**
	 * Execute the question and return the answer exactly once.
	 */
	public static final int AM_ANSWER_ONCE = 2;
	
	/**
	 * Execute-and-answer the question every step.
	 */
	public static final int AM_ANSWER_CONTINUOUSLY = 3;
	
	/**
	 * Execute-and-answer the question in the step the question is received and every 5th step from then on.
	 */
	public static final int AM_ANSWER_EVERY_5_STEPS = 4;

	/**
	 * Execute-and-answer the question in the step the question is received and every 10th step from then on.
	 */
	public static final int AM_ANSWER_EVERY_10_STEPS = 5;
	
	/**
	 * Execute-and-answer the question in the step the question is received and every 50th step from then on.
	 */
	public static final int AM_ANSWER_EVERY_50_STEPS = 6;
		
	/**
	 * Execute-and-answer the question in the step the question is received and every 100th step from then on.
	 */
	public static final int AM_ANSWER_EVERY_100_STEPS = 7;
		
	/**
	 * Returns the name/type of the question (must match one of the registered questionType's names)
	 * @return the question name
	 */
	public String getQuestionName();
	
	/**
	 * Returns an anrray of String parameters. Should not be null.
	 * @return the parameters of the question
	 */
	public String[] getParameters();
	
	/**
	 * Returns the parameter at "at". Should not throw any exceptions, but the returned value can be null.
	 * @return the requested parameter or null if there is no such parameter.
	 */
	public String retrieveParameter(int at);
	
	/**
	 * Returns the simulation step when the question was created.
	 * @return the simulation step.
	 */
	public long getStep();
	
	/**
	 * Returns the answer mode. For known answer modes see the AM_* constants in this interface.
	 * @return the answer mode.
	 */
	public int getAnswerMode();
	
	/**
	 * Returns the additional data belonging to the question (if there is such data, else returns null.)
	 * What that data is exactly depends on the questionType. It is also important to keep in mind that in
	 * most cases the additional data has to be transported somehow. So, if your questions are encoded into XML
	 * structures, the additionalData field must also be encodable.<br> As a rule of thumb: Do not use 
	 * additionalData if you can transmit the data you want to transmit as a list of Strings (use parameters then).
	 * If you can't do so - because you want to transmit tree structures or Strings with blanks or the like -
	 * you should use @see org.micropsi.comp.messages.MTreeNode to encode your data.<br> If this still
	 * does not fit your needs, you'll have to write your own (encodable) message object.
	 * @return the additional data if there is such data, els null.  
	 */
	public Object getAdditionalData();
	
	/**
	 * Returns the name of the component that created the question.
	 * @return the creator's name
	 */
	public String getOrigin();
    
    public String getDestination();

    public void setAnswerMode(int mode);
    
    public Object clone();
    
	public String getStorageKey();

}
