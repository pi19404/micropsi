/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/library/LibraryManager.java,v 1.10 2005/08/20 16:28:10 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.library;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IType;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.common.utils.MultiPassInputStream;
import org.micropsi.eclipse.common.ProgressDialog;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.mindconsole.widgets.EntityTransferData;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.eclipse.runtime.RuntimePlugin;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetWeaver;
import org.micropsi.nodenet.NodeSpaceModule;

public class LibraryManager {

	public static final String encodeLibraryName(String name) {
		StringBuffer b = new StringBuffer();
		
		for(int i=0;i<name.length();i++) {
			char c = name.charAt(i);
			if(Character.isLetter(c) || Character.isDigit(c)) {
				b.append(c);
			} else {
				b.append('_');
			}
		}
		
		return b.toString();
	}
	
	private Logger logger;	
	private File directory;
	
//	private HashMap<String,HashMap<String,String>> javadata = new HashMap<String,HashMap<String,String>>();
	
	public LibraryManager(Logger logger, String path) {		
		this.logger = logger;
		this.directory = new File(path);
	
		if(!directory.exists()) {
			directory.mkdirs();
		}
		
//		loadLibrary();
	}
	
//	public void loadLibrary() throws MicropsiException, IOException {
//	
//		File[] entries = directory.listFiles();
//		if(entries == null) entries = new File[0];
//		for(int i=0;i<entries.length;i++) {
//			if(entries[i].isDirectory()) {
//				continue;
//			}
//			
//			String name = entries[i].getName();
//			File datadir = new File(entries[i],"data");
//			File[] datafiles = datadir.listFiles();
//			if(datafiles == null) datafiles = new File[0];
//			for(int j=0;j<datafiles.length;j++) {
//				if(datafiles[j].isDirectory()) {
//					continue;
//				}
//				String dataname = datafiles[j].getName();				
//				String content = "";
//				int read = 0;
//				char[] buffer = new char[1024];
//				FileReader r = new FileReader(datafiles[i]);
//				do {
//					read = r.read(buffer);
//					if(read > 0)
//						content += new String(buffer,0,read);
//				} while(read > 0);
//							
//				if(!javadata.containsKey(name))
//					javadata.put(name, new HashMap<String,String>());
//				
//				HashMap<String,String> javaEntryData = javadata.get(name);
//				if(!javaEntryData.containsKey(dataname)) {
//					javaEntryData.put(dataname, content);
//				}
//			}
//		}		
//	}
		
//	public void reloadLibraryNets(ProgressMonitorIF progress) throws FileNotFoundException, MicropsiException {
//		MultiPassInputStream in = new MultiPassInputStream(netdata);
//		net.loadNet(in, true);
//		
//		FileInputStream fin = new FileInputStream(netdata);
//		netModel = new NetModel();
//		netModel.initFromNet(net, fin, progress);
//	}
	
//	public void saveLibrary() throws MicropsiException, IOException {
//		FileOutputStream fout = new FileOutputStream(netdata);
//		net.saveNet(fout);
//		
//		fout = new FileOutputStream(metadata);
//		netModel.saveModels(fout);
//		
//		File[] files = datadir.listFiles();
//		if(files == null) files = new File[0];
//		for(int i=0;i<files.length;i++)
//			files[i].delete();
//			
//		Iterator entryKeys = javadata.keySet().iterator();
//		while(entryKeys.hasNext()) {
//			String entryKey = (String) entryKeys.next();
//			Map<String,String> entryJavaDataMap = javadata.get(entryKey);
//			Iterator<String> javaDataKeys = entryJavaDataMap.keySet().iterator();
//			while(javaDataKeys.hasNext()) {
//				String javaDataKey = javaDataKeys.next();
//				String content = entryJavaDataMap.get(javaDataKey); 
//			
//				File newFile = new File(
//					datadir.getAbsolutePath()+"/"+
//					entryKey+"_"+
//					javaDataKey.replace('.', '_'));
//				newFile.createNewFile();
//				FileWriter fw = new FileWriter(newFile);
//				fw.write(content);
//				fw.close();	
//			}
//		}
//	}
		
