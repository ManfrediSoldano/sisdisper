package sisdisper.server.model;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
@XmlRootElement(name="Game")
public class Game implements Serializable{


	private static final long serialVersionUID = 1L;
	private ArrayList<Player> playerList = new ArrayList<Player>();
	public void setPlayerList(ArrayList<Player> playerList) {
		this.playerList = playerList;
	}


	private String id;
	private int dimension;
	
	public Game (){
		
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
	public ArrayList<Player> getPlayerList(){
		return playerList;
	}
	
	public void addPlayer(Player player){
		playerList.add(player);
	}
	

	public void removePlayer(String id){
		for(Player checkPlayer: playerList){
			if(checkPlayer.getId().equals(id)){
				playerList.remove(checkPlayer);
				break;
			}
		}
	}
	
	
}
