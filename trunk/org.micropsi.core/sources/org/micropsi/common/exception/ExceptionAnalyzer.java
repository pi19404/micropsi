/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/exception/ExceptionAnalyzer.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.exception;

public class ExceptionAnalyzer {
 
  private static String BREAK = System.getProperty("line.separator");

  public static String getHeadline(Throwable e) {
  	String toReturn = "Exception! ID: ";
  	int id = 0;
  	if(e instanceof MicropsiException) id = ((MicropsiException)e).id;	
  	if(id != 0) toReturn += id; else toReturn += "no id found";
    return toReturn;    	
  }

  public static String getDescription(Throwable e, AbstractExceptionInfo i) {
  	int id = 0;
  	String description = "";
  	if(e instanceof MicropsiException) {
    	id = ((MicropsiException)e).id;
    	description = ((MicropsiException)e).description;
  	}
  	String severity;
  	switch (i.getSeverityForID(id)) {
  		case ExceptionSeverityIF.EX_SEVERITY_WARNING:
  			severity = "WARNING";
  			break;
  		case ExceptionSeverityIF.EX_SEVERITY_SEVERE:
  			severity = "SEVERE";
  			break;
  		case ExceptionSeverityIF.EX_SEVERITY_FATAL:
  			severity = "FATAL";
  			break;
  		default:
			severity = "UNHANDLED";		
  	}
  	String dtext = "";
  	if(description == null) description = "";
  	if(!i.getTextForID(id).equals("")) dtext += i.getTextForID(id)+" ";
  	if(!description.equals("")) dtext += description+" ";
  	return dtext+"Severity: "+severity; 
  }
  
  private static String shortStacktrace(StackTraceElement element) {
  	return element.getFileName()+":"+element.getLineNumber()+" ("+element.getClassName()+"."+element.getMethodName()+")";
  }
  
  private static String fullStacktrace(Throwable e) {
  	String message = e.getMessage();
  	if(message == null) message = " "; else message = " "+message;
  	String toReturn = e.toString()+":"+message+BREAK;
  	StackTraceElement[] elements = e.getStackTrace();
  	for(int i=0;i<elements.length;i++) {
    	toReturn += "\t\tat "+
    				elements[i].getClassName()+"."+
    				elements[i].getMethodName()+" ("+
    				elements[i].getFileName()+":"+
    				elements[i].getLineNumber()+")"+BREAK;
  	}
  	return toReturn;
  }
  
  public static String getShortExceptionAnalysis(Throwable e, AbstractExceptionInfo i) {
    String toReturn = getHeadline(e) + BREAK;
    toReturn += getDescription(e,i) + BREAK;
    toReturn += "Location: "+ shortStacktrace(e.getStackTrace()[0]) + System.getProperty("line.separator");   	
  	return toReturn;
  }
  
  public static String getExtendedExceptionAnalysis(Throwable e, AbstractExceptionInfo i) {
    String toReturn = getHeadline(e) + BREAK;
    toReturn += getDescription(e,i) + BREAK;
    toReturn += "Stacktrace: "+ shortStacktrace(e.getStackTrace()[0]);   	
  	return toReturn;
  }
  
  public static String getFullExceptionAnalysis(Throwable e, AbstractExceptionInfo i) {
    String toReturn = getHeadline(e) + BREAK;
    toReturn += getDescription(e,i) + BREAK;
    toReturn += "Stacktrace: "+fullStacktrace(e);   	   	

	if(e instanceof MicropsiException) {
		MicropsiException aepe = (MicropsiException)e;
		if(aepe.getRootCause() != null) {
			toReturn += System.getProperty("line.separator")+"----------------- root cause: ---"+BREAK+BREAK;
			toReturn += getFullExceptionAnalysis(aepe.getRootCause(),i);
		}
	} 
  	return toReturn;
  }
  	
}