	public synchronized void createEntry(String entryName,  List<EntityTransferData> list) throws LibraryException {
		
		entryName = encodeLibraryName(entryName);
		
		if(getEntries().contains(entryName)) return;
		
		try {
			String entryID = entryName;
			File entryDir = new File(directory,entryID);
			entryDir.mkdir();
			
			File netdata = new File(entryDir,"library.mpn");
			File metadata = new File(entryDir,"library.mpm");
			File datadir = new File(entryDir,"data");
			
			if(!netdata.exists()) {
				netdata.createNewFile();
				FileWriter fw = new FileWriter(netdata);
				fw.write("<MicroPsiNet><Entities netstep=\"0\"><Entity id=\"LibraryRoot\" name=\"null\" type=\"1\" dissoconstant=\"1\" learningconst=\"0\" strengthconst=\"0\" parent=\"null\"/></Entities></MicroPsiNet>");
				fw.flush();
				fw.close();
			}
	
			if(!metadata.exists()) {
				metadata.createNewFile();
				FileWriter fw = new FileWriter(metadata);
				fw.write("<MicroPsiMetadata/>");
				fw.flush();
				fw.close();
			}
			
			if(!datadir.exists()) {
				datadir.mkdir();
			}
			
			// process data dir entries
			ArrayList<NetEntity> entities = new ArrayList<NetEntity>();		
			for(int i=0;i<list.size();i++) {
				EntityTransferData data = list.get(i); 
				entities.add(data.entity);
				
				if(data.entity.getEntityType() == NetEntityTypesIF.ET_MODULE_NATIVE) {
					NativeModule nm = (NativeModule)data.entity;
					String cn = nm.getImplementationClassName();
	
					if(cn != null) {
						IType type = ModuleJavaManager.getInstance().findType(cn);
						String sourceCode = type.getCompilationUnit().getSource();
						File dataDir = new File(directory,entryName+"/data");
						File dataFile = new File(dataDir,nm.getImplementationClassName());
						dataFile.createNewFile();
						FileWriter w = new FileWriter(dataFile);
						w.write(sourceCode);
						w.flush();
						w.close();
					}
				}
			}
			
			File netfile = new File(directory,entryName+"/library.mpn");
			File metafile = new File(directory,entryName+"/library.mpm");
			
			// process net
			
			LocalNetFacade net = new LocalNetFacade(logger, new LibraryNetProperties());
			net.loadNet(new MultiPassInputStream(netfile),true);
			NetModel netModel = new NetModel();
			netModel.initFromNet(net,new FileInputStream(metafile),ProgressMonitorIF.DUMMY);
	
			HashMap<String,String> cloneMap = new HashMap<String,String>();
			
			int preservemode = NetWeaver.PM_PRESERVE_INTER;
			
			ProgressDialog dlg = new ProgressDialog("Inserting entities...", RuntimePlugin.getDefault().getShell());
		
			NetWeaver.insertEntities(
				net, 
				entities, 
				net.getRootNodeSpaceModule().getID(),
				preservemode, 
				cloneMap,
				dlg
			);
			
			net.saveNet(new FileOutputStream(new File(directory,entryName+"/library.mpn")));
	
			for(int i=0;i<list.size();i++) {
				EntityTransferData data = list.get(i);
				NetEntity cloneentity = data.entity;
				String cloneId = cloneMap.get(cloneentity.getID());
	
				EntityModel newItemModel = netModel.getModel(cloneId);
				newItemModel.setX(data.x);
				newItemModel.setY(data.y);
			}
			
			netModel.saveModels(new FileOutputStream(metafile));
			
			net.destroy();
		} catch (Exception e) {
			deleteEntry(entryName);
			throw new LibraryException("Could not create entry",e);
		}

	}
	
