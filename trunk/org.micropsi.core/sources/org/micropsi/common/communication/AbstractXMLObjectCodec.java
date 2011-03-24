/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/AbstractXMLObjectCodec.java,v 1.8 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.common.communication;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.RecyclableIF;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractXMLObjectCodec extends DefaultHandler implements RecyclableIF {
	
	private static final Logger readlogger = Logger.getLogger("xmlcom.read");
	private static final Logger writelogger = Logger.getLogger("xmlcom.write");
	
	/**
	 * 
	 * Note that this class does not have to be thread-safe: XMLReader.parse would mess it all up anyway.
	 * So please ensure that only one thread uses a codec instance at a time - create lots of instances
	 * and recycle them!
	 * 
	 */
	
	private static final byte[] REQUEST = {'<','r','e','q',' ','i','d','=','"'};
	private static final byte[] RESPONSE = {'<','r','e','s','p',' ','i','d','=','"'};	
	private static final byte[] RESPSEP = {'"',' ','t','y','p','e','=','"'};
	private static final byte[] REQSEP = {'"',' ','s','e','n','d','e','r','=','"'};
	private static final byte[] CLOSETAG = {'"','>'};
	private static final byte[] CLOSEREQUEST = {'<','/','r','e','q','>'};
	private static final byte[] CLOSERESPONSE = {'<','/','r','e','s','p','>'};	
	private static final int DECODE_RESPONSE = 0;
	private static final int DECODE_REQUEST = 1;
	
	private XMLReader reader;
	private ComChannelResponse transresponse;
	private ComChannelRequest transrequest;
	// TODO: Use two stacks or create common superclass
	private Stack<Object> ostack = new Stack<Object>();
	private int whoami = DECODE_REQUEST;
	private boolean debug = false;
	
	protected AbstractXMLObjectCodec(boolean debug) throws SAXException,ParserConfigurationException {
		this.debug = debug;
		SAXParserFactory parserFactory = SAXParserFactory.newInstance(); 
		SAXParser saxParser = parserFactory.newSAXParser(); 
		reader = saxParser.getXMLReader();		
		reader.setContentHandler(this);
		
		transrequest = new ComChannelRequest();
		transresponse = new ComChannelResponse();
	}
	
	public void reset() {
		ostack.clear();
	}
	
	public abstract void encodeObject(Object o, OutputStreamWriter out) throws MicropsiException,IOException;
	
	public abstract Object decodeObject(String name, Attributes attrs, Object prevObject) throws MicropsiException;
	
	public abstract void decodeTextToObject(String text, Object object);

	public abstract Object decodeRootObject(String name, Attributes attrs) throws MicropsiException;

	public void writeRequest(ComChannelRequest request,OutputStream outp) throws MicropsiException {
		try {
			
			if(debug) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				bout.write(REQUEST);
				bout.write(request.getRequestName().getBytes());
				bout.write(REQSEP);
				bout.write(request.getSender().getBytes());
				bout.write(CLOSETAG);
				encodeObject(request.getRequestData(),new OutputStreamWriter(bout));
				bout.write(CLOSEREQUEST);
				bout.flush();
				String s = bout.toString();
				writelogger.debug("write request: "+s);
			}
			
			outp.write(REQUEST);
			outp.write(request.getRequestName().getBytes());
			outp.write(REQSEP);
			outp.write(request.getSender().getBytes());
			outp.write(CLOSETAG);
			encodeObject(request.getRequestData(),new OutputStreamWriter(outp));
			outp.write(CLOSEREQUEST);
			outp.flush();
		} catch (IOException e) {
			throw new MicropsiException(14,e.getMessage(),e);
		}
	}
	
	public synchronized void writeResponse(ComChannelResponse response, OutputStream outp) throws MicropsiException {
		try {
			
			if(debug) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				bout.write(RESPONSE);
				bout.write(response.getRequestName().getBytes());
				bout.write(RESPSEP);
				bout.write(response.getBResponseType());	
				bout.write(CLOSETAG);
				encodeObject(response.getResponseData(), new OutputStreamWriter(bout));
				bout.write(CLOSERESPONSE);
				bout.flush();		
				String s = bout.toString();
				writelogger.debug("write response: "+s);
			}
			
			outp.write(RESPONSE);
			outp.write(response.getRequestName().getBytes());
			outp.write(RESPSEP);
			outp.write(response.getBResponseType());	
			outp.write(CLOSETAG);
			encodeObject(response.getResponseData(), new OutputStreamWriter(outp));
			outp.write(CLOSERESPONSE);
			outp.flush();
		} catch (IOException e) {
			throw new MicropsiException(14,e.getMessage(),e);
		}
	}
	
	public ComChannelResponse receiveResponse(InputStream inp) throws MicropsiException {
	
		try {

			BufferedInputStream bufin = new BufferedInputStream(inp);
			
			if(debug) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				int r;
				byte[] buffer = new byte[1024];
				do {
					r = bufin.read(buffer);
					if(r < 0) r = 0;
					bout.write(new String(buffer,0,r).trim().getBytes());
				} while (r == 1024);
				
				readlogger.debug("receive response: "+bout.toString().trim());
				reader.parse(new InputSource(new ByteArrayInputStream(bout.toByteArray())));
			} else {
			
				reader.parse(new InputSource(bufin));
				
			}
		} catch (IOException e) {
			throw new MicropsiException(14,e.getMessage(),e);	
		} catch (SAXException e) {
			throw new MicropsiException(101,e.getMessage(),e);
		}
		return transresponse;
	}
	
	public ComChannelRequest receiveRequest(InputStream inp) throws MicropsiException {

		try {
			
			
			if(debug) {
				BufferedInputStream bufin = new BufferedInputStream(inp);
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				int r;
				byte[] buffer = new byte[1024];
				do {
					r = bufin.read(buffer);
					if(r < 0) r = 0;
					bout.write(new String(buffer,0,r).trim().getBytes());
				} while (r == 1024);
				readlogger.debug("receive request: "+bout.toString().trim());
				
				reader.parse(new InputSource(new ByteArrayInputStream(bout.toByteArray())));
				
			} else {
				reader.parse(new InputSource(inp));				
			}

		} catch (IOException e) {
			throw new MicropsiException(14,e.getMessage(),e);	
		} catch (SAXException e) {		
			throw new MicropsiException(101,e.getMessage(),e);
		}
		return transrequest;		
	}
	
	public void startElement(String uri,String localName,String qName,Attributes attr) throws SAXException {
				
		if(qName.equals("req")) {
			whoami = DECODE_REQUEST;
			ostack.empty();
			transrequest.setRequestName(attr.getValue("id"));
			transrequest.setSender(attr.getValue("sender"));
			ostack.push(transrequest);
		} else if(qName.equals("resp")) {
			whoami = DECODE_RESPONSE;
			ostack.empty();
			transresponse.setRequestName(attr.getValue("id"));			
			transresponse.setResponseType(Integer.parseInt(attr.getValue("type")));
			ostack.push(transresponse);			
		} else {
			Object next;
			if(ostack.size() == 1) {
				try {
					next = decodeRootObject(qName,attr);
					switch(whoami) {
						case DECODE_REQUEST: 
							transrequest.setRequestData(next); 
							break;							
						case DECODE_RESPONSE: 
							transresponse.setResponseData(next); 
							break;
					}
				} catch (Exception e) {
					String error = e.getStackTrace()[0].getClassName()+":"+e.getStackTrace()[0].getLineNumber();
					System.err.println("Decoder failure, the message codec is bad: "+error);
					throw new SAXException("Decoder bad, error: "+error,e);
				}
			} else {
				try {
					next = decodeObject(qName,attr,ostack.peek());
				} catch (Exception e) {
					String error = e.getStackTrace()[0].getClassName()+":"+e.getStackTrace()[0].getLineNumber();
					System.err.println("Decoder failure, the message codec is bad: "+error);
					throw new SAXException("Decoder bad, error: "+error,e);
				}	
			}
			ostack.push(next);
		}	
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(!ostack.isEmpty())
			ostack.pop();
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		decodeTextToObject(new String(ch, start, length),ostack.peek());
	}
	
}
