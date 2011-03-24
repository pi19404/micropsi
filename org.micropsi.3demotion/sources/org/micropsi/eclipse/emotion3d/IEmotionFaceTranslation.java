package org.micropsi.eclipse.emotion3d;

import java.util.List;

import org.micropsi.comp.messages.MTreeNode;

/**
 * Translator interface.
 * Implementations need to specify what face parameters shall be used.
 * Every time the face updates, calculateFaceParameters is called with current data from the agent.
 * @author rv
 */
public interface IEmotionFaceTranslation {

	/**
	 * Returns the name of this face translation
	 * @return the name of this translation
	 */
	public String getName();
	
	/**
	 * Specifies what the names of the face parameters are.
	 * The mapping to the parameter values returned by calculateFaceParameters() is done by index,
	 * so the first parameter returned by calculateFaceParameters() will have the name of the
	 * first entry in the list returned by this method.
	 * @return the list of the face parameter names, never null
	 */
	public List<String> getFaceParameterNames();
	
	/**
	 * Calculates the face parameters from agent actor data. 
	 * This method should be robust agains the absence of agent data -- missing values etc.
	 * @param agentActorData the emotion data from the agent
	 * @return an array of doubles in [0/1] of the same length as the list returned by getFaceParameterNames()
	 */
	public double[] calculateFaceParameters(MTreeNode agentActorData);
	
}
