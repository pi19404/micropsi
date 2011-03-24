/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MessageCodec.java,v 1.11 2005/07/12 12:55:17 vuine Exp $
 */
package org.micropsi.comp.messages;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.micropsi.common.communication.AbstractXMLObjectCodec;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MessageCodec extends AbstractXMLObjectCodec {
	
	public MessageCodec(boolean debug) throws SAXException, ParserConfigurationException {
		super(debug);
	}

	private void addBasicMessageAttributes(MessageIF message, OutputStreamWriter out) throws MicropsiException,IOException {
		if(!(message instanceof RootMessage)) {
			out.write("");
			return;
		}
		attribute("time", ((RootMessage)message).getTime(),out);
	}
	
	private void addBasicMessageElements(MessageIF message, OutputStreamWriter out) throws MicropsiException,IOException {
		String strg = "";
		if(!(message instanceof RootMessage)) {
			out.write(strg);
			return;
		}
		RootMessage bm = (RootMessage)message;
		if(bm.getQuestions().size() > 0) {
			List<QuestionIF> q = bm.getQuestions();
			for(int i=0;i<q.size();i++) encodeObject(q.get(i),out);
		}
		if(bm.getAnswers().size() > 0) {
			List<AnswerIF> a = bm.getAnswers();
			for(int i=0;i<a.size();i++) encodeObject(a.get(i),out);
		}		
	}
	
	private static void openTag(MessageIF message, OutputStreamWriter out) throws IOException {
		out.write("<m");
		out.write(Integer.toString(message.getMessageType()));
	}
	
	private static void endOpenTag(OutputStreamWriter out) throws IOException {
		out.write(">");
	}
	
	private static void endEmptyTag(OutputStreamWriter out) throws IOException {
		out.write("/>");
	}

	private static void closeTag(MessageIF message, OutputStreamWriter out) throws IOException {
		out.write("</m");
		out.write(Integer.toString(message.getMessageType()));
		out.write(">");		
	}
	
/*	private static void compactTag(MessageIF message, OutputStreamWriter out) throws IOException {
		out.write("<m");
		out.write(Integer.toString(message.getMessageType()));
		out.write("/>");		
	}
*/	
	private static void attribute(String key, String value, OutputStreamWriter out) throws IOException {
		out.write(" ");
		out.write(key);
		out.write("=\"");
		out.write((value != null) ? value : "");
		out.write("\"");
	}
		
	private static void attribute(String key, double value, OutputStreamWriter out) throws IOException {
		out.write(" ");
		out.write(key);
		out.write("=\"");
		out.write(Double.toString(value));
		out.write("\"");		
	}

	private static void attribute(String key, long value, OutputStreamWriter out) throws IOException {
		out.write(" ");
		out.write(key);
		out.write("=\"");
		out.write(Long.toString(value));
		out.write("\"");		
	}

	private static void attribute(String key, int value, OutputStreamWriter out) throws IOException {
		out.write(" ");
		out.write(key);
		out.write("=\"");
		out.write(Integer.toString(value));
		out.write("\"");		
	}


	public void encodeObject(Object o, OutputStreamWriter out) throws MicropsiException,IOException {
		if(o == null) return;
		if(!(o instanceof MessageIF)) return;
		MessageIF message = (MessageIF)o;
        int length = 0;
		switch(message.getMessageType()) {
			case MessageTypesIF.MTYPE_COMMON_VERSION:
					MVersion version = (MVersion)message;
					openTag(message,out);
					attribute("major",version.getRemoteMajor(),out);
					attribute("minor",version.getRemoteMinor(),out);
					attribute("name",version.getRemoteName(),out);
					endEmptyTag(out);
					break;
			case MessageTypesIF.MTYPE_COMMON_CONFIRMATION:
					openTag(message,out);
					addBasicMessageAttributes(message, out);
					endOpenTag(out);
					addBasicMessageElements(message,out);
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_COMMON_TOUCH:
					openTag(message,out);
					addBasicMessageAttributes(message, out);
					endOpenTag(out);
					addBasicMessageElements(message,out);			
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_SERVER_UPDATEWORLD:
					MUpdateWorld update = (MUpdateWorld)message;
					openTag(message,out);
					addBasicMessageAttributes(message, out);

					length = update.getNewAgents().size();
					for(int i=0;i<length;i++)
						attribute("newAgent"+i,update.getNewAgents().get(i),out);

					length = update.getDeletedAgents().size();
					for(int i=0;i<length;i++)
						attribute("delAgent"+i,update.getDeletedAgents().get(i),out);

					endOpenTag(out);
					List actions = update.getActions();
					for(int i=0;i<actions.size();i++)
						encodeObject(actions.get(i),out);
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_AGENT_REQ:
					MAgentReq magentreq = (MAgentReq)message;
					openTag(message,out);
					addBasicMessageAttributes(message, out);
					attribute("type",magentreq.getRequestType(),out);
					attribute("agent",magentreq.getAgentID(),out);
					attribute("atype",magentreq.getAgentType(),out);
					endOpenTag(out);
					encodeObject(magentreq.getAction(),out);
					addBasicMessageElements(message,out);
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_AGENT_RESP:
					MAgentResp agentresp = (MAgentResp)message;
					openTag(message,out);
					addBasicMessageAttributes(message, out);
					attribute("type",agentresp.getResponseType(),out);
					attribute("ctext",agentresp.getControltext(),out);
					endOpenTag(out);
					encodeObject(agentresp.getPreviousActionResponse(), out);
					addBasicMessageElements(message, out);
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_AGENT_ACTION:
					MAction maction = (MAction)message;
					openTag(message,out);
					attribute("type",maction.getActionType(),out);
					attribute("agent",maction.getAgentName(),out);
					attribute("oid",maction.getTargetObject(),out);
					attribute("ticket",maction.getTicket(),out);
					length = maction.getParameterCount();
					for(int i=0;i<length;i++)
						attribute("param"+i,maction.getParameter(i),out);
					endEmptyTag(out);
					break;
			case MessageTypesIF.MTYPE_AGENT_ACTIONRESPONSE:
					MActionResponse mresp = (MActionResponse)message;
					openTag(message,out);
					attribute("agent",mresp.getAgentName(),out);
					attribute("succ",mresp.getSuccess(),out);
					attribute("ticket",mresp.getTicket(),out);
					endOpenTag(out);
					List bodyPropertyChanges = mresp.getBodyPropertyChanges();
					for (int i = 0; i < bodyPropertyChanges.size(); i++) {
						encodeObject(bodyPropertyChanges.get(i), out);
					}
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_AGENT_PERCEPTIONREQ:
					MPerceptionReq preq = (MPerceptionReq)message;
					openTag(message,out);
					attribute("agent",preq.getAgentID(),out);
					endEmptyTag(out);
					break;
			case MessageTypesIF.MTYPE_AGENT_PERCEPTIONRESP:
					MPerceptionResp perception = (MPerceptionResp)message;
					openTag(message,out);
					endOpenTag(out);
					List list = perception.getPercepts();
					for(int i=0;i<list.size();i++)
						encodeObject(list.get(i),out);
					closeTag(message, out);
					break;
			case MessageTypesIF.MTYPE_AGENT_PERCEPTIONVALUE:
					MPerceptionValue bodyPropertyChange = (MPerceptionValue)message;
					openTag(message, out);
					attribute("key", bodyPropertyChange.getKey(), out);
					attribute("value", bodyPropertyChange.getValue(), out);
					endEmptyTag(out);
					break;
			case MessageTypesIF.MTYPE_AGENT_PERCEPT:
					MPercept percept = (MPercept)message;
					openTag(message, out);
					attribute("name", percept.getName(), out);
					endOpenTag(out);
					List perceptionValues = percept.getPerceptionValues();
					for (int i = 0; i < perceptionValues.size(); i++) {
						encodeObject(perceptionValues.get(i), out);
					}
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_TIMER_TICK:
					//MTick tick = (MTick)message;
					openTag(message,out);
					addBasicMessageAttributes(message, out);
					endEmptyTag(out);
					break;
			case MessageTypesIF.MTYPE_CONSOLE_REQ:
					MConsoleReq creq = (MConsoleReq)message;
					openTag(message,out);
					attribute("type",creq.getRequestType(),out);
					endOpenTag(out);
					addBasicMessageElements(message,out);
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_CONSOLE_RESP:
					MConsoleResp cresp = (MConsoleResp)message;
					openTag(message,out);
					addBasicMessageAttributes(message, out);
					attribute("text",cresp.getControltext(),out);
					endOpenTag(out);
					addBasicMessageElements(message,out);
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_CONSOLE_QUESTION:
					MQuestion q = (MQuestion)message;
					openTag(message,out);
					attribute("dest",q.getDestination(),out);
					attribute("step",q.getStep(),out);
					attribute("am",q.getAnswerMode(),out);
					attribute("origin",q.getOrigin(),out);
					attribute("qname",q.getQuestionName(),out);
					List params = q.getParameterList();
					for(int i=0;i<params.size();i++)
						attribute("param"+i,(String)params.get(i),out);
					endOpenTag(out);
					if(q.getAdditionalData() != null) encodeObject(q.getAdditionalData(),out);
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_CONSOLE_ANSWER:
					MAnswer a = (MAnswer)message;
					openTag(message,out);
					attribute("type",a.getAnswerType(),out);
					attribute("step",a.getStep(),out);
					attribute("dest",a.getDestination(),out);
					attribute("origin",a.getOrigin(),out);
					endOpenTag(out);
					encodeObject(a.getAnsweredQuestion(),out);
					switch(a.getAnswerType()) {
						case AnswerTypesIF.ANSWER_TYPE_ERROR:
							out.write((String)a.getContent());
							break;
						case AnswerTypesIF.ANSWER_TYPE_STRING:
							Object content = a.getContent();
							if(content != null) {
								out.write((String)content);
							} else {
								out.write("null");
							}
							break;
						case AnswerTypesIF.ANSWER_TYPE_OK:
							break;
						default:
							encodeObject(a.getContent(),out);
					}
					closeTag(message,out);
					break;
			case MessageTypesIF.MTYPE_CONSOLE_TREENODE:
					MTreeNode node = (MTreeNode)message;
					openTag(message,out);
					attribute("name",node.getName(),out);
					attribute("value",node.getValue(),out);
					endOpenTag(out);
					Iterator<MTreeNode> i = node.children();
					if(i != null) while(i.hasNext()) {
						o = i.next();
						encodeObject(o,out);
					}
					closeTag(message,out);
					break;
			default: throw new RuntimeException("FIX THIS: UNKNOWN MESSAGE TYPE: "+message.getMessageType());					
		}
		out.flush();
	}

	public Object decodeObject(String name,Attributes attr,Object prevObject) throws MicropsiException {
		MessageIF prev = (MessageIF)prevObject;
		int type = Integer.parseInt(name.substring(1,name.length()));
		
		if(prevObject instanceof RootMessage) {
			RootMessage m = (RootMessage)prevObject;
			switch(type) {
				case MessageTypesIF.MTYPE_CONSOLE_QUESTION:
					MQuestion question = new MQuestion();
					question.setDestination(attr.getValue("dest"));
					question.setOrigin(attr.getValue("origin"));
					question.setQuestionName(attr.getValue("qname"));
					question.setAnswerMode(Integer.parseInt(attr.getValue("am")));
					question.setStep(Long.parseLong(attr.getValue("step")));
		
					int attcount = attr.getLength()-3;
					for(int i=0;i<attcount;i++)
						question.addParameter(attr.getValue("param"+i));
					m.addQuestion(question);
					return question;
				case MessageTypesIF.MTYPE_CONSOLE_ANSWER:
					MAnswer answer = new MAnswer();
					answer.setAnswerType(Integer.parseInt(attr.getValue("type")));
					answer.setOrigin(attr.getValue("origin"));
					answer.setDestination(attr.getValue("dest"));
					answer.setStep(Long.parseLong(attr.getValue("step")));
					m.addAnswer(answer);
					return answer;
				default:
				/* 
				 * If the parent is a RootMessage, but the new element is either
				 * a question nor an answer: continue normal decoding
				 */
			}
		}
		
		switch(type) {
			case MessageTypesIF.MTYPE_CONSOLE_QUESTION:
				// normally, questions have been decoded into the root message,
				// but in case of questions nested into answers, there has to
				// be some extra decoding.
				if(prevObject instanceof MAnswer) {
					MQuestion question = new MQuestion();
					question.setDestination(attr.getValue("dest"));
					question.setOrigin(attr.getValue("origin"));
					question.setQuestionName(attr.getValue("qname"));
					question.setAnswerMode(Integer.parseInt(attr.getValue("am")));
					question.setStep(Long.parseLong(attr.getValue("step")));
		
					int attcount = attr.getLength()-3;
					for(int i=0;i<attcount;i++)
						question.addParameter(attr.getValue("param"+i));
					((MAnswer)prevObject).setAnsweredQuestion(question);
					return question;			
				}
			case MessageTypesIF.MTYPE_CONSOLE_TREENODE:
				MTreeNode node = new MTreeNode(
					attr.getValue("name"),
					attr.getValue("value"),
					null
				);
				if(prevObject instanceof MAnswer) 
					((MAnswer)prevObject).setContent(node);
				else if(prevObject instanceof MQuestion)
					((MQuestion)prevObject).setAdditionalData(node); 
				else ((MTreeNode)prevObject).addChild(node);
				return node;
			case MessageTypesIF.MTYPE_AGENT_ACTION:
				MAction maction = new MAction();
				maction.setActionType(attr.getValue("type"));
				maction.setTargetObject(Integer.parseInt(attr.getValue("oid")));
				maction.setAgentName(attr.getValue("agent"));
				maction.setTicket(Long.parseLong(attr.getValue("ticket")));
				int attcount = attr.getLength()-2;
				for(int i=0;i<attcount;i++)	
					maction.addParameter(attr.getValue("param"+i));
				switch(prev.getMessageType()) {
					case MessageTypesIF.MTYPE_AGENT_REQ:
						((MAgentReq)prevObject).setAction(maction);
						break;
					case MessageTypesIF.MTYPE_SERVER_UPDATEWORLD:
						((MUpdateWorld)prevObject).addAction(maction);
						break;
				}				
				return maction;
			case MessageTypesIF.MTYPE_AGENT_ACTIONRESPONSE:
				MActionResponse mresp = new MActionResponse();
				mresp.setAgentName(attr.getValue("agent"));
				mresp.setTicket(Long.parseLong(attr.getValue("ticket")));
				mresp.setSuccess(Double.parseDouble(attr.getValue("succ")));
				((MAgentResp)prev).setPreviousActionResponse(mresp);
				return mresp;
			case MessageTypesIF.MTYPE_AGENT_PERCEPTIONVALUE:
				MPerceptionValue val = new MPerceptionValue();
				val.setKey(attr.getValue("key"));
				val.setValue(attr.getValue("value"));
				switch(prev.getMessageType()) {
					case MessageTypesIF.MTYPE_AGENT_PERCEPT:
						((MPercept)prev).addParameter(val);
						break;
					case MessageTypesIF.MTYPE_AGENT_ACTIONRESPONSE:
						((MActionResponse)prev).addBodyPropertyChange(val);
						break;
				}
				return val;
			case MessageTypesIF.MTYPE_AGENT_PERCEPT:
				MPercept percept = new MPercept();
				percept.setName(attr.getValue("name"));
				((MPerceptionResp)prev).addPercept(percept);
				return percept;
			default: throw new RuntimeException("FIX THIS: UNKNOWN MESSAGE TYPE "+type);	
		}
	}

	public void decodeTextToObject(String text, Object object) {	
		MessageIF prev = (MessageIF)object;
		switch(prev.getMessageType()) {
			case MessageTypesIF.MTYPE_CONSOLE_ANSWER:			
					String old = "";
					if(((MAnswer)prev).getContent() != null) old = (String)((MAnswer)prev).getContent();
					((MAnswer)prev).setContent(old+text);
				 	break;
			default: throw new RuntimeException("FIX THIS: ILLEGAL TEXT WITHIN ELEMENT TYPE "+prev.getMessageType()+": "+text);	
		}
	}

	public Object decodeRootObject(String name, Attributes attr) throws MicropsiException {	
		int type = Integer.parseInt(name.substring(1,name.length()));
		switch(type) {
			case MessageTypesIF.MTYPE_COMMON_VERSION:
				MVersion version = new MVersion();
				version.setRemoteMajor(Integer.parseInt(attr.getValue("major")));
				version.setRemoteMinor(Integer.parseInt(attr.getValue("minor")));
				version.setRemoteName(attr.getValue("name"));
				return version;
			case MessageTypesIF.MTYPE_TIMER_TICK:
				MTick tick = new MTick();
				tick.setTime(Long.parseLong(attr.getValue("time")));
				return tick;
			case MessageTypesIF.MTYPE_AGENT_REQ:
				MAgentReq magentreq = new MAgentReq();
				magentreq.setRequestType(Integer.parseInt(attr.getValue("type")));
				magentreq.setAgentID(attr.getValue("agent"));
				magentreq.setAgentType(attr.getValue("atype"));
				return magentreq;
			case MessageTypesIF.MTYPE_AGENT_RESP:
				MAgentResp magentresp = new MAgentResp();
				magentresp.setResponseType(Integer.parseInt(attr.getValue("type")));
				magentresp.setControltext(attr.getValue("ctext"));
				magentresp.setTime(Long.parseLong(attr.getValue("time")));
				return magentresp;
			case MessageTypesIF.MTYPE_AGENT_PERCEPTIONREQ:
				MPerceptionReq perceptionRequest = new MPerceptionReq();
				perceptionRequest.setAgentID(attr.getValue("agent"));
				return perceptionRequest;
			case MessageTypesIF.MTYPE_AGENT_PERCEPTIONRESP:
				MPerceptionResp perception = new MPerceptionResp();
				return perception;
			case MessageTypesIF.MTYPE_CONSOLE_REQ:
	        	MConsoleReq creq = new MConsoleReq();
	        	creq.setRequestType(Integer.parseInt(attr.getValue("type")));
   		     	return creq;
   		    case MessageTypesIF.MTYPE_CONSOLE_RESP:
	        	MConsoleResp cresp = new MConsoleResp();
	        	cresp.setTime(Long.parseLong(attr.getValue("time")));
	        	cresp.setControltext(attr.getValue("text"));
   		     	return cresp;
   		    case MessageTypesIF.MTYPE_SERVER_UPDATEWORLD:				
				MUpdateWorld update = new MUpdateWorld();
				ArrayList<String> newAgents = new ArrayList<String>();
				ArrayList<String> deletedAgents = new ArrayList<String>();
				for(int i=0;i<attr.getLength();i++) {
					if(attr.getLocalName(i).startsWith("new"))
						newAgents.add(attr.getValue(i));
					else if(attr.getLocalName(i).startsWith("del"))
						deletedAgents.add(attr.getValue(i));
				}
				
				update.setTime(Long.parseLong(attr.getValue("time")));
				
				return update;
			case MessageTypesIF.MTYPE_COMMON_TOUCH:
				MTouch touch = new MTouch();
				touch.setTime(Long.parseLong(attr.getValue("time")));
				return touch;
			case MessageTypesIF.MTYPE_COMMON_CONFIRMATION:
				MConfirmation confirmation = new MConfirmation();
				confirmation.setTime(Long.parseLong(attr.getValue("time")));
				return confirmation;
			default:
				throw new MicropsiException(200,name);
		}
	}
}

