package sisdisper.client.model.action;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BombManager;
import sisdisper.client.BombObservable;
import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.client.model.Buffer;

import sisdisper.client.socket.Client;
import sisdisper.client.socket.ClientObservable;
import sisdisper.server.model.Coordinate;
import sisdisper.server.model.Player;

public class PassToken extends Action {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	public boolean test = false;
	public int i = 0;

	public Boolean execute() {
		try {
		Boolean first = true;
		ClientToServerCommunication com = new ClientToServerCommunication();
		if(test)
		System.out.println("###PassToken## TEST: Inside token");

		while ((BufferController.mygame.getPlayerList().size() == 1 || first || BufferController.addingAPlayer)
				&& (BufferController.alive)) {

			// Setto che sono già passato:
			first = false;

			// Prendo tutte le azioni che aspettavano un token
			ArrayList<Action> listactions = new ArrayList<Action>();
			ArrayList<Action> temp;

			// Se sto aspettando qualche azione non eseguo il token
			if (!BufferController.block) {
				temp = Buffer.getAllActionsThatNeedsAToken();

				for (Action action : temp) {
					listactions.add(action);
				}

				temp = null;

				if (listactions.size() > 0) {
					for (Action actioninside : listactions) {
						if (!(actioninside instanceof AddBomb)) {
							System.out.println("Azione: " + actioninside);
							BufferController.cli
									.publishString("##InsideToken### " + actioninside.getClass() + "#####");
						}
					}
				}

				for (Action actioninside : listactions) {
					// ADDING A PLAYER

					if (actioninside instanceof NewPlayer) {
						BufferController.cli.publishString("##InsideToken### receivedNewPlayerContact #####");

						BufferController.tokenBlocker = true;
						// synchronized (Buffer) {
						Buffer.deleteAction(actioninside);
						// }

						BufferController.addingAPlayer = true;

						receivedNewPlayerContact((NewPlayer) actioninside);

						break;

					} else if (actioninside instanceof NewPlayerResponse) {
						BufferController.cli.publishString("##InsideToken### taking new player response #####");

						// synchronized (buffer) {
						Buffer.deleteAction(actioninside);
						// }
						BufferController.cli
								.publishString("##InsideToken### After deleting new player response  #####");

						newPlayerConfirmedToHaveMyClientHandler((NewPlayerResponse) actioninside);
						BufferController.addingAPlayer = false;

						return true;

						// BOMB
					} else if (actioninside instanceof ExplodingBomb) {
						BufferController.cli.publishString("##InsideToken### Exploding Bomb  #####");

						if (BufferController.mygame.getPlayerList().size() == 1) {
							if (((ExplodingBomb) actioninside).area == BufferController.me
									.getArea(BufferController.mygame.getDimension())) {

								BufferController.cli.returnBomb("Bomb killed you.");
								com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId());
								BufferController.alive=false;
								

							}
							Buffer.deleteAction(actioninside);
						} else {

							BufferController.tokenBlocker = true;

							AfterBombCheck afterbombcheck = new AfterBombCheck();
							afterbombcheck.setArea(((ExplodingBomb) actioninside).area);
							afterbombcheck.setPlayer(((ExplodingBomb) actioninside).player);
							afterbombcheck.setToken(BufferController.me);

							if (((ExplodingBomb) actioninside).area == BufferController.me
									.getArea(BufferController.mygame.getDimension())) {

								BufferController.cli.returnBomb("Bomb killed you.");
								com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId());

							} else {
								afterbombcheck.add(BufferController.me);
								BufferController.cli.returnBomb("Bomb didn't kill you.");
							}
							BufferController.cli.publishString("##InsideToken### ADDING TO LIST  #####");

							// synchronized (buffer) {
							Buffer.deleteAction(actioninside);
							// }

							try {
								BufferController.cli.publishString("##InsideToken### Sending to NEXT  #####");

								BufferController.server.sendMessageToPlayer(BufferController.next, afterbombcheck);
								BufferController.cli.publishString("##InsideToken### Sent to NEXT  #####");
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}

							return true;
						}
					}

				}
				if (BufferController.me.getCoordinate() != null) {
					Action action;

					action = Buffer.getFirstActionThatNeedAToken();

					// ###### MOVE #####
					if (action instanceof MoveCLI) {
						BufferController.cli.publishString("##InsideToken### MOVE ACTION #####");
						Boolean done = false;

						// ###### UP #####
						if (((MoveCLI) action).getWhere() == MoveCLI.Where.UP) {
							BufferController.cli.publishString("##InsideToken### UP #####");

							if (BufferController.me.getCoordinate().getY() < (BufferController.mygame.getDimension())) {
								Coordinate coordinate = BufferController.me.getCoordinate();
								coordinate.setY(BufferController.me.getCoordinate().getY() + 1);
								BufferController.me.setCoordinate(coordinate);
								done = true;
							} else {
								BufferController.cli.returnMove("You have reached the eotw");
								synchronized (BufferController.cli) {
									BufferController.cli.notify();
								}
							}
						}
						// ###### DOWN #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.DOWN) {
							if (BufferController.me.getCoordinate().getY() > 0) {
								Coordinate coordinate = BufferController.me.getCoordinate();
								coordinate.setY(BufferController.me.getCoordinate().getY() - 1);
								BufferController.me.setCoordinate(coordinate);
								done = true;
							} else {
								BufferController.cli.returnMove("You have reached the eotw");
								synchronized (BufferController.cli) {
									BufferController.cli.notify();
								}
							}
						} // ###### RIGHT #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.RIGHT) {
							if (BufferController.me.getCoordinate().getX() < (BufferController.mygame.getDimension())) {
								Coordinate coordinate = BufferController.me.getCoordinate();
								coordinate.setX(BufferController.me.getCoordinate().getX() + 1);
								BufferController.me.setCoordinate(coordinate);
								done = true;
							} else {
								BufferController.cli.returnMove("You have reached the eow");
								synchronized (BufferController.cli) {
									BufferController.cli.notify();
								}
							}
						}
						// ###### LEFT #####
						else if (((MoveCLI) action).getWhere() == MoveCLI.Where.LEFT) {
							if (BufferController.me.getCoordinate().getX() > 0) {
								Coordinate coordinate = BufferController.me.getCoordinate();
								coordinate.setX(BufferController.me.getCoordinate().getX() - 1);
								BufferController.me.setCoordinate(coordinate);
								done = true;
							} else {
								BufferController.cli.returnMove("You have reached the eow");
								synchronized (BufferController.cli) {
									BufferController.cli.notify();
								}
							}
						}

						if (done) {

							if (BufferController.mygame.getPlayerList().size() != 1) {
								BufferController.tokenBlocker = true;
								BufferController.cli.publishString("##InsideToken### SENDING #####");

								MoveCom movecom = new MoveCom();
								movecom.setPlayer(BufferController.me);
								movecom.setCoordinate(BufferController.me.getCoordinate());
								try {
									BufferController.server.sendMessageToAll(movecom);
								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}

							} else {
								BufferController.cli
										.returnMove("New position--> x: " + BufferController.me.getCoordinate().getX()
												+ " and y: " + BufferController.me.getCoordinate().getY());

								BufferController.cli.returnMove(
										"Zone: " + BufferController.me.getArea(BufferController.mygame.getDimension()));

								BufferController.cli.move(BufferController.me.getCoordinate().getX(),
										BufferController.me.getCoordinate().getY());

								synchronized (BufferController.cli) {
									BufferController.cli.notify();
								}

							}
						}

					} else if (action instanceof AddBomb) {
						if (BufferController.me.area == null) {
							BufferController.me.area = ((AddBomb) action).area;
							BufferController.cli
									.returnBomb("Thanks to your accelerometer you have acquired a new bomb for the "
											+ BufferController.me.area + " sector!");

						}
						// ADVICE BOMB
					}
					// BOMB
					else if (action instanceof Bomb) {
						// Don't have a bomb
						if (BufferController.me.area == null) {
							BufferController.cli.returnBomb("You don't have a bomb");
							synchronized (BufferController.cli) {
								BufferController.cli.notify();
							}
						} // Have a bomb
						else {
							BombObservable bombobservable = new BombObservable();
							bombobservable.addObserver(BufferController.buffer);
							BombManager bomb = new BombManager(BufferController.server, BufferController.me,
									BufferController.me.area, bombobservable);

							BufferController.cli
									.returnBomb("You have launched a bomb in the " + BufferController.me.area
											+ " sector! You'll now have 5 second to escape from that area.");

							bomb.start();

							BufferController.me.area = null;

						}
					}
				} else {

					// NON ho ancora una posizione
					try {

						if (BufferController.mygame.getPlayerList().size() != 1) {
							if (!BufferController.addingAPlayer) {

								BufferController.cli
										.publishString("##InsideToken### ASKING OTHER POSITIONS #####");
								AskPosition ask = new AskPosition();
								ask.setPlayer(BufferController.me);
								BufferController.server.sendMessageToAll(ask);
								BufferController.tokenBlocker = true;
							}
						} else {
							int x = ThreadLocalRandom.current().nextInt(0, BufferController.mygame.getDimension());
							int y = ThreadLocalRandom.current().nextInt(0, BufferController.mygame.getDimension());
							Coordinate coordinata_player = new Coordinate();
							coordinata_player.setX(x);
							coordinata_player.setY(y);

							BufferController.me.setCoordinate(coordinata_player);

							BufferController.cli
									.returnMove("Position --> x: " + BufferController.me.getCoordinate().getX()
											+ " and y: " + BufferController.me.getCoordinate().getY());

							synchronized (BufferController.cli) {
								BufferController.cli.notify();
							}
						}
					} catch (JsonProcessingException e) {
						e.printStackTrace();

					}
				}

