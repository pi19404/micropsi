package org.micropsi.comp.console.worldconsole;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.coordinates.Area2D;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.console.worldconsole.model.AbstractWorldObject;
import org.micropsi.comp.console.worldconsole.model.ImageLibrary;
import org.micropsi.comp.console.worldconsole.model.LocalWorldModel;
import org.micropsi.comp.console.worldconsole.model.WorldObject;
import org.micropsi.comp.console.worldconsole.model.WorldObjectProperties;
import org.micropsi.comp.console.worldconsole.model.WorldObjectProperty;
import org.micropsi.comp.messages.MTreeNode;

/**Controller used to read and modify all "local" world data known by the worldconsole. The local
 * world data will be automatically updated, and you can subsribe to all kinds of changes. 
 * This class will also encapsulate all requests to the "remote" world (unfinished).
 * 
 * @author Matthias, David
 */
public class LocalWorld extends AbstractController implements AnswerHandlerIF {
	
	private static int remoteUpdateQuestionId = 1;
	/**
	 * ignore some outdated worldglobals version for some time if i have just
	 * requested a worldglobals update.
	 */
	private int numberIgnoreWorldGlobalsChanged = 0;
	
	private AnswerQueueIF answerQueue;
	private ConsoleFacadeIF console = null;
	private AnswerHandlerIF questionErrorHandler = null;
	
	protected Map<Integer,IRequestResultHandler> requestResultHandler = new HashMap<Integer,IRequestResultHandler>(10);
	
	/**Creates a new Controller using a new world model to store data the worldconsole must know about
	 * the "remote" world. Takes as parameter a ConsoleFacadeIF used to communicate with the remote world.
	 * The local world model will be automatically updated, and you can subsribe to all kinds of changes.
	 * 
	 * @param configFile the config file to be used
	 * @param console the ConsoleFacadeIF used to communicate with the "remote" world.
	 * @param answerQueueClassToUse the implementation of 
	 * @throws MicropsiException
	 */
	public LocalWorld(String configFile, ConsoleFacadeIF console, Class answerQueueClassToUse) throws MicropsiException {
		this.console = console;
		try {
			answerQueue = (AnswerQueueIF)answerQueueClassToUse.getConstructor(new Class[] {AnswerHandlerIF.class}).newInstance(new Object[] {this});
		} catch (Exception e) {
			throw new MicropsiException(10,answerQueueClassToUse.getName(),e);
		}
		
		setData(new LocalWorldModel(configFile, console));

		requestObjectList();
		subscribeObjectChanges();
		requestWorldGlobals();
		numberIgnoreWorldGlobalsChanged = 10;
		
	}
		
	/**
	 * return object with given id or null if id is not found
	 */
	public WorldObject getObjectByID(long objID) {
		return getWorldModel().getObjectByID(objID);
	}

