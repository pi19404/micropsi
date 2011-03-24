/*
 * Created on 21.06.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.w3c.dom.Element;

/**Stores information about an overlay renderer class and creates renderer instances.
 * 
 * @author Matthias
 */
public class OverlayRendererDescriptor {
	
	private String name;
	private String className;
	private IOverlayRenderer renderer;
	private int zOrder;
	private boolean enabled;
	
	protected ConsoleFacadeIF console;

	public OverlayRendererDescriptor(Element config, ConsoleFacadeIF console) throws MicropsiException {
		this.console = console;
		name = config.getAttribute("name");
		if (name == null || name == "") {
			console.getLogger().warn("Initialising overlay renderer: renderer has no 'name' attribute, using 'unknown'.");
			name = "unknown";
		}
		try {
			className = XMLElementHelper.getElementValueByTagName(config, "class");
		} catch (XMLElementNotFoundException e1) {
			throw new MicropsiException(-1, "Initialising overlay renderer '" + name + "': no 'class' element given. So who shall render?", e1);
		}
		try {
			zOrder = Integer.parseInt(XMLElementHelper.getElementValueByTagName(config, "z-index"));
		} catch (NumberFormatException e) {
			console.getLogger().warn("Initialising overlay renderer '" + name + "': z-index is no integer. Using default: 1.", e);
			zOrder = 1;
		} catch (XMLElementNotFoundException e) {
			// parameter optional
			zOrder = 1;
		}
		try {
			enabled = "true".equals((XMLElementHelper.getElementValueByTagName(config, "enabled")));
		} catch (NumberFormatException e) {
			console.getLogger().warn("Initialising overlay renderer '" + name + "': enabled is no boolean. Using default: true.", e);
			zOrder = 1;
		} catch (XMLElementNotFoundException e) {
			// parameter optional
			enabled = true;
		}
	}
	
	public OverlayRendererDescriptor(IOverlayRenderer renderer, String name, int zOrder, boolean enabled, ConsoleFacadeIF console) {
		this.console = console;
		this.renderer = renderer;
		
		this.name = name;
		
		if (name == null || name == "") {
			console.getLogger().warn("Initialising overlay renderer: renderer has no 'name' attribute, using 'unknown'.");
			this.name = "unknown";
		}
		if(renderer != null) {
			this.className = renderer.getClass().getName();
		}
		this.zOrder = zOrder;
		this.enabled = enabled;
	}

	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the zOrder.
	 */
	public int getZOrder() {
		return zOrder;
	}
	
	/**Creates a new instance of the renderer. If anytthing goes wrong, it will log the error
	 * and return null.
	 * 
	 * @return the renderer instance. May be null.
	 */
	public IOverlayRenderer getRenderObject() {
		if(renderer == null) {		
			try {
				Class renderClass = Class.forName(getClassName());
				renderer = (IOverlayRenderer) renderClass.getConstructor(new Class[0]).newInstance(new Object[0]);
			} catch (ClassNotFoundException e) {
				console.getLogger().error("Class '" + getClassName() + "' not found for overlay renderer '" + getName() + "'.", e);
			} catch (Exception e) {
				console.getLogger().error("Overlay renderer class '" + getClassName() + "': Exception creating instance.", e);
			}
		}
		return renderer;
	}

	/**
	 * @return Returns the enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

}
