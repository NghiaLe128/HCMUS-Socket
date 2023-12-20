package Server;

import javax.swing.UIManager;

public class Main {

	public static SocketControl socketControl;
	public static Screen mainScreen;

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		mainScreen = new Screen();

	}
}
