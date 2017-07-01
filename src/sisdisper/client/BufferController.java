package sisdisper.client;

import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.Token;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.Action;
import sisdisper.client.model.action.AddMeToGame;
import sisdisper.client.model.action.AskPosition;
import sisdisper.client.model.action.Bomb;
import sisdisper.client.model.action.CreateGame;
import sisdisper.client.model.action.DeleteMe;
import sisdisper.client.model.action.GetGamesFromServer;
import sisdisper.client.model.action.MoveCLI;
import sisdisper.client.model.action.MoveCom;
import sisdisper.client.model.action.NewPlayer;
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
	private Game mygame = new Game();
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

	private int points = 0;

	public void start() {
		t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {

		buffer = new Buffer();
		CLI cli = new CLI();
		cli.setBuffer(buffer);
		cli.start();
		server.setPlayer(me);
		server.start();

		sisdisper.client.model.action.Action action = buffer.getFirstAction();
		// Add me on a game
		while (true) {
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
							mygame = ((AddMeToGame) action).getGame();
							cli.returnAdded(type);
							synchronized (cli) {
								cli.notify();
							}
							adviceOfMyPresence();
						}
						if (type != ResponseAddToGame.Type.ACK) {
							cli.returnAdded(type);
							synchronized (cli) {
								cli.notify();
							}
							insideAGame = true;
						}
					} else {
						//// AGGIUNGERE RITORNO ERRORE////
					}
				} else {
					cli.returnAdded(Type.AREADY_EXIST);
					synchronized (cli) {
						cli.notify();
					}
				}
			}
			// ###### RITORNO DEI GIOCHI ######
			else if (action instanceof GetGamesFromServer) {
				GetGames games = com.getGamesFromServer();
				cli.getGames(games);
				synchronized (cli) {
					cli.notify();
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
							next = null;
							prev = null;
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
				if (tokenBlocker) {
					numberAck++;
					checKAll();
				}
			}
			// ###### CREA NUOVO GIOCATORE ######
			else if (action instanceof NewPlayer) {
				me = ((NewPlayer) action).getPlayer();
			}
			// ###### CONTROLLA LA RISPOSTA ALL'AGGIUNTA DI UN GIOCATORE ######
			else if (action instanceof WelcomeNewPlayer) {
				welcomeNewPlayer(action);
				// ###### RICEVUTO UN TOKEN ######
			} else if (action instanceof PassToken) {
				receivedToken();
			}
			// ###### RICEVUTO UN TOKEN ######
			else if (action instanceof AskPosition) {
				ReturnPosition rtn = new ReturnPosition();
				rtn.setCoordinate(me.getCoordinate());
				try {
					((AskPosition) action).getClient().send(rtn);
				} catch (JAXBException e) {
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
							if (coordinata.getX() == x && coordinata.getY() == y) {
								ok = false;
							}
						}
						if (ok) {
							ok = false;
							Coordinate coordinata_player = new Coordinate();
							coordinata_player.setX(x);
							coordinata_player.setY(y);
							me.setCoordinate(coordinata_player);
							tokenBlocker = false;
							receivedToken();

						} else {
							ok = true;
						}
					}
					
				}
			}
			// ###### RESPONDING TO A MOVE REQUEST ######
			else if (action instanceof MoveCom){
				if (((MoveCom) action).getCoordinate().equal(me.getCoordinate())){
					ResponseMove response = new ResponseMove();
					response.setPlayer(me);
					response.setResponse(ResponseMove.Response.KILLED_ME);
					try {
						((MoveCom) action).getClient().send(response);
					} catch (JAXBException e) {
						e.printStackTrace();
					}
					
					
				}
			}
			
			// ###### RESPONSE FROM A MOVE FROM ALL OTHER PEERS ######
			else if (action instanceof ResponseMove) {
				responseMoves.add(((ResponseMove) action));
				if (responseMoves.size() == mygame.getPlayerList().size() - 1) {
				   for (ResponseMove responseMove: responseMoves){
					   if(responseMove.getResponse()== ResponseMove.Response.KILLED_ME){
						   points++;	
						   PlayerReceivedAPoint point = new PlayerReceivedAPoint();
						   point.setPlayer(me);
						   point.setPoints(points);
						   try {
							server.sendMessageToAll(point);
						} catch (JAXBException e) {
							e.printStackTrace();
						}
					   }
				   }
				   
				   tokenBlocker = false;				
				}
				
			}

			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	private void checKAll() {
		if (numberAck == mygame.getPlayerList().size() - 1) {
			tokenBlocker = false;
			try {
				server.sendMessageToAll(new Ack());
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			receivedToken();

		}

	}

	private void welcomeNewPlayer(Action action) {

		if (((WelcomeNewPlayer) action).getNewPlayer().getId() != me.getId()) {
			Client client = new Client(((WelcomeNewPlayer) action).getNewPlayer());
			clients.add(client);

		} else {
			next = ((WelcomeNewPlayer) action).getNext();
			prev = ((WelcomeNewPlayer) action).getPrev();
			try {
				server.sendMessageToPlayer(((WelcomeNewPlayer) action).getSender(), new Ack());
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}

		if (((WelcomeNewPlayer) action).getSender().getId() == next.getId()) {
			next = ((WelcomeNewPlayer) action).getNext();
			try {
				server.sendMessageToPlayer(((WelcomeNewPlayer) action).getSender(), new Ack());
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}

	}

	private void adviceOfMyPresence() {
		for (Player player : mygame.getPlayerList()) {
			Client client = new Client(player);
			clients.add(client);
			client.start();
		}

		Server server = new Server();
		server.start();

	}

	private void receivedNewPlayerContact(NewPlayer newPlayer) {
		Player player = newPlayer.getPlayer();
		Client client = new Client(player);
		clients.add(client);
		if (token.getIsMine()) {
			WelcomeNewPlayer welcomeNew = new WelcomeNewPlayer();
			WelcomeNewPlayer welcomePrev = new WelcomeNewPlayer();
			welcomeNew.setSender(me);
			welcomePrev.setSender(me);
			if (prev == null && next == null) {
				welcomeNew.setNext(me);
				welcomeNew.setPrev(me);
				welcomeNew.setNewPlayer(player);
				next = player;
				prev = player;

				try {
					server.sendMessageToPlayer(player, welcomeNew);
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
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
				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	private void receivedToken() {

		ArrayList<Action> actions = buffer.getAllActions();
		for (Action action : actions) {
			if (action instanceof NewPlayer) {
				receivedNewPlayerContact((NewPlayer) action);
				tokenBlocker = true;
				return;
			}
		}

		if (me.getCoordinate().getX() != -5) {
			Action action = buffer.getFirstActionThatNeedAToken();
			// ###### MOVE #####
			if (action instanceof MoveCLI) {

				Boolean done = false;

				// ###### UP #####
				if (((MoveCLI) action).getWhere() == MoveCLI.Where.UP) {
					if (me.getCoordinate().getY() + 1 < mygame.getDimension()) {
						Coordinate coordinate = me.getCoordinate();
						coordinate.setY(me.getCoordinate().getY() + 1);
						done = true;
					}
				}
				// ###### DOWN #####
				else if (((MoveCLI) action).getWhere() == MoveCLI.Where.DOWN) {
					if (me.getCoordinate().getY() - 1 > 0) {
						Coordinate coordinate = me.getCoordinate();
						coordinate.setY(me.getCoordinate().getY() - 1);
						done = true;
					}
				} // ###### RIGHT #####
				else if (((MoveCLI) action).getWhere() == MoveCLI.Where.RIGHT) {
					if (me.getCoordinate().getX() + 1 < mygame.getDimension()) {
						Coordinate coordinate = me.getCoordinate();
						coordinate.setX(me.getCoordinate().getX() + 1);
						done = true;
					}
				}
				// ###### LEFT #####
				else if (((MoveCLI) action).getWhere() == MoveCLI.Where.LEFT) {
					if (me.getCoordinate().getX() - 1 > 0) {
						Coordinate coordinate = me.getCoordinate();
						coordinate.setX(me.getCoordinate().getX() + 1);
						done = true;
					}
				}

				if (done) {
					MoveCom com = new MoveCom();
					com.setCoordinate(me.getCoordinate());
					try {
						server.sendMessageToAll(com);
					} catch (JAXBException e) {
						e.printStackTrace();
					}
					if(!(mygame.getPlayerList().size()==1)){
					tokenBlocker = true;
					
					}
				}
				// ###### BOMB #####
			} else if (action instanceof Bomb) {

			}
		} else {
			try {
				server.sendMessageToAll(new AskPosition());
				if(!(mygame.getPlayerList().size()==1)){
				tokenBlocker = true;
				}
				else {
					int x = ThreadLocalRandom.current().nextInt(0, mygame.getDimension());
					int y = ThreadLocalRandom.current().nextInt(0, mygame.getDimension());
					Coordinate coordinata_player = new Coordinate();
					coordinata_player.setX(x);
					coordinata_player.setY(y);
					me.setCoordinate(coordinata_player);
				}
			} catch (JAXBException e) {
				e.printStackTrace();

			}
		}
		if (!tokenBlocker) {
			try {
				server.sendMessageToPlayer(next, new PassToken());
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}

}
