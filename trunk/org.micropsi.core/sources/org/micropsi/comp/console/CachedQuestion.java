package org.micropsi.comp.console;

import org.micropsi.common.consoleservice.QuestionIF;


/**
 * @author daniel
 * 
 * This class is a lightweight question for caching.
 * @see org.micropsi.comp.common.messages.MQuestion.
 * 
 * It has no origin and no answerMode and no step. Parameters are saved as
 * string.
 *
 */
public class CachedQuestion {
	private String parameters;
	private Object additionalData;
	private String questionname;
	private String destination;

	public CachedQuestion(
		String destination,
		String question,
		String parameterString) {
		this.parameters = parameterString;
		this.questionname = question;
		this.destination = destination;
		this.additionalData = null;
	}
	
	public CachedQuestion(QuestionIF q){
		String[] parameters = q.getParameters();
		int n = parameters.length - 1;
		for(int i=0;i<n;i++)
			this.parameters += parameters[i] + " ";
        if(n >= 0)
		  this.parameters += parameters[n];
		
		this.additionalData = q.getAdditionalData();
		this.questionname = q.getQuestionName();
		this.destination = q.getDestination();
	}
	
    
	public String getQuestionName() {
		return questionname;
	}

	public void setQuestionName(String questionname) {
		this.questionname = questionname;
	}

	protected String getParameters() {
		return parameters;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Object getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(Object additionalData) {
		this.additionalData = additionalData;
	}

    

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
        boolean ret = false;
        CachedQuestion q;
        if(obj instanceof CachedQuestion){
            q = (CachedQuestion) obj;
            ret |= Utilities.equiv(q.getQuestionName()==null,this.questionname==null)
            && Utilities.equiv(q.getDestination()==null, this.destination==null)
            && Utilities.equiv(q.getParameters()==null, this.parameters==null)
            && Utilities.equiv(q.getAdditionalData()==null, this.additionalData==null)
            && (this.questionname!=null?this.questionname.equals(q.getQuestionName()):true)
            && (this.destination!=null?this.questionname.equals(q.getQuestionName()):true)
            && (this.parameters!=null?this.parameters.equals(q.getParameters()):true)
            && (this.additionalData!=null?this.additionalData.equals(q.getAdditionalData()):true);   
        }
        return ret;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (questionname != null? questionname.hashCode():0) 
        + (parameters != null? parameters.hashCode():0) 
        + (destination != null? destination.hashCode():0) 
        + (additionalData != null?additionalData.hashCode():0);
	}

}
