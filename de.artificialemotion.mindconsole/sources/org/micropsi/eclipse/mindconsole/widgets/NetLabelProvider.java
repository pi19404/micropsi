/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/widgets/NetLabelProvider.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole.widgets;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;


public class NetLabelProvider extends LabelProvider {

	private static boolean initialized = false;
	private static Image SPACE;
	private static Image NODE;
	private static Image NATIVE;
	private static Image SENSOR;
	private static Image ACTOR;
	
	public NetLabelProvider() {
		if(initialized) return;
		
		try {
		
			SPACE = new Image(
				Display.getCurrent(),
				Platform.asLocalURL(MindPlugin.getDefault().getBundle().getEntry("/icons/space.gif")).getFile());

			NODE = new Image(
				Display.getCurrent(),
				Platform.asLocalURL(MindPlugin.getDefault().getBundle().getEntry("/icons/node.gif")).getFile());

			NATIVE = new Image(
				Display.getCurrent(),
				Platform.asLocalURL(MindPlugin.getDefault().getBundle().getEntry("/icons/native.gif")).getFile());

			SENSOR = new Image(
				Display.getCurrent(),
				Platform.asLocalURL(MindPlugin.getDefault().getBundle().getEntry("/icons/sensor.gif")).getFile());

			ACTOR = new Image(
				Display.getCurrent(),
				Platform.asLocalURL(MindPlugin.getDefault().getBundle().getEntry("/icons/actor.gif")).getFile());
			
			
		} catch (IOException e) {
			MindPlugin.getDefault().handleException(e);
		}
		
	}
	
	public Image getImage(Object o) {
		NetEntity entity = (NetEntity)o;

		switch(entity.getEntityType()) {
			case NetEntityTypesIF.ET_MODULE_NATIVE:
				return NATIVE;
			case NetEntityTypesIF.ET_MODULE_NODESPACE:
				return SPACE;
			case NetEntityTypesIF.ET_NODE:
				switch(((Node)entity).getType()) {
					case NodeFunctionalTypesIF.NT_SENSOR:
						return SENSOR;
					case NodeFunctionalTypesIF.NT_ACTOR:
						return ACTOR;
					default:
						return NODE;
				}
				
		}
		return null;
	}
	
	public String getText(Object o) {
		NetEntity entity = (NetEntity)o;
	
		String toReturn = entity.getEntityName();
		
		switch(entity.getEntityType()) {
			case NetEntityTypesIF.ET_MODULE_NATIVE:
				NativeModule m = (NativeModule)entity;
				toReturn = m.getImplementationName()+" "+toReturn;
				break;
			case NetEntityTypesIF.ET_MODULE_NODESPACE:
				break;
			case NetEntityTypesIF.ET_NODE:
				toReturn += " "+TypeStrings.nodeType(((Node)entity).getType());
				break;
		}
		
		return toReturn;
	}

	
}