	public void deleteEntry(String entryName) {
		
		entryName = encodeLibraryName(entryName);
		
		if(!getEntries().contains(entryName)) return;
		
		File entryDir = new File(directory,entryName);
		if(!entryDir.exists()) {
			return;
		}
		
		File dataDir = new File(entryDir,"data");
		if(dataDir.exists()) {
			File[] dataFiles = dataDir.listFiles();
			if(dataFiles == null) {
				dataFiles = new File[0];
			}
			for(int i=0;i<dataFiles.length;i++) {
				dataFiles[i].delete();
			}
		}
		
		File[] contents = entryDir.listFiles();
		if(contents == null) {
			contents = new File[0];
		}
		for(int i=0;i<contents.length;i++) {
			contents[i].delete();
		}
		
		entryDir.delete();
		
//		javadata.remove(entryName);
	}

	public List<String> getEntries() {

		ArrayList<String> list = new ArrayList<String>();
		
		File[] entries = directory.listFiles();
		if(entries == null) {
			entries = new File[0];
		}
		for(int i=0;i<entries.length;i++) {
			if(!entries[i].isDirectory()) {
				continue;
			} else {
				list.add(entries[i].getName());
			}
		}
		
		return list;
	}
	
	public List<EntityModel> getElementsOfEntry(String entryName) throws LibraryException {

		ArrayList<EntityModel> list = new ArrayList<EntityModel>();
		
		if(!getEntries().contains(entryName)) return list;
		
		try {
			File netfile = new File(directory,entryName+"/library.mpn");
			File metafile = new File(directory,entryName+"/library.mpm");
			
			LocalNetFacade net = new LocalNetFacade(logger,new LibraryNetProperties());
			net.loadNet(new MultiPassInputStream(netfile),true);
			
			NetModel netModel = new NetModel();
			netModel.initFromNet(net,new FileInputStream(metafile),ProgressMonitorIF.DUMMY);
			
			NodeSpaceModule space = net.getRootNodeSpaceModule();
			Iterator iter = space.getAllEntities();
			while(iter.hasNext())
				list.add(netModel.getModel(((NetEntity)iter.next()).getID()));
		} catch (Exception e) {
			throw new LibraryException("Could not retrieve library content",e);
		}
			
		return list;
	}
	
	public String getElementText(String entryName) throws MicropsiException {
//		if(!getEntries().contains(entryName)) return "";
//		String id = findIDForEntryName(entryName);
//		return netModel.getModel(id).getComment();
		return "not implemented";
	}
	
	public void setElementText(String entryName, String text) throws MicropsiException {
//		if(!getEntries().contains(entryName)) return;
//		String id = findIDForEntryName(entryName);
//		netModel.getModel(id).setComment(text);
	} 
//
//	public int getElementEntityCount(String entryName) throws MicropsiException {
//		if(!getEntries().contains(entryName)) return 0;
//		
//		String id = findIDForEntryName(entryName);
//		Iterator iter = net.getNodeSpaceModule(id).getAllEntities();
//		int i=0;
//		while(iter.hasNext()) {
//			iter.next();
//			i++;
//		} 		
//		
//		return i;
//	}

	public String getElementSourceCode(String entryName, String entryJavaName) throws LibraryException {
		if(!getEntries().contains(entryName)) return null;

		try {
			File sourceFile = new File(directory,entryName+"/data/"+entryJavaName);		
			String content = "";
			int read = 0;
			char[] buffer = new char[1024];
			FileReader r = new FileReader(sourceFile);
			do {
				read = r.read(buffer);
				if(read > 0)
					content += new String(buffer,0,read);
			} while(read > 0);
			
			return content;

		} catch (Exception e) {
			throw new LibraryException("Cannot read source file",e);
		}
	}
	
