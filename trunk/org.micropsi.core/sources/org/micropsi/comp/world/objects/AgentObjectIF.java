/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 18.05.2003
 *
 */
package org.micropsi.comp.world.objects;

import java.util.Collection;

import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MPerceptionResp;

/**
 * @author matthias
 *
 */
public interface AgentObjectIF {
	public abstract void handleAction(MAction action);
	public abstract MPerceptionResp getPerception();
	public abstract String getAgentName();
	public abstract Collection returnActionAnswers();
}