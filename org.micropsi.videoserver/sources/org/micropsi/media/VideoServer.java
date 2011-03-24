package org.micropsi.media;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.media.Buffer;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.Manager;
import javax.media.Player;
import javax.media.PlugInManager;
import javax.media.RealizeCompleteEvent;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.micropsi.media.camera.Camera;
import org.micropsi.media.camera.CameraException;
import org.micropsi.media.codec.video.jpeg.JavaEncoder;
import org.micropsi.media.image.Image;
import org.micropsi.media.isight.ISight;
import org.micropsi.media.rtp.RTP;
import org.micropsi.media.rtp.RTPException;
import org.micropsi.media.rtp.RTPServer;

public class VideoServer {
		
	private String name;
	private IVideoSourceProvider provider;
	private Properties sourceProps = null;
	private Properties[] targetProps = null;
	private List<IVideoTarget> targets = new ArrayList<IVideoTarget>();
	private Player player;
	private FrameGrabbingControl frameGrabber;
	
	private Dimension visualSize = new Dimension(0,0);
	private Component visualComponent;
	private JPanel visualComponentWrapper;
	
	private boolean isRealized = false;
	
	/**
	 * Creates a new Video Server.
	 * @param name a name
	 * @param sourceProps image source properties (type, devicename, etc...)
	 * @param targetProps target properties (where to serve data?)
	 * @param lightWeight lightWeight renderer? (defaults to yes for use in micropsi environment)
	 * @throws MediaServerException if the server cannot be created
	 */
	public VideoServer(String name, Properties sourceProps, Properties[] targetProps, boolean lightWeight) throws MediaServerException {
		this.sourceProps = sourceProps;
		this.targetProps = targetProps;
		this.name = name;
		
		try {

			PlugInManager.addPlugIn(
				"org.micropsi.media.codec.video.jpeg.JavaEncoder",
				new Format[] {JavaEncoder.rgbFormat},
				new Format[] {JavaEncoder.jpegFormat},
				PlugInManager.CODEC
			);

			PlugInManager.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, lightWeight);
		Manager.setHint(Manager.PLUGIN_PLAYER, Boolean.TRUE);
		
		if(this.targetProps == null) {
			this.targetProps = new Properties[0];
		}
		
		start();
	}
	
	/**
	 * Creates a new Video Server with a lightweight renderer
	 * @param name a name
	 * @param sourceProps image source properties (type, devicename, etc...)
	 * @param targetProps target properties (where to serve data?)
	 * @throws MediaServerException if the server cannot be created
	 */
	public VideoServer(String name, Properties sourceProps, Properties[] targetProps) throws MediaServerException {
		this(name,sourceProps,targetProps,true);
	}
	
	private void start() throws MediaServerException {
			
		String type = sourceProps.getProperty("type");
		if(type == null) {
			type = null;
		}
		
		String size = sourceProps.getProperty("size");
		int width = Integer.parseInt(size.substring(0,size.indexOf("x")));
		int height = Integer.parseInt(size.substring(size.indexOf("x")+1));
		visualSize.width = width;
		visualSize.height = height;
		
		visualComponentWrapper = new JPanel();
		visualComponentWrapper.setLayout(new CardLayout());
		visualComponentWrapper.setSize(width,height);
		visualComponentWrapper.setBackground(new Color(0,0,255));
		
		if("camera".equals(type)) {
			try {
				provider = new Camera(sourceProps);
			} catch (CameraException e) {
				throw new MediaServerException("Could not create camera source.",e);				
			}
		} else if("isight".equals(type)) {
			try {
				provider = new ISight(sourceProps);
			} catch (Exception e) {
				throw new MediaServerException("Could not create iSight source.",e);				
			}
		} else if("image".equals(type)) {
			provider = new Image(sourceProps); 
		} else if("net-rtp".equals(type)) {
			try {
				provider = new RTP(sourceProps);
			} catch (RTPException e) {
				throw new MediaServerException("Could not create RTP source.",e);				
			} 
		} else {
			throw new MediaServerException("Unknown source type: "+type);
		}

		try {
			provider.getVideoSource().connect();
			provider.getVideoSource().start();
		} catch (IOException e) {
			throw new MediaServerException("IO Problem. Unable to connect or start.",e);
		}
		
		try {
			player = Manager.createPlayer(provider.getVideoSource());
			player.addControllerListener(new ControllerListener() {
				public void controllerUpdate(ControllerEvent evt) {
					if(evt instanceof RealizeCompleteEvent) {
						player.prefetch();
						player.start();
						visualComponent = player.getVisualComponent();
						if(visualComponent == null) {
							throw new RuntimeException("Visual component was null");
						} else {
							visualComponentWrapper.add(visualComponent,"sole");
							visualComponentWrapper.doLayout();
							
						}
						frameGrabber = (FrameGrabbingControl)player.getControl("javax.media.control.FrameGrabbingControl");
						if(frameGrabber == null) {
							throw new RuntimeException("Frame grabber was null");
						}
						isRealized = true;
					} 
				}				
			});
			player.prefetch();
			player.realize();
		} catch (Exception e) {
			throw new MediaServerException("Unable to start player",e);
		}
		
		for(int i=0;i<targetProps.length;i++) {
			Properties target = targetProps[i];
			type = target.getProperty("type");
			if("net-rtp".equals(type)) {
				try {
					IVideoTarget server = new RTPServer(target,provider.getVideoSource()); 
					server.start();
					targets.add(server);
				} catch (Exception e) {
					throw new MediaServerException("Unable to create target",e);
				}
			} else {
				throw new MediaServerException("Unknown target type: "+type);
			}
		}
					
	}
	
