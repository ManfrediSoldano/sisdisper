package sisdisper.client.socket;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.Socket;
import java.util.Scanner;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sisdisper.client.BufferController;
import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.PassToken;
import sisdisper.server.model.Player;
/**
 * A simple Swing-based client for the capitalization server. It has a main
 * frame window with a text field for entering strings and a textarea to see the
 * results of capitalizing them.
 */
public class Client extends Thread {
	private Thread t;
	private Scanner in; 
	private PrintWriter out;
	private String ip;
	private int port;
	private String received_text;
	private Player player;
	private Buffer buffer;
	private Socket socket;
	public Boolean end=false;
	public ClientObservable clientObserver= null;
	
	public ClientObservable getClientObserver() {
		return clientObserver;
	}

	public void setClientObserver(ClientObservable clientObserver) {
		this.clientObserver = clientObserver;
	}

	public void start() {
		t = new Thread(this);
		t.start();
		buffer = new Buffer();
		try {
			connectToServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Constructs the client by laying out the GUI and registering a listener
	 * with the textfield so that pressing Enter in the listener sends the
	 * textfield contents to the server.
	 */
	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;

	}

	public Client(Player player) {
		this.ip = player.getIp();
		this.port = player.getPort();
		this.player = player;
	}

	/**
	 * Implements the connection logic by prompting the end user for the
	 * server's IP address, connecting, setting up streams, and consuming the
	 * welcome messages from the server. The Capitalizer protocol says that the
	 * server sends three lines of text to the client immediately after
	 * establishing a connection.
	 */
	public void connectToServer() throws IOException {

		// Get the server address from a dialog box.

		// Make connection and initialize streams
		
		
		socket = new Socket(ip, port);
		in = new Scanner(socket.getInputStream());
		out = new PrintWriter(socket.getOutputStream(), true);

		// Consume the initial welcoming messages from the server

	}

	public void run() {
		try {
			while (BufferController.alive) {

				try {

					String whil = in.nextLine();
					if (whil != null) {
						setReceived_text(whil);
					}
				} catch (Exception exc) {
					//System.out.println("@@@@CLIENT@@ ERROR @@@@@@@ " + exc);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		
		try {
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void send(Action action) throws JsonProcessingException {
		String saction = action.serialize();
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(saction);

		out.println(jsonInString);
		out.flush();

	}

	
	public void setReceived_text(String received_text) {
		try {
		
		ObjectMapper mapper = new ObjectMapper();
		String saction = mapper.readValue(received_text, String.class);
		Action deser = new Action();
		Action action = deser.deserialize(saction);
		
		if(!(action instanceof PassToken))
		System.out.println("@@@CLIENT@@@ Received: "+action+"@@@");
	
		clientObserver.setActionChanged(action);
		

		} catch ( IOException  e) {
			// TODO Auto-generated catch block
			System.err.println(received_text);

			e.printStackTrace();
		}

	}

}