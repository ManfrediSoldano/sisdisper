package sisdisper.client.socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AfterBombCheck;
import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.NewPlayerResponse;
import sisdisper.client.model.action.PassToken;
import sisdisper.client.model.action.WelcomeNewPlayer;
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
	public Boolean end=false;
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
		@SuppressWarnings("resource")
		Socket socket = new Socket(ip, port);
		in = new Scanner(socket.getInputStream());
		out = new PrintWriter(socket.getOutputStream(), true);

		// Consume the initial welcoming messages from the server

	}

	public void run() {
		try {
			while (!end) {

				try {

					String whil = in.nextLine();
					if (whil != null) {
						setReceived_text(whil);
					}
				} catch (Exception exc) {
					// System.out.println("@@@@CLIENT@@ ERROR @@@@@@@ " + exc);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public synchronized void send(Action action) throws JsonProcessingException {
		String saction = action.serialize();
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(saction);

		out.println(jsonInString);
		out.flush();

	}

	public String getReceived_text() {

		return received_text;

	}

	public void setReceived_text(String received_text) {
		// System.out.println("@@@@CLIENT@@ RECEIVED NEW TEXT! @@@@@@@ ");
		try {
		ObjectMapper mapper = new ObjectMapper();
		String saction = mapper.readValue(received_text, String.class);
		Action deser = new Action();
		Action action = deser.deserialize(saction);

		
		if (action instanceof Ack) {
			System.out.println("@@@@CLIENT@@ RECEIVED ACK @@@@@@@ ");
		}
		if (action instanceof AskPosition) {
			System.out.println("@@@@CLIENT@@ RECEIVED ASK POSITION @@@@@@@ ");
		}
		if (action instanceof AfterBombCheck) {
			System.out.println("@@@@CLIENT@@ RECEIVED AFTERBOMBCHECK @@@@@@@ ");
		}
		
		

	
			if (action instanceof NewPlayerResponse) {
				System.out.println("@@@@CLIENT@@ NEW PLAYER RESPONSE @@@@@@@ ");
			}
			
				Buffer.addAction(action, this);
			
			if (action instanceof NewPlayerResponse) {
				System.out.println("@@@@CLIENT@@ AFTER ADDING A NEW PLAYER RESPONSE @@@@@@@ ");
			}

		} catch (JAXBException | InterruptedException | IOException  e) {
			// TODO Auto-generated catch block
			System.err.println(received_text);

			e.printStackTrace();
		}

	}

}