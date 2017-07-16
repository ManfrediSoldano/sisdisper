package sisdisper.client.model.action;


import sisdisper.server.model.Area;
import sisdisper.server.model.Player;

public class AfterBombCheck extends Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5656;
	/**
	 * 
	 */
	private Area area;
	private Player player;
	private Player token;
	private Player[] list = new Player[0];

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player[] getList() {
		return list;
	}

	public void setList(Player[] list) {
		this.list = list;
	}

	public void add(Player player) {
		try {
			
				int dim = list.length;
				Player[] newlist = new Player[dim+1];
				for (int i = 0; i < dim; i++) {
					//System.out.println("Adding element");
					newlist[i] = list[i];
				}
				newlist[dim] = player;
				list = newlist;
			
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public Player getToken() {
		return token;
	}

	public void setToken(Player token) {
		this.token = token;
	}

}
