package org.micropsi.comp.console.worldconsole;


import org.eclipse.swt.widgets.Display;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.DefaultAnswerQueue;

/**
 * 
 *
 */
public class SWTAwareAnswerQueue extends DefaultAnswerQueue {


    /**
	 * Method SWTAwareAnswerQueue.
	 * @param part
	 */
	public SWTAwareAnswerQueue(AnswerHandlerIF part){
		super(part);
    }
    
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.console.AnswerQueueIF#handleAnswers()
	 */
	public void handleAnswers(){
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {	
				SWTAwareAnswerQueue.super.handleAnswers();
			}
		});
	}
}
