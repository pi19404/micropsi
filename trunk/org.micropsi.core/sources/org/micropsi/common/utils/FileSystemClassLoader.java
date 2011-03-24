/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/utils/FileSystemClassLoader.java,v 1.3 2005/07/12 12:55:16 vuine Exp $ 
 */
package org.micropsi.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FileSystemClassLoader extends ClassLoader {

	private String directory;
	
	public FileSystemClassLoader(String directory, ClassLoader parent) {
		super(parent);
		this.directory = directory;
	}	
	
	public Class<?> findClass(String classname) throws ClassNotFoundException {
		return fileLoad(
			classname,
			calcFilePath(directory,classname)
		);
	}

	protected String calcFilePath(String directory, String classname) {
		String filepath = classname.replace('.', '/') + ".class";
		directory.replace('\\', '/');
		if(!directory.endsWith("/")) directory += "/";
		return directory + filepath;
	}

	protected Class fileLoad(String classname, String filepath) throws ClassNotFoundException {		
		try {
			File osfile = new File(filepath);				
			FileInputStream finp = new FileInputStream(osfile);				
			byte[] b = new byte[(int)osfile.length()];				
			finp.read(b);				
			return defineClass(classname, b, 0, b.length);
		} catch (FileNotFoundException e) {
			throw new ClassNotFoundException("Classfile not found: "+e.getMessage());		
		} catch (ClassFormatError e) {
			throw new ClassNotFoundException("Illegal classfile: "+e.getMessage());
		} catch (IOException e) {
			throw new ClassNotFoundException("IO error when loading class: "+e.getMessage());
		}
	}

}
