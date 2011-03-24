/*
 * Created on 03.03.2003
 *
 */
package org.micropsi.comp.console;


/**
 * @author daniel
 */
public class NoAnswer extends CachedAnswer {
    
    private long step;
    
    public NoAnswer(long step){
        this.step = step;
    }

	/* (non-Javadoc)
	 * @see org.micropsi.common.consoleservice.AnswerIF#getStep()
	 */
	public long getStep() {
        return step;
	}

}
