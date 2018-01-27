package sisdisper.client.model.action;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.server.model.Game;
import sisdisper.server.model.comunication.GetGames;

public class GetGamesFromServer extends Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Boolean execute(){
		ClientToServerCommunication com = new ClientToServerCommunication();
		GetGames games = com.getGamesFromServer();
		
		System.out.println("Befroe notify cli");
		synchronized (BufferController.cli) {
			BufferController.cli.notifyAll();
		}
		BufferController.cli.getGames(games);
		

		return true;
	}

}
