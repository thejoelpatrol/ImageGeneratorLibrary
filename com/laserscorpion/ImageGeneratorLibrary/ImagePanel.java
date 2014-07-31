package com.laserscorpion.ImageGeneratorLibrary;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.*;
import java.awt.image.*;

public class ImagePanel extends JPanel implements ComponentListener {
	private PixelGridGenerator pixelGenerator;
	private int width;
	private int height;
	
	
	public ImagePanel(int startWidth, int startHeight, PixelGridGenerator imageGenerator) {
		super();
		width = startWidth;
		height = startHeight;
		pixelGenerator = imageGenerator;
		addComponentListener(this);
		updateImage();
	}
	

	private void updateImage() {
		int[][] pixelValues = pixelGenerator.generate(width, height);
		BufferedImage image = createImage(pixelValues);
		paintImage(image);
	}
	
	private BufferedImage createImage(int[][] pixelValues) {
		int[] pixelArray = new int[pixelValues.length * width];
		for (int i = 0; i < pixelValues.length; i++) {
			for (int j = 0; j < width; j++) 
				pixelArray[i*width + j] = pixelValues[i][j];
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		raster.setDataElements(raster.getMinX(), raster.getMinY(), width, height, pixelArray);
		return image;
	}
	
	private void paintImage(BufferedImage image) {
		JLabel picture = new JLabel(new ImageIcon(image));
		removeAll();
		add(picture);
	}
	
	/* Implementation of the ComponentListener interface */
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) { 		
		width = this.getSize().width;
		height = this.getSize().height;
		updateImage(); 
	}
	public void componentShown(ComponentEvent e) { }	
}
