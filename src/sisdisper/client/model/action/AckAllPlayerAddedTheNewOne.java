package sisdisper.client.model.action;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.server.model.Player;

public class AckAllPlayerAddedTheNewOne extends Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

		System.out.println("##BUFFERcontroller### ACK: ALL CHECKED #####");

		BufferController.tokenBlocker = false;
		try {
			BufferController.server.sendMessageToAll(new Ack());
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		PassToken token = new PassToken();
		token.execute();
		return true;
	} 
}
