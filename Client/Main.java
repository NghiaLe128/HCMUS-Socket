//package Client;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

	public static Connect connectServer;
	public static Screen mainScreen;
	public static SocketControl socketControl;

	public static void main(String arg[]) {
		try {
			
				//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			} catch (Exception e) {
				e.printStackTrace();
			}
		SwingUtilities.invokeLater(() -> {
			connectServer = new Connect();
		});
		
	}

	public static ImageIcon getScaledImage(String path, int width, int height) {
		Image img = new ImageIcon(Main.class.getResource(path)).getImage();
		Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaledImage);
	}
}
