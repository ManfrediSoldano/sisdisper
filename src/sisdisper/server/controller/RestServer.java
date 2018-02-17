package sisdisper.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.container.AsyncResponse;

import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.DeleteMe;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;

public class RestServer {
	private static RestServer instance = null;
	final static Map<String, AsyncResponse> waiters = new ConcurrentHashMap<>();  

	public static RestServer getIstance() {
		if (instance == null) {
			instance = new RestServer();
		}
		return instance;
	}

	private Comunication com;
	private ArrayList<Game> games = new ArrayList<Game>();
	private ArrayList<Player> analitics = new ArrayList<Player>();

	public RestServer() {

	}

	public synchronized GetGames getGames() {

		ArrayList<Game> temp = new ArrayList<Game>();
		GetGames getgames = new GetGames();
		for (Game game : games) {
			if (game.live) {
				temp.add(game);
			}
		}

		getgames.setGames(temp);
		return getgames;
	}

	public synchronized String postNewGame(Game game) {

		for (Game checkGame : games) {
			if (checkGame.getId().equals(game.getId())) {
				System.out.println("Game already exist");
				return "Game already exists";
			}
		}

		game.start= new Date();
		games.add(game);
		Set<String> nicks = waiters.keySet();  

		for (String n : nicks) {
			
			waiters.get(n).resume("A new game has just started: "+game.getId());
		}
			return "ack";
	}

	public synchronized ResponseAddToGame addMeOnAGame(AddToGame add) {
		ResponseAddToGame response = new ResponseAddToGame();

		for (Game checkGame : games) {
			if (checkGame.live) {
				if (checkGame.getId().equals(add.getGame().getId())) {
					for (Player player : checkGame.getPlayerList()) {

						if (player.getId().equals(add.getPlayer().getId())) {
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
		}

		response.setType(ResponseAddToGame.Type.GAME_NOT_FOUND);
		return response;
	}

	public synchronized String deleteMeFromTheGame(String playerid, String gameid, String points, String winner) {
		for (Game checkGame : games) {
			if (checkGame.getId().equals(gameid)) {
				for (Player player : checkGame.getPlayerList()) {
					
					if (player.getId().equals(playerid)) {
						checkGame.removePlayer(playerid, points,winner);
						
						if (checkGame.getPlayerList().isEmpty()) {
							checkGame.live = false;
							checkGame.end = new Date();
							long time = ((checkGame.end.getTime()/60000) - (checkGame.start.getTime()/60000));
							String win ="";
							
							for (Player playe: checkGame.deadPlayers ) {
								if (playe.winner.equals("winner"))
									win= player.getId();	
							}
							
							Set<String> nicks = waiters.keySet();  
							
							for (String n : nicks) {
								waiters.get(n).resume("This game has just end: "+checkGame.getId()+" the match has ended after "+time+" minutes. The winner is "+ win);
							}
							
						}
						
						return "Deleted";
					}
				}
			}

		}

		return "Error";
	}

	public synchronized ResponseAddToGame newAnalyst(String playerid, String ip) {
		ResponseAddToGame response = new ResponseAddToGame();
		for (Player player : analitics) {
			if (player.getId() == playerid) {
				response.setType(ResponseAddToGame.Type.AREADY_EXIST);
				return response;
			}
		}
		Player newp = new Player();
		newp.setId(playerid);
		analitics.add(newp);
		response.setType(ResponseAddToGame.Type.ACK);
		return response;
	}

	
	public synchronized Game getGame(String playerid) {
		for (Game checkGame : games) {
			if (checkGame.getId().equals(playerid)) {
				return checkGame;
			}
			}
		return null;
	}
	
	public synchronized GetGames getAnalystGames() {


		GetGames getgames = new GetGames();
		getgames.setGames(games);
		return getgames;
	}
}
