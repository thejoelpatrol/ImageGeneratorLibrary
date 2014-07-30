import javax.swing.JFrame;

public class ImageWindow {
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;
	private JFrame window;
	private ImagePanel panel;
	
	public ImageWindow() {
		window = new JFrame("ImageWindow");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		window.setVisible(true);
		panel = new ImagePanel();
		window.getContentPane().add(panel);
		window.validate();
	}
	
	public static void main (String args[]) {
		ImageWindow window = new ImageWindow();
		
	}
}
