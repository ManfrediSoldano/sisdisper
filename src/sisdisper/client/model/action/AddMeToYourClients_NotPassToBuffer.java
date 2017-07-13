package sisdisper.client.model.action;

import sisdisper.server.model.Player;

public class AddMeToYourClients_NotPassToBuffer extends Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	Player player=null;
	Player player_do_not_reply=null;

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
	
	

}