	/**
	 * notify all change listeners of change
	 */
	public void notifyObjectChangeListeners(WorldObject currentObject) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			WeakReference ref = (WeakReference) it.next();
			Object referenced = ref.get();
			if (referenced != null) {
				((ILocalWorldListener) referenced).onObjectChanged(this, currentObject);
			} else {
				it.remove();
			}
		}
	}

	/**
	 * notify all change listeners of multiple changes
	 */
	public void notifyObjectChangeListeners(List<AbstractWorldObject> changedObjects) {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			WeakReference ref = (WeakReference) it.next();
			Object referenced = ref.get();
			if (referenced != null) {
				((ILocalWorldListener) referenced).onMultipleObjectsChanged(this, changedObjects);
			} else {
				it.remove();
			}
		}
	}

	/**
	 * notify all change listeners of refresh
	 */
	public void notifyRefreshListeners() {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			WeakReference ref = (WeakReference) it.next();
			Object referenced = ref.get();
			if (referenced != null) {
				((ILocalWorldListener) referenced).onObjectListRefreshed(this);
			} else {
				it.remove();
			}
		}
	}

	/**
	 * notify all change listeners of change
	 */
	public void notifyGlobalChangeListeners() {
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			WeakReference ref = (WeakReference) it.next();
			Object referenced = ref.get();
			if (referenced != null) {
				((ILocalWorldListener) referenced).onGlobalsChanged();
			} else {
				it.remove();
			}
		}
	}

	/**
	 * 
	 */
	public void requestObjectList() {
		getConsole().getInformation(0, "world", "getobjectlist", "", answerQueue);
	}

	public void subscribeObjectChanges() {
		getConsole().subscribe(1, "world", "getobjectchangelist", "", answerQueue);
	}

	public void requestWorldGlobals() {
		getConsole().getInformation(0, "world", "getglobaldata", "", answerQueue);
	}

	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.AnswerHandlerIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
	 */
	public void handleAnswer(AnswerIF answer) {
		if (answer.getStep() > getWorldModel().getSimStep()) {
			getWorldModel().setSimStep(answer.getStep());
		}
		if (answer.getAnsweredQuestion().getQuestionName().equals(
				"getobjectlist")
				|| answer.getAnsweredQuestion().getQuestionName().equals(
						"getobjectchangelist")) {
			if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
				MTreeNode node = (MTreeNode) answer.getContent(); 
				if (node != null && node.getName() != null && node.getName().equals("success")) {
					processObjectListUpdate(node);
				}
			}
		} else if (answer.getAnsweredQuestion().getQuestionName().equals("changeobjectproperties") && answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_OK && answer.getAnsweredQuestion().getParameters().length >= 2) {
			int requestId = Integer.parseInt(answer.getAnsweredQuestion().getParameters()[1]);
			Integer requestIdKey = new Integer(requestId);
			requestResultHandler.get(requestIdKey).handleRequestResult(requestId, answer);
			requestResultHandler.remove(requestIdKey);
		} else if (answer.getAnsweredQuestion().getQuestionName().equals("getglobaldata") && answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
			processUpdateWorldGlobals(answer);
		} else {
			if (getQuestionErrorHandler() != null) {
				getQuestionErrorHandler().handleAnswer(answer);
			}
		}
	}

	/**
	 * @param object
	 */
	public int requestMoveObject(WorldObject object, Position position, IRequestResultHandler resultHandler) {
		WorldObjectProperties newProperties = new WorldObjectProperties(object.getId());
		newProperties.addProperty(new WorldObjectProperty("position", position.toString()));
		String questionParameters = Long.toString(object.getId()) + " " + remoteUpdateQuestionId;
		int res = remoteUpdateQuestionId;
		remoteUpdateQuestionId = (remoteUpdateQuestionId + 1) % Integer.MAX_VALUE;
		getConsole().getInformation(
			0,
			"world",
			"changeobjectproperties",
			questionParameters,
			answerQueue,
			newProperties.toMTreeNode());
		requestResultHandler.put(new Integer(res), resultHandler);
		return res;
	}
	
	/**
	 * @return
	 */
	public Collection getObjects() {
		return getWorldModel().getObjects();
	}
	
	public void processObjectListUpdate(MTreeNode update) {
		List<AbstractWorldObject> changeList = new ArrayList<AbstractWorldObject>();
		MTreeNode objectList = update.searchChild("object change list");
		if (objectList != null) {
			getWorldModel().updateChangedObjects(objectList, changeList);
		} else {
			objectList = update.searchChild("object list");
			if (objectList != null) {
				getWorldModel().updateAllObjects(objectList, changeList);
			}
		}
		if (changeList.size() == 1) {
			WorldObject obj = (WorldObject) changeList.get(0);
			notifyObjectChangeListeners(obj);
		} else if (changeList.size() >= 1){
			notifyObjectChangeListeners(changeList);
		}
		int version = update.searchChild("globals version").intValue();
		if (numberIgnoreWorldGlobalsChanged <= 0) {
			if (version != getWorldModel().getVersionGlobalData()) {
				requestWorldGlobals();
				numberIgnoreWorldGlobalsChanged = 2;
			}
		} else {
			numberIgnoreWorldGlobalsChanged--;
		}
	}
	
	public void processUpdateWorldGlobals(AnswerIF answer) {
		MTreeNode node = (MTreeNode) answer.getContent();
		if (node.getName().equals("success")) {
			getWorldModel().setWorldFileName(node.searchChild("filename").getValue());
			MTreeNode groundMapNode = node.searchChild("groundmap");
			getWorldModel().setGroundmapArea(new Area2D(groundMapNode.searchChild("area")));
			getWorldModel().setGroundmapImageFile(groundMapNode.searchChild("image filename").getValue());
			getWorldModel().setVisibleArea(new Area2D(node.searchChild("visible area")));
			getWorldModel().setVersionGlobalData(node.searchChild("version").intValue());
			notifyGlobalChangeListeners();
		}
	}

	public LocalWorldModel getWorldModel() {
		return ((LocalWorldModel) data);
	}
	
	public Image getGroundImage() {
		return getWorldModel().getGroundImage();
	}
	
	public String getGroundImageFileName() {
		return getWorldModel().getGroundmapImageFile();
	}
	
	public String getWorldFileName() {
		return getWorldModel().getWorldFileName();
	}

	public ImageLibrary getImageLibrary() {
		return getWorldModel().getImageLibrary();
	}
	
	protected ConsoleFacadeIF getConsole() {
		return console;
	}
	
	/**
	 * @return Returns the questionErrorHandler.
	 */
	public AnswerHandlerIF getQuestionErrorHandler() {
		return questionErrorHandler;
	}
	/**
	 * @param questionErrorHandler The questionErrorHandler to set.
	 */
	public void setQuestionErrorHandler(AnswerHandlerIF questionAnswerHandler) {
		this.questionErrorHandler = questionAnswerHandler;
	}
}

