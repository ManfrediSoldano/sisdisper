package sisdisper.client.model.action;

import sisdisper.server.model.Area;
import sisdisper.server.model.Player;

public class Bomb  extends Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private Player sender;
	private Area area;
	
	
	public Player getSender() {
		return sender;
	}
	public void setSender(Player sender) {
		this.sender = sender;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
}
