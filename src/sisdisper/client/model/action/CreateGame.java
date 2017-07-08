package sisdisper.client.model.action;

import sisdisper.server.model.Game;

public class CreateGame  extends Action  {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private Game game;

public Game getGame() {
	return game;
}

public void setGame(Game game) {
	this.game = game;
}
}
