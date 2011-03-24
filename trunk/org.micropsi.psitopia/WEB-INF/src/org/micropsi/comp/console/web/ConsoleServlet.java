/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.psitopia/WEB-INF/src/org/micropsi/comp/console/web/ConsoleServlet.java,v 1.2 2005/05/22 19:05:19 vuine Exp $ 
 */
package org.micropsi.comp.console.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.micropsi.common.consoleservice.AnswerIF;


public class ConsoleServlet extends HttpServlet {

	String server = null;
	
	public void init() throws ServletException {
		server = getInitParameter("server");
	}
		
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String action = request.getParameter("action");
		if(action == null) {
			action = "";
		}		
		
		String destination = request.getParameter("destination");
		if(destination == null) {
			destination = server;
		}
		request.getSession().setAttribute("destination",destination);
		
		if(action.equals("refreshComponents")) {
			AnswerIF answer = WebConsole.getInstance().askQuestion("getenvironment", server, "", this);
			request.getSession().setAttribute("environment", answer);
			
			answer = WebConsole.getInstance().askQuestion("getcomponentdescriptor", destination, "", this);
			request.getSession().setAttribute("descriptor", answer);				
		} else if(action.equals("refreshQuestions")) {
			AnswerIF answer = WebConsole.getInstance().askQuestion("getcomponentdescriptor", destination, "", this);
			request.getSession().setAttribute("descriptor", answer);							
		} else if(action.equals("invalidate")) {
			response.sendRedirect("..");
			request.getSession().invalidate();
		} else {
			String question = request.getParameter("question");
			String parameters = request.getParameter("parameters");
			AnswerIF answer = WebConsole.getInstance().askQuestion(question, destination, parameters, this);
			request.getSession().setAttribute("answer", answer);
		}
		
		response.sendRedirect("console.jsp");
	}
	
	public void destroy() {
		super.destroy();
		WebConsole.getInstance().shutdown();
	}
	

}
