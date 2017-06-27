package sisdisper.server.controller;

import java.util.ArrayList;


import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.DeleteMe;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;

public class RestServer {


	
	private Comunication com;
	private ArrayList<Game> games = new ArrayList<Game>();
	public RestServer(){
		games.add(new Game());
	}
	
	public GetGames getGames(){
		Game game = new Game();
		game.setId("asd");
		games.add(game);
		
		GetGames getgames = new GetGames();
		getgames.setGames(games);
		return getgames;
	}
	

	public synchronized String postNewGame(Game game) {
		
		for(Game checkGame: games){
			if(checkGame.getId().equals(game.getId())){
				return "Game already exists";
			} 
		}
		
		games.add(game);
		return "Game added";		
	}

	public synchronized ResponseAddToGame addMeOnAGame(AddToGame add) {
		ResponseAddToGame response = new ResponseAddToGame();
		
		for(Game checkGame: games){
			if(checkGame.getId().equals(add.getGame().getId())){
				for(Player player: checkGame.getPlayerList()){
					
					if(player.getId().equals(add.getPlayer().getId())){
						response.setType(ResponseAddToGame.Type.AREADY_EXIST);
						return response;
					}
				}
				
				checkGame.addPlayer(add.getPlayer());
				response.setGame(checkGame);
				response.setType(ResponseAddToGame.Type.ACK);
				return response;
			} 
		}
		
		response.setType(ResponseAddToGame.Type.GAME_NOT_FOUND);
		return response;
	}

	public synchronized String deleteMeFromTheGame(DeleteMe toBeDeleted) {
		for(Game checkGame: games){
			if(checkGame.getId().equals(toBeDeleted.getGame().getId())){
				for(Player player: checkGame.getPlayerList()){
					if(player.getId().equals(toBeDeleted.getPlayer().getId())){
						checkGame.removePlayer(toBeDeleted.getPlayer().getId());
						if(checkGame.getPlayerList().isEmpty()){
							games.remove(checkGame);
						}
						return "Deleted";
					}
				}
			}
				
		}
		
	
		return "Error";
	}

	

}
