package sisdisper.client.model.action;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.client.socket.Client;
import sisdisper.server.model.Player;

public class AckAfterBomb extends Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	Player player;
	Player sender;

	public Player getSender() {
		return sender;
	}

	public void setSender(Player sender) {
		this.sender = sender;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Boolean execute() {

		System.out.println("####BUFFERController## RECEIVED AckAfterBomb ####");

		BufferController.ack.add(this);

		if (BufferController.ack.size() == BufferController.mygame.getPlayerList().size() - 1) {
			System.out.println("####BUFFERController## INside update ####");
			updateNextPrev();
			BufferController.ack = new ArrayList<AckAfterBomb>();
		}
		return true;
	}

	private void updateNextPrev() {
		ClientToServerCommunication com = new ClientToServerCommunication();

		UpdateYourNextPrev afc = BufferController.tokenUpdate;
		int i = 0;
		ArrayList<Player> newPlayerList = new ArrayList<Player>();
		System.out.println(afc.alive.length);
		for (Player player : afc.alive) {
			if (player != null) {

				if (player.getId().equals(BufferController.me.getId())) {
					if (afc.alive.length == 1) {
						System.out.println("###Buffercontroller## I'm Alone after bomb!###");
						BufferController.next = BufferController.me;
						BufferController.prev = BufferController.me;
					} else if (afc.alive.length == 2) {
						System.out.println("###Buffercontroller## We're in two!###");
						if (i == 0) {
							BufferController.next = afc.get(1);
							BufferController.prev = afc.get(1);
							System.out
									.println("###Buffercontroller## Seeting the next to:" + afc.get(1).getId() + "###");

						} else {
							BufferController.next = afc.get(0);
							BufferController.prev = afc.get(0);
							System.out
									.println("###Buffercontroller## Seeting the next to:" + afc.get(0).getId() + "###");
						}
					} else {
						System.out.println("###Buffercontroller## We're in more than two!###");
						if (i == 0) {
							BufferController.next = afc.get(1);
							BufferController.prev = afc.get(afc.alive.length - 1);
							System.out
									.println("###Buffercontroller## Seeting the next to:" + afc.get(1).getId() + "###");
						} else if (i == afc.alive.length - 1) {
							BufferController.next = afc.get(0);
							BufferController.prev = afc.get(i - 1);
							System.out
									.println("###Buffercontroller## Seeting the next to:" + afc.get(0).getId() + "###");
						} else {
							BufferController.next = afc.get(i + 1);
							BufferController.prev = afc.get(i - 1);
							System.out.println(
									"###Buffercontroller## Seeting the next to:" + afc.get(i + 1).getId() + "###");
						}
					}
				}
				newPlayerList.add(afc.get(i));
			}
			i++;

		}

		if (afc.getPlayer().getId().equals(BufferController.me.getId())) {
			BufferController.cli.returnBomb("You have gained " + (BufferController.mygame.getPlayerList().size() - afc.alive.length) + " points");
			
			BufferController.points += BufferController.mygame.getPlayerList().size() - afc.alive.length;
			synchronized (BufferController.cli) {
				BufferController.cli.notify();
			}
			
			if (BufferController.points >= BufferController.winpoint) {
				BufferController.cli.returnMove("YOU'RE THE WINNER!");
				Winner winner = new Winner();
				winner.setPlayer(BufferController.me);
				try {
					BufferController.server.sendMessageToAll(winner);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				

				com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId());
				for (Client client : BufferController.clients) {
					client.end = true;
				}
			}
		}
		BufferController.mygame.setPlayerList(newPlayerList);

		if (!BufferController.tokenBlocker) {
			
				try {
					BufferController.server.sendMessageToPlayer(afc.getToken(), new AckAfterBomb());
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("###Buffercontroller## Sent ackafterbomb to:" + afc.getToken() + "###");
			
		}

		if (BufferController.tokenBlocker) {
			Boolean imAlive = false;
			for (Player player : afc.alive) {
				if (player != null) {
					if (player.getId().equals(BufferController.me.getId())) {
						imAlive = true;
					}
				}
			}
			if (imAlive) {
				System.out.println("###Buffercontroller## I'm the token man and i'm alive!###");
				BufferController.tokenBlocker = false;
				synchronized (BufferController.cli) {
					BufferController.cli.notify();
				}
				PassToken token = new PassToken();
				token.execute();

			} else {
				System.out.println("###Buffercontroller## I'm the token man and i'm dead!###");
				if (afc.alive.length > 0) {
					try {
						System.out
								.println("###Buffercontroller## Sending the token to: " + afc.alive[0].getId() + "###");
						PassToken passtoken = new PassToken();
						passtoken.i = 1;
						BufferController.server.sendMessageToPlayer(afc.alive[0], passtoken);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("###Buffercontroller## I've died too!###");

					com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId());
				}
			}

		}
		
	}
	
	
	

}
