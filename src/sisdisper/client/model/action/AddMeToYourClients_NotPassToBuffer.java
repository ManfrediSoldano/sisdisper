package sisdisper.client.model.action;

import sisdisper.client.BufferController;
import sisdisper.server.model.Player;

public class AddMeToYourClients_NotPassToBuffer extends Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	Player player = null;
	Player player_do_not_reply = null;

	public Player getPlayer_do_not_reply() {
		return player_do_not_reply;
	}

	public void setPlayer_do_not_reply(Player player_do_not_reply) {
		this.player_do_not_reply = player_do_not_reply;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Boolean execute() {
		try {
			BufferController.numberAdd++;
			if (BufferController.numberAdd == (BufferController.mygame.getPlayerList().size() - 2)) {
				if (BufferController.welcome && BufferController.acknewplayer) {
					System.out.println("#AddMetoYourClients_not## COMPLETED WELCOME NEW PLAYER & Ack new Player #####");

					BufferController.server.sendMessageToPlayer(BufferController.next,
							new AckAllPlayerAddedTheNewOne());
				} else {
					System.out
							.println("#AddMetoYourClients_not## Welcome program not completed, setting me true #####");

					BufferController.addmetoyourclients = true;

				}
			}
		} catch (Exception e) {
			System.out.println("##AddMetoYourClients_not## exception:" + e);
		}

		return true;

	}

}
