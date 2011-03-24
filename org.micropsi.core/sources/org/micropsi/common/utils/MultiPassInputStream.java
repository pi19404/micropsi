/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/utils/MultiPassInputStream.java,v 1.2 2005/10/17 12:29:39 vuine Exp $ 
 */
package org.micropsi.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MultiPassInputStream. InputStream that can be used more than once.
 * Note that the close() method of this implementation does nothing.
 *
 * @author rv
 */
public class MultiPassInputStream extends FilterInputStream {

	private static final int MODE_BUFFER 	= 0;
	private static final int MODE_FILE		= 1;
	
	private static InputStream createBuffer(InputStream in) throws IOException {

		ByteArrayOutputStream bytes;
		
		int BUFSIZE = 1024;
		
		bytes = new ByteArrayOutputStream();
		byte[] buf = new byte[BUFSIZE];
		
		int read = 0;
		do {
			read = in.read(buf);
			bytes.write(buf,0,read);
		} while(read == BUFSIZE || in.available() > 0);
		
		return new ByteArrayInputStream(bytes.toByteArray());
	}
	
	
	private int mode = -1;
	private File file = null;
	
	/**
	 * Constructs a MultiPassInputStream from an inputStream. Note that this
	 * will load the entire contents of the stream into memory as there is
	 * no other way to ensure that the stream contents can be read again.
	 * <br><br>
	 * If you pass a stream type that needs to be closed, you MUST ensure
	 * that the passed stream is closed as the close() method of this
	 * implementation does nothing.
	 * 
	 * @param in the inputStream to use, != null
	 * @throws IOException
	 */
	public MultiPassInputStream(InputStream in) throws IOException {		
		super(createBuffer(in));
		super.mark(Integer.MAX_VALUE);
		mode = MODE_BUFFER;
	}
	
	/**
	 * 
	 * Constructs a MultiPassInputStream from a file. This will not load
	 * the file contents into memory. When the stream is accessed another
	 * time, the file is just reopened and read again.
	 * 
	 * @param file the file to be used, != null
	 * @throws FileNotFoundException
	 */
	public MultiPassInputStream(File file) throws FileNotFoundException {
		super(new FileInputStream(file));
		this.file = file;
		mode = MODE_FILE;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#markSupported()
	 */
	public boolean markSupported() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		switch(mode) {
			case MODE_BUFFER:
				super.reset();
				break;
			case MODE_FILE:
				super.in.close();
				super.in = new FileInputStream(file);
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#mark(int)
	 */
//	public synchronized void mark(int readlimit) {
//	}
	
	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	public void close() {
	}
	
	public void dump() {
				
		switch(mode) {
		case MODE_BUFFER:
			try {
				super.reset();
				int read = 0;
				byte[] buf = new byte[1024];
				do {
					read = super.read(buf);
					System.err.print(new String(buf,0,read));
				} while (read > 0);
			} catch (Exception e) {
				System.err.println("Unable to dump: "+e.getMessage());
			}
			break;
		case MODE_FILE:
			try {
				FileInputStream fin = new FileInputStream(this.file);
				int read = 0;
				byte[] buf = new byte[1024];
				do {
					read = fin.read(buf);
					System.err.print(new String(buf,0,read));
				} while (read > 0);
			} catch (Exception e) {
				System.err.println("Unable to dump: "+e.getMessage());
			}
			break;
	}

	}
	

}
