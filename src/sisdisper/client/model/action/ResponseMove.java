package sisdisper.client.model.action;

import sisdisper.server.model.Player;

public class ResponseMove extends Action{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Response {
		ACK,
		KILLED_ME,
		ERROR	
	}
	
	private Player player; 
	private Response response;

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
