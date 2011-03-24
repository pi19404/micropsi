/*
 * Created on 15.04.2003
 *
 */
package org.micropsi.comp.console;

/**
 * @author daniel
 */
public abstract class AbstractAnswerQueue implements AnswerQueueIF {
    
    protected AnswerHandlerIF part;
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean ret = false;
        if(obj instanceof AbstractAnswerQueue){
            AbstractAnswerQueue q = (AbstractAnswerQueue) obj;
            ret |= //Utilities.equiv(q.getHandle(), this.handle) && 
                   Utilities.equiv(q.getHandler(), this.part);
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return Utilities.key(part);
    }

	/**
	 * @return AnswerHandlerIF
	 */
	public AnswerHandlerIF getHandler() {
		return part;
	}

}
