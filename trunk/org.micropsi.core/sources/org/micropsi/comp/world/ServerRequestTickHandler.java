/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/ServerRequestTickHandler.java,v 1.11 2006/01/22 10:57:03 fuessel Exp $
 */
package org.micropsi.comp.world;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MUpdateWorld;
import org.micropsi.comp.messages.MWorldResponse;
import org.micropsi.comp.world.objects.AbstractAgentObject;
import org.micropsi.comp.world.objects.AgentObjectIF;

public class ServerRequestTickHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "updateworld";
	private WorldComponent world;

	public ServerRequestTickHandler(WorldComponent world) {
		this.world = world;
	}

	public ComChannelResponse handleRequest(ComChannelRequest request) {
		MUpdateWorld update = (MUpdateWorld) request.getRequestData();
		ComChannelResponse resp =
			new ComChannelResponse(HANDLEREQ, ComChannelResponse.RESPONSE_OK);

		synchronized(world.getWorld()) {
			world.tick(update.getTime());
			
			List<QuestionIF> questions = update.getQuestions();
			
			//add new agents
			Iterator it = update.getNewAgents().iterator();
			while (it.hasNext()) {
				String s = (String) it.next();
				int splitPos = s.lastIndexOf(',');
				if (splitPos <= 0) {
					world.getLogger().error("Creating Agent: '" + s + "' must be 'agentName,javaClass'.");
				} else {
					String agentName = s.substring(0, splitPos);
					String agentJavaClass = s.substring(splitPos + 1);
					AgentObjectIF agent = findOrCreateAgentObject(agentName, agentJavaClass);
					if (agent != null) {
						world.getAgents().put(agentName, agent);
					}
				}
			}
			
			it = update.getDeletedAgents().iterator();
			while (it.hasNext()) {
				String agentID = (String) it.next();
				AbstractAgentObject agent = world.getAgent(agentID);
				world.getWorld().removeObject(agent);
			}
			
			// send actions to agents
			it = update.getActions().iterator();
			while (it.hasNext()) {
				MAction action = (MAction) it.next();
				String agentID = action.getAgentName();
				AgentObjectIF agent = world.getAgent(agentID);
				if (agent != null) {
					agent.handleAction(action);
				} else {
					world.getLogger().warn("World: got Action for nonexisting agent object '" + agentID + "'.");
				}
			}
			
			// collect agent answers
			ArrayList<MActionResponse> responses = new ArrayList<MActionResponse>();
			it = world.getAgents().values().iterator();
			while (it.hasNext()) {
				AgentObjectIF agent = (AgentObjectIF) it.next();
				Iterator it2 = agent.returnActionAnswers().iterator();
				while (it2.hasNext()) {
					MActionResponse actionResponse = (MActionResponse) it2.next();
					// @todo3 why here? Better in agent object, I think - matthias
					actionResponse.setAgentName(agent.getAgentName());
					responses.add(actionResponse);
				}
			}
			
			MWorldResponse worldResponse = new MWorldResponse(responses);
			resp.setResponseData(worldResponse);
			
			List<AnswerIF> answers = world.getConsoleService().answerStoredQuestions(null, world.getSimStep());
			answers.addAll(world.getConsoleService().answerQuestions(questions,world.getSimStep()));
			
			for (int i = 0; i < answers.size(); i++) {
				worldResponse.addAnswer((MAnswer) answers.get(i));
			}
		}
		
		return resp;
	}

	/**
	 * @param agentName
	 * @param agentJavaClass
	 * @return
	 */
	private AgentObjectIF findOrCreateAgentObject(String agentName, String agentJavaClass) {
		if (agentJavaClass.indexOf('.') < 0) {
			agentJavaClass = "org.micropsi.comp.world.objects." + agentJavaClass;
		}
		Iterator it = world.getWorld().getObjects().iterator();
		while (it.hasNext()) {
			Object object = it.next();
			if (object instanceof AgentObjectIF && ((AgentObjectIF) object).getAgentName().equals(agentName)) {
				Class requiredClass;
				try {
					requiredClass = Class.forName(agentJavaClass);
				} catch (ClassNotFoundException e) {
					world.getLogger().error("Creating agent object: class not found: " + agentJavaClass, e);
					return null;
				}
				if (requiredClass.isInstance(object)) {
					return (AgentObjectIF) object;
				} else {
					world.getLogger().error(
						"Creating agent object: agent object '"
							+ agentName
							+ "' found, but is no instance of class "
							+ agentJavaClass + ".");
					return null;
				}
			}
		}
					
		Class[] parameters = new Class[2];
		try {
			parameters[0] = Class.forName("java.lang.String");
			parameters[1] = Class.forName("org.micropsi.common.coordinates.Position");
		} catch (ClassNotFoundException e) {
			world.getLogger().error("Creating agent object: some required class not found.", e);
			return null;
		}

		Object[] parameterValues = new Object[2];
		parameterValues[0] = agentName;
		parameterValues[1] = world.getWorld().getSpawnLocation();

		AbstractAgentObject agent = null;
		try {
			Constructor myConstr = Class.forName(agentJavaClass).getConstructor(parameters);
			agent = (AbstractAgentObject) myConstr.newInstance(parameterValues);
		} catch (ClassNotFoundException e) {
			world.getLogger().error("Creating agent object: class not found: " + agentJavaClass, e);
		} catch (NoSuchMethodException e) {
			world.getLogger().error("Creating agent object: class " + agentJavaClass + "has no matching constructor.", e);
		} catch (InstantiationException e) {
			world.getLogger().error("Creating agent object: Error creating class " + agentJavaClass + ".", e);
		} catch (IllegalAccessException e) {
			world.getLogger().error("Creating agent object: Error creating class " + agentJavaClass + ".", e);
		} catch (java.lang.reflect.InvocationTargetException e) {
			Exception f = (Exception) e.getCause();
			world.getLogger().error("Creating agent object: Exception in constructor for class " + agentJavaClass + ".", f == null ? e : f);
		}

		world.getWorld().addObject(agent);
		return agent;
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
