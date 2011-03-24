/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.psitopia/WEB-INF/src/org/micropsi/comp/console/web/WebConsole.java,v 1.24 2005/07/12 13:40:30 vuine Exp $ 
 */
package org.micropsi.comp.console.web;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.ConsoleComponent;
import org.micropsi.comp.console.ConsoleFunctionalityIF;
import org.micropsi.comp.console.DefaultAnswerQueue;
import org.micropsi.comp.console.worldconsole.LocalWorld;
import org.micropsi.comp.console.worldconsole.WorldRenderer;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.comp.messages.MTreeNode;

//import com.jb2works.reference.HttpScanner;


public class WebConsole implements ConsoleFunctionalityIF, AnswerHandlerIF {

	private static final int MAX_WAIT = 1000;
	
	private static WebConsole instance;
	
	private ConsoleComponent console;
	private LocalWorld localWorld;
	private HashMap<String,String> agents;
	
	private boolean initComplete = false;
	
	public static WebConsole getInstance() {
		return instance;
	}
	
	public WebConsole() {

//		  if( !HttpScanner.isStarted()) {
//		      HttpScanner.start( this );
//		   }
		
	}
	
	/*(non-Javadoc)
	 * @see org.micropsi.comp.console.ConsoleFunctionalityIF#initialize(org.micropsi.comp.console.ConsoleComponent)
	 */
	public void initialize(ConsoleComponent component, ConfigurationReaderIF config, String configPrefix) throws MicropsiException {
		
		final String worldconsoleconfig = config.getConfigValue(configPrefix+".worldconsoleconfig");		
		this.console = component;
		
		Runnable worldLoaderRunnable = new Runnable() {
			public void run() {
				try {
					localWorld = new LocalWorld(worldconsoleconfig, console, DefaultAnswerQueue.class);
					initComplete = true;
				} catch (MicropsiException e) {
					console.getLogger().error("WebConsole world load error: ",e);
				}				
			}	
		};
		
		//new Thread(worldLoaderRunnable,"localWorldLoader").start();
		worldLoaderRunnable.run();
		
		agents = new HashMap<String,String>();
		console.subscribe(
				QuestionIF.AM_ANSWER_EVERY_10_STEPS, 
				"world",
				"getagentlist", 
				"", 
				new DefaultAnswerQueue(this));
		
		instance = this;
	}

	public void writeImageToStream(OutputStream outputStream, Position pos, int sizeX, int sizeY, double scale) {

		if(initComplete) {
		
			WorldRenderer wr = new WorldRenderer(localWorld,null);
	
			wr.setScale(scale,scale);
			wr.setWorldArea(localWorld.getWorldModel().getVisibleArea());
			wr.setGroundmapArea(localWorld.getWorldModel().getGroundmapArea());				
			
			Image img = new Image(Display.getDefault(),sizeX,sizeY);		
			GC gc = new GC(img);

			wr.setOverlayEnabled("Coordinate grid", false);
			wr.paintWorld(gc,pos);
	
			ImageLoader loader = new ImageLoader();
			ImageData imageData = img.getImageData();
	
			loader.data = new ImageData[] {imageData};
			loader.save(outputStream, SWT.IMAGE_JPEG);
			
			gc.dispose();
			wr.dispose();
		
		} else {
			writeWaitImage(outputStream, sizeX, sizeY);
		}
		
	}

	public void writeImageToStream(OutputStream outputStream, long objectToTrack, int sizeX, int sizeY, double scale) {

		if(initComplete) {
		
			WorldRenderer wr = new WorldRenderer(localWorld,null);
	
			wr.setScale(scale,scale);
			wr.setWorldArea(localWorld.getWorldModel().getVisibleArea());
			wr.setGroundmapArea(localWorld.getWorldModel().getGroundmapArea());				
			
			Image img = new Image(Display.getDefault(),sizeX,sizeY);		
			GC gc = new GC(img);

			wr.setOverlayEnabled("Coordinate grid", false);
			wr.paintWorld(gc,objectToTrack);
	
			ImageLoader loader = new ImageLoader();
			ImageData imageData = img.getImageData();
	
			loader.data = new ImageData[] {imageData};
			loader.save(outputStream, SWT.IMAGE_JPEG);
			
			gc.dispose();
			wr.dispose();
		
		} else {
			writeWaitImage(outputStream, sizeX, sizeY);
		}
		
	}
	
	private void writeWaitImage(OutputStream outputStream, int sizeX, int sizeY) {
		
		Image img = new Image(Display.getDefault(),sizeX,sizeY);
		GC gc = new GC(img);
		gc.drawString("Starting up....", 20, 20);
		ImageLoader loader = new ImageLoader();
		ImageData imageData = img.getImageData();

		loader.data = new ImageData[] {imageData};
		loader.save(outputStream, SWT.IMAGE_JPEG);
		
		gc.dispose();
		img.dispose();
		
	}
	
	public AnswerIF askQuestion(String question, String destination, String parameters, ConsoleServlet servlet) {
		MQuestion q = new MQuestion(question,QuestionIF.AM_ANSWER_ONCE);
		q.setDestination(destination);	
		q.setParameters(parameters.split(" "));
		q.setOrigin(console.getComponentID());
		
		try {
			return console.askBlockingQuestion(q, MAX_WAIT);
		} catch (MicropsiException e) {
			return	new MAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,q,"Timeout, component "+destination+" does not answer.",0);
		}
	}	

	public void shutdown() {
		try {
			console.getComponentRunner().shutdown();
		} catch (ComponentRunnerException e) {
			console.getLogger().error("Shutdown exeception.",e);
		}
	}

	/*(non-Javadoc)
	 * @see org.micropsi.comp.console.AnswerHandlerIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
	 */
	public void handleAnswer(AnswerIF answer) {
		if("getagentlist".equals(answer.getAnsweredQuestion().getQuestionName())) {
			synchronized(agents) {
				agents.clear();
				MTreeNode node = (MTreeNode) answer.getContent();
				
				Iterator<MTreeNode> children = node.children();
				if(children == null) return;
				while(children.hasNext()) {
					MTreeNode agentnode = children.next();
					String name = agentnode.getName();
					String id = agentnode.getValue();					
					agents.put(name, id);
				}
			}
		}
	}
	
	public Map getAgentMap() {
		synchronized(agents) {
			return new HashMap<String,String>(agents);
		}
	}


}