	public void exportLibraryEntry(String entryName, File file) throws LibraryException {
		
		try {
		
			LocalNetFacade exportnet = new LocalNetFacade(logger, new LibraryNetProperties());
			NodeSpaceModule rootspace = exportnet.createNodeSpace(null);
			NetModel exportmodel = new NetModel();
			exportmodel.initFromNet(exportnet, null, null);
			
			List<EntityModel> toExport = getElementsOfEntry(entryName);
			ArrayList<NetEntity> entities = new ArrayList<NetEntity>();
			
			for(int i=0;i<toExport.size();i++)
				entities.add(toExport.get(i).getEntity());
				
			HashMap<String,String> cloneMap = new HashMap<String,String>();
			
			NetWeaver.insertEntities(
				exportnet, 
				entities,
				rootspace.getID(),
				NetWeaver.PM_PRESERVE_ALL, 
				cloneMap,
				null
			);
	
			for(int i=0;i<toExport.size();i++) {
				
				EntityModel origModel = toExport.get(i);
				String cloneId = cloneMap.get(origModel.getEntity().getID());
				EntityModel newItemModel = exportmodel.getModel(cloneId);
				newItemModel.setX(origModel.getX());
				newItemModel.setY(origModel.getY());
			}	
	
			ByteArrayOutputStream netdata = new ByteArrayOutputStream();
			ByteArrayOutputStream metdata = new ByteArrayOutputStream();
	
			exportnet.saveNet(netdata);
			exportmodel.saveModels(metdata);
			
			ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file));
			
			ZipEntry e = new ZipEntry("net.mpn");
			zout.putNextEntry(e);
			zout.write(netdata.toByteArray());
			zout.closeEntry();
			netdata = null;
			
			e = new ZipEntry("net.mpm");
			zout.putNextEntry(e);
			zout.write(metdata.toByteArray());
			zout.closeEntry();
			metdata = null;		
			
			File[] dataFiles = new File(directory,entryName+"/data").listFiles(); 
			if(dataFiles == null) {
				dataFiles = new File[0];
			}
			for(int i=0;i<dataFiles.length;i++) {
				String name = dataFiles[i].getName();
				String content = getElementSourceCode(entryName,name);
				e = new ZipEntry(name);
				zout.putNextEntry(e);
				zout.write(content.getBytes());
				zout.closeEntry();
	
			}
			
			zout.close();
		} catch (Exception e) {
			throw new LibraryException("Could not export",e);
		}
	}
	
	public void importEntry(String entryName, File file) throws LibraryException {

		entryName = encodeLibraryName(entryName);
		
		createEntry(entryName,new ArrayList<EntityTransferData>());

		try {
		
			ZipFile zipFile = new ZipFile(file, ZipFile.OPEN_READ);
			Enumeration zipFileEntries = zipFile.entries();
			
			while (zipFileEntries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			
				String currentEntry = entry.getName();
				
				BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
				
				String content = "";
			
				int read = 0;
				byte[] buffer = new byte[1024];			
				do {
					read = is.read(buffer);
					if(read > 0)
						content += new String(buffer,0,read);
				} while(read > 0);
				
				if(	currentEntry.equals("net.mpn")) {
					File libFile = new File(directory,entryName+"/library.mpn");
					libFile.createNewFile();
					FileWriter writer = new FileWriter(libFile);
					writer.write(content);
					writer.flush();
					writer.close();				
				} else if(currentEntry.equals("net.mpm")) {
					File libFile = new File(directory,entryName+"/library.mpm");
					libFile.createNewFile();
					FileWriter writer = new FileWriter(libFile);
					writer.write(content);
					writer.flush();
					writer.close();								
				} else {
					File dataDir = new File(directory,entryName+"/data");
					File dataFile = new File(dataDir,currentEntry);
					dataFile.createNewFile();
					FileWriter writer = new FileWriter(dataFile);
					writer.write(content);
					writer.flush();
					writer.close();
				}
			}
		} catch (Exception e) {
			deleteEntry(entryName);
			throw new LibraryException("Could not import entry",e);
		}		
	}
}
