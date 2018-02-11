package sisdisper.client.model.action;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.client.ClientToServerCommunication;
import sisdisper.server.model.Player;

public class DeleteMe extends Action {

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

	public Boolean execute() {

		
		BufferController.mygame.removePlayer(player.getId());
		
		if (next.getId().equals(BufferController.me.getId())) {
			BufferController.prev = getPrev();
			System.out.println("##DeleteMe### New prev " + BufferController.prev.getId() + " #####");

		}
		if (prev.getId().equals(BufferController.me.getId())) {
			BufferController.next = getNext();
			System.out.println("##DeleteMe### New next " + BufferController.next.getId() + " #####");

		}
		
		try {
			Deleted del = new Deleted();
			del.setPlayer(getPlayer());
			del.setSender(getSender());
			BufferController.server.sendMessageToPlayer(getSender(), del);
			System.out.println("##DeleteMe### SENT Deleted to " + getSender().getId() + " #####");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return true;
	}
}
