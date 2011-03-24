/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/exception/MicropsiException.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.exception;

public class MicropsiException extends Exception {
	
	protected int id;
	protected String description;
	private Throwable rootcause;
	
	public MicropsiException(int id) {
		this.id = id;
	}
	
	public MicropsiException(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public MicropsiException(int id, String description, Throwable rcause) {
		this.id = id;
		this.description = description;
		this.rootcause = rcause;
		
		if(rootcause == null) return;
		while(true) {
			if(!(rootcause instanceof MicropsiException)) break;
			if(((MicropsiException)rootcause).rootcause == null) break;
			rootcause = ((MicropsiException)rootcause).rootcause;
		}
		super.initCause(rootcause);
	}
	
	public MicropsiException(int id, Throwable rcause) {
		this.id = id;
		this.rootcause = rcause;
		
		if(rootcause == null) return;
		while(true) {
			if(!(rootcause instanceof MicropsiException)) break;
			if(((MicropsiException)rootcause).rootcause == null) break;
			rootcause = ((MicropsiException)rootcause).rootcause;
		}
		super.initCause(rootcause);
	}
	
	public String getMessage() {
		return "Id: "+id+" desc: "+description;
	}
	
	public Throwable getRootCause() {
		return rootcause;
	}
	
/*	public Throwable getCause() {
		return rootcause;
	}*/
	
}
