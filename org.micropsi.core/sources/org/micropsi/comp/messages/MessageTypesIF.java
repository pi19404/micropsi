/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MessageTypesIF.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.messages;

public interface MessageTypesIF {
	
	public static final int MTYPE_COMMON_CONFIRMATION = 100;
	public static final int MTYPE_COMMON_VERSION = 101;
	public static final int MTYPE_COMMON_TOUCH = 102;

	public static final int MTYPE_AGENT_REQ = 200;
	public static final int MTYPE_AGENT_RESP = 201;
	public static final int MTYPE_AGENT_ACTION = 202;
	public static final int MTYPE_AGENT_ACTIONRESPONSE = 203;
	public static final int MTYPE_AGENT_PERCEPTIONREQ = 204;
	public static final int MTYPE_AGENT_PERCEPTIONRESP = 205;
	public static final int MTYPE_AGENT_PERCEPTIONVALUE = 206;
	public static final int MTYPE_AGENT_PERCEPT = 207;


	public static final int MTYPE_CONSOLE_REQ = 300;	
	public static final int MTYPE_CONSOLE_RESP = 301;
	public static final int MTYPE_CONSOLE_QUESTION = 302;	
	public static final int MTYPE_CONSOLE_ANSWER = 303;
	public static final int MTYPE_CONSOLE_TREENODE = 304;
	
	public static final int MTYPE_SERVER_UPDATEWORLD = 400;
	public static final int MTYPE_SERVER_AGENTREQUEST = 401;

	public static final int MTYPE_TIMER_TICK = 500;

	public static final int MTYPE_WORLD_RESPONSE = 600;

}
