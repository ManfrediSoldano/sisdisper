package sisdisper.client.socket;

import java.io.StringWriter;
import java.net.ServerSocket;
import java.util.ArrayList;

import sisdisper.client.model.action.Action;
import sisdisper.server.model.Player;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * A server program which accepts requests from clients to capitalize strings.
 * When clients connect, a new thread is started to handle an interactive dialog
 * in which the client sends in a string and the server thread sends back the
 * capitalized version of the string.
 *
 * The program is runs in an infinite loop, so shutdown in platform dependent.
 * If you ran it from a console window with the "java" interpreter, Ctrl+C
 * generally will shut it down.
 */
public class Server implements Runnable {
	private Thread t;
	Player me = new Player();
	private ArrayList<ServerClientsHandler> clients = new ArrayList<ServerClientsHandler>();
	/**
	 * Application method to run the server runs in an infinite loop listening
	 * on port 9898. When a connection is requested, it spawns a new thread to
	 * do the servicing and immediately returns to listening. The server keeps a
	 * unique client number for each client that connects just to show
	 * interesting logging messages. It is certainly not necessary to do this.
	 */
	public void run() {
		try {
			System.out.println("The  server is running.");
			
			ServerSocket listener = new ServerSocket(me.getPort());
			try {
				while (true) {
					ServerClientsHandler client = new ServerClientsHandler(listener.accept());
					clients.add(client);
					client.start();
				}
			} finally {
				listener.close();
			}
		} catch (Exception e) {

		}
	}

	public void start() {
		t = new Thread(this);
		t.start();
	}

	public void setPlayer(Player me) {
		this.me = me;
	}

	public void sendMessageToAll(Action action) throws JAXBException {
		StringWriter sw = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(Action.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		
		jaxbMarshaller.marshal(action, sw);
		String xmlString = sw.toString();
		
		for (ServerClientsHandler client: clients){
			client.sendMessage(xmlString);
		}
		
	}
	
	public void sendMessageToPlayer(Player player, Action action) throws JAXBException{
		StringWriter sw = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(Action.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		
		jaxbMarshaller.marshal(action, sw);
		String xmlString = sw.toString();
		
		for (ServerClientsHandler client: clients){
			if(client.getClientNumber() == player.getPort() && client.getAddress().toString()==player.getIp()){
			client.sendMessage(xmlString);
			}
		}
	}

	/**
	 * A private thread to handle capitalization requests on a particular
	 * socket. The client terminates the dialogue by sending a single line
	 * containing only a period.
	 */

}