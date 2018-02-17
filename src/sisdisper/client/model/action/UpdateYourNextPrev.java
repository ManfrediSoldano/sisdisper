package sisdisper.client.model.action;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.client.socket.Client;
import sisdisper.server.model.Player;

public class UpdateYourNextPrev  extends Action  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	Player player;
	Player token;
	public Player getToken() {
		return token;
	}

	public void setToken(Player token) {
		this.token = token;
	}

	public Player[] alive = new Player[1];
	

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player[] getList() {
		return alive;
	}

	public void setList(Player[] list) {
		this.alive = list;
	}
	
	public void add(Player player) {
		try {
			if (alive.length == 1) {
				alive[0] = player;
			} else {
				int dim = alive.length;
				Player[] newlist = new Player[dim];
				for (int i = 0; i < dim; i++) {
					//System.out.println("Adding element");
					newlist[i] = alive[i];
				}
				newlist[dim] = player;
				alive = newlist;
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	public Player get(int i){
		try {
		return alive[i];
		} catch (Exception e) {
			System.out.println(e);
		} return null;
	}
	
	public Boolean execute() {
		
		ClientToServerCommunication com = new ClientToServerCommunication();
		int i = 0;
		ArrayList<Player> newPlayerList = new ArrayList<Player>();
		
		System.out.println(alive.length);
		
		for (Player player : alive) {
			if (player != null) {

				if (player.getId().equals(BufferController.me.getId())) {
					if (alive.length == 1) {
						BufferController.cli.publishString("##UpdateYourNextPrev# I'm Alone after bomb!###");
						BufferController.next = BufferController.me;
						BufferController.prev = BufferController.me;
					} else if (alive.length == 2) {
						BufferController.cli.publishString("##UpdateYourNextPrev# We're in two!###");
						if (i == 0) {
							BufferController.next = get(1);
							BufferController.prev = get(1);
							BufferController.cli.publishString("##UpdateYourNextPrev# Seeting the next to:" + get(1).getId() + "###");

						} else {
							BufferController.next =get(0);
							BufferController.prev =get(0);
							BufferController.cli.publishString("##UpdateYourNextPrev# Seeting the next to:" + get(0).getId() + "###");
						}
					} else {
						BufferController.cli.publishString("##UpdateYourNextPrev# We're in more than two!###");
						if (i == 0) {
							BufferController.next = get(1);
							BufferController.prev = get(alive.length - 1);
							BufferController.cli.publishString("##UpdateYourNextPrev# Seeting the next to:" + get(1).getId() + "###");
						} else if (i == alive.length - 1) {
							BufferController.next = get(0);
							BufferController.prev = get(i - 1);
							BufferController.cli.publishString("##UpdateYourNextPrev# Seeting the next to:" + get(0).getId() + "###");
						} else {
							BufferController.next = get(i + 1);
							BufferController.prev = get(i - 1);
							BufferController.cli.publishString("##UpdateYourNextPrev# Seeting the next to:" + get(i + 1).getId() + "###");
						}
					}
				}
				newPlayerList.add(get(i));
			}
			i++;

		}

		if (player.getId().equals(BufferController.me.getId())) {
			BufferController.cli.returnBomb("You have gained " + (BufferController.mygame.getPlayerList().size() - alive.length) + " points");
			int addPoint = BufferController.mygame.getPlayerList().size() - alive.length;
			if (addPoint>3)
				addPoint=3;
			BufferController.points += addPoint;
			
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
				

				com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId(), Integer.toString(BufferController.points),"winner");
				for (Client client : BufferController.clients) {
					client.end = true;
				}
			}
		}
		
		BufferController.mygame.setPlayerList(newPlayerList);

		if (!BufferController.tokenBlocker) {
			try {
				BufferController.server.sendMessageToPlayer(token, new AckAfterBomb());
				BufferController.cli.publishString("##UpdateYourNextPrev# Sent ackafterbomb to:" + token + "###");
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		if (BufferController.tokenBlocker) {
			Boolean imAlive = false;
			for (Player player : alive) {
				if (player != null) {
					if (player.getId().equals(BufferController.me.getId())) {
						imAlive = true;
					}
				}
			}
			if (imAlive) {
				BufferController.cli.publishString("##UpdateYourNextPrev# I'm the token man and i'm alive!###");
				BufferController.tokenBlocker = false;
				synchronized (	BufferController.cli) {
					BufferController.cli.notify();
				}
				PassToken token = new PassToken();
				token.execute();

			} else {
				BufferController.cli.publishString("##UpdateYourNextPrev# I'm the token man and i'm dead!###");
				if (alive.length > 0) {
					try {
						BufferController.cli.publishString("##UpdateYourNextPrev# Sending the token to: " + alive[0].getId() + "###");
						PassToken passtoken = new PassToken();
						passtoken.i = 1;
						BufferController.server.sendMessageToPlayer(alive[0], passtoken);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				} else {
					com.deleteMe(BufferController.me.getId(), BufferController.mygame.getId(),Integer.toString(BufferController.points),"loser");
				}
			}

		}
		
		
		
		return true;
	}
	
}
