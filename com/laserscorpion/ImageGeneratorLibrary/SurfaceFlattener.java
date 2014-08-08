package com.laserscorpion.ImageGeneratorLibrary;

import uk.co.cogitolearning.cogpar.*;

public class SurfaceFlattener extends PixelGridGenerator {
	String surfaceExpression;
	ExpressionNode expressionTree;
	int[][] oldGrid; 
	
	/**
	 * The only constructor. Must supply the function to graph.
	 * @param function The function z(x,y) = ... that defines the surface. Omit the z = , just include the 
	 * polynomial in terms of x and y. Multiplied terms need a *, e.g. 5*x^2, not 5x^2 or 5(x^2). 
	 * Parenthesized terms should be within double quotes. 
	 */
	public SurfaceFlattener(String function) {
		surfaceExpression = function;
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
		return surfaceExpression;
	}
	
	@Override
	public int[][] generate(int width, int height) {
		int[][] grid;
		if (oldGrid == null) {
			grid = new int[height][width];
			generateRegion(0, 0, height, width, grid); // slow
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
		int newHeight = grid.length;
		int newWidth = grid[0].length;
		int newRowsAbove = (newHeight - oldHeight) / 2 + balanceRows(oldHeight, newHeight);
		int newColsLeft = (grid[0].length - oldWidth) / 2  + balanceColumns(oldWidth, newWidth); 
		
		generateRegion(0, 0, newRowsAbove, newWidth, grid);
		generateRegion(newRowsAbove, 0, newRowsAbove + oldHeight, newWidth, grid);
		copyOldGrid(newRowsAbove, newColsLeft, grid);
		generateRegion(newRowsAbove, newColsLeft + oldWidth, newRowsAbove + oldHeight, newWidth, grid);
		generateRegion(newRowsAbove + oldHeight, 0, newHeight, newWidth, grid);
	}
	
	private void copyOldGrid(int startRow, int startCol, int[][] grid) {
		for (int i = 0; i < oldGrid.length; i++) {
			for (int j = 0; j < oldGrid[0].length; j++) {
				if (i + startRow < 0) System.out.println("i: " + i + " startRow: "  + startRow);
				if (j + startCol < 0) System.out.println("j: " + j + " startCol: "  + startCol);

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
		int newHeight = grid.length;
		int newWidth = grid[0].length;
		int oldHeight = oldGrid.length;
		int oldWidth = oldGrid[0].length;
		if (newHeight < oldHeight) {
			int trimmedRows = oldHeight - newHeight;
			int trimmedTop = trimmedRows / 2 + balanceRows(oldHeight, newHeight);
			if (newWidth < oldWidth) {
				int trimmedLeft = (oldWidth - newWidth) / 2 + balanceColumns(oldWidth, newWidth);
				for (int i = 0; i < newHeight; i++) {
					for (int j = 0; j < newWidth; j++) {
						grid[i][j] = oldGrid[trimmedTop + i][trimmedLeft + j];
					}
				}
			} else {
				int addedCols = newWidth - oldWidth;
				int addedLeft = addedCols / 2 + balanceColumns(oldWidth, newWidth);
				generateRegion(0, 0, newHeight, addedLeft, grid);
				for (int i = 0; i < newHeight; i++) {
					for (int j = addedLeft; j < addedLeft + oldWidth; j++) {
						grid[i][j] = oldGrid[trimmedTop + i][j - addedLeft];
					}
				}
				generateRegion(0, addedLeft + oldWidth, newHeight, newWidth, grid);	
			}
		} else {
			int addedRows = newHeight - oldHeight;
			int addedTop = addedRows / 2 + balanceRows(oldHeight, newHeight);
			if (newWidth < oldWidth) {
				int trimmedLeft = (oldWidth - newWidth) / 2 + balanceColumns(oldWidth, newWidth); 
				generateRegion(0, 0, addedTop, newWidth, grid);
				for (int i = addedTop; i < addedTop + oldHeight; i++) {
					for (int j = 0; j < newWidth; j++) {
						grid[i][j] = oldGrid[i - addedTop][j + trimmedLeft];
					}
				}				
				generateRegion(addedTop + oldHeight, 0, newHeight, newWidth, grid);
			} else {
				System.out.println("um.");
			}
		}
		
	}

	/**
	 * This deserves some explanation. 
	 * When shrinking (growing) the visible area, either an odd or even number of columns must be removed (added). 
	 * When odd, either the right or left will have one more column removed (added) than the other side.
	 * Without alternating the sides, say always removing an even number from the left side, one side will always 
	 * shrink in an unbalanced way (e.g. more are always removed from the right side).
	 * When an odd number of columns are removed (added), this function alternates which side the extra comes from.
	 * @param oldSize the old width or height
	 * @param newSize the new width or height
	 * @return 1 or 0, depending whether the extra should be removed from the left
	 */
	private boolean extraOnLeft = false;
	private int balanceColumns(int oldSize, int newSize) {
		if ((oldSize - newSize) % 2 != 0 ) {
			if (extraOnLeft) {
				extraOnLeft = false;
				return 1;
			}
			extraOnLeft = true;
		}
		return 0; 
	}
	
	/** See balanceColumns(). Unify them soon */
	private boolean extraOnTop = false;
	private int balanceRows(int oldSize, int newSize) {
		if ((oldSize - newSize) % 2 != 0 ) {
			if (extraOnTop) {
				extraOnTop = false;
				return 1;
			}
			extraOnTop = true;
		}
		return 0; 
	}	
	
	/**
	 * Creates a width x height matrix of ints, puts the origin at the center of the matrix, and fills
	 * in the values for each (x,y) point according to function()
	 */
	private int[][] generateFreshGrid(int width, int height) {
		int[][] grid = new int[height][width];
		generateRegion(0, 0, height, width, grid);
		/*int lowerBoundWidth = -width / 2;
		int upperBoundWidth = width/2 + width%2;
		int lowerBoundHeight = -height / 2;
		int upperBoundHeight = height/2 + height%2;
		for (int y = lowerBoundHeight; y < upperBoundHeight ; y++) {
			for (int x = lowerBoundWidth; x < upperBoundWidth ; x++) {
				int i = y - lowerBoundHeight;
				int j = x - lowerBoundWidth;
				grid[i][j] = function(x, y) ;
 			}
		}*/
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
	private int yValue = 0;
	private int function(int x, int y) {
		setVariable("x", x);
		if (y != yValue) { // don't need to set y on every pixel
			setVariable("y", (double)y);
			yValue = y;
		}
		try {
			return Math.abs((int)expressionTree.getValue());			
		} catch (EvaluationException e) {
			System.out.println("Error evaluating expression: " + e.getMessage());
			System.exit(-1);
			return -1; // never reached
		}
	}

	private void setVariable(String name, double value) {
		SetVariable setter = new SetVariable(name, value);
		expressionTree.accept(setter);
	}

}
