/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/robot/RobotWorldComponent.java,v 1.6 2005/05/12 13:31:12 vuine Exp $
 */
package org.micropsi.comp.robot;

import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.WorldTypesIF;

public class RobotWorldComponent extends AbstractComponent {

	private long simStep;
	private String connectedAgent;
	private ServerRequestTickHandler serverRequestTickHandler;
	private ServerRequestPerceptionHandler serverRequestPerceptionHandler; 

	public long getSimStep() {
		return simStep;
	}
	
	public void setConnectedAgent(String connectedAgent) {
		this.connectedAgent = connectedAgent;
	}
	
	public String getConnectedAgent() {
		return connectedAgent;
	}

	protected void performInitialisation() throws MicropsiException {

		String executor = config.getConfigValue(prefixKey + ".executor");
		String extractor = config.getConfigValue(prefixKey + ".extractor");
		
		RobotActionExecutor.setup(executor,config,prefixKey,this);
		RobotPerceptionExtractor.setup(extractor,config,prefixKey,this);
		
		ConsoleQuestionTypeIF[] executorQuestionTypes = RobotActionExecutor.getInstance().getConsoleQuestionContributions();
		if(executorQuestionTypes == null) {
			executorQuestionTypes = new ConsoleQuestionTypeIF[] {};
		}
		ConsoleQuestionTypeIF[] extractorQuestionTypes = RobotPerceptionExtractor.getInstance().getConsoleQuestionContributions();
		if(extractorQuestionTypes == null) {
			extractorQuestionTypes = new ConsoleQuestionTypeIF[] {};
		}

		for(int i=0;i<executorQuestionTypes.length;i++) {
			consoleService.registerQuestionType(executorQuestionTypes[i]);
		}

		for(int i=0;i<extractorQuestionTypes.length;i++) {
			consoleService.registerQuestionType(extractorQuestionTypes[i]);
		}
		
		serverRequestTickHandler = new ServerRequestTickHandler(this);
		serverRequestPerceptionHandler = new ServerRequestPerceptionHandler(this);

		servers.registerRequestHandler("worldserverserver", serverRequestTickHandler);
		servers.registerRequestHandler("worldserverserver",	serverRequestPerceptionHandler);

	}

	protected void tick(long simStep) {
		this.simStep = simStep;
		try {
			RobotActionExecutor.getInstance().tick(simStep);
			RobotPerceptionExtractor.getInstance().tick(simStep);
		} catch (MicropsiException e) {
			getExproc().handleException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.common.AbstractComponent#getInnerType()
	 */
	public int getInnerType() {
		return WorldTypesIF.WT_ROBOT;
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.common.AbstractComponent#shutdown()
	 */
	public void shutdown() {
		try {
			RobotActionExecutor.getInstance().shutdown();
			RobotPerceptionExtractor.getInstance().shutdown();
		} catch (MicropsiException e) {
			getExproc().handleException(e);
		}
	}

}