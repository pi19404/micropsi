/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/internal/ConsoleProxy.java,v 1.3 2005/06/05 16:28:41 vuine Exp $ 
 */
package org.micropsi.eclipse.console.internal;

import org.apache.log4j.Logger;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.eclipse.console.ConsoleRuntimeUser;


public class ConsoleProxy implements ConsoleFacadeIF {
	
	public AnswerIF askBlockingQuestion(MQuestion arg0)
			throws MicropsiException {
		return ConsoleRuntimeUser.getInstance().getConsole().askBlockingQuestion(arg0);
	}
	public AnswerIF askBlockingQuestion(MQuestion arg0, int arg1)
			throws MicropsiException {
		return ConsoleRuntimeUser.getInstance().getConsole().askBlockingQuestion(arg0, arg1);
	}
	public void getInformation(int arg0, int arg1, String arg2, String arg3,
			String arg4, AnswerQueueIF arg5, Object arg6, boolean arg7) {
		ConsoleRuntimeUser.getInstance().getConsole().getInformation(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}
	public void getInformation(int arg0, String arg1, String arg2, String arg3,
			AnswerQueueIF arg4) {
		ConsoleRuntimeUser.getInstance().getConsole().getInformation(arg0, arg1, arg2, arg3, arg4);
	}
	public void getInformation(int arg0, String arg1, String arg2, String arg3,
			AnswerQueueIF arg4, Object arg5) {
		ConsoleRuntimeUser.getInstance().getConsole().getInformation(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public void sendCommand(int arg0, int arg1, String arg2, String arg3,
			String arg4, Object arg5, boolean arg6) {
		ConsoleRuntimeUser.getInstance().getConsole().sendCommand(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}
	public void sendCommand(int arg0, String arg1, String arg2, String arg3,
			boolean arg4) {
		ConsoleRuntimeUser.getInstance().getConsole().sendCommand(arg0, arg1, arg2, arg3, arg4);
	}
	public void sendCommand(int arg0, String arg1, String arg2, String arg3,
			Object arg4, boolean arg5) {
		ConsoleRuntimeUser.getInstance().getConsole().sendCommand(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public void subscribe(int arg0, String arg1, String arg2, String arg3,
			AnswerQueueIF arg4) {
		ConsoleRuntimeUser.getInstance().getConsole().subscribe(arg0, arg1, arg2, arg3, arg4);
	}
	public void subscribe(int arg0, String arg1, String arg2, String arg3,
			AnswerQueueIF arg4, Object arg5) {
		ConsoleRuntimeUser.getInstance().getConsole().subscribe(arg0, arg1, arg2, arg3, arg4, arg5);
	}
	public void unsubscribe(String arg0, String arg1, String arg2,
			AnswerQueueIF arg3) {
		ConsoleRuntimeUser.getInstance().getConsole().unsubscribe(arg0, arg1, arg2, arg3);
	}
	public void unsubscribe(String arg0, String arg1, String arg2,
			AnswerQueueIF arg3, Object arg4) {
		ConsoleRuntimeUser.getInstance().getConsole().unsubscribe(arg0, arg1, arg2, arg3, arg4);
	}
	public void unsubscribe(MQuestion arg0, AnswerQueueIF arg1) {
		ConsoleRuntimeUser.getInstance().getConsole().unsubscribe(arg0, arg1);
	}
	public void unsubscribeAll(AnswerQueueIF arg0) {
		ConsoleRuntimeUser.getInstance().getConsole().unsubscribeAll(arg0);
	}
	/* @see org.micropsi.comp.console.ConsoleFacadeIF#getExproc()*/
	public ExceptionProcessor getExproc() {
		return ConsoleRuntimeUser.getInstance().getConsole().getExproc();
	}
	/* @see org.micropsi.comp.console.ConsoleFacadeIF#getLogger()*/
	public Logger getLogger() {
		return ConsoleRuntimeUser.getInstance().getConsole().getLogger();
	}
	/*(non-Javadoc)
	 * @see org.micropsi.comp.console.ConsoleFacadeIF#unsubscribeAll()
	 */
	public void unsubscribeAll() {
		ConsoleRuntimeUser.getInstance().getConsole().unsubscribeAll();
	}
}
