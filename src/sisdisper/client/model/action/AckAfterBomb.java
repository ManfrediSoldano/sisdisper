package sisdisper.client.model.action;

import java.util.ArrayList;

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

		ack.add((AckAfterBomb) action);

		if (ack.size() == mygame.getPlayerList().size() - 1) {
			System.out.println("####BUFFERController## INside update ####");
			updateNextPrev(update);
			ack = new ArrayList<AckAfterBomb>();
		}
		return true;
	}

}
