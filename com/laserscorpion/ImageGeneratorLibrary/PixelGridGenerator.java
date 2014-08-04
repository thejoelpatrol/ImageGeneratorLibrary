package com.laserscorpion.ImageGeneratorLibrary;

public abstract class PixelGridGenerator {
	private static final int alphaMask = 0x70FFFFFF;
		
	/**
	 * Generates a grid of ints according to some algorithm, chosen by the implementation, 
	 * which are suitable for use as RGB color values with no alpha (fully opaque).
	 * @param width The width of the desired grid of ints
	 * @param height The height of the desired grid of ints
	 * @return A (width)x(height) 2D array of ints that are suitable for use as RGB color values
	 */
	public abstract int[][] generate(int width, int height);



}
