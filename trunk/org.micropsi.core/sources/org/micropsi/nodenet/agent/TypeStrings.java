/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/agent/TypeStrings.java,v 1.7 2006/08/03 15:40:51 rvuine Exp $
 */
package org.micropsi.nodenet.agent;

import java.util.HashMap;
import java.util.Iterator;

import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.SlotTypesIF;

public class TypeStrings {
	
	private static final HashMap<String,TypeStringsExtensionIF> extensions = new HashMap<String,TypeStringsExtensionIF>();
	
	public static final void activateExtension(TypeStringsExtensionIF ext) {
		extensions.put(ext.getExtensionID(),ext); 
	}
		
	private final static String GEN = "GEN";
	private final static String SUB = "SUB";
	private final static String SUR = "SUR";
	private final static String POR = "POR";
	private final static String RET = "RET";
	private final static String CAT = "CAT";
	private final static String EXP = "EXP";
	private final static String SYM = "SYM";
	private final static String REF = "REF";
	
	private final static String ASSOCIATION = "Asso";
	private final static String DISSOCIATION = "Disso";
	
	public static final String gateType(int type) {
		Iterator iter = extensions.values().iterator();
		while(iter.hasNext()) {
			TypeStringsExtensionIF ext = (TypeStringsExtensionIF)iter.next();
			String ret = ext.gateType(type);
			if(ret != null) return ret;			
		}
		
		switch(type) {
			case GateTypesIF.GT_GEN:	return GEN;
			case GateTypesIF.GT_SUB:	return SUB;
			case GateTypesIF.GT_SUR:	return SUR;
			case GateTypesIF.GT_POR:	return POR;
			case GateTypesIF.GT_RET:	return RET;
			case GateTypesIF.GT_CAT:	return CAT;
			case GateTypesIF.GT_EXP:	return EXP;
			case GateTypesIF.GT_SYM:	return SYM;
			case GateTypesIF.GT_REF:	return REF;
			case GateTypesIF.GT_ASSOCIATION:	return ASSOCIATION;
			case GateTypesIF.GT_DISSOCIATION:	return DISSOCIATION;
			default: return "#"+type;
		}
	}

	public static String slotType(int type) {
		Iterator iter = extensions.values().iterator();
		while(iter.hasNext()) {
			TypeStringsExtensionIF ext = (TypeStringsExtensionIF)iter.next();
			String ret = ext.slotType(type);
			if(ret != null) return ret;			
		}

		switch(type) {
			case SlotTypesIF.ST_GEN:	return GEN;
			case SlotTypesIF.ST_POR:	return POR;
			case SlotTypesIF.ST_RET:	return RET;
			case SlotTypesIF.ST_SUB:	return SUB;
			case SlotTypesIF.ST_SUR:	return SUR;
			default: return "#"+type;
		}
	}

	public static final String ACT_GEN = "ACT_GEN";
	public static final String ACT_SUR = "ACT_SUR";
	public static final String ACT_SUB = "ACT_SUB";
	public static final String ACT_POR = "ACT_POR";
	public static final String ACT_RET = "ACT_RET";
	public static final String ACT_CAT = "ACT_CAT";
	public static final String ACT_EXP = "ACT_EXP";
	public static final String ACT_SYM = "ACT_SYM";
	public static final String ACT_REF = "ACT_REF";
	public static final String ACTIVATOR = "ACTIVATOR";
	public static final String DEACTIVATOR = "DEACTIVATOR";
	public static final String ASSOCIATOR = "ASSOCIATOR";
	public static final String DISSOCIATOR = "DISSOCIATOR";
	public static final String CONCEPT = "CONCEPT";
	public static final String TOPO = "TOPO (experimental)";
	public static final String CHUNK = "CHUNK (experimental)";
	public static final String REGISTER = "REGISTER";
	public static final String SENSOR = "SENSOR";				
	public final static String ACTOR = "ACTOR";									
	
	public static final String nodeType(int i) {
		switch(i) {
			case NodeFunctionalTypesIF.NT_ACT_SUR:		return ACT_SUR;
			case NodeFunctionalTypesIF.NT_ACT_SUB:		return ACT_SUB;
			case NodeFunctionalTypesIF.NT_ACT_POR:		return ACT_POR;
			case NodeFunctionalTypesIF.NT_ACT_RET:		return ACT_RET;
			case NodeFunctionalTypesIF.NT_ACT_CAT:		return ACT_CAT;
			case NodeFunctionalTypesIF.NT_ACT_EXP:		return ACT_EXP;
			case NodeFunctionalTypesIF.NT_ACT_SYM:		return ACT_SYM;
			case NodeFunctionalTypesIF.NT_ACT_REF:		return ACT_REF;
			case NodeFunctionalTypesIF.NT_ACTIVATOR:	return ACTIVATOR;
			case NodeFunctionalTypesIF.NT_DEACTIVATOR:	return DEACTIVATOR;
			case NodeFunctionalTypesIF.NT_ASSOCIATOR:	return ASSOCIATOR;
			case NodeFunctionalTypesIF.NT_DISSOCIATOR:	return DISSOCIATOR;
			case NodeFunctionalTypesIF.NT_CONCEPT:		return CONCEPT;
			case NodeFunctionalTypesIF.NT_TOPO:			return TOPO;
			case NodeFunctionalTypesIF.NT_CHUNK:		return CHUNK;
			case NodeFunctionalTypesIF.NT_REGISTER:		return REGISTER;
			case NodeFunctionalTypesIF.NT_SENSOR:		return SENSOR;
			case NodeFunctionalTypesIF.NT_ACTOR:		return ACTOR;
			default: return "#"+i;
		}
	}

	
}
