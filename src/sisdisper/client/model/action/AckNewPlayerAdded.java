package sisdisper.client.model.action;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.server.model.Player;

public class AckNewPlayerAdded extends Action {
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

		if (player != null) {
			if (player.getId().equals(BufferController.me.getId())) {
				BufferController.numberAck++;
				if (BufferController.numberAck == (BufferController.mygame.getPlayerList().size() - 2)) {
					try {
						System.out.println("#AckNewPlayer## ALL ACK PROCESSED #####");
						if (BufferController.next != null && BufferController.prev != null) {
							System.out.println("#AckNewPlayer## COMPLETED WELCOME NEW PLAYER #####");

							BufferController.server.sendMessageToPlayer(BufferController.next,
									new AckAllPlayerAddedTheNewOne());
							BufferController.numberAck = 0;
						} else {
							System.out.println("#AckNewPlayer## NOT YET COMPLETD WELCOME NEW PLAYER #####");

							BufferController.ackProcessedBeforeWelcomeNewPlayer = true;
						}

					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return true;
	}
}
