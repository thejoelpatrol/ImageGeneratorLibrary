package com.laserscorpion.ImageGeneratorLibrary;

import uk.co.cogitolearning.cogpar.*;

public class SurfaceFlattener extends PixelGridGenerator {
	String surface;
	ExpressionNode expressionTree;
	int[][] oldGrid; 
	
	/**
	 * The only constructor. Must supply the function to graph.
	 * @param function The function z(x,y) = ... that defines the surface. Omit the z = , just include the 
	 * polynomial in terms of x and y. Multiplied terms need a *, e.g. 5*x^2, not 5x^2 or 5(x^2). 
	 * Parenthesized terms should be within double quotes. 
	 */
	public SurfaceFlattener(String function) {
		surface = function;
		Parser parser = new Parser();
		try {
			expressionTree = parser.parse(function.toLowerCase());
		} catch (ParserException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	@Override
	public String getName() {
		return surface;
	}
	
	@Override
	public int[][] generate(int width, int height) {
		int[][] grid;
		if (oldGrid == null) {
			grid = generateFreshGrid(width, height); // slow
		} else {
			grid = new int[height][width];
			int oldWidth = oldGrid[0].length;
			int oldHeight = oldGrid.length;
			if (width >= oldWidth && height >= oldHeight)
				expandOldGrid(grid); // faster than redrawing from scratch
			else subsetOldGrid(grid); // fast			
		}
		oldGrid = grid;
		return grid;
	}
	
	private void expandOldGrid(int[][] grid) {
		int oldHeight = oldGrid.length;
		int oldWidth = oldGrid[0].length;
		int newRowsAbove = (grid.length - oldHeight) / 2;
		//int newRowsBelow = grid.length - oldHeight - newRowsAbove;
		int newColsLeft = (grid[0].length - oldWidth) / 2;
		//int newColsRight = grid[0].length - oldWidth - newColsLeft;
		
		generateRegion(0, 0, newRowsAbove, grid[0].length, grid);
		generateRegion(newRowsAbove, 0, newRowsAbove + oldHeight, newColsLeft, grid);
		copyOldGrid(newRowsAbove, newColsLeft, grid);
		generateRegion(newRowsAbove, newColsLeft + oldWidth, newRowsAbove + oldHeight, grid[0].length, grid);
		generateRegion(newRowsAbove + oldHeight, 0, grid.length, grid[0].length, grid);
	}
	
	private void copyOldGrid(int startRow, int startCol, int[][] grid) {
		for (int i = 0; i < oldGrid.length; i++) {
			for (int j = 0; j < oldGrid[0].length; j++) {
				grid[i + startRow][j + startCol] = oldGrid[i][j];
			}
		}
	}
	
	private void generateRegion(int startRow, int startCol, int endRow, int endCol, int[][] grid) {
		for (int i = startRow; i < endRow; i++) {
			for (int j = startCol; j < endCol; j++) {
				int x = j - grid[0].length/2;
				int y = i - grid.length/2;
				grid[i][j] = function(x, y);
			}
		}
	}
	
	private void subsetOldGrid(int[][] grid) {
		int newRows = grid.length;
		int newCols = grid[0].length;
		int oldRows = oldGrid.length;
		int oldCols = oldGrid[0].length;
		if (newRows < oldRows) {
			int trimmedRows = oldRows - newRows;
			int trimmedTop = trimmedRows / 2;
			if (newCols < oldCols) {
				int trimmedCols = oldCols - newCols;
				for (int i = 0; i < newRows; i++) {
					for (int j = 0; j < newCols; j++) {
						grid[i][j] = oldGrid[trimmedTop + i][trimmedCols/2 + j];
					}
				}
			} else {
				int addedCols = newCols - oldCols;
				int addedLeft = addedCols / 2;
				generateRegion(0, 0, newRows, addedLeft, grid);
				for (int i = 0; i < newRows; i++) {
					for (int j = addedLeft; j < addedLeft + oldCols; j++) {
						grid[i][j] = oldGrid[trimmedTop + i][j - addedLeft];
					}
				}
				generateRegion(0, addedLeft + oldCols, newRows, newCols, grid);	
			}
		} else {
			int addedRows = newRows - oldRows;
			int addedTop = addedRows / 2;
			if (newCols < oldCols) {
				int trimmedLeft = (oldCols - newCols) / 2;
				generateRegion(0, 0, addedTop, newCols, grid);
				for (int i = addedTop; i < addedTop + oldRows; i++) {
					for (int j = 0; j < newCols; j++) {
						grid[i][j] = oldGrid[i - addedTop][j + trimmedLeft];
					}
				}				
				generateRegion(addedTop + oldRows, 0, newRows, newCols, grid);
			} else {
				System.out.println("um.");
			}
		}
		
	}
	
	/**
	 * Creates a width x height matrix of ints, puts the origin at the center of the matrix, and fills
	 * in the values for each (x,y) point according to function()
	 */
	private int[][] generateFreshGrid(int width, int height) {
		int[][] grid = new int[height][width];
		int lowerBoundWidth = -width / 2;
		int upperBoundWidth = width/2 + width%2;
		int lowerBoundHeight = -height / 2;
		int upperBoundHeight = height/2 + height%2;
		for (int y = lowerBoundHeight; y < upperBoundHeight ; y++) {
			for (int x = lowerBoundWidth; x < upperBoundWidth ; x++) {
				int i = y - lowerBoundHeight;
				int j = x - lowerBoundWidth;
				grid[i][j] = function(x, y) ;
 			}
		}	
		oldGrid = grid;
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
