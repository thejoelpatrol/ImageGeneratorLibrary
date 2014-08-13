package com.laserscorpion.ImageGeneratorLibrary;

import uk.co.cogitolearning.cogpar.*;
import java.awt.Point;

public class SurfaceFlattener extends PixelGridGenerator {
	String surfaceExpression;
	ExpressionNode expressionTree;
	int[][] oldGrid; 
	Point oldOrigin;
	
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
		int[][] grid = new int[height][width];;
		if (oldGrid == null) {
			generateRegion(0, 0, height, width, grid); // slow
			oldOrigin = new Point(width/2, height/2);
		} else {
			Point newOrigin = new Point(width/2, height/2);
			shrinkOrExpandOldGrid(newOrigin, grid); // faster		
			oldOrigin = newOrigin;
		}
		oldGrid = grid;
		return grid;
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
	
	private void shrinkOrExpandOldGrid(Point newOrigin, int[][] grid) {
		int newHeight = grid.length;
		int newWidth = grid[0].length;
		int oldHeight = oldGrid.length;
		int oldWidth = oldGrid[0].length;
		
		// "added" amounts can be negative, when the grid shrinks in one or more dimensions
		int addedTop = newOrigin.y - oldOrigin.y;
		int addedBottom = (newHeight - newOrigin.y) - (oldHeight - oldOrigin.y);
		int addedLeft = newOrigin.x - oldOrigin.x; 
		int addedRight = (newWidth - newOrigin.x) - (oldWidth - oldOrigin.x);
		
		int innerStartRow = Math.max(0, addedTop);
		int innerStartCol = Math.max(0, addedLeft);
		int innerEndRow = Math.min(newHeight, addedTop + oldHeight);
		int innerEndCol = Math.min(newWidth, addedLeft + oldWidth);

		if (addedTop > 0) generateRegion(0, 0, addedTop, newWidth, grid);
		if (addedLeft > 0) generateRegion(innerStartRow, 0, innerEndRow, innerStartCol, grid);
		
		for (int i = innerStartRow; i < innerEndRow; i++) {
			for (int j = innerStartCol; j < innerEndCol; j++) {
				grid[i][j] = oldGrid[i - addedTop][j - addedLeft];
			}
		}			
		
		if (addedRight > 0) generateRegion(innerStartRow, innerEndCol, innerEndRow, newWidth, grid);
		if (addedBottom > 0) generateRegion(addedTop + oldHeight, 0, newHeight, newWidth, grid);


		
		oldOrigin = newOrigin;
	}
	
	// this was an intermediate step in changing how the old grid is used
	private void usePartialGrid(int newWidth, int newHeight, int oldWidth, int oldHeight, Point newOrigin, int[][] grid) {
		// "added" amounts can be negative, when the grid shrinks in one or more dimensions
		int addedTop = newOrigin.y - oldOrigin.y;
		int addedBottom = (newHeight - newOrigin.y) - (oldHeight - oldOrigin.y);
		int addedLeft = newOrigin.x - oldOrigin.x; 
		int addedRight = (newWidth - newOrigin.x) - (oldWidth - oldOrigin.x);
		
		int innerStartRow = Math.max(0, addedTop);
		int innerStartCol = Math.max(0, addedLeft);
		int innerEndRow = Math.min(newHeight, addedTop + oldHeight);
		int innerEndCol = Math.min(newWidth, addedLeft + oldWidth);

		if (addedTop > 0) generateRegion(0, 0, addedTop, newWidth, grid);
		if (addedLeft > 0) generateRegion(innerStartRow, 0, innerEndRow, innerStartCol, grid);
		
		for (int i = innerStartRow; i < innerEndRow; i++) {
			for (int j = innerStartCol; j < innerEndCol; j++) {
				grid[i][j] = oldGrid[i - addedTop][j - addedLeft];
			}
		}			
		
		if (addedRight > 0) generateRegion(innerStartRow, innerEndCol, innerEndRow, newWidth, grid);
		if (addedBottom > 0) generateRegion(addedTop + oldHeight, 0, newHeight, newWidth, grid);
	}
	
	private void subsetOldGrid(int newWidth, int newHeight, Point newOrigin, int[][] grid) {
		int trimmedTop = oldOrigin.y - newOrigin.y;
		int trimmedLeft = oldOrigin.x - newOrigin.x;
		for (int i = 0; i < newHeight; i++) {
			for (int j = 0; j < newWidth; j++) {
				grid[i][j] = oldGrid[trimmedTop + i][trimmedLeft + j];
			}
		}
	}

	private void expandOldGrid(int[][] grid, Point newOrigin) {
		int oldHeight = oldGrid.length;
		int oldWidth = oldGrid[0].length;
		int newHeight = grid.length;
		int newWidth = grid[0].length;
		int newRowsAbove = newOrigin.y - oldOrigin.y;   
		int newColsLeft = newOrigin.x - oldOrigin.x;
		
		generateRegion(0, 0, newRowsAbove, newWidth, grid);
		generateRegion(newRowsAbove, 0, newRowsAbove + oldHeight, newWidth, grid);
		copyOldGrid(newRowsAbove, newColsLeft, grid);
		generateRegion(newRowsAbove, newColsLeft + oldWidth, newRowsAbove + oldHeight, newWidth, grid);
		generateRegion(newRowsAbove + oldHeight, 0, newHeight, newWidth, grid);
	}
	
	private void copyOldGrid(int startRow, int startCol, int[][] grid) {
		for (int i = 0; i < oldGrid.length; i++) {
			for (int j = 0; j < oldGrid[0].length; j++) {
				grid[i + startRow][j + startCol] = oldGrid[i][j];
			}
		}
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
