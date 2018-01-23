import javax.swing.*;

public class ServerTest {
	public static void main(String[] args) {
		ChatServer server1 = new ChatServer();
		server1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server1.startRunning();
	}
}