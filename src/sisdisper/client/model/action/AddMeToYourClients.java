package sisdisper.client.model.action;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
import sisdisper.server.model.Player;

public class AddMeToYourClients extends Action {
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
		System.out.println("###BUFFERController## RICEVUTA RICHIESTA CHE MI CONFERMA CHE IL TIPO CON IL TOKEN ORA E' INSERITO TRA I MIEI CLIENT #####");
		NewPlayerResponse newp = new NewPlayerResponse();
		newp.setPlayer(BufferController.me);
		try {
			BufferController.server.sendMessageToPlayer(player, newp);
			System.out.println("###BUFFERController## Inviato il newplayerresponde al token peer #####");

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return true;
	}

}
