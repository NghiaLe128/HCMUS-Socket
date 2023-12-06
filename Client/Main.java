//package Client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

	public static Connect connectServer;

	public static void main(String arg[]) {
		try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		SwingUtilities.invokeLater(() -> {
			connectServer = new Connect();
		});
		
	}

}
