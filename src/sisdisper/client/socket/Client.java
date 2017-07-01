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

import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.Action;
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

	public void start() {
		t = new Thread(this);
		t.start();
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
		while (true) {
			try {
				String whil = in.nextLine();

				setReceived_text(whil);
			} catch (Exception exc) {

			}
		}
	}

	public void send(Action action) throws JAXBException {
		StringWriter sw = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(Action.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.marshal(action, sw);
		String xmlString = sw.toString();
		out.println(xmlString);
		out.flush();

	}

	public String getReceived_text() {

		return received_text;
	}

	public void setReceived_text(String received_text) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Action.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(received_text);
		Action action = (Action) jaxbUnmarshaller.unmarshal(reader);
	
		if (action instanceof Ack) {
			synchronized (buffer) {
				buffer.notifyAll();
				return;
			}
		}
		buffer.addAction(action);

	}

}