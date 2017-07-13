package sisdisper.client.socket;


import java.net.ServerSocket;
import java.util.ArrayList;

import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.Action;
import sisdisper.server.model.Player;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private static ArrayList<ServerClientsHandler> clients = new ArrayList<ServerClientsHandler>();
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
					System.out.println("@@@@SERVER@@@@ Client added @@@@@@@@ ");
					synchronized(clients){
					clients.add(client);
					}
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

	public void sendMessageToAll(Action action) throws JsonProcessingException {
		String saction = action.serialize();
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(saction);
		
		
		for (ServerClientsHandler client: clients){
		
			client.sendMessage(jsonInString);
			
		}
		
	}
	
	public synchronized void sendMessageToPlayer(Player player, Action action) throws JsonProcessingException{
		String saction = action.serialize();
     	ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(saction);
		
		if(action instanceof Ack){
		//System.out.println("@@@SERVER@@@ SENDING ACK for player: "+player.getId());
		}
		synchronized(clients){
		for (ServerClientsHandler client: clients){
			//System.out.println("@@@SERVER@@@ SENDING ACK for player: "+client.getPlayer_id());
			if(client.getPlayer_id().equals(player.getId())){
			//System.out.println("@@@SERVER@@@ SENDING to player"+player.getId()+" @@@@@@@  ");
			 client.sendMessage(jsonInString);
			}
		}
		}
	}

	/**
	 * A private thread to handle capitalization requests on a particular
	 * socket. The client terminates the dialogue by sending a single line
	 * containing only a period.
	 */

}