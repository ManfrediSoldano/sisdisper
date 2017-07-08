package sisdisper.client;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.Token;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AddMeToGame;
import sisdisper.client.model.action.AddMeToYourClients;
import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.CLINewPlayer;
import sisdisper.client.model.action.CreateGame;
import sisdisper.client.model.action.DeleteMe;
import sisdisper.client.model.action.Deleted;
import sisdisper.client.model.action.GetGamesFromServer;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.MoveCom;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.client.model.action.NewPlayerResponse;
import sisdisper.client.model.action.PassToken;
import sisdisper.client.model.action.PlayerReceivedAPoint;
import sisdisper.client.model.action.ResponseMove;
import sisdisper.client.model.action.ReturnPosition;
import sisdisper.client.model.action.WelcomeNewPlayer;
import sisdisper.client.socket.Client;
import sisdisper.client.socket.Server;
import sisdisper.server.model.Coordinate;
import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;
import sisdisper.server.model.comunication.ResponseAddToGame.Type;
import java.util.concurrent.ThreadLocalRandom;


public class BufferController implements Runnable {
	private ClientToServerCommunication com = new ClientToServerCommunication();
	private Player me = new Player();
	private Token token = new Token();
	private Game mygame;
	private Boolean insideAGame = false;
	private Player next = new Player();
	private Player prev = new Player();
	private ArrayList<Client> clients = new ArrayList<Client>();

	public Player getMe() {
		return me;
	}

	private Server server = new Server();
	private Thread t;
	private Buffer buffer;
	private Boolean tokenBlocker = false;
	private int numberAck = 0;
	private ArrayList<Coordinate> coordinate = new ArrayList<Coordinate>();
	private ArrayList<ResponseMove> responseMoves = new ArrayList<ResponseMove>();
	private Boolean end = false;
	private int points = 0;
	private ArrayList<Deleted> deleted = new ArrayList<Deleted>();
	private Boolean block = false;

	CLI cli;

	public void start() {
		t = new Thread(this);
		t.start();

		buffer = new Buffer();
		buffer.setBufferController(this);

		cli = new CLI();
		cli.setBuffer(buffer);
		cli.start();

	}