/*

	$Log: MessageCodec.java,v $
	Revision 1.11  2005/07/12 12:55:17  vuine
	Migration to Java 5
	
	Revision 1.10  2005/06/19 18:52:02  vuine
	bugfix
	
	Revision 1.9  2005/05/24 23:32:28  vuine
	fixed bug #74
	
	Revision 1.8  2005/01/20 23:24:56  vuine
	moved console code from eclipse console component
	
	Revision 1.7  2004/11/24 16:32:22  vuine
	cleanup
	
	Revision 1.6  2004/11/04 15:12:43  vuine
	implemented ticket mechanism
	
	Revision 1.5  2004/08/10 14:38:16  fuessel
	Changes in Gantikow
	
	Revision 1.1  2004/08/06 13:51:41  cvsuser
	Stand CVS Osnabrück
	
	Revision 1.4  2004/06/20 16:36:02  vuine
	i didn't test it, but this body property changes thing was surely wrong
	
	Revision 1.3  2004/06/08 21:23:41  vuine
	now compliant with Non-Xerces XML parsers
	
	Revision 1.2  2004/05/23 22:08:09  vuine
	agent responses now contain step information
	
	Revision 1.1  2004/05/07 21:05:07  vuine
	initial checkin / move from the old HU repository
	
	Revision 1.2  2004/02/29 17:14:28  vuine
	rename
	
	Revision 1.1  2004/02/24 13:57:21  vuine
	rename
	
	Revision 1.27  2003/07/02 19:48:36  vuine
	more stable, prevents another NullPointerException
	
	Revision 1.26  2003/07/01 23:43:22  vuine
	fix
	
	Revision 1.25  2003/07/01 18:04:38  vuine
	just some brackets
	
	Revision 1.24  2003/06/29 23:57:00  vuine
	works with null strings in MAnswers
	
	Revision 1.23  2003/06/27 23:29:59  vuine
	now simply returns when encountering something that 
can't be encoded becaus it isn't a MessageIF
	

*/