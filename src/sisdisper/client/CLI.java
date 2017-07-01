package sisdisper.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AddMeToGame;
import sisdisper.client.model.action.CreateGame;
import sisdisper.client.model.action.GetGamesFromServer;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;
import sisdisper.server.model.comunication.ResponseAddToGame.Type;

public class CLI implements Runnable {

	private Thread t;
	Buffer buffer = new Buffer();
	Boolean notInsideAGame = true;
	Boolean lock = true;
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	public Thread getT() {
		return t;
	}

	public void start() {
		t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {
			// Imposto il giocatore
			System.out.println("Welcome to MMOG");
			System.out.println("Set your unique username:");
			String name = br.readLine();
			System.out.println("Set your ip address:");
			String address = br.readLine();
			System.out.println("Set your port number:");
			int port = getPort();
			Player player = new Player();
			player.setId(name);
			player.setIp(address);
			player.setPort(port);
			NewPlayer newplayer = new NewPlayer();
			newplayer.setPlayer(player);
			buffer.addAction(newplayer);

			/// CHiedo cosa vuole fare dopo
			System.out.println("Thanks for set your username, " + player.getId());
			System.out.println();
			System.out.println("Possible Actions:");
			System.out.println("GetGames //return all active games from server");
			System.out.println("AddMeOnAGame //Get inside a game");
			System.out.println("CreateANewGame //Create a new game");
			System.out.println("Help //Create a new game");			
			System.out.println("All required inforamtion will be later asked");

			while (notInsideAGame) {
				try {

					String action = br.readLine();
					if (action.equals("GetGames")) {

						buffer.addAction(new GetGamesFromServer());
						
						synchronized (this) {
							wait();
						}

					} else if (action.equals("AddMeOnAGame")) {
						System.out.println("Which game?");
						String gameid = br.readLine();
						Game gametobeadded = new Game();
						gametobeadded.setId(gameid);

						AddMeToGame add = new AddMeToGame();
						add.setGame(gametobeadded);

						buffer.addAction(add);

						synchronized (this) {
							wait();
						}

					} else if (action.equals("CreateANewGame")) {
						System.out.println("How do you want to call the game?");
						String gameid = br.readLine();
						System.out.println("Set a dimension:");
						int dimension = Integer.parseInt(br.readLine());
						// Temp Object
						Game gameToBeCreated = new Game();
						gameToBeCreated.setId(gameid);
						gameToBeCreated.setDimension(dimension);
						// Action object
						CreateGame create = new CreateGame();
						create.setGame(gameToBeCreated);
						buffer.addAction(create);
						synchronized (this) {
							wait();
						}

					} else if (action.equals("Help")) {
						System.out.println("Possible Actions:");
						System.out.println("GetGames //return all active games from server");
						System.out.println("AddMeOnAGame //Get inside a game");
						System.out.println("CreateANewGame //Create a new game");
						System.out.println("All required inforamtion will be later asked");
					} else {
						System.out.println("Wrong command");
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			System.out.println();
			System.out.println();
			System.out.println("Possible Actions inside the game:");
			System.out.println("Move up (you can also use 1)");
			System.out.println("Move down (you can also use 2)");
			System.out.println("Move left (you can also use 3)");
			System.out.println("Move right (you can also use 4)");			
			System.out.println("Bomb (you can also use 5)");
			
			while (lock) {
				String receivedCommand = br.readLine();
				
				if(receivedCommand.equals("Bomb")||receivedCommand.equals("5")){
					
				} else if(receivedCommand.equals("Move up")||receivedCommand.equals("1")){
					
				} else if(receivedCommand.equals("Move down")||receivedCommand.equals("2")){
					
				} else if(receivedCommand.equals("Move left")||receivedCommand.equals("3")){
					
				} else if(receivedCommand.equals("Move right")||receivedCommand.equals("4")){
					
				} else {
					System.out.println("Wrong command");
				}
				
			}
			
			
			

		} catch (IOException e) {
		}

	}

	private int getPort() {
		int port;
		System.out.println("Set your port number:");
		while (true) {
			try {
				port = Integer.parseInt(br.readLine());
				return port;
			} catch (Exception e) {
				System.out.println("It's not a number, try again.");

			}
		}

	}

	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;

	}

	public void getGames(GetGames games) {

		for (Game game : games.getGames()) {
			System.out.println("_____________________");
			System.out.println("Game: " + game.getId() + " Dimension: " + game.getDimension());
			System.out.println("Players of the game " + game.getId() + ": ");
			for (Player player : game.getPlayerList()) {
				System.out.println("-" + player.getId());
			}
		}

	}

	public void returnAdded(Type type) {
		if (type == ResponseAddToGame.Type.ACK) {
			System.out.println("Correcly added to the game.");
			notInsideAGame = false;
		} else {
			System.out.println("Unfortunately we wasn't able to add you to the game : " + type.toString());
		}
	}
	
	public void returnCreated(String string, Boolean inside){
		
		System.out.println(string);
		notInsideAGame = inside;
	}




}