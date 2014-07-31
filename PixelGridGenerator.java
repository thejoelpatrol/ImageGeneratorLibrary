
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
	protected int[][] generateZ(int width, int height) {
		int[][] grid = new int [height][width];
		int lowerBoundWidth = -width / 2;
		int upperBoundWidth = width/2 + width%2;
		int lowerBoundHeight = -height / 2;
		int upperBoundHeight = height/2 + height%2;
		for (int y = lowerBoundHeight; y < upperBoundHeight ; y++) {
			for (int x = lowerBoundWidth; x < upperBoundWidth ; x++) {
				int i = y - lowerBoundHeight;
				int j = x - lowerBoundWidth;
				grid[i][j] = function(x, y) & alphaMask;
 			}
		}	
		return grid;
	}	
	
	/**
	 * Generates the Z value for an X and Y value, according to the implementation's algorithm. If
	 * using generateZ() to generate a surface, this function is the actual function z(x,y) = ...
	 * @param x the X coordinate for which Z is desired
	 * @param y the Y coordinate for which Z is desired
	 * @return the Z coordinate
	 */
	protected abstract int function(int x, int y);

}
