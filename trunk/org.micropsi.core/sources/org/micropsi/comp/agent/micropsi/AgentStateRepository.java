/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/AgentStateRepository.java,v 1.3 2005/07/12 12:55:16 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.micropsi.common.exception.MicropsiException;


public class AgentStateRepository {

	private File repository;
	private String sep = System.getProperty("file.separator");
	private String defaultstate = null;

	public AgentStateRepository(String directoryname, String defaultstate) throws MicropsiException {
		File directory = new File(directoryname);
		if(!directory.exists()) directory.mkdirs();
		if(!directory.isDirectory()) throw new MicropsiException(17,directory.getAbsolutePath());
		repository = directory;
		this.defaultstate = defaultstate;
	}

	public AgentStateRepository(File directory, String defaultstate) throws MicropsiException {
		if(!directory.exists()) directory.mkdirs();
		if(!directory.isDirectory()) throw new MicropsiException(17,directory.getAbsolutePath());
		repository = directory;
		this.defaultstate = defaultstate;
	}
	
	public String getDefaultAgentState() {
		if(defaultstate == null) return null;
		if(defaultstate.equals("null")) return null;
		
		List<String> allStates = getAgentStates();
		if(allStates.contains(defaultstate)) return defaultstate;
		
		if(allStates.size() == 0) return null;
		return allStates.get(0);
	}
	
	public List<String> getAgentStates() {
		String[] files = repository.list();
		ArrayList<String> toReturn = new ArrayList<String>();
		for(int i=0;i<files.length;i++) {
			String file = files[i];
			if(!file.endsWith(".mpn")) continue;
			toReturn.add(file.substring(0,file.lastIndexOf(".mpn")));
		}
		return toReturn;
	}
	
	public void deleteAgentState(String state) {
		File f = new File(getAgentStateDataPath(state));
		f.delete();
		f = new File(getAgentStateMetadataPath(state));
		f.delete();		
	}
	
	public void renameAgentState(String oldname, String newname) {
		File o = new File(getAgentStateDataPath(oldname));
		
		String newpath = repository.getAbsolutePath();
		if(!newpath.endsWith(sep)) newpath += sep;
		newpath += newname + ".mpn";
		
		File n = new File(newpath);
		if(n.exists()) return;
		o.renameTo(n);
		
		o = new File(getAgentStateMetadataPath(oldname));

		newpath = repository.getAbsolutePath();
		if(!newpath.endsWith(sep)) newpath += sep;
		newpath += newname + ".mpm";
		
		n = new File(newpath);
		
		if(n.exists()) return;
		o.renameTo(n);
	}
	
	public void createAgentState(String stateName) throws IOException {
		File f = new File(getAgentStateDataPath(stateName));
		if(!f.exists()) 
			f.createNewFile();
		
		f = new File(getAgentStateMetadataPath(stateName));
		if(!f.exists()) 
			f.createNewFile();
	}
	
	public boolean stateExists(String agentState) {
		File f = new File(getAgentStateDataPath(agentState));
		return f.exists();
	}
	
	public String getAgentStateDataPath(String agentState) {
		if(agentState == null) return null;
//		ArrayList states = getAgentStates();
//		if(!states.contains(agentState)) return null;
		
		String path = repository.getAbsolutePath();
		if(!path.endsWith(sep)) path += sep;
		path += agentState + ".mpn";
		return path;
	}
	
	public String getAgentStateMetadataPath(String agentState) {
		if(agentState == null) return null;
//		ArrayList states = getAgentStates();
		//if(!states.contains(agentState)) return null;

		String path = repository.getAbsolutePath();
		if(!path.endsWith(sep)) path += sep;
		path += agentState + ".mpm";
		return path;
	}
	
}
