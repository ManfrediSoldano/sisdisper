package sisdisper.client.model.action;

import java.util.ArrayList;

import sisdisper.client.BufferController;
import sisdisper.server.model.Player;

public class Deleted extends Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	Player player;
	Player next;
	Player prev;
	Player sender;

	public Player getSender() {
		return sender;
	}

	public void setSender(Player sender) {
		this.sender = sender;
	}

	public Player getNext() {
		return next;
	}

	public void setNext(Player next) {
		this.next = next;
	}

	public Player getPrev() {
		return prev;
	}

	public void setPrev(Player prev) {
		this.prev = prev;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Boolean execute() {
		int test = 0;

		BufferController.deleted.add(this);

		for (Deleted del : BufferController.deleted) {
			System.out.println("###Deleted## Delete action " + del.getPlayer().getId() + " other: "
					+ getSender().getId());

			if (del.getPlayer().getId().equals(getPlayer().getId())) {
				test++;
			}
		}

		if (test == BufferController.mygame.getPlayerList().size() - 1) {
			System.out.println("###Deleted### Deleted all");
			
			BufferController.deleted = new ArrayList<Deleted>();
			BufferController.tokenBlocker = false;
			BufferController.cli.returnMove("Move completed");
			System.out.println("###Deleted### Coordinate, x: "+ BufferController.me.getCoordinate().getX() +" y: " + BufferController.me.getCoordinate().getY());

			BufferController.cli.move(BufferController.me.getCoordinate().getX(),
					BufferController.me.getCoordinate().getY());
			
			PassToken token = new PassToken();
			token.test=false;
			token.execute();
			System.out.println("###Deleted### After token");

		}
		return true;
	}

}
