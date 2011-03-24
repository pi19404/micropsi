package org.micropsi.eclipse.media;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.comp.console.worldconsole.IOverlayChangeNotifier;
import org.micropsi.comp.console.worldconsole.IOverlayRenderer;
import org.micropsi.comp.console.worldconsole.IRenderInfo;


public class OverlayCanvas extends JComponent implements IOverlayChangeNotifier {

	private Shell shell;
	private HashMap<String, IOverlayRenderer> renderers = new HashMap<String,IOverlayRenderer>();
	private HashMap<String, BufferedImage> images = new HashMap<String,BufferedImage>();
	private IRenderInfo renderInfo;
	
	public OverlayCanvas(Shell shell, IRenderInfo renderInfo) {
		this.setOpaque(false);
		this.shell = shell;
		this.renderInfo = renderInfo;
	}
	
	public void addOverlayRenderer(String name, IOverlayRenderer renderer) {
		BufferedImage newImage = renderOverlay(renderer,name);
		images.put(name, newImage);
		renderers.put(name, renderer);
		repaint();
	}
	
	public void removeOverlayRenderer(String name) {
		renderers.remove(name);
		images.remove(name);
	}
	
	public void updateImages() {
		Iterator<String> i = renderers.keySet().iterator();
		while(i.hasNext()) {
			String name = i.next();
			IOverlayRenderer renderer = renderers.get(name);
			BufferedImage newImage = renderOverlay(renderer,name);
			images.put(name, newImage);
			renderers.put(name, renderer);
		}
	}
	
	private BufferedImage renderOverlay(IOverlayRenderer renderer, String name) {
		ImageData data = new ImageData(getWidth(),getHeight(),32,new PaletteData(0x00FF, 0x0000FF, 0x000000FF));

		int whitePixel = data.palette.getPixel(new RGB(0,0,0));
		data.transparentPixel = whitePixel;
		Image img = new Image(shell.getDisplay(),data);
		
		GC gc = new GC(img);
		Color c = new Color(shell.getDisplay(),0,0,0);	
		gc.setBackground(c);
		gc.fillRectangle(0, 0, img.getBounds().width, img.getBounds().height);
		
		renderer.paintOverlay(gc, 0, 0, renderInfo);
		gc.dispose();
				
		BufferedImage overlay = convertToAWT(img.getImageData());
				
		img.dispose();
		return overlay;
	}

	public void paint(Graphics g) {
		ArrayList<BufferedImage> l = new ArrayList<BufferedImage>();
		l.addAll(images.values());
		for(Iterator<BufferedImage>i=l.iterator();i.hasNext();) {
			BufferedImage img = i.next();
			((Graphics2D)g).drawImage(img, null, 0, 0);
		}
	}
	
	public BufferedImage convertToAWT(ImageData data) {
		PaletteData palette = data.palette;
		BufferedImage bufferedImage = new BufferedImage(data.width,data.height,BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = bufferedImage.getRaster();
			
		int[] pixelArray = new int[4];
		for (int y = 0; y < data.height; y++) {
			for (int x = 0; x < data.width; x++) {
				
				int pixel = data.getPixel(x, y);
				RGB rgb = palette.getRGB(pixel);
				int transparency = 255;
				if(rgb.red == 0 && rgb.blue == 0 && rgb.green == 0) {
					transparency = 0;
				}
				
				pixelArray[0] = rgb.red;
				pixelArray[1] = rgb.green;
				pixelArray[2] = rgb.blue;				
				pixelArray[3] = transparency;
				raster.setPixels(x, y, 1, 1, pixelArray);
			}
		}
				
		return bufferedImage;
	}

	public void redraw() {
		updateImages();
	}

	public void redraw(int x, int y, int width, int height) {
		redraw();
	}
}
