package com.laserscorpion.ImageGeneratorLibrary;

public class ImageDrawer {
	public static void main (String args[]) {
		//XYPlusX3 imageGenerator = new XYPlusX3();
		SurfaceFlattener imageGenerator = new SurfaceFlattener("100*(0.3*x*y + 0.001*x^3)");
		ImageWindow window = new ImageWindow(imageGenerator);
	}
}
