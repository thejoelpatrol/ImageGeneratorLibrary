
public class ZAxisColorGenerator extends PixelGridGenerator {
	private static final double BIAS = 100;
	
	
	@Override
	public int[][] generate(int width, int height) {
		return generateZ(width, height);
	}
	
	protected int function(int x, int y) {
		double xf = x;
		double z = 0.2*xf*(double)y + 0.001*xf*xf*xf;
		z *= BIAS;
		return (int)Math.abs(z); 
	}
}


