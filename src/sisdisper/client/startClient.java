package sisdisper.client;

import sisdisper.server.model.Game;
import sisdisper.server.model.comunication.GetGames;

public class startClient {

	public static void main(String[] args)  {
		ClientToServerCommunication com = new ClientToServerCommunication();
		com.getGamesFromServer();
		
		Game game = new Game();
		game.setId("sfsf");
		
		com.createNewGame(game);
		GetGames games = com.getGamesFromServer();
		for(Game checkGame: games.getGames()){
		      System.out.println(checkGame.getId());

		}
		
	}
	
}
