package sisdisper.client.model.action;

import sisdisper.server.model.Player;

public class DeleteMe  extends Action  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	
	
}
