
public class ZAxisColorGenerator extends PixelGridGenerator {
	private static final double BIAS = 100;
	private static final int alphaMask = 0x70FFFFFF;
	
	
	@Override
	public int[][] generate(int width, int height) {
		int[][] grid = new int [height][width];
		int lowerBoundWidth = -width / 2;
		int upperBoundWidth = width/2 + width%2;
		int lowerBoundHeight = -height / 2;
		int upperBoundHeight = height/2 + height%2;
		for (int y = lowerBoundHeight; y < upperBoundHeight - 1; y++) {
			for (int x = lowerBoundWidth; x < upperBoundWidth - 1; x++) {
				double xf = x;
				double z = 0.2*xf*(double)y + 0.01*xf*xf*xf;
				z *= BIAS;
				int i = y - lowerBoundHeight;
				int j = x - lowerBoundWidth;
 				grid[i][j] = (int)Math.abs(z) & alphaMask; 
 			}
		}	
		return grid;
	}
}


