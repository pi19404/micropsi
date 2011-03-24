package org.micropsi.common.xml;

/**
 *  $Header $
 *  @author matthias
 *
 */
public class XMLElementNotFoundException extends Exception {

	/**
	 * Constructor for XMLElementNotFoundException.
	 */
	public XMLElementNotFoundException() {
		super();
	}

	/**
	 * Constructor for XMLElementNotFoundException.
	 * @param arg0
	 */
	public XMLElementNotFoundException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor for XMLElementNotFoundException.
	 * @param arg0
	 * @param arg1
	 */
	public XMLElementNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor for XMLElementNotFoundException.
	 * @param arg0
	 */
	public XMLElementNotFoundException(Throwable arg0) {
		super(arg0);
	}

}
