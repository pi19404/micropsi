package org.micropsi.eclipse.whiskerbotcontrol;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.worldconsole.EditSession;
import org.micropsi.comp.console.worldconsole.IOverlayChangeNotifier;
import org.micropsi.comp.console.worldconsole.IOverlayRenderer;
import org.micropsi.comp.console.worldconsole.IRenderInfo;
import org.micropsi.comp.console.worldconsole.LocalWorld;
import org.micropsi.comp.console.worldconsole.SWTAwareAnswerQueue;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.media.VideoServerRegistry;

public class MemoryOverlayRenderer implements IOverlayRenderer, AnswerHandlerIF {
	
	
	
	private Color color =new Color(null,254,254,254);
	private Logger logger;
	private IOverlayChangeNotifier notifier=null;
	private IRenderInfo info;
	private Iterator<MTreeNode> children;
	private Iterator<MTreeNode> transchildren;
	private Vector<String> memorizex=new Vector<String>(50,50);
	private Vector<String> memorizey=new Vector<String>(50,50);
	private int height=(int)VideoServerRegistry.getInstance().getServer("tracker").getVisualSize().getHeight();
	private int width = (int)VideoServerRegistry.getInstance().getServer("tracker").getVisualSize().getWidth();
	private double mistake=0;
	
	public void init(LocalWorld arg0, EditSession arg1,
			IOverlayChangeNotifier arg2, IRenderInfo arg3) {
		// TODO Auto-generated method stub

	}

	public void init(IOverlayChangeNotifier notifier, IRenderInfo renderInfo) {
		
		logger = RobotConsole.getInstance().getBserv().getLogger();
		
		logger.debug("[MemoryOverlayRenderer] init");
		try {
		this.notifier=notifier;
		
		IPreferenceStore preferences = PlatformUI.getPreferenceStore();
		String agentName = preferences.getString(RobotPreferences.CFG_KEY_AGENTNAME);
		
		RobotConsole.getInstance().getConsole().subscribe(
			100,
			agentName,
			"getmemorypoints",
			"",
			new SWTAwareAnswerQueue(this),
			null
		);
		
		logger.debug("[MemoryOverlayRenderer] redraw picture");
		} catch (Exception e)  {
			logger.error("fehler",e);
		}
	}

	public void paintOverlay(GC paint, int xoff, int yoff, IRenderInfo render) {
		
		double scalingx=render.getScaleX();
		double scalingy=render.getScaleY();
		double mistakex;
		double mistakey;
		
		
		paint.setBackground(color);
		paint.setForeground(color);
		mistakex=mistake/100*height;
		mistakey=mistake/100*width;
		
		for(int i=0;i<memorizex.size();i++){
			paint.drawOval((int)(scalingx*((Double.parseDouble(memorizex.get(i))/100)*width-mistakex/2)),(int)(scalingy*((height-(Double.parseDouble(memorizey.get(i))/100)*height))-mistakey/2),(int)(scalingx*mistakex),(int)(scalingy*mistakey));
		}
		this.info=render;
		
		
		//logger.debug("[MemoryOverlayRenderer] Redraw picture with new Coordinates");
		
	}

	public void setScale(double arg0, double arg1) {
		

	}

	public void setEnabled(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void handleAnswer(AnswerIF answer) {
		
		int index=0;
		boolean onetime=true;
		MTreeNode step;
		
		memorizex.clear();
		memorizey.clear();
		
		MTreeNode data = (MTreeNode)answer.getContent();
		children=data.children();
		
		if(children!=null){
		while(children.hasNext()){
			step=children.next();
			if(onetime){
				mistake=step.doubleValue();
				onetime=false;
			}
			transchildren = step.children();
			memorizex.add(index,transchildren.next().getValue());
			memorizey.add(index,transchildren.next().getValue());
			index +=1;	
		}
		}
		
		
		
		
		notifier.redraw();		
	}

}