	/**
	 * Shuts this video server down.
	 * @throws MediaServerException if there is a problem on shutdown
	 */
	public void shutdown() throws MediaServerException {
		
		try {
		
			for(int i=0;i<targets.size();i++) {
				targets.get(i).stop();
			}
				
			player.stop();
			player.close();
			player.deallocate();
			
			provider.getVideoSource().stop();
			provider.getVideoSource().disconnect();
						
			visualComponent.setEnabled(false);
			
		} catch (Throwable e) {
			throw new MediaServerException("Unable to stop or disconnect",e);
		}
	}
		
	/**
	 * Returns an AWT lightweight component displaying the image of this
	 * video server.
	 * @return the component displaying the video stream
	 */
	public Component getVisualComponent() {
		return visualComponentWrapper;
	}
	
	/**
	 * Returns the natural size of the video stream served. (= the resoultion)
	 * @return the resplution of the video stream
	 */
	public Dimension getVisualSize() {
		return visualSize;
	}
	
	/**
	 * Grabs a raw frame from the video stream. The frame will be in the format
	 * defined by the video stream.
	 * @return a Buffer with raw data from the video stream.
	 */
	public Buffer grabFrame() {
		int counter = 0;
		while(!isRealized) {
			try {
				Thread.sleep(100);
				counter++;
				
				if(counter == 100) {
					System.err.println("Waiting for video server '"+name+"'s realization...");
				}
				if(counter == 10000) {
					throw new RuntimeException("Video server '"+name+"' did not realize");
				}
			} catch (InterruptedException e) {
			}
		}
		return frameGrabber.grabFrame();
	}
	
	/**
	 * Grabs a frame from the video stream as a Java BufferedImage with the given
	 * width, height an BufferedImage.TYPE_xxx format.
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param format the type of the image. For possible values see the BufferedImage.TYPE_ constants
	 * @return a buffered image from the video stream
	 */
	public BufferedImage grabImageAWT(int width, int height, int format) {
		Buffer buffer = grabFrame();
		BufferToImage converter = new BufferToImage((VideoFormat)buffer.getFormat());
		java.awt.Image image = converter.createImage(buffer);
		BufferedImage bufImg = new BufferedImage(width,height,format);
		Graphics2D g = bufImg.createGraphics(); 
		g.drawImage(image,0,0,width,height,null);
		g.dispose();
		return bufImg;
	}
	
	/**
	 * Grabs a frame from the video stream and returns it in its natural resolution/size
	 * in 24 bit RGB (Mask 0xFF, 0x00FF, 0x0000FF).
	 * @return a buffered image from the video stream
	 */
	public BufferedImage grabImageAWTRGB() {
		return grabImageAWT(getVisualSize().width,getVisualSize().height, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * Returns the name of this video server.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public JFrame showVideo() {
		JFrame frame = new JFrame("Video server "+name);
		frame.setSize(getVisualSize().width, getVisualSize().height);
		frame.add(getVisualComponent());
		frame.setEnabled(true);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		return frame;
	}

	public static void main(String[] args) throws Exception {

		File propertyFile = null;
		
		if(args.length == 0) {
			propertyFile = new File("source.properties");
			if(!propertyFile.exists() || !propertyFile.canRead()) {
				System.err.println("Cannot read source properties from file: "+propertyFile.getAbsolutePath());
				System.exit(-1);
			}
		} else {
			propertyFile = new File(args[0]);
			if(!propertyFile.exists() || !propertyFile.canRead()) {
				System.err.println("Cannot read source properties from file: "+propertyFile.getAbsolutePath());
				System.exit(-1);
			}
		}
		
		File targetFile = null;
		if(args.length == 0) {
			targetFile = new File("target.properties");
			if(!targetFile.exists() || !targetFile.canRead()) {
				targetFile = null;
			}
		} else {
			targetFile = new File(args[0]);
			if(!targetFile.exists() || !targetFile.canRead()) {
				targetFile = null;
			}
		}
		
		Properties[] targets = null;
		if(targetFile != null) {
			Properties targetProps = new Properties();
			targetProps.load(new FileInputStream(targetFile));
			targets = new Properties[] {targetProps};
		}
		
		Properties props = new Properties();
		props.load(new FileInputStream(propertyFile));
		
		final VideoServer m = new VideoServer("standalone",props,targets);
		JFrame frame = m.showVideo();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				try {
					m.shutdown();
				} catch (MediaServerException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		
		while(true) {
			Thread.sleep(200);
			if(System.in.read() != 0) {
				System.out.println("Shutting down...");
				m.shutdown();
				System.out.println("Shutdown complete");
				System.exit(0);
			}
		}
		
		
	}
}
