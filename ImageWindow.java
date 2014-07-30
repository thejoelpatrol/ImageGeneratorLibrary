import javax.swing.JFrame;

public class ImageWindow {
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	private JFrame frame;
	private ImagePanel panel;
	
	public ImageWindow() {
		frame = new JFrame("ImageWindow");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setVisible(true);
		panel = new ImagePanel(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.add(panel);
		frame.pack();
		frame.validate();
	}
	

	public static void main (String args[]) {
		ImageWindow window = new ImageWindow();
	}
}
