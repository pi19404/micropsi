/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/utils/FileAppender.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.utils;

import java.io.*;

import org.apache.log4j.Layout;

/**
 * @author daniel
 * 
 * Our own FileAppender class to stay platform independent.
 * The original @see org.apache.log4j.FileAppender class did not
 * distinguish between Unix and DOS filenames.
 *
 */
public class FileAppender extends org.apache.log4j.FileAppender {

	/**
	 * Constructor for FileAppender.
	 */
	public FileAppender() {
		super();
	}

	/**
	 * Constructor for FileAppender.
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @throws IOException
	 */
	public FileAppender(
		Layout arg0,
		String arg1,
		boolean arg2,
		boolean arg3,
		int arg4)
		throws IOException {
    		super(arg0, arg1.replace('\\',File.separatorChar).replace('/', File.separatorChar), 
                  arg2, arg3, arg4);
	}

	/**
	 * Constructor for FileAppender.
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws IOException
	 */
	public FileAppender(Layout arg0, String arg1, boolean arg2)
		throws IOException {
		super(arg0, arg1.replace('\\', File.separatorChar).replace('/',File.separatorChar), arg2);
	}

	/**
	 * Constructor for FileAppender.
	 * @param arg0
	 * @param arg1
	 * @throws IOException
	 */
	public FileAppender(Layout arg0, String arg1) throws IOException {
		super(arg0, arg1.replace('\\', File.separatorChar).replace('/',File.separatorChar));
	}

}
