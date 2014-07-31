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

	// can extend this to take a vector for n-dimensional points
	@Override
	protected int function(int x, int y) {
		setVariable("x", (double)x);
		setVariable("y", (double)y);
		return (int)expressionTree.getValue();
	}

	// can store a vector of name/value pairs, and avoid setting any if the current vector matches
	private void setVariable(String name, double value) {
		SetVariable setter = new SetVariable(name, value);
		expressionTree.accept(setter);
	}

}
