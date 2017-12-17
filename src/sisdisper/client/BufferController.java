package sisdisper.client;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.CountingSemaphore;
import sisdisper.client.model.action.Ack;
import sisdisper.client.model.action.AckAfterBomb;
import sisdisper.client.model.action.AckNewPlayerAdded;
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
import sisdisper.client.view.CLI;
import sisdisper.client.view.UserObservable;
import sisdisper.server.model.Coordinate;
import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;
import sisdisper.server.model.comunication.ResponseAddToGame.Type;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unused")
public class BufferController implements Runnable {
		
	
	
	private Boolean addingAPlayer = false;
	public Boolean imFree = true;
	private Boolean test_something_changed = false;
	public String test = "";
	private int test_count =0;
	
	
	//used by actions
	public static CLI cli;
	public static Game mygame=null;
	public static Player me = new Player();
	public static Server server = new Server();
	public static Player next = new Player();
	public static Player prev = new Player();
	public static ArrayList<Client> clients = new ArrayList<Client>();
	public static int numberAck = 0;
	public static Boolean tokenBlocker = false;
	public static ArrayList<Coordinate> receivedCoordinate = new ArrayList<Coordinate>();
	public static Boolean end = false;
	public static ArrayList<ResponseMove> responseMoves = new ArrayList<ResponseMove>();
    public static int points = 0;
    public static int winpoint = 3;
	public static ArrayList<AckAfterBomb> ack = new ArrayList<AckAfterBomb>();
	public static UpdateYourNextPrev tokenUpdate = new UpdateYourNextPrev();
	public static ArrayList<Deleted> deleted = new ArrayList<Deleted>();
	//Da capire cosa faccia
	public static Boolean block = false;
	
	//internals
	//Semaphore
	private CountingSemaphore semaphore = CountingSemaphore.getInstance();
	private Thread t;
	private Buffer buffer;

	public void start() {
		t = new Thread(this);
		t.start();

		buffer = new Buffer();
		buffer.setBufferController(this);

		cli = new CLI();
		cli.setBuffer(buffer);
		UserObservable observable = new UserObservable();
		cli.setObservable(observable);
		observable.addObserver(buffer);
		cli.start();

	}

	public Player getMe() {
		return me;
	}

	@Override
	public void run() {
		
		Action action = new Action();
		// Add me on a game

		while (!end) {

			
			try{
			semaphore.release();
			action = Buffer.getFirstAction();
			action.execute();

			}catch(Exception e){
				
			}
			

		}

	}


	private void updateNextPrev(UpdateYourNextPrev action) {

	
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

	

	public void receivedToken() {
		Boolean first = true;
		//testing
		//test= test+"\r\n Count: " + test_count;
		//test_count++;
		//if(test_count>=2000){test_count=0;}
		///
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
							BombObservable bombobservable = new BombObservable();
							bombobservable.addObserver(buffer);
							BombManager bomb = new BombManager(server, me, me.area, bombobservable);
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
