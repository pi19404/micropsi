/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ComponentExceptionInfo.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.common;

import org.apache.log4j.Logger;

import org.micropsi.common.exception.AbstractExceptionInfo;
import org.micropsi.common.exception.ExceptionHandlerIF;
import org.micropsi.common.exception.ExceptionSeverityIF;
import org.micropsi.common.exception.SystemLogExceptionHandler;
import org.micropsi.common.log.LoggingExceptionHandler;

public class ComponentExceptionInfo extends AbstractExceptionInfo {

	private LoggingExceptionHandler handler;
	
	public ComponentExceptionInfo(Logger logger) {
	  handler = new LoggingExceptionHandler(logger);		
	}
	
	
	/**
	 * @see org.micropsi.common.exception.AbstractExceptionInfo#getTextForID(int)
	 */
	public String getTextForID(int id) {
		switch(id) {
			case 0: return "";
			case 10: return "Class not found or instantiation problem: ";
			case 11: return "Logfile not found: ";
			case 12: return "Bad URL: ";
			case 14: return "IO problem in tcp stream: ";
			case 15: return "IO problem when saving: ";
			case 16: return "No such classloader: ";
			case 17: return "Not a directory: ";
			case 18: return "Bad cast, object not of expected type: ";
			case 19: return "Singleton not initialized: ";
			case 20: return "Cannot register questionType: already registered";
			case 30: return "Client and Server message protocol versions are not compatible: ";
			case 100: return "Configuration file not found: ";
			case 101: return "Parser failure: ";
			case 102: return "IO problem in configuration: ";
			case 110: return "Node not found: ";
			case 111: return "Bad data type at: ";
			case 200: return "Unknown message type: ";
			case 201: return "Message has no element with that index: ";			
			case 202: return "Message element was null: ";
			case 203: return "Unexpected message type: ";
			case 300: return "There is no such channelclient: ";
			case 301: return "There is no such channelserver: ";
			case 302: return "There is no such requesthandler within the server: ";
			case 303: return "There is already a requesthandler named ";
			case 304: return "Unknown server type: ";
			case 305: return "Unknown client type: ";
			case 306: return "Exception during blocking request: ";
			case 307: return "Exception during non-blocking request: ";
			case 400: return "Exception in world.objects.ObjectState: ";
			case 500: return "WorldAdapter cannot be used with that agent type: ";
			case 1000: return "Server exception: Agent unknown: ";
			case 1001: return "Server exception: Component unknown: ";
			case 2500: return "Net integrity violated: ";
			case 2501: return "No such datasource for sensor: ";
			case 2502: return "Node is not a sensor and can't be connected to a datasource: ";
			case 2503: return "Entity is not of expected type (bad cast): ";
			case 2504: return "No such datatarget for actuator: ";
			case 3000: return "No such multicast tick list";
            case 4000: return "Timeout while waiting for an answer to the blocking question:";		
        	default: return "No text for errorid "+id+" ";
		}
	}

	/**
	 * @see org.micropsi.common.exception.AbstractExceptionInfo#getSeverityForID(int)
	 */
	public int getSeverityForID(int id) {
		return ExceptionSeverityIF.EX_SEVERITY_SEVERE;
	}
	
	public Object getParameterForID(int id) {
		return null;
	}

	/**
	 * @see org.micropsi.common.exception.AbstractExceptionInfo#getHandlerForID(int)
	 */
	public ExceptionHandlerIF getHandlerForID(int id) {
		switch(id) {
			case 11: return new SystemLogExceptionHandler();  	// Logger setup error
			default: return handler;
		} 
	}

}
