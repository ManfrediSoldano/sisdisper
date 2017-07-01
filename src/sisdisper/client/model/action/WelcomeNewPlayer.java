package sisdisper.client.model.action;

import sisdisper.server.model.Player;

public class WelcomeNewPlayer implements Action {
	Player next = new Player();
	Player prev = new Player();
	Player newPlayer = new Player();
	Player sender = new Player();
	
	public Player getSender() {
		return sender;
	}
	public void setSender(Player sender) {
		this.sender = sender;
	}
	public Player getNewPlayer() {
		return newPlayer;
	}
	public void setNewPlayer(Player newPlayer) {
		this.newPlayer = newPlayer;
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
	

}