	@Override
	public synchronized void run() {
		Action action;
		// Add me on a game

		while (!end) {

			if (!end) {

				try {
					wait();
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}

			synchronized (buffer) {
				action = buffer.getFirstAction();
			}
			
			// ###### AGGIUNTA AD UN GIOCO ######
			if (action instanceof AddMeToGame) {
				if (!insideAGame) {
					if (!me.getId().equals(null)) {
						AddToGame addme = new AddToGame();
						addme.setPlayer(me);
						addme.setGame(((AddMeToGame) action).getGame());
						ResponseAddToGame response = com.putMeOnaGame(addme);
						ResponseAddToGame.Type type = response.getType();

						if (type == ResponseAddToGame.Type.ACK) {
							mygame = ((ResponseAddToGame) response).getGame();
							cli.returnAdded(type);
							synchronized (cli) {
								cli.notify();
							}
							adviceOfMyPresence();

							insideAGame = true;
						}
						if (type != ResponseAddToGame.Type.ACK) {
							cli.returnAdded(type);
							synchronized (cli) {
								cli.notify();
							}
						}
					} else {
						//// AGGIUNGERE RITORNO ERRORE////
					}
				} else {
					cli.returnAdded(Type.AREADY_EXIST);
					synchronized (cli) {
						cli.notifyAll();
					}
				}
			}

			// ###### RITORNO DEI GIOCHI ######
			else if (action instanceof GetGamesFromServer) {
				GetGames games = com.getGamesFromServer();
				cli.getGames(games);
				synchronized (cli) {
					cli.notifyAll();
				}
			}

			// ###### NUOVO GIOCO ######
			else if (action instanceof CreateGame) {
				if (!insideAGame) {
					if (!me.getId().equals(null)) {
						Game game = ((CreateGame) action).getGame();
						game.addPlayer(me);
						String returnString = com.createNewGame(game);
						if (returnString.equals("ack")) {
							mygame = game;
							cli.returnCreated("Game correctly created", false);
							insideAGame = true;
							next = me;
							prev = me;
							receivedToken();
						}

						else {
							cli.returnCreated(returnString, true);
						}
						synchronized (cli) {
							cli.notify();
						}
					}
				} else {
					cli.returnCreated("WTF_Error#1: You are already inside a game! ", false);
					synchronized (cli) {
						cli.notify();
					}
				}
			}
			// ###### ELIMINAMI DAL GIOCO ######

			else if (action instanceof DeleteMe) {
				if (!token.getIsMine()) {

					com.deleteMe(me.getId(), mygame.getId());

				} else {
					//// AGGIUNGERE RITORNO ERRORE////

				}
			}

			// ###### RICEVUTO UN ACK ######

			else if (action instanceof Ack) {
				System.out.println("###BUFFERController## Ricevuto ACK #####");
				if (tokenBlocker) {
					System.out.println("#BUFFERCONTROLLER## Aggiunto ACK #####");
					numberAck++;
					checKAll();
				}
			}
			// ###### CREA NUOVO GIOCATORE ######
			else if (action instanceof CLINewPlayer) {
				me = ((CLINewPlayer) action).getPlayer();
				server.setPlayer(me);
				server.start();
				synchronized (cli) {
					cli.notify();
				}

			}

			// ###### CONTROLLA LA RISPOSTA ALL'AGGIUNTA DI UN GIOCATORE ######
			else if (action instanceof WelcomeNewPlayer) {
				System.out.println("###BUFFERController## WELCOME NEW PLAYER #####");
				
				welcomeNewPlayer(action);

			}
			// ###### RICEVUTO l?ACK CHE INDICA DI AVER PROCESSATO CORRETTAMENTE IL NUOVO CLIENT DAL GIOCATORE TOKEN ######
			else if (action instanceof AddMeToYourClients) {
				System.out.println("###BUFFERController## RICEVUTA RICHIESTA CHE MI CONFERMA CHE IL TIPO CON IL TOKEN ORA E' INSERITO TRA I MIEI CLIENT #####");
				NewPlayerResponse newp = new NewPlayerResponse();
				newp.setPlayer(me);
				try {
					server.sendMessageToPlayer(((AddMeToYourClients) action).getPlayer(), newp);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			// ###### RICEVUTO UN TOKEN ######
			else if (action instanceof PassToken) {
				receivedToken();
			}
			
			// ###### RICEVUTO UNA POSIZIONE ######
			else if (action instanceof AskPosition) {
				ReturnPosition rtn = new ReturnPosition();
				rtn.setCoordinate(me.getCoordinate());
				try {
					((AskPosition) action).getClient().send(rtn);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}

			}
			// ###### RICEVUTA RICHIESTA DI POSIZIONE ######
			else if (action instanceof ReturnPosition) {
				coordinate.add(((ReturnPosition) action).getCoordinate());
				if (coordinate.size() == mygame.getPlayerList().size() - 1) {
					Boolean ok = true;
					while (ok) {
						int x = ThreadLocalRandom.current().nextInt(0, mygame.getDimension());
						int y = ThreadLocalRandom.current().nextInt(0, mygame.getDimension());
						for (Coordinate coordinata : coordinate) {
							if (coordinata != null) {
								if (coordinata.getX() == x && coordinata.getY() == y) {
									ok = false;
								}
							}
						}
						if (ok) {
							ok = false;
							Coordinate coordinata_player = new Coordinate();
							coordinata_player.setX(x);
							coordinata_player.setY(y);
							me.setCoordinate(coordinata_player);
							tokenBlocker = false;
							cli.returnMove("New position--> x: " + me.getCoordinate().getX() + " and y: "
									+ me.getCoordinate().getY());
							receivedToken();

						} else {
							ok = true;
						}
					}

				}
			}
			// ###### RESPONDING TO A MOVE REQUEST ######
			else if (action instanceof MoveCom) {
				if (((MoveCom) action).getCoordinate().equal(me.getCoordinate())) {
					ResponseMove response = new ResponseMove();
					response.setPlayer(me);
					response.setResponse(ResponseMove.Response.KILLED_ME);
					cli.returnMove("Killed by" + ((MoveCom) action).getPlayer().getId());
					try {
						try {
							((MoveCom) action).getClient().send(response);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
						DeleteMe deleteme = new DeleteMe();
						deleteme.setPlayer(me);
						deleteme.setNext(next);
						deleteme.setPrev(prev);
						server.sendMessageToAll(deleteme);
						com.deleteMe(me.getId(), mygame.getId());
						end = true;

					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				} else {
					ResponseMove response = new ResponseMove();
					response.setPlayer(me);
					response.setResponse(ResponseMove.Response.ACK);
					try {
						((MoveCom) action).getClient().send(response);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			}

			// ###### RESPONSE FROM A MOVE FROM ALL OTHER PEERS ######
			else if (action instanceof ResponseMove) {
				responseMoves.add(((ResponseMove) action));
				if (responseMoves.size() == mygame.getPlayerList().size() - 1) {
					for (ResponseMove responseMove : responseMoves) {
						if (responseMove.getResponse() == ResponseMove.Response.KILLED_ME) {
							points++;
							cli.returnMove("Killed: " + responseMove.getPlayer().getId());
							PlayerReceivedAPoint point = new PlayerReceivedAPoint();
							point.setPlayer(me);
							point.setPoints(points);

							try {
								server.sendMessageToAll(point);
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}

						}
					}
					cli.returnMove("Move completed");
					cli.returnMove("Now you have an amount of: " + points);
					synchronized (cli) {
						cli.notify();
					}
					tokenBlocker = false;
					receivedToken();
				}

			} else if (action instanceof PlayerReceivedAPoint) {
				cli.returnMove("Player " + ((PlayerReceivedAPoint) action).getPlayer().getId()
						+ " has just gained a point, now he have " + ((PlayerReceivedAPoint) action).getPoints()
						+ " points");
				cli.returnMove("You have " + points + " points");
			}

			// ###### ASKED DELETING ######
			else if (action instanceof DeleteMe) {
				receivedNewDeleteContact((DeleteMe) action);
			} 
			// ###### CHECK IF EVERYONE HAVE DELETE IT ######
			else if (action instanceof Deleted) {
				int test = 0;
				Boolean otherone = false;
				deleted.add(((Deleted) action));
				for (Deleted del : deleted) {
					if (del.getPlayer().getId() == ((Deleted) action).getPlayer().getId()) {
						test++;
					} else {
						otherone = true;
					}
				}
				if (test == mygame.getPlayerList().size() - 1 && !otherone) {
					block = false;
				}

			} else {
				if (action != null) {
					System.out.println("Errore: non ho elaborato: " + action.toString());
				}
			}

		}
	}

	private void checKAll() {
		if (numberAck == mygame.getPlayerList().size() - 1) {
			System.out.println("##BUFFERcontroller### ACK: ALL CHECKED #####");
			tokenBlocker = false;
			try {
				server.sendMessageToAll(new Ack());

			} catch (JsonProcessingException e) {

				e.printStackTrace();
			}
			receivedToken();

		}

	}

	private void welcomeNewPlayer(Action action) {
		
		if (!((WelcomeNewPlayer) action).getNewPlayer().getId().equals(me.getId())) {
			System.out.println("##BUFFERcontroller### RECEIVED A NEW ONE #####");
			Client client = new Client(((WelcomeNewPlayer) action).getNewPlayer());
			client.start();
			AddMeToYourClients addMeToYourClients = new AddMeToYourClients();
			addMeToYourClients.setPlayer(me);

			try {
				synchronized(client){
				client.send(addMeToYourClients);
				}
			} catch (JsonProcessingException e1) {

				e1.printStackTrace();
			}
			
			clients.add(client);

			ArrayList<Player> ply = mygame.getPlayerList();
			ply.add(((WelcomeNewPlayer) action).getNewPlayer());
			
			mygame.setPlayerList(ply);
			
			try {
				server.sendMessageToPlayer(((WelcomeNewPlayer) action).getSender(), new Ack());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("##BUFFERcontroller### I WAS ADDED #####");
			next = ((WelcomeNewPlayer) action).getNext();
			prev = ((WelcomeNewPlayer) action).getPrev();
			try {
				server.sendMessageToPlayer(((WelcomeNewPlayer) action).getSender(), new Ack());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		if (((WelcomeNewPlayer) action).getSender().getId().equals(next.getId())
				&& !((WelcomeNewPlayer) action).getNewPlayer().getId().equals(me.getId())) {
			System.out.println("##BUFFERcontroller### I'M THE NEXT ONE #####");
			next = ((WelcomeNewPlayer) action).getNext();
			try {
				server.sendMessageToPlayer(((WelcomeNewPlayer) action).getSender(), new Ack());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

	}

	private void adviceOfMyPresence() {
		for (Player player : mygame.getPlayerList()) {
			if (!player.getId().equals(me.getId())) {
				Client client = new Client(player);
				clients.add(client);
				client.start();
			}
		}
		for (Client client : clients) {
			NewPlayer newp = new NewPlayer();
			newp.setPlayer(me);
			if (client != null) {
				try {
					client.send(newp);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void receivedNewPlayerContact(NewPlayer newPlayer) {
		System.out.println("##BUFFERcontroller### receivedNewPlayerContact #####");
		Player player = newPlayer.getPlayer();
		Client client = new Client(player);
		client.start();
		AddMeToYourClients addMeToYourClients = new AddMeToYourClients();
		addMeToYourClients.setPlayer(me);

		try {
			synchronized(client){
				client.send(addMeToYourClients);
				}
		} catch (JsonProcessingException e1) {

			e1.printStackTrace();
		}
		clients.add(client);
		System.out.println("##BUFFERcontroller### Client informed #####");
	}
	
	private void newPLyaerConfirmedToHaveMyClientHandler(NewPlayerResponse newPlayer){
		Player player = newPlayer.getPlayer();
		WelcomeNewPlayer welcomeNew = new WelcomeNewPlayer();
		WelcomeNewPlayer welcomePrev = new WelcomeNewPlayer();
		
		welcomeNew.setSender(me);
		welcomePrev.setSender(me);
		if (prev == me && next == me) {
			welcomeNew.setNext(me);
			welcomeNew.setPrev(me);
			welcomeNew.setNewPlayer(player);
			next = player;
			prev = player;

			try {
				System.out.println("##BUFFERcontroller### I WAS ALONE ||SENDING to:  "+player.getId()+" #####");
				server.sendMessageToPlayer(player, welcomeNew);
				System.out.println("##BUFFERcontroller### I WAS ALONE ||SENT #####");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("##BUFFERcontroller### I DON'T WASN'T ALONE #####");
			welcomeNew.setNext(me);
			welcomeNew.setPrev(prev);
			welcomeNew.setNewPlayer(player);
			welcomePrev.setNext(player);
			welcomePrev.setPrev(prev);
			welcomePrev.setNewPlayer(player);
			welcomePrev.setSender(me);

			prev = player;
			try {
				server.sendMessageToPlayer(player, welcomeNew);
				server.sendMessageToPlayer(prev, welcomePrev);
				WelcomeNewPlayer notifyall = new WelcomeNewPlayer();
				notifyall.setNewPlayer(player);
				for (Player player_to_all : mygame.getPlayerList()) {
					if (player_to_all != player && player_to_all != prev) {
						server.sendMessageToPlayer(player_to_all, notifyall);
					}
				}
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void receivedNewDeleteContact(DeleteMe deletePlayer) {
		block = true;
		mygame.removePlayer(deletePlayer.getPlayer().getId());
		if (deletePlayer.getNext().getId().equals(me.getId())) {
			prev = deletePlayer.getPrev();
		}
		if (deletePlayer.getPrev().getId().equals(me.getId())) {
			next = deletePlayer.getNext();
		}
		try {
			server.sendMessageToAll(new Deleted());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private void receivedToken() {
		Boolean first = true;
		ArrayList<Action> listactions;
		while (mygame.getPlayerList().size() == 1 || first) {

			if (!block) {
				synchronized (buffer) {
					listactions = buffer.getAllActions();
				}
				for (Action actioninside : listactions) {

					if (actioninside instanceof NewPlayer) {
						synchronized(buffer){
							buffer.deleteAction(actioninside);
							}
						receivedNewPlayerContact((NewPlayer) actioninside);
						
						
					} else if(actioninside instanceof NewPlayerResponse){
						synchronized(buffer){
							buffer.deleteAction(actioninside);
							}
						tokenBlocker = true;
						newPLyaerConfirmedToHaveMyClientHandler((NewPlayerResponse) actioninside);
						return;
					}
					

				}
				first = false;
				if (me.getCoordinate() != null) {
					Action action;
					synchronized (buffer) {
						action = buffer.getFirstActionThatNeedAToken();
					}
					// ###### MOVE #####
					if (action instanceof MoveCLI) {

						Boolean done = false;

						// ###### UP #####
						if (((MoveCLI) action).getWhere() == MoveCLI.Where.UP) {

							if (me.getCoordinate().getY() + 1 < mygame.getDimension()) {
								Coordinate coordinate = me.getCoordinate();
								coordinate.setY(me.getCoordinate().getY() + 1);
								me.setCoordinate(coordinate);
								done = true;
							}
						}
						// ###### DOWN #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.DOWN) {
							if (me.getCoordinate().getY() - 1 > 0) {
								Coordinate coordinate = me.getCoordinate();
								coordinate.setY(me.getCoordinate().getY() - 1);
								me.setCoordinate(coordinate);
								done = true;
							}
						} // ###### RIGHT #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.RIGHT) {
							if (me.getCoordinate().getX() + 1 < mygame.getDimension()) {
								Coordinate coordinate = me.getCoordinate();
								coordinate.setX(me.getCoordinate().getX() + 1);
								me.setCoordinate(coordinate);
								done = true;
							}
						}
						// ###### LEFT #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.LEFT) {
							if (me.getCoordinate().getX() - 1 > 0) {
								Coordinate coordinate = me.getCoordinate();
								coordinate.setX(me.getCoordinate().getX() + 1);
								me.setCoordinate(coordinate);
								done = true;
							}
						}

						if (done) {

							if (mygame.getPlayerList().size() != 1) {
								MoveCom com = new MoveCom();
								com.setPlayer(me);
								com.setCoordinate(me.getCoordinate());
								try {
									server.sendMessageToAll(com);
								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}
								if (!(mygame.getPlayerList().size() == 1)) {
									tokenBlocker = true;

								}
							} else {
								cli.returnMove("New position--> x: " + me.getCoordinate().getX() + " and y: "
										+ me.getCoordinate().getY());
								synchronized (cli) {
									cli.notify();
								}

							}
						}
						// ###### BOMB #####
					} else if (action instanceof Bomb) {

					}
				} else {
					try {

						if (mygame.getPlayerList().size() != 1) {
							server.sendMessageToAll(new AskPosition());
							tokenBlocker = true;
						} else {
							int x = ThreadLocalRandom.current().nextInt(0, mygame.getDimension());
							int y = ThreadLocalRandom.current().nextInt(0, mygame.getDimension());
							Coordinate coordinata_player = new Coordinate();
							coordinata_player.setX(x);
							coordinata_player.setY(y);
							me.setCoordinate(coordinata_player);
							cli.returnMove("Position --> x: " + me.getCoordinate().getX() + " and y: "
									+ me.getCoordinate().getY());
							synchronized (cli) {
								cli.notify();
							}
						}
					} catch (JsonProcessingException e) {
						e.printStackTrace();

					}
				}
				if (!tokenBlocker) {
					if (mygame.getPlayerList().size() != 1) {
						try {
							server.sendMessageToPlayer(next, new PassToken());
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
					}
				}
			}

			else {

				receivedToken();

			}
		}
	}

}
