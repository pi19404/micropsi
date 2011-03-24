/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ComponentService.java,v 1.8 2005/06/02 10:11:01 vuine Exp $
 */
package org.micropsi.comp.common;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.micropsi.common.communication.XMLTCPChannelServer;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.common.utils.Recycler;
import org.micropsi.comp.messages.MessageCodec;

/**
 * This is the servlet used by the servlet engine when using xml/tcp communication
 */
public class ComponentService extends HttpServlet {

	private XMLTCPChannelServer server;
	private ExceptionProcessor exproc;
	private Recycler codecRecycler = new Recycler();
	private boolean debug = false;
	
	public void init() throws ServletException {
					
		try {
			
			if(getInitParameter("home") != null) {
				ComponentRunner.setGlobalVariable("MICROPSI_HOME",getInitParameter("home"));
			}
			if(getInitParameter("debug") != null) {
				debug = getInitParameter("debug").equals("true");
			}
			
			ComponentRunner.getInstance(getInitParameter("configFile"),this.getClass().getClassLoader(),null).registerServlet(
				this,
				getInitParameter("component"),
				getInitParameter("channelserver")
			);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new ServletException("Initialization failure. "+e.getMessage(),e);
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		OutputStream outp = response.getOutputStream();
		
		outp.write(	(	"<html><head></head><body><h2>Service is alive: " +
						this.getServletContext().getServletContextName() + " / " +
						this.getServletName()+"</h2></body></html>").getBytes());
		outp.flush();
		return;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
				
		OutputStream outp = response.getOutputStream();
		
		try {
			
			MessageCodec codec = null;
			if(codecRecycler.isEmpty()) {
				codec = new MessageCodec(debug);
			} else {
				codec = (MessageCodec)codecRecycler.recycle();
			}							
			
			server.processRequest(request.getInputStream(),outp,codec);
				
			codecRecycler.trash(codec);

		} catch (Exception e) {
			String errorString = exproc.handleException(e);
			outp.write("<resp id=\"-1\">".getBytes());
			outp.write(errorString.getBytes());
			outp.write("</resp>".getBytes());
			outp.flush();	
		}
	}

	public XMLTCPChannelServer getServer() {
		return server;
	}

	public void setServer(XMLTCPChannelServer server) {
		this.server = server;
	}

	public void setExproc(ExceptionProcessor exproc) {
		this.exproc = exproc;
	}

	public ExceptionProcessor getExproc() {
		return exproc;
	}

}
