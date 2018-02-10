package sisdisper.client.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import sisdisper.client.AccelerometerManager;
import sisdisper.client.BombObservable;
import sisdisper.client.model.Alive;
import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.AddMeToGame;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.CLINewPlayer;
import sisdisper.client.model.action.CreateGame;
import sisdisper.client.model.action.GetGamesFromServer;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.PassToken;
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
	public static final String ANSI_RED = "\u001B[31m";
	UserObservable observable = null;
	GUI gui = null;

	public UserObservable getObservable() {
		return observable;
	}

	public void setObservable(UserObservable observable) {
		this.observable = observable;
	}

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

			String address;
			int port;
			// Imposto il giocatore
			System.err.println("Welcome to MMOG");
			System.err.println("Set your unique username:");
			String name = br.readLine();
			if (name.equals("ttest")) {
				name = "tTest";
				address = "localhost";
				port = 334;
			} else if (name.equals("ptest")) {
				name = "pTest";
				address = "localhost";
				port = 335;
			} else if (name.equals("ytest")) {
				name = "yTest";
				address = "localhost";
				port = 336;
			} else if (name.equals("utest")) {
				name = "uTest";
				address = "localhost";
				port = 337;
			} else if (name.equals("itest")) {
				name = "iTest";
				address = "localhost";
				port = 338;
			} else if (name.equals("otest")) {
				name = "oTest";
				address = "localhost";
				port = 339;
			} else {
				System.err.println("Set your ip address:");
				address = br.readLine();
				port = getPort();
			}
			Player player = new Player();
			player.setId(name);
			player.setIp(address);
			player.setPort(port);
			CLINewPlayer newplayer = new CLINewPlayer();
			newplayer.setPlayer(player);

			synchronized (buffer) {
				observable.setActionChanged(newplayer);

			}

			/// Chiedo cosa vuole fare dopo
			System.err.println("Thanks for set your username, " + player.getId());
			System.err.println();
			System.err.println("Possible Actions:");
			System.err.println("1 GetGames //return all active games from server");
			System.err.println("2 AddMeOnAGame //Get inside a game");
			System.err.println("3 CreateANewGame //Create a new game");
			System.err.println("Help //Create a new game");
			System.err.println("All required inforamtion will be later asked");
			gui = new GUI();

			while (notInsideAGame) {
				try {

					String action = br.readLine();
					if (action.equals("GetGames") || action.equals("1")) {
						observable.setActionChanged(new GetGamesFromServer());

						synchronized (this) {
							wait();
							System.err.println("Fuori dal wait");
						}

					} else if (action.equals("AddMeOnAGame") || action.equals("2")) {
						System.err.println("Which game?");
						String gameid = br.readLine();
						Game gametobeadded = new Game();
						gametobeadded.setId(gameid);

						AddMeToGame add = new AddMeToGame();
						add.setGame(gametobeadded);
						observable.setActionChanged(add);

						synchronized (this) {
							wait();
						}

					} else if (action.equals("CreateANewGame") || action.equals("3")) {
						System.err.println("How do you want to call the game?");
						String gameid = br.readLine();
						System.err.println("Set a dimension:");
						int dimension = Integer.parseInt(br.readLine());
						// Temp Object
						Game gameToBeCreated = new Game();
						gameToBeCreated.setId(gameid);
						gameToBeCreated.setDimension(dimension);
						// Action object
						CreateGame create = new CreateGame();
						create.setGame(gameToBeCreated);
						observable.setActionChanged(create);

						synchronized (this) {
							wait();
						}

					} else if (action.equals("Help")) {
						System.err.println("Possible Actions:");
						System.err.println("GetGames //return all active games from server");
						System.err.println("AddMeOnAGame //Get inside a game");
						System.err.println("CreateANewGame //Create a new game");
						System.err.println("All required inforamtion will be later asked");
					} else {
						System.err.println("Wrong command");
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// GUI STARTING POINT
			AccelerometerManager acc = new AccelerometerManager();

			BombObservable bombobservable = new BombObservable();
			acc.setObservable(bombobservable);
			bombobservable.addObserver(buffer);
			acc.start();

			UserObservable uo = new UserObservable();
			gui.setObservable(uo);
			uo.addObserver(buffer);
			gui.startGUI();

			System.err.println();
			System.err.println();
			System.err.println("Possible Actions inside the game:");
			System.err.println("Move up (you can also use 1)");
			System.err.println("Move down (you can also use 2)");
			System.err.println("Move left (you can also use 3)");
			System.err.println("Move right (you can also use 4)");
			System.err.println("Bomb (you can also use 5)");

			while (lock) {
				String receivedCommand = br.readLine();

				if (receivedCommand.equals("Bomb") || receivedCommand.equals("5")) {
					Bomb bomb = new Bomb();
					observable.setActionChanged(bomb);

				} else if (receivedCommand.equals("Move up") || receivedCommand.equals("1")) {
					MoveCLI movecli = new MoveCLI();
					movecli.setWhere(MoveCLI.Where.UP);
					observable.setActionChanged(movecli);

				} else if (receivedCommand.equals("Move down") || receivedCommand.equals("2")) {
					MoveCLI movecli = new MoveCLI();
					movecli.setWhere(MoveCLI.Where.DOWN);
					observable.setActionChanged(movecli);

				} else if (receivedCommand.equals("Move left") || receivedCommand.equals("3")) {
					MoveCLI movecli = new MoveCLI();
					movecli.setWhere(MoveCLI.Where.LEFT);
					observable.setActionChanged(movecli);

				} else if (receivedCommand.equals("Move right") || receivedCommand.equals("4")) {
					MoveCLI movecli = new MoveCLI();
					movecli.setWhere(MoveCLI.Where.RIGHT);
					observable.setActionChanged(movecli);

				} else {
					System.err.println("Wrong command");
				}
				if(!Alive.alive) {
					return;
				}

			}

		} catch (IOException e) {
		}

	}

	private int getPort() {
		int port;
		System.err.println("Set your port number:");
		while (true) {
			try {
				port = Integer.parseInt(br.readLine());
				return port;
			} catch (Exception e) {
				System.err.println("It's not a number, try again.");

			}
		}

	}

	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;

	}

	public void getGames(GetGames games) {

		for (Game game : games.getGames()) {
			System.err.println("_____________________");
			System.err.println("Game: " + game.getId() + " Dimension: " + game.getDimension());
			System.err.println("Players of the game " + game.getId() + ": ");
			for (Player player : game.getPlayerList()) {
				System.err.println("-" + player.getId());
			}
		}

	}

	public void returnAdded(ResponseAddToGame response) {
		if (response.getType() == ResponseAddToGame.Type.ACK) {
			System.err.println("Correcly added to the game.");
			gui.setDim(response.getGame().getDimension());
			notInsideAGame = false;
		} else {
			System.err.println("Unfortunately we wasn't able to add you to the game : " + response.toString());
		}
	}

	public void returnCreated(String string, Boolean inside, Boolean token) {

		System.err.println(string);
		notInsideAGame = inside;
		if (token) {
			PassToken actionToken = new PassToken();
			observable.setActionChanged(actionToken);
		}

	}

	public void returnMove(String string) {
		System.err.println(string);
		if (gui.live) {
			gui.addText(string);
		}
	}

	public void returnBomb(String string) {
		System.err.println(string);
		if (gui.live) {
			gui.addText(string);
		}

	}

	public void publishString(String string) {
		System.err.println(string);
		if (gui.live) {
			gui.addText(string);
		}
	}

	public void move(int x, int y) {
		if (gui.live) {
		
			gui.move(x, y);
			
		}
	}

	public void bomb(int x, int y) {
		if (gui.live) {
			gui.bomb(x, y);
		}
	}

	public void explodedBomb(int x, int y) {
		if (gui.live) {
			gui.explodedBomb(x, y);
		}
	}

}
