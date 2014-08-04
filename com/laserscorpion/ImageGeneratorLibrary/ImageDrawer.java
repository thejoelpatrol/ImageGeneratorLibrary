package com.laserscorpion.ImageGeneratorLibrary;

public class ImageDrawer {
	public static void main (String[] args) {
		PixelGridGenerator imageGenerator;
		if (args[0].equals("-z") || args[0].equals("--surface")) 
			imageGenerator = new SurfaceFlattener(args[1]);
		else if (args[0].equals("-m") || args[0].equals("--matrix"))
			imageGenerator = new MatrixPainter(args);
		else {
			printUsageAndExit();
			imageGenerator = new SurfaceFlattener("0"); // to silence the compiler; never executed
		}
		
		//SurfaceFlattener imageGenerator = new SurfaceFlattener("100*(0.3*x*y + 0.001*x^3 + y^2)");
		//SurfaceFlattener imageGenerator = new SurfaceFlattener("y*sin(10*x)^x + x");
		//SurfaceFlattener imageGenerator = new SurfaceFlattener("y*sin(10*x)^x + x*y");
		ImageWindow window = new ImageWindow(imageGenerator);
	}
	
	static void printUsageAndExit() {
		System.out.println("Usage: java ImageDrawer <-z | -m> <option-specific arguments>");
		System.out.println("Options:");
		System.out.println('\t' + "-z or --surface: draw the RGB color of the z coordinate of the R3 surface given by the argument.");
		System.out.println('\t' + "-m or --matrix: color a matrix of numbers (argument 1) using the color definitions supplied in argument 2.");
		System.exit(0);
	}
}
