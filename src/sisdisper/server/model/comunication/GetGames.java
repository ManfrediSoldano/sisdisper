package sisdisper.server.model.comunication;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import sisdisper.server.model.Game;
@XmlRootElement(name="GetGames")
public class GetGames {

	private ArrayList<Game> games = new ArrayList<Game>();

	public ArrayList<Game> getGames() {
		return games;
	}

	public void setGames(ArrayList<Game> games) {
		this.games = games;
	}
}
