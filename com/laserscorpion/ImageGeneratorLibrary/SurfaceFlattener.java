package com.laserscorpion.ImageGeneratorLibrary;

import uk.co.cogitolearning.cogpar.*;

public class SurfaceFlattener extends PixelGridGenerator {
	ExpressionNode expressionTree;
	
	public SurfaceFlattener(String function) {
		Parser parser = new Parser();
		try {
			expressionTree = parser.parse(function.toLowerCase());
		} catch (ParserException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	@Override
	public int[][] generate(int width, int height) {
		return generateZ(width, height);
	}

	/**
	 * Generates a grid of ints according to some algorithm defined by function(), chosen by the implementation, 
	 * which are suitable for use as RGB color values with no alpha (fully opaque). 
	 * generateZ() provides a base on which generate() may be implemented, if the algorithm
	 * involves calculating the height of a surface (the Z coordinates). The origin is at the center
	 * of the grid, and each cell contains the Z value calculated by function() at that coordinate.
	 * @param width The width of the desired grid of ints
	 * @param height The height of the desired grid of ints
	 * @return A (width)x(height) 2D array of ints that are suitable for use as RGB color values
	 */
	private int[][] generateZ(int width, int height) {
		int[][] grid = new int [height][width];
		int lowerBoundWidth = -width / 2;
		int upperBoundWidth = width/2 + width%2;
		int lowerBoundHeight = -height / 2;
		int upperBoundHeight = height/2 + height%2;
		for (int y = lowerBoundHeight; y < upperBoundHeight ; y++) {
			for (int x = lowerBoundWidth; x < upperBoundWidth ; x++) {
				int i = y - lowerBoundHeight;
				int j = x - lowerBoundWidth;
				grid[i][j] = function(x, y) ;//& alphaMask;
 			}
		}	
		return grid;
	}	
	
	/**
	 * Generates the Z value for an X and Y value, according to the function supplied to the constructor.
	 * This calculates the actual function z(x,y) = ...
	 * @param x the X coordinate for which Z is desired
	 * @param y the Y coordinate for which Z is desired
	 * @return the Z coordinate
	 */
	// can extend this to take a vector for n-dimensional points
	private double yValue = 0;
	private int function(int x, int y) {
		setVariable("x", x);
		if (y != (int)yValue) { // don't need to set y on every pixel
			setVariable("y", (double)y);
			yValue = y;
		}
		try {
			return Math.abs((int)expressionTree.getValue());			
		} catch (EvaluationException e) {
			System.out.println("Error evaluating expression: " + e.getMessage());
			System.exit(-1);
			return -1; // never executed
		}
	}

	private void setVariable(String name, double value) {
		SetVariable setter = new SetVariable(name, value);
		expressionTree.accept(setter);
	}

}
