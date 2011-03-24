/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.psitopia/WEB-INF/src/org/micropsi/comp/console/web/ImageServlet.java,v 1.12 2005/06/07 20:10:54 vuine Exp $ 
 */
package org.micropsi.comp.console.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.messages.MAnswer;


public class ImageServlet extends HttpServlet {

	String server = null;
	MAnswer answer = null;
	
	public void init() throws ServletException {
		server = getInitParameter("server");
	}
	
	public void setAnswer(MAnswer answer) {
		this.answer = answer;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		long objectId = -1;
		Position pos = null;
		if(request.getParameter("watch") != null && !request.getParameter("watch").equals("null")) {
			objectId = Long.parseLong(request.getParameter("watch"));
		} else {
			double ox = Double.parseDouble(request.getParameter("ox"));
			double oy = Double.parseDouble(request.getParameter("oy"));
			pos = new Position(ox,oy);
		}
		
		double scale = 15 * (Double.parseDouble(request.getParameter("scale"))*100) / 100;

		response.setContentType("image/jpeg");
		
		OutputStream out = response.getOutputStream();
		
		if(pos != null) {
			WebConsole.getInstance().writeImageToStream(out,pos,500,500,scale);
		} else {
			WebConsole.getInstance().writeImageToStream(out,objectId,500,500,scale);
		}
		
		out.flush();
		out.close();
		
	}
	
	

}
