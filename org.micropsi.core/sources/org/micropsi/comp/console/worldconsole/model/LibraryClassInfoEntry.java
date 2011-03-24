/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 30.03.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Matthias
 *
 */
public class LibraryClassInfoEntry {
	
//	private ObjectStates ignoredStates = null;
	private Set<LibraryIconInfoEntry> iconInfoSet = null;
//	private String className = null;

	/**
	 * 
	 */
	public LibraryClassInfoEntry(String className, Element config, ImageRegistry images, ConsoleFacadeIF console, String objectImageDir) throws MicropsiException {
//		this.className = className;
//		ignoredStates = null;
		NodeList iconInfoNodes = config.getElementsByTagName("iconinfo");
		iconInfoSet = new HashSet<LibraryIconInfoEntry>(1);
		for (int i = 0; i < iconInfoNodes.getLength(); i++) {
			iconInfoSet.add(new LibraryIconInfoEntry((Element) iconInfoNodes.item(i), images, console, objectImageDir));
		}
	}
	
	public IIconKeyRetriever getIconRetriever(ObjectStates states) {
		int score = 0;
		IIconKeyRetriever res = null;
		if (iconInfoSet != null) {
			Iterator it = iconInfoSet.iterator();
			while (it.hasNext()) {
				LibraryIconInfoEntry iconInfo = (LibraryIconInfoEntry) it.next();
				if (iconInfo.getScore() > score && iconInfo.matches(states)) {
					score = iconInfo.getScore();
					res = iconInfo.getIconRetriever();
				}
			}
		}
		return res;
	}

}
