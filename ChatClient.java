import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChatClient extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	public ChatClient(String host){
		super("Instant Messenger - Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sndMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.SOUTH);		//border layout can be North if the textbox needs to be on the top of the window
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(400, 250); 						//size of the GUI
		setVisible(true);
	}
	
	public void startRunning(){
		try{
			conServer(); 						//used to connected to the server using localhost
			Streams();							//used to connect to streams
			Chatting();							//once connection is setup, this will help to chat between client and server
		}catch(EOFException eofException){
			swMessage("\nConnection Ended");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			clsConnection();
		}
	}
	
	private void conServer() throws IOException{
		swMessage("Attempting connection \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		swMessage("Connection Successful. Connected to: " + connection.getInetAddress().getHostName());
	}

	private void Streams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		swMessage("\nYou're now connected \n");
	}
	
	private void Chatting() throws IOException{
		ableToType(true);						//it is possible to type only once the connection is setup
		do{
			try{
				message = (String) input.readObject();
				swMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				swMessage("Error!");
			}
		}while(!message.equals("Server - Bye"));	
	}

	private void clsConnection(){
		swMessage("\nClosing the connection");
		ableToType(false);						//since connection is closed, it is not possible to type
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}

	private void sndMessage(String message){
		try{
			output.writeObject("Client - " + message);
			output.flush();						//deletes memory
			swMessage("\nClient - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n Something went wrong!");
		}
	}

	private void swMessage(final String message){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(message);		//used for attaching text
				}
			}
		);
	}
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
}