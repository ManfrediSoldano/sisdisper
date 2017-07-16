package sisdisper.client;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.AckAfterBomb;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AddBomb;
import sisdisper.client.model.action.AddMeToGame;
import sisdisper.client.model.action.AddMeToYourClients;
import sisdisper.client.model.action.AddMeToYourClients_NotPassToBuffer;
import sisdisper.client.model.action.AdviceBomb;
import sisdisper.client.model.action.AfterBombCheck;
import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.CLINewPlayer;
import sisdisper.client.model.action.CreateGame;
import sisdisper.client.model.action.DeleteMe;
import sisdisper.client.model.action.Deleted;
import sisdisper.client.model.action.ExplodingBomb;
import sisdisper.client.model.action.GetGamesFromServer;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.MoveCom;
import sisdisper.client.model.action.NewPlayer;
import sisdisper.client.model.action.NewPlayerResponse;
import sisdisper.client.model.action.PassToken;
import sisdisper.client.model.action.PlayerReceivedAPoint;
import sisdisper.client.model.action.ResponseMove;
import sisdisper.client.model.action.ReturnPosition;
import sisdisper.client.model.action.UpdateYourNextPrev;
import sisdisper.client.model.action.WelcomeNewPlayer;
import sisdisper.client.model.action.Winner;
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
	private Game mygame;
	private Boolean insideAGame = false;
	private Player next = new Player();
	private Player prev = new Player();
	private ArrayList<Client> clients = new ArrayList<Client>();

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
	private Boolean addingAPlayer = false;
	public Boolean imFree = true;
	private Boolean test_something_changed = false;
	private ArrayList<AckAfterBomb> ack = new ArrayList<AckAfterBomb>();
	private UpdateYourNextPrev update = new UpdateYourNextPrev();
	CLI cli;

	private int winpoint = 3;

	public void start() {
		t = new Thread(this);
		t.start();

		buffer = new Buffer();
		buffer.setBufferController(this);

		cli = new CLI();
		cli.setBuffer(buffer);
		cli.start();

	}

	public Player getMe() {
		return me;
	}

	@Override
	public void run() {
		ArrayList<Action> actions;
		Action action;
		// Add me on a game

		while (!end) {

			if (!end) {

				try {

					actions = Buffer.getAllActions();

					if (actions.size() == 0) {
						
						
						synchronized (this) {
							imFree = true;
							System.out.println("###BUFFERController## GOING IN WAIT #####");
							synchronized(buffer){
							buffer.notify();
							}
							wait();
							
							imFree = false;

							System.out.println("###BUFFERController## WAKE UP #####");
						}
					}
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}

			System.out.println("###BUFFERController## GETTING FIRST ACTION #####");

			action = Buffer.getFirstAction();
			System.out.println("###BUFFERController## AFTER FIRST ACTION #####");

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
							next = me;
							prev = me;
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

			else if (action instanceof Winner) {
				cli.returnMove(((Winner) action).getPlayer().getId() + " HAS WON THIS MATCH!");
				com.deleteMe(me.getId(), mygame.getId());
				for (Client client : clients) {
					client.end = true;
				}
			}
			// ###### ELIMINAMI DAL GIOCO ######

			// ###### RICEVUTO UN ACK ######

			else if (action instanceof Ack) {
				if (((Ack) action).getPlayer() != null) {
					if (((Ack) action).getPlayer().getId().equals(me.getId())) {
						numberAck++;
						if (numberAck == (mygame.getPlayerList().size() - 2)) {
							try {
								System.out.println("#BUFFERCONTROLLER## ALL ACK PROCESSED #####");

								server.sendMessageToPlayer(next, new Ack());
								numberAck = 0;
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (tokenBlocker) {
					System.out.println("#BUFFERCONTROLLER## Aggiunto ACK #####");

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

			// ###### CONTROLLA LA RISPOSTA ALL'AGGIUNTA DI UN GIOCATORE
			// ######
			else if (action instanceof WelcomeNewPlayer) {
				System.out.println("###BUFFERController## WELCOME NEW PLAYER #####");

				welcomeNewPlayer(action);

			}
			// ###### RICEVUTO l?ACK CHE INDICA DI AVER PROCESSATO
			// CORRETTAMENTE
			// IL NUOVO CLIENT DAL GIOCATORE TOKEN ######
			else if (action instanceof AddMeToYourClients) {
				System.out.println(
						"###BUFFERController## RICEVUTA RICHIESTA CHE MI CONFERMA CHE IL TIPO CON IL TOKEN ORA E' INSERITO TRA I MIEI CLIENT #####");
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
				System.out.println("###BUFFERController## POSITION REQUESTED BY "
						+ ((AskPosition) action).getClient().getId() + " #####");

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
									System.out.println("###BUFFERController## POSIZIONE GIA' OCCUPATA #####");
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
							cli.returnMove("Zone: " + me.getArea(mygame.getDimension()));
							receivedToken();

						} else {
							ok = true;
						}
					}

				}
			}

			// ###### RESPONDING TO A MOVE REQUEST ######
			else if (action instanceof MoveCom) {
				System.out.println("###BUFFERController## RECEIVED A MOVE REQUEST FROM "
						+ ((MoveCom) action).getPlayer().getId() + " #####");
				if (((MoveCom) action).getCoordinate().equal(me.getCoordinate())) {

					ResponseMove response = new ResponseMove();
					response.setPlayer(me);
					response.setNext(next);
					response.setPrev(prev);
					System.out.println("###BUFFERController## My next: " + next + " my prev: " + prev + " #####");
					response.setResponse(ResponseMove.Response.KILLED_ME);

					cli.returnMove("Killed by" + ((MoveCom) action).getPlayer().getId());

					try {
						((MoveCom) action).getClient().send(response);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}

					com.deleteMe(me.getId(), mygame.getId());
					end = true;
					for (Client client : clients) {
						client.end = true;
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
				System.out.println("####BUFFERController## RECEIVED RESPONSE FROM "
						+ ((ResponseMove) action).getPlayer().getId() + " #### ");
				Boolean killed = false;
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

							checkIfImAWinner();

							killed = true;
							tokenBlocker = true;

							mygame.removePlayer(responseMove.getPlayer().getId());

							if (mygame.getPlayerList().size() != 1) {
								DeleteMe deleteme = new DeleteMe();
								deleteme.setPlayer(responseMove.getPlayer());
								deleteme.setNext(responseMove.getNext());
								deleteme.setPrev(responseMove.getPrev());
								deleteme.setSender(me);

								if (responseMove.getNext().getId().equals(me.getId())) {
									prev = responseMove.getPrev();
									System.out.println("####BUFFERController## NEW PREV: " + prev.getId() + " ####");

								}
								if (responseMove.getPrev().getId().equals(me.getId())) {
									next = responseMove.getNext();
									System.out.println("####BUFFERController## NEW NEXT: " + next.getId() + " ####");

								}

								try {
									server.sendMessageToAll(deleteme);
								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}
							} else {
								killed = false;
								next = me;
								prev = me;
							}

						}
					}

					cli.returnMove("New position: X: " + me.getCoordinate().getX() + "Y:" + me.getCoordinate().getY());
					cli.returnMove("Now you have an amount of: " + points);
					cli.returnMove("Zone: " + me.getArea(mygame.getDimension()));
					responseMoves = new ArrayList<ResponseMove>();
					synchronized (cli) {
						cli.notify();
					}
					if (!killed) {
						tokenBlocker = false;
						cli.returnMove("Move completed");
						receivedToken();
					}

				}

			} else if (action instanceof PlayerReceivedAPoint) {
				cli.returnMove("Player " + ((PlayerReceivedAPoint) action).getPlayer().getId()
						+ " has just gained a point, now he have " + ((PlayerReceivedAPoint) action).getPoints()
						+ " points");
				cli.returnMove("You have " + points + " points");

				// ADD BOMB
			} else if (action instanceof AdviceBomb) {
				cli.returnBomb("In 5 second one bomb sent by " + ((AdviceBomb) action).player.getId()
						+ " will explode in the " + ((AdviceBomb) action).area + " area.");
				cli.returnBomb("You're currently in the " + me.getArea(mygame.getDimension()) + " area");

			}

			else if (action instanceof UpdateYourNextPrev) {
				updateNextPrev((UpdateYourNextPrev) action);

			} else if (action instanceof AckAfterBomb) {
				System.out.println("####BUFFERController## RECEIVED AckAfterBomb ####");

				ack.add((AckAfterBomb) action);

				if (ack.size() == mygame.getPlayerList().size() - 1) {
					System.out.println("####BUFFERController## INside update ####");
					updateNextPrev(update);
					ack = new ArrayList<AckAfterBomb>();
				}

			}
			// AFTER BOMBM CHECK
			else if (action instanceof AfterBombCheck) {

				System.out.println("####BUFFERController## Token Blocker" + tokenBlocker + " ####");

				AfterBombCheck afc = (AfterBombCheck) action;

				if (tokenBlocker) {
					ArrayList<Player> alive = new ArrayList<Player>();
					for (Player player : afc.getList()) {

						alive.add(player);

					}
					System.out.println("####BUFFERController## Update your nextprev: AfterBombCheck ####");

					UpdateYourNextPrev update = new UpdateYourNextPrev();
					update.setPlayer(afc.getPlayer());
					update.alive = alive.toArray(new Player[alive.size()]);
					update.setToken(me);
					
					try {
						server.sendMessageToAll(update);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					this.update = update;
					// updateNextPrev(update);

				} else {
					System.out.println("####BUFFERController## I'm just passing the data: AfterBombCheck ####");

					AfterBombCheck afterbombcheck = new AfterBombCheck();
					afterbombcheck.setArea(((AfterBombCheck) action).getArea());
					afterbombcheck.setPlayer(((AfterBombCheck) action).getPlayer());
					afterbombcheck.setList(((AfterBombCheck) action).getList());

					System.out.println("####BUFFERController## Getting informations ####");

					if (((AfterBombCheck) action).getArea() == me.getArea(mygame.getDimension())) {
						cli.returnBomb("Bomb killed you.");
					
					} else {
						afterbombcheck.add(me);
						cli.returnBomb("Bomb didn't kill you.");
					}

					try {
						server.sendMessageToPlayer(next, afterbombcheck);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}

				}

			}

			// ###### ASKED DELETING ######
			else if (action instanceof DeleteMe) {
				receivedNewDeleteContact((DeleteMe) action);
			}
			// ###### CHECK IF EVERYONE HAVE DELETE IT ######
			else if (action instanceof Deleted) {

				int test = 0;

				deleted.add(((Deleted) action));

				for (Deleted del : deleted) {
					System.out.println("###BUFFERController## Delete action " + del.getPlayer().getId() + " other: "
							+ ((Deleted) action).getSender());

					if (del.getPlayer().getId().equals(((Deleted) action).getPlayer().getId())) {
						test++;
					}
				}

				if (test == mygame.getPlayerList().size() - 1) {
					System.out.println("###BufferController### Deleted all");
					deleted = new ArrayList<Deleted>();
					block = false;
					tokenBlocker = false;
					cli.returnMove("Move completed");
					receivedToken();
				}

			} else {
				if (action != null) {
					System.out.println(
							"####BufferController### -------!!!!!!--------  Errore: non ho elaborato: -------!!!!!!-------- "
									+ action.getClass());
				}
			}

		}

	}

	private void checkIfImAWinner() {
		if (points >= winpoint) {
			cli.returnMove("YOU'RE THE WINNER!");
			Winner winner = new Winner();
			winner.setPlayer(me);
			try {
				server.sendMessageToAll(winner);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			com.deleteMe(me.getId(), mygame.getId());
			for (Client client : clients) {
				client.end = true;
			}
		}

	}

	private void updateNextPrev(UpdateYourNextPrev action) {

		UpdateYourNextPrev afc = (UpdateYourNextPrev) action;
		int i = 0;
		ArrayList<Player> newPlayerList = new ArrayList<Player>();
		System.out.println(afc.alive.length);
		for (Player player : afc.alive) {
			if (player != null) {

				if (player.getId().equals(me.getId())) {
					if (afc.alive.length == 1) {
						System.out.println("###Buffercontroller## I'm Alone after bomb!###");
						next = me;
						prev = me;
					} else if (afc.alive.length == 2) {
						System.out.println("###Buffercontroller## We're in two!###");
						if (i == 0) {
							next = afc.get(1);
							prev = afc.get(1);
							System.out
									.println("###Buffercontroller## Seeting the next to:" + afc.get(1).getId() + "###");

						} else {
							next = afc.get(0);
							prev = afc.get(0);
							System.out
									.println("###Buffercontroller## Seeting the next to:" + afc.get(0).getId() + "###");
						}
					} else {
						System.out.println("###Buffercontroller## We're in more than two!###");
						if (i == 0) {
							next = afc.get(1);
							prev = afc.get(afc.alive.length - 1);
							System.out
									.println("###Buffercontroller## Seeting the next to:" + afc.get(1).getId() + "###");
						} else if (i == afc.alive.length - 1) {
							next = afc.get(0);
							prev = afc.get(i - 1);
							System.out
									.println("###Buffercontroller## Seeting the next to:" + afc.get(0).getId() + "###");
						} else {
							next = afc.get(i + 1);
							prev = afc.get(i - 1);
							System.out.println(
									"###Buffercontroller## Seeting the next to:" + afc.get(i + 1).getId() + "###");
						}
					}
				}
				newPlayerList.add(afc.get(i));
			}
			i++;

		}

		if (afc.getPlayer().getId().equals(me.getId())) {
			cli.returnBomb("You have gained " + (mygame.getPlayerList().size() - afc.alive.length) + " points");
			test_something_changed=true;
			points += mygame.getPlayerList().size() - afc.alive.length;
			synchronized (cli) {
				cli.notify();
			}
			checkIfImAWinner();
		}
		mygame.setPlayerList(newPlayerList);

		if (!tokenBlocker) {
			try {
				server.sendMessageToPlayer(afc.getToken(), new AckAfterBomb());
				System.out.println("###Buffercontroller## Sent ackafterbomb to:" + afc.getToken() + "###");
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		if (tokenBlocker) {
			Boolean imAlive = false;
			for (Player player : afc.alive) {
				if (player != null) {
					if (player.getId().equals(me.getId())) {
						imAlive = true;
					}
				}
			}
			if (imAlive) {
				System.out.println("###Buffercontroller## I'm the token man and i'm alive!###");
				tokenBlocker = false;
				synchronized (cli) {
					cli.notify();
				}
				receivedToken();

			} else {
				System.out.println("###Buffercontroller## I'm the token man and i'm dead!###");
				if (afc.alive.length > 0) {
					try {
						System.out
								.println("###Buffercontroller## Sending the token to: " + afc.alive[0].getId() + "###");
						PassToken passtoken = new PassToken();
						passtoken.i = 1;
						server.sendMessageToPlayer(afc.alive[0], passtoken);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				} else {
					com.deleteMe(me.getId(), mygame.getId());
				}
			}

		}
	}

	private void checKAll() {

		System.out.println("##BUFFERcontroller### ACK: ALL CHECKED #####");

		tokenBlocker = false;
		try {
			server.sendMessageToAll(new Ack());
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		receivedToken();

	}

	private void welcomeNewPlayer(Action action) {

		if (!((WelcomeNewPlayer) action).getNewPlayer().getId().equals(me.getId())) {
			System.out.println("##BUFFERcontroller### RECEIVED A NEW ONE #####");
			Client client = new Client(((WelcomeNewPlayer) action).getNewPlayer());
			client.start();
			AddMeToYourClients_NotPassToBuffer addMeToYourClients = new AddMeToYourClients_NotPassToBuffer();
			addMeToYourClients.setPlayer(me);

			try {
				synchronized (client) {
					client.send(addMeToYourClients);
				}
			} catch (JsonProcessingException e1) {

				e1.printStackTrace();
			}

			clients.add(client);

			ArrayList<Player> ply = mygame.getPlayerList();
			ply.add(((WelcomeNewPlayer) action).getNewPlayer());

			mygame.setPlayerList(ply);

			if (!((WelcomeNewPlayer) action).getSender().getId().equals(next.getId())) {
				System.out.println("##BUFFERcontroller### SENDING AN ACK TO: " + next.getId() + " #####");

				try {
					Ack ack = new Ack();
					ack.setPlayer(((WelcomeNewPlayer) action).getNewPlayer());
					ack.setSender(me);
					server.sendMessageToPlayer(((WelcomeNewPlayer) action).getNewPlayer(), ack);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}

		} else {
			System.out.println("##BUFFERcontroller### I WAS ADDED #####");
			next = ((WelcomeNewPlayer) action).getNext();
			prev = ((WelcomeNewPlayer) action).getPrev();

			if (next.getId().equals(prev.getId())) {
				try {
					System.out.println("##BUFFERcontroller### SENDING ACK #####");
					Ack ack = new Ack();
					ack.setPlayer(((WelcomeNewPlayer) action).getNewPlayer());
					server.sendMessageToPlayer(((WelcomeNewPlayer) action).getSender(), ack);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}

		if (((WelcomeNewPlayer) action).getSender().getId().equals(next.getId())
				&& !((WelcomeNewPlayer) action).getNewPlayer().getId().equals(me.getId())) {
			System.out.println("##BUFFERcontroller### I'M THE NEXT ONE #####");
			next = ((WelcomeNewPlayer) action).getNext();
			try {
				Ack ack = new Ack();
				ack.setPlayer(((WelcomeNewPlayer) action).getNewPlayer());
				server.sendMessageToPlayer(((WelcomeNewPlayer) action).getNewPlayer(), ack);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		}

	}

	private void adviceOfMyPresence() {
		// Check per verificare che tutti mi abbiano dato l'ok prima di andare
		// avanti
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
		Player player = newPlayer.getPlayer();
		Client client = new Client(player);
		client.start();
		AddMeToYourClients addMeToYourClients = new AddMeToYourClients();
		addMeToYourClients.setPlayer(me);
		clients.add(client);

		try {
			synchronized (client) {
				client.send(addMeToYourClients);
			}
		} catch (JsonProcessingException e1) {

			e1.printStackTrace();
		}

		System.out.println("##BUFFERcontroller### Client informed #####");

		// Ricevo una risposta e torno in attesa nel token fino a quando non
		// ricevo il newplayerresponse e vado sotto
		// newPLyaerConfirmedToHaveMyClientHandler
	}

	private void newPLyaerConfirmedToHaveMyClientHandler(NewPlayerResponse newPlayer) {
		Player player = newPlayer.getPlayer();
		WelcomeNewPlayer welcomeNew = new WelcomeNewPlayer();
		WelcomeNewPlayer welcomePrev = new WelcomeNewPlayer();
		ArrayList<Player> ply = mygame.getPlayerList();
		ply.add(player);

		mygame.setPlayerList(ply);
		welcomeNew.setSender(me);
		welcomePrev.setSender(me);
		// Ero da solo
		if (prev == me && next == me) {
			welcomeNew.setNext(me);
			welcomeNew.setPrev(me);
			welcomeNew.setNewPlayer(player);
			next = player;
			prev = player;

			try {
				System.out.println("##BUFFERcontroller### I WAS ALONE ||SENDING to:  " + player.getId() + " #####");
				server.sendMessageToPlayer(player, welcomeNew);
				System.out.println("##BUFFERcontroller### I WAS ALONE ||SENT #####");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// C'è qualcun altro
		} else {
			System.out.println("##BUFFERcontroller### I WASN'T ALONE #####");
			welcomeNew.setNext(me);
			welcomeNew.setPrev(prev);
			welcomeNew.setNewPlayer(player);

			welcomePrev.setNext(player);
			welcomePrev.setPrev(prev);
			welcomePrev.setNewPlayer(player);

			try {
				System.out.println("##BUFFERcontroller### SENDING TO: " + player.getId() + " As the new one #####");
				server.sendMessageToPlayer(player, welcomeNew);
				System.out.println("##BUFFERcontroller### SENDING TO: " + prev.getId() + " As the old prev #####");
				server.sendMessageToPlayer(prev, welcomePrev);

				WelcomeNewPlayer notifyall = new WelcomeNewPlayer();
				notifyall.setNewPlayer(player);
				notifyall.setSender(me);

				for (Player player_to_all : mygame.getPlayerList()) {
					if (!(player_to_all.getId().equals(player.getId())) && !(player_to_all.getId().equals(prev.getId()))
							&& !(player_to_all.getId().equals(me.getId()))) {
						System.out.println("##BUFFERcontroller### SENDING TO: " + player_to_all.getId()
								+ " As general information #####");
						server.sendMessageToPlayer(player_to_all, notifyall);
					}
				}
				System.out.println("##BUFFERcontroller### NEXT player:  " + player.getId() + " #####");
				prev = player;

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		}
	}

	private void receivedNewDeleteContact(DeleteMe deletePlayer) {

		mygame.removePlayer(deletePlayer.getPlayer().getId());
		if (deletePlayer.getNext().getId().equals(me.getId())) {
			prev = deletePlayer.getPrev();
			System.out.println("##BUFFERcontroller### New prev " + prev.getId() + " #####");

		}
		if (deletePlayer.getPrev().getId().equals(me.getId())) {
			next = deletePlayer.getNext();
			System.out.println("##BUFFERcontroller### New next " + next.getId() + " #####");

		}
		test_something_changed = true;
		try {
			Deleted del = new Deleted();
			del.setPlayer(deletePlayer.getPlayer());
			del.setSender(deletePlayer.getSender());
			server.sendMessageToPlayer(deletePlayer.getSender(), del);
			System.out.println("##BUFFERcontroller### SENT Deleted to " + deletePlayer.getSender().getId() + " #####");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void receivedToken() {
		Boolean first = true;
		
		while (mygame.getPlayerList().size() == 1 || first || addingAPlayer) {
			ArrayList<Action> listactions = new ArrayList<Action>();
			first = false;
			ArrayList<Action> temp;
			if (!block) {
				
				temp = Buffer.getAllActionsThatNeedsAToken();
				for (Action action : temp) {
					listactions.add(action);
				}

				temp = null;

				if (listactions.size() > 0) {

					for (Action actioninside : listactions) {
						if (!(actioninside instanceof AddBomb)) {
							System.out.println("##BUFFERcontroller### " + actioninside.getClass() + "#####");
						}
					}
				}
				
				for (Action actioninside : listactions) {

					if (actioninside instanceof NewPlayer) {
						System.out.println("##BUFFERcontroller### receivedNewPlayerContact #####");
						tokenBlocker = true;
						synchronized (buffer) {
							Buffer.deleteAction(actioninside);
						}

						addingAPlayer = true;
						receivedNewPlayerContact((NewPlayer) actioninside);
						test_something_changed = true;
						break;

					} else if (actioninside instanceof NewPlayerResponse) {
						System.out.println("##BUFFERcontroller### taking new player response #####");

						synchronized (buffer) {
							Buffer.deleteAction(actioninside);
						}
						System.out.println("##BUFFERcontroller### After deleting new player response  #####");

						newPLyaerConfirmedToHaveMyClientHandler((NewPlayerResponse) actioninside);
						addingAPlayer = false;
						test_something_changed = true;
						return;

					} else if (actioninside instanceof ExplodingBomb) {
						System.out.println("##BUFFERcontroller### Exploding Bomb  #####");

						tokenBlocker = true;
						AfterBombCheck afterbombcheck = new AfterBombCheck();
						afterbombcheck.setArea(((ExplodingBomb) actioninside).area);
						afterbombcheck.setPlayer(((ExplodingBomb) actioninside).player);
						afterbombcheck.setToken(me);

						if (((ExplodingBomb) actioninside).area == me.getArea(mygame.getDimension())) {

							cli.returnBomb("Bomb killed you.");
							com.deleteMe(me.getId(), mygame.getId());

						} else {
							afterbombcheck.add(me);
							cli.returnBomb("Bomb didn't kill you.");
						}
						System.out.println("##BUFFERcontroller### ADDING TO LIST  #####");

						synchronized (buffer) {
							Buffer.deleteAction(actioninside);
						}
						try {
							System.out.println("##BUFFERcontroller### Sending to NEXT  #####");

							server.sendMessageToPlayer(next, afterbombcheck);
							System.out.println("##BUFFERcontroller### Sent to NEXT  #####");
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}

						return;
					}

				}

				if (me.getCoordinate() != null) {
					Action action;
					
						action = Buffer.getFirstActionThatNeedAToken();
						

					// ###### MOVE #####
					if (action instanceof MoveCLI) {
						System.out.println("##BUFFERcontroller### MOVE ACTION #####");
						test_something_changed = true;
						Boolean done = false;

						// ###### UP #####
						if (((MoveCLI) action).getWhere() == MoveCLI.Where.UP) {
							System.out.println("##BUFFERcontroller### UP #####");

							if (me.getCoordinate().getY() < (mygame.getDimension())) {
								Coordinate coordinate = me.getCoordinate();
								coordinate.setY(me.getCoordinate().getY() + 1);
								me.setCoordinate(coordinate);
								done = true;
							} else {
								cli.returnMove("You have reached the eow");
								synchronized (cli) {
									cli.notify();
								}
							}
						}
						// ###### DOWN #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.DOWN) {
							if (me.getCoordinate().getY() > 0) {
								Coordinate coordinate = me.getCoordinate();
								coordinate.setY(me.getCoordinate().getY() - 1);
								me.setCoordinate(coordinate);
								done = true;
							} else {
								cli.returnMove("You have reached the eow");
								synchronized (cli) {
									cli.notify();
								}
							}
						} // ###### RIGHT #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.RIGHT) {
							if (me.getCoordinate().getX() < (mygame.getDimension())) {
								Coordinate coordinate = me.getCoordinate();
								coordinate.setX(me.getCoordinate().getX() + 1);
								me.setCoordinate(coordinate);
								done = true;
							} else {
								cli.returnMove("You have reached the eow");
								synchronized (cli) {
									cli.notify();
								}
							}
						}
						// ###### LEFT #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.LEFT) {
							if (me.getCoordinate().getX() > 0) {
								Coordinate coordinate = me.getCoordinate();
								coordinate.setX(me.getCoordinate().getX() - 1);
								me.setCoordinate(coordinate);
								done = true;
							} else {
								cli.returnMove("You have reached the eow");
								synchronized (cli) {
									cli.notify();
								}
							}
						}

						if (done) {

							if (mygame.getPlayerList().size() != 1) {
								tokenBlocker = true;
								System.out.println("##BUFFERcontroller### SENDING #####");

								MoveCom com = new MoveCom();
								com.setPlayer(me);
								com.setCoordinate(me.getCoordinate());
								try {
									server.sendMessageToAll(com);
								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}

							} else {
								cli.returnMove("New position--> x: " + me.getCoordinate().getX() + " and y: "
										+ me.getCoordinate().getY());
								cli.returnMove("Zone: " + me.getArea(mygame.getDimension()));
								synchronized (cli) {
									cli.notify();
								}

							}
						}

					} else if (action instanceof AddBomb) {
						if (me.area == null) {
							me.area = ((AddBomb) action).area;
							cli.returnBomb("Thanks to your accelerometer you have acquired a new bomb for the "
									+ me.area + " sector!");

						}
						// ADVICE BOMB
					}
					// BOMB
					else if (action instanceof Bomb) {
						// Don't have a bomb
						if (me.area == null) {
							cli.returnBomb("You don't have a bomb");
							synchronized (cli) {
								cli.notify();
							}
						} // Have a bomb
						else {

							BombManager bomb = new BombManager(server, me, me.area);
							cli.returnBomb("You have launched a bomb in the " + me.area
									+ " sector! You'll now have 5 second to escape from that area.");
							bomb.start();
							me.area = null;

						}
					}
				} else {

					try {

						if (mygame.getPlayerList().size() != 1) {
							if (!addingAPlayer) {
								System.out.println("##BUFFERcontroller### ASKING OTHER POSITIONS #####");

								test_something_changed = true;
								server.sendMessageToAll(new AskPosition());
								tokenBlocker = true;
							}
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
							if (test_something_changed) {
								System.out.println("##BUFFERcontroller### PASSING THE TOKEN to " + next.getId()
										+ " After something changed #####");

								test_something_changed = false;
							}
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
