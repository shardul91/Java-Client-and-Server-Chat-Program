import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ChatServer extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public ChatServer() {
		super("Instant Messenger - Server");
		userText = new JTextField();
		userText.setEditable(false); 
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sndMessage(event.getActionCommand());
						userText.setText("");
					}
				}
				);
				add(userText, BorderLayout.SOUTH);		//the border layout can be set to north if we require the textbox on top of the window
				chatWindow = new JTextArea();
				add(new JScrollPane(chatWindow));
				setSize(400,250);						//size of GUI
				setVisible(true);
	}
		public void startRunning() {
			try {
				server = new ServerSocket(6789, 100);
				while(true) {
					try {
						wConnection();				//this will help for server to lookup for client connection
						Stream();					//used to connect stream
						Chatting();					//this function will help client and server to chat once connection is setup
					}catch(EOFException eofexception) {
						swMessage("\nConnection Ended");
					}finally {
						closeChat();
					}
				}
			}catch(IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		private void wConnection() throws IOException{
			swMessage("Waiting for connection \n");
			connection = server.accept();
			swMessage("Connection establised to " + connection.getInetAddress().getHostAddress());
		}
		
		private void Stream() throws IOException{
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush(); 							//to remove previous data
			input = new ObjectInputStream(connection.getInputStream());
			swMessage("\nConnection Successful \n");
		}
		
		public void Chatting() throws IOException{
			String message = "You're now connected";
			sndMessage(message);
			ableToType(true);						//once connection is setup then only it is applicable to type or else it won't work
			do {
				try {
					message = (String) input.readObject();
					swMessage("\n" + message);
				}catch(ClassNotFoundException classNotFoundException) {
					swMessage("\nInvalid message \n");
				}
			}while(!message.equals("Client - Bye"));
		}
		
		private void closeChat() {
			swMessage("\nClosing connection \n");
			ableToType(false);						//since connection is closed, it is unable to type
			try {
				output.close();
				input.close();
				connection.close();
			}catch(IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		private void sndMessage(String message) {
			try {
				output.writeObject("Server - " + message);
				output.flush();  					//delete memory and clear the textbox
				swMessage("\n Server - " + message);
			}catch(IOException ioException) {
				chatWindow.append("\nError: Can't send the message");
			}
		}
	
		private void  swMessage(final String text) {
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							chatWindow.append(text);		//used for attaching text
						}
					}
					);
		}
		
		private void ableToType(final boolean tof) {
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							userText.setEditable(tof);
						}
					}
					);
		}
}