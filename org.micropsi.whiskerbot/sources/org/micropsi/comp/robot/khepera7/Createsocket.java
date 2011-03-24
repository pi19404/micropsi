/*
 * Created on Nov 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.robot.khepera7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

public class Createsocket {
	
	private Logger logger = null;

	private Socket socket;

	private OutputStream out;

	private BufferedReader in;

	private static Createsocket sharedInstance;

	public Createsocket(Logger logger) {
		this.logger = logger;
	}

	/*
	 * Method which test if the Object of Createsocket exists
	 */

	public static Createsocket sharedinstance(Logger logger) {
		if (sharedInstance == null) {
			sharedInstance = new Createsocket(logger);
			logger.debug("new socket instance created...");
			sharedInstance.createSocket();
		}
		return sharedInstance;
	}

	public synchronized void createSocket() {
		try {
			socket = new Socket("heinz.neurobiopsychologie.uos.de", 30001);
			out = socket.getOutputStream();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			logger.debug("socket created!");
		} catch (Exception e) {
			logger.error("No socket created.",e);
		}
	}

	public void send(String s) {		
		if(!s.endsWith("\n")) {
			s = s+"\n";
		}
		try {
			if (socket == null) {
				createSocket();
			}
			out.write(s.getBytes());
			out.flush();
		} catch (Exception e) {
			logger.error("No packages were sent.",e);
			close();
			socket = null;
		}
	}

	public String read() {
		try {
			if (socket == null) {
				createSocket();
			}
			String str = "";
			try {
				str = in.readLine();
			} catch (RuntimeException e) {
				logger.debug("(Socket) readline() error!",e);
				e.printStackTrace();
			}
			return str;
		} catch (Exception e) {
			logger.error("No packages were read",e);
			return "";
		}		
	}

	public void close() {
		try {
			if(out != null) {
				out.close();	
			}
			if(in != null) {
				in.close();
			}
			if(socket != null) {
				socket.close();	
			}
		} catch (Exception e) {
			logger.error("Problems with closing the socket and the Streams.",e);
			socket = null;
		}
	}

}

