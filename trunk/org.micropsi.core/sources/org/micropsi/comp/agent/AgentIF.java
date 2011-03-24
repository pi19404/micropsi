/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/AgentIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.agent;

import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.exception.MicropsiException;

public interface AgentIF extends Runnable {

	public ConsoleQuestionTypeIF[] initialize(AgentFrameworkComponent teclayer, String configurationRoot, ConfigurationReaderIF config) throws MicropsiException;

	public void startCycle(long cyclecounter);

	public void receiveCycleSignal(long cyclecounter, int state);

	public void endCycle(long cyclecounter);

	public void stopEverything() throws MicropsiException;

	public int getAgentType();
	
	public boolean isAlive();
}
