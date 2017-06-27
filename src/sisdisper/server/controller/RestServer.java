package sisdisper.server.controller;

import java.util.ArrayList;


import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.DeleteMe;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;

public class RestServer {
	  private static RestServer instance = null;
	public static RestServer getIstance(){
		 if(instance == null) {
	         instance = new RestServer();
	      }
	      return instance;
	}
	
	
	private Comunication com;
	private ArrayList<Game> games = new ArrayList<Game>();
	public RestServer(){

	}
	
	public synchronized GetGames getGames(){
		
		
		GetGames getgames = new GetGames();
		getgames.setGames(games);
		return getgames;
	}
	

	public synchronized String postNewGame(Game game) {
		String id="id: ";
		for(Game checkGame: games){
			
			id = id+"|"+ checkGame.getId();
			if(checkGame.getId().equals(game.getId())){
				 System.out.println("Game already exist");
				return "Game already exists";
			
			} 
		}
		
		if(games.add(game))
		return "Game added" +id + "   "+games.size() + " | Game id: "+game.getId();		
		
		return "En error occured";
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