				if (!BufferController.tokenBlocker) {
					if (BufferController.mygame.getPlayerList().size() != 1) {

						try {
							PassToken token = new PassToken();
							if(test) {
								System.out.println("###PassToken## TEST: Passing the token to " +BufferController.next.getId() );
								token.test=true;
							}
							BufferController.server.sendMessageToPlayer(BufferController.next, token);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
					}
				}
			}

			else {

				PassToken passtoken = new PassToken();
				passtoken.execute();

			}

		}
		}catch (Exception e)
		{
			System.out.println("##PassToken## Exception: "+e);
		}

		return true;
	}

	private void receivedNewPlayerContact(NewPlayer newPlayer) {
		Player player = newPlayer.getPlayer();
		Client client = new Client(player);
		ClientObservable observ = new ClientObservable();
		client.setClientObserver(observ);
		observ.addObserver(BufferController.buffer);
		client.start();
		AddMeToYourClients addMeToYourClients = new AddMeToYourClients();
		addMeToYourClients.setPlayer(BufferController.me);
		BufferController.clients.add(client);

		try {
			synchronized (client) {
				client.send(addMeToYourClients);
			}
		} catch (JsonProcessingException e1) {

			e1.printStackTrace();
		}

		BufferController.cli.publishString("##InsideToken### Client informed #####");

		// Ricevo una risposta e torno in attesa nel token fino a quando non
		// ricevo il newplayerresponse e vado sotto
		// newPLyaerConfirmedToHaveMyClientHandler
	}

	private void newPlayerConfirmedToHaveMyClientHandler(NewPlayerResponse newPlayer) {

		Player player = newPlayer.getPlayer();
		WelcomeNewPlayer welcomeNew = new WelcomeNewPlayer();
		WelcomeNewPlayer welcomePrev = new WelcomeNewPlayer();

		ArrayList<Player> ply = BufferController.mygame.getPlayerList();
		ply.add(player);

		BufferController.mygame.setPlayerList(ply);
		welcomeNew.setSender(BufferController.me);
		welcomePrev.setSender(BufferController.me);
		// Ero da solo
		if (BufferController.prev == BufferController.me && BufferController.next == BufferController.me) {
			welcomeNew.setNext(BufferController.me);
			welcomeNew.setPrev(BufferController.me);
			welcomeNew.setNewPlayer(player);
			BufferController.next = player;
			BufferController.prev = player;

			try {
				BufferController.cli
						.publishString("##InsideToken### I WAS ALONE ||SENDING to:  " + player.getId() + " #####");
				BufferController.server.sendMessageToPlayer(player, welcomeNew);
				BufferController.cli.publishString("##InsideToken### I WAS ALONE ||SENT #####");
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// C'è qualcun altro
		} else {
			BufferController.cli.publishString("##InsideToken### I WASN'T ALONE #####");
			welcomeNew.setNext(BufferController.me);
			welcomeNew.setPrev(BufferController.prev);
			welcomeNew.setNewPlayer(player);

			welcomePrev.setNext(player);
			welcomePrev.setPrev(BufferController.prev);
			welcomePrev.setNewPlayer(player);

			try {
				BufferController.cli
						.publishString("##InsideToken### SENDING TO: " + player.getId() + " As the new one #####");
				BufferController.server.sendMessageToPlayer(player, welcomeNew);
				BufferController.cli.publishString(
						"##InsideToken### SENDING TO: " + BufferController.prev.getId() + " As the old prev #####");
				BufferController.server.sendMessageToPlayer(BufferController.prev, welcomePrev);

				WelcomeNewPlayer notifyall = new WelcomeNewPlayer();
				notifyall.setNewPlayer(player);
				notifyall.setSender(BufferController.me);

				for (Player player_to_all : BufferController.mygame.getPlayerList()) {
					if (!(player_to_all.getId().equals(player.getId()))
							&& !(player_to_all.getId().equals(BufferController.prev.getId()))
							&& !(player_to_all.getId().equals(BufferController.me.getId()))) {
						BufferController.cli.publishString("##InsideToken### SENDING TO: " + player_to_all.getId()
								+ " As general information #####");
						BufferController.server.sendMessageToPlayer(player_to_all, notifyall);
					}
				}
				BufferController.cli.publishString("##InsideToken### NEXT player:  " + player.getId() + " #####");
				BufferController.prev = player;

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		}
	}

}
