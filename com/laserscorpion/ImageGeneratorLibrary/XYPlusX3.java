package com.laserscorpion.ImageGeneratorLibrary;
/**
 * Example PixelGridGenerator that uses one specific function to generate a 
 * pretty rad image. Not flexible at all, does the same thing every time, as
 * an example of how to implement this class.
 *
 */

public class XYPlusX3 extends PixelGridGenerator {
	private static final double BIAS = 100;
	
	public int[][] generate(int width, int height) {
		return generateZ(width, height);
	}
	
	protected int function(int x, int y) {
		double xf = x;
		double z = 0.3*xf*(double)y + 0.001*xf*xf*xf;// + y*y;
		z *= BIAS;
		return (int)Math.abs(z); 
	}
}


