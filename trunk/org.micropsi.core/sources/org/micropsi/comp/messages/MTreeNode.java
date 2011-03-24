/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MTreeNode.java,v 1.3 2005/07/12 12:55:17 vuine Exp $
 */
package org.micropsi.comp.messages;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class MTreeNode implements MessageIF {

	private ArrayList<MTreeNode> children;
	private String name;
	private String value;
	private MTreeNode parent;
	
	public MTreeNode() {
	}
		
	public MTreeNode(String name, String value, MTreeNode parent) {
		this.name = name;
		this.value = value;
		if(parent != null) {
			parent.addChild(this);
		} else this.parent = null;
	}
	
	public void addChild(MTreeNode newChild) {
		if(children == null) children = new ArrayList<MTreeNode>();
		children.add(newChild);
		newChild.setParent(this);
	}
	
	public MTreeNode addChild(String name, String value) {
		if(children == null) children = new ArrayList<MTreeNode>();
		MTreeNode newChild = new MTreeNode(name,value,this);
		return newChild;
	}
	
	/**
	 * @deprecated Use children() instead. DO NOT USE THIS METHOD ANY MORE
	 */
	public Enumeration<MTreeNode> enumerateChildren() {
		if(children != null) {
			Vector<MTreeNode> v = new Vector<MTreeNode>(children);
			return v.elements();
		}	
		else return null;
	}
	
	/**
	 * Returns an iterator with children or null if there are no children
	 * @return the children iterator, may be null
	 */
	public Iterator<MTreeNode> children() {
		if(children != null) {
			return children.iterator();
		} else {
			return null;		
		}
	}
	
	public MTreeNode[] getChildren() {
		if(children == null) return null;
		MTreeNode[] toReturn = new MTreeNode[children.size()];
		for(int i=0;i<children.size();i++)
			toReturn[i] = children.get(i);
		return toReturn;		
	}
		
	public String[] extractChildrenNames() {
		if(children == null) return null;
		String[] toReturn = new String[children.size()];
		for(int i=0;i<children.size();i++)
			toReturn[i] = children.get(i).getName();
		return toReturn;
	}
	
	public MTreeNode searchChild(String name) {
		for(int i=0;i<children.size();i++)
			if(children.get(i).getName().equals(name))
				return children.get(i);
		return null;
	}
	
	public int getMessageType() {
		return MessageTypesIF.MTYPE_CONSOLE_TREENODE;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	protected MTreeNode getParent() {
		return parent;
	}
	
	public boolean isRoot() {
		return (parent == null);
	}

	public String createNameTrace() {
		MTreeNode actual = this;
		String toReturn = "";
		do {
			toReturn = actual.getName() + "." +toReturn;
			actual = actual.getParent();
		} while (!actual.isRoot());
		return toReturn;
	} 
	
	public MTreeNode addChild(String name, int value) {
		return addChild(name,Integer.toString(value));
	}
	
	public MTreeNode addChild(String name, long value) {
		return addChild(name,Long.toString(value));
	}
	
	public MTreeNode addChild(String name, double value) {
		return addChild(name,Double.toString(value));
	}
	
	public MTreeNode addChild(String name, boolean value) {
		return addChild(name, value+"");	
	}
	
	public int intValue() {
		return Integer.parseInt(getValue());
	}

	public double doubleValue() {
		return Double.parseDouble(getValue());
	}
	
	public long longValue() {
		return Long.parseLong(getValue());
	}
	
	public boolean boolValue() {
		return getValue().equalsIgnoreCase("true");
	}
	
	public String toString() {
		String toReturn = "+-"+name+" - "+value+"\n";
		MTreeNode p = getParent();
		while (p != null) {
			toReturn = " " + toReturn;
			p = p.getParent();
		}
		if(children != null)
			for(int i=0;i<children.size();i++) {
				toReturn += children.get(i).toString();
			}
		return toReturn;
	}
	
	/**
	 * 
	 * Ronnie wants Object comparison -- thus we don't override hashCode() here
	 * but this is thought for putting the object into HashMaps.
	 * 
	 * @return
	 */
	public int getHashKey(){
		int ret = 0;
		Iterator childrenIterator;
		
		if (name != null)
			ret += name.hashCode();
		
		if (value != null)
			ret += value.hashCode();
		
		//we don't check the parent -- assuming, that hashKeys are calculated from top nodes
		//otherwise we would get a no-terminating recursion when traversing through the children
		
		if (children != null && !(children.isEmpty())){
			childrenIterator = children.iterator();
			
			while(childrenIterator.hasNext()){
				ret += ((MTreeNode)childrenIterator.next()).getHashKey(); 
			}
		}		
		return ret;
	}

	
	/**
	 * 
	 * same as above (@see #getHashKey())
	 * 
	 * @param o
	 * @return
	 */
	public boolean contentEquals(Object o){
		
		boolean ret = false;
		MTreeNode comparison;
		
		if(o != null){
			if (o instanceof MTreeNode){
				ret = true;
				comparison = (MTreeNode) o;
				
				ret &= stringsEqual(this.name, comparison.getName());
				ret &= stringsEqual(this.value, comparison.getValue());

				//we don't check the parent -- assuming, that contentEquals is calculated from top nodes
				//otherwise we would get a no-terminating recursion when traversing through the children

				if (this.children != null && !children.isEmpty()){
					if(comparison.getChildren() != null) {
						if (this.children.size() == comparison.getChildren().length){
							Iterator itThis = children.iterator();
							Enumeration itComp = comparison.enumerateChildren();
				
							while(itThis.hasNext() && itComp.hasMoreElements())
								ret &= ((MTreeNode) itThis.next()).contentEquals(itComp.nextElement());
						} else
							ret = false;
					}
				} else 
					ret &= (comparison.getChildren() == null);
			}
		}
	return ret;
		
	}
	
	/**
	 * Compares two strings, assuming the empty string is equivalent to the null
	 * string. TODO: Ronnie/Matthias: This is needed because there is a bug in treenodes of answered questions, where
	 * where empty strings are replaced by null strings. This happens only in distributed
	 * mode, where questions are sent to a remote server. 
	 *  
	 * @param a
	 * @param b
	 * @return
	 */
	protected boolean stringsEqual(String a, String b){
		return ((a == null || a.equals("")) && (b == null || b.equals(""))) 
			|| a.equals(b);
	}
	
	/**
	 * Sets the parent.
	 * @param parent The parent to set
	 */
	protected void setParent(MTreeNode parent) {
		this.parent = parent;
	}

}
