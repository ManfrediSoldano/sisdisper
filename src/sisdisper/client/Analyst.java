package sisdisper.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;

public class Analyst {

	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private static Boolean out = true;

	public static void main(String[] args) throws IOException {
		String name = "";
		System.out.println("Welcome to MMOG - Analyst");
		while (out) {
			System.out.println("Set your unique username:");
			name = br.readLine();
			System.out.println("Set your unique ip:");
			String ip = br.readLine();
			ClientToServerCommunication com = new ClientToServerCommunication();

			ResponseAddToGame add = com.newAlayticalPLayer(name, ip);
			if (add.getType() == ResponseAddToGame.Type.ACK) {
				System.out.println("Correctly log in");
				out=false;
			} else {
				System.out.println("This id already exists");

			}
			
			
		}
		Boolean exit = false;
		
		AnalyticalThread analisLive = new AnalyticalThread();
		analisLive.ID= name;
		analisLive.start();
		
		while(!exit) {
			System.out.println("Possible actions:");
			System.out.println("1: All games statistcs");
			System.out.println("2: A specific game statistic");
			System.out.println("3: Leaderboard");
			System.out.println("4: Exit");
			
			String action = br.readLine();
			
			switch (action) {
			case "1":
				allGamesStatistics();
				break;
			case "2":
				System.out.println("Which game?");
				String game = br.readLine();
				getGameStatistics(game);
				break;
			case "3":
				getLeaderboard();
				break;
			case "4":
				exit= true;
				analisLive.end= false;
				break;
			default:
				System.out.println("Wrong command.");
				break;
			}
			
		}
		
		

	}

	private static void getLeaderboard() {
		ClientToServerCommunication com = new ClientToServerCommunication();
		ArrayList<Player> players = new ArrayList<Player>();
		GetGames games = com.getAnalyticsOfPlayer();
		for(Game game : games.getGames()) {
			if(!game.live) {
				for (Player player:game.deadPlayers) {
					Boolean test = true;
					for(Player old: players) {
						if(old.getId().equals(player.getId())) {
							old.setPoint(old.getPoint()+player.getPoint());
							test=false;
						}
					}
					if(test) {
						players.add(player);
					}
				}
			}
		}
		System.out.println("------------Leaderboard--------------");

		for (Player player: players) {
		System.out.println("Player "+player.getId()+" has gained across all matchs "+player.getPoint()+" points");

		}
		
		System.out.println("----------end leaderboard------------");

	}

	private static void getGameStatistics(String gameID) {
		ClientToServerCommunication com = new ClientToServerCommunication();
		Game game = com.getAnalyticsOfGame(gameID);
		if(gameID!=null) {
		statFromGame(game);
		} else {
			System.out.println("No game found with that ID");

		}
	}

	private static void allGamesStatistics() {
		ClientToServerCommunication com = new ClientToServerCommunication();
		GetGames games = com.getGamesAnalytics();
		if(games.getGames().size()==0) {
			System.out.println("No games available yet.");
			return;

		}else {
			for (Game game: games.getGames()) {
				statFromGame(game);
			}
		}

	}

	private static void statFromGame(Game game) {
		System.out.println("---------------------------");
		System.out.println("GAME: "+game.getId());
		System.out.println("Status: "+game.live);
		System.out.println("Players online: "+game.getPlayerList().size());
		System.out.println("Players dead: "+game.deadPlayers.size());
		System.out.println("Players list: ");
		
		for(Player player: game.getPlayerList()) {
			System.out.println("       Player: "+player.getId()+" is live. ");
		}
		
		for(Player player: game.deadPlayers) {
			System.out.println("       Player: "+player.getId()+" is dead. With "+player.getPoint()+" points. He's a "+player.winner);
		}
		
		if(!game.live) {
			long time = ((game.end.getTime()/60000) - (game.start.getTime()/60000));
			System.out.println("Duration of this game "+time);
		}

	}

}
