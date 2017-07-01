package sisdisper.client.model.action;

import sisdisper.server.model.Game;

public class CreateGame  implements Action  {
private Game game;

public Game getGame() {
	return game;
}

public void setGame(Game game) {
	this.game = game;
}
}
