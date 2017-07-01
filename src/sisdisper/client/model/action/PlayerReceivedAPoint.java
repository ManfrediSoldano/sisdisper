package sisdisper.client.model.action;

import sisdisper.server.model.Player;

public class PlayerReceivedAPoint implements Action {
Player player;
public Player getPlayer() {
	return player;
}
public void setPlayer(Player player) {
	this.player = player;
}
public int getPoints() {
	return points;
}
public void setPoints(int points) {
	this.points = points;
}
int points;

}
