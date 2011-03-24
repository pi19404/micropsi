package org.micropsi.comp.world.messages;

import java.util.HashSet;
import java.util.Set;

import org.micropsi.comp.world.PostOffice;
import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 */
public class WorldMessage extends AbstractWorldMessage {
	
	private Set<AbstractObjectPart> originators = null;
	
	public WorldMessage(String messageClass, String messageContent, AbstractObjectPart sender)
	{
		super(messageClass, messageContent, sender);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.messages.AbstractWorldMessage#delegateToParent(org.micropsi.comp.world.objects.AbstractObject, org.micropsi.comp.world.objects.AbstractObject)
	 */
	public void delegateToParent(AbstractObjectPart obj, AbstractObjectPart parent) {
		originators = new HashSet<AbstractObjectPart>(1);
		originators.add(obj);
		PostOffice.sendMessage(this, parent);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.messages.AbstractWorldMessage#getCurrentRecipientOriginators()
	 */
	public Set getCurrentRecipientOriginators() {
		return originators;
	}

}