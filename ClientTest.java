import javax.swing.*;

public class ClientTest {
	public static void main(String[] args) {
		ChatClient client1;
		client1 = new ChatClient("127.0.0.1");	//this is local host IP address
		client1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client1.startRunning();
	}
}