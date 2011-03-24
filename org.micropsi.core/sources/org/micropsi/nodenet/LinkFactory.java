/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/LinkFactory.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

public class LinkFactory {
	
	private static LinkFactory instance = new LinkFactory();
	
	/**
	 * Get an instance of the LinkFactory
	 * @return LinkFactory
	 */
	public static LinkFactory getInstance() {
		return instance;
	}
	
	private LinkFactory() {
	}
	
	/**
	 * Creates an instance of a Link. This is low-level and does not ensure that
	 * the link is valid. (See the documentation of the Link constructor).
	 * @param type the technical type of the link (simple association, spacio-
	 * temporal etc)
	 * @param manager the NetEnitityManager
	 * @param from the Gate where the link originates
	 * @param to the linked entity's ID
	 * @param slot the slot type where the link shall end
	 * @param weigth the inital weight of the new link
	 * @param confidence the initial confidence of the new link
	 * @return Link the newly created link
	 */
	public Link createLink(int type, NetEntityManager manager, Gate from, String to, int slot, double weigth, double confidence) {
		switch (type) {
			case LinkTypesIF.LINKTYPE_SIMPLEASSOCIATION:
				return new Link(from, to, slot, manager, weigth,confidence);
			case LinkTypesIF.LINKTYPE_SPACIOTEMPORAL:
				return new LinkST(from, to, slot, manager, weigth,confidence);
			default: throw new RuntimeException("FIX THIS: bad link type requested: "+type);
		}
	} 

}
