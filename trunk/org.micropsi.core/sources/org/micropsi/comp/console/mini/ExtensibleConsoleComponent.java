package org.micropsi.comp.console.mini;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.AnswerDispatcherIF;
import org.micropsi.comp.common.TouchCreator;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MConsoleReq;
import org.micropsi.comp.messages.MConsoleResp;
import org.micropsi.comp.messages.MQuestion;

/**
 * @author rvuine
 */
public class ExtensibleConsoleComponent extends AbstractComponent implements AnswerDispatcherIF {

	private TouchCreator toucher;
	private ArrayList<AbstractConsoleExtension> extensions = new ArrayList<AbstractConsoleExtension>();

	protected void performInitialisation() throws MicropsiException {
		toucher = new TouchCreator("consoleserverclient",this,this,null);
		toucher.start();
		
		String extstr = config.getConfigValue(prefixKey + ".extensions");
		StringTokenizer tokener = new StringTokenizer(extstr,",");
		while(tokener.hasMoreTokens()) {
			String classname = tokener.nextToken();
			if(classname.indexOf(".") < 0)
				classname = "org.micropsi.comp.console.extensions."+classname;
			
			try {
				Object o = Class.forName(classname).newInstance();
				AbstractConsoleExtension ext = (AbstractConsoleExtension)o;
				ext.setComponent(this);
				extensions.add(ext);
			} catch (Exception e) {
				logger.error("Did not activate extension: "+classname,e);
			}
		}
		
	}

	protected int getInnerType() {
		return 0;
	}

	public void dispatchAnswers(List<AnswerIF> answers) {
		for(int i=0;i<answers.size();i++) {
			for(int j=0;j<extensions.size();j++) {
				extensions.get(j).receiveAnswer((MAnswer)answers.get(i));
			}
		}
	}

	public void reportException(MicropsiException e) {
		getExproc().handleException(e);
	}
	
	protected void sendQuestion(MQuestion question) {
		MConsoleReq request = new MConsoleReq();

		toucher.reportTouch();
		request.addQuestion(question);

		try {
			MConsoleResp resp = ((MConsoleResp) clients.performRequest(
				"consoleserverclient", 
				new ComChannelRequest(
							"console", 
							request,	
							getComponentID()
						)
					).getResponseData());
					
			List<AnswerIF> answers = resp.getAnswers();
			dispatchAnswers(answers);
			
		} catch (MicropsiException e) {
			reportException(e);            
		}
	}

	public void shutdown() {
		toucher.shutdown();
	}

}
