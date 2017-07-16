package sisdisper.client.model.action;

import java.util.ArrayList;

import sisdisper.server.model.Player;

public class UpdateYourNextPrev  extends Action  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	Player player;
	Player token;
	public Player getToken() {
		return token;
	}

	public void setToken(Player token) {
		this.token = token;
	}

	public Player[] alive = new Player[1];
	

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player[] getList() {
		return alive;
	}

	public void setList(Player[] list) {
		this.alive = list;
	}
	
	public void add(Player player) {
		try {
			if (alive.length == 1) {
				alive[0] = player;
			} else {
				int dim = alive.length;
				Player[] newlist = new Player[dim];
				for (int i = 0; i < dim; i++) {
					//System.out.println("Adding element");
					newlist[i] = alive[i];
				}
				newlist[dim] = player;
				alive = newlist;
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	public Player get(int i){
		try {
		return alive[i];
		} catch (Exception e) {
			System.out.println(e);
		} return null;
	}
	
}
