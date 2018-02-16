package sisdisper.server.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement(name = "Game")
public class Game implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Player> playerList = new ArrayList<Player>();
	private ArrayList<Player> deadPlayers = new ArrayList<Player>();
	public Boolean live = true;

	public void setPlayerList(ArrayList<Player> playerList) {
		this.playerList = playerList;
	}

	private String id;
	private int dimension;

	public Game() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	@XmlElement
	public ArrayList<Player> getPlayerList() {
		return playerList;
	}

	public void addPlayer(Player player) {
		playerList.add(player);
	}

	public void removePlayer(String id, String points, String winner) {
		for (Player checkPlayer : playerList) {
			if (checkPlayer.getId().equals(id)) {
				playerList.remove(checkPlayer);
				try {
					checkPlayer.setPoint(Integer.parseInt(points));
				} catch (Exception e) {
					System.out.println("Error removing player on parsing int: " + e);
				}
					checkPlayer.winner=winner;
					deadPlayers.add(checkPlayer);	
				break;
			}
		}

	}

}
