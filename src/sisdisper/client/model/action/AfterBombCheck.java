package sisdisper.client.model.action;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BufferController;
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
			Player[] newlist = new Player[dim + 1];
			for (int i = 0; i < dim; i++) {
				// System.out.println("Adding element");
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

	public Boolean execute() {

		BufferController.cli.publishString("###AfterBombCheck# Token Blocker" + BufferController.tokenBlocker + " ####");


		if (BufferController.tokenBlocker) {
			ArrayList<Player> alive = new ArrayList<Player>();
			for (Player player : getList()) {

				alive.add(player);

			}
			
			BufferController.cli.publishString("###AfterBombCheck# Update your nextprev: AfterBombCheck ####");

			UpdateYourNextPrev update = new UpdateYourNextPrev();
			update.setPlayer(getPlayer());
			update.alive = alive.toArray(new Player[alive.size()]);
			update.setToken(BufferController.me);

			try {
				BufferController.server.sendMessageToAll(update);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			BufferController.tokenUpdate = update;
			// updateNextPrev(update);

		} else {
			BufferController.cli.publishString("###AfterBombCheck# I'm just passing the data: AfterBombCheck ####");

			AfterBombCheck afterbombcheck = new AfterBombCheck();
			afterbombcheck.setArea(getArea());
			afterbombcheck.setPlayer(getPlayer());
			afterbombcheck.setList(getList());

			BufferController.cli.publishString("###AfterBombCheck# Getting informations ####");

			if (getArea() == BufferController.me.getArea(BufferController.mygame.getDimension())) {
				BufferController.cli.returnBomb("Bomb killed you.");

			} else {
				afterbombcheck.add(BufferController.me);
				BufferController.cli.returnBomb("Bomb didn't kill you.");
			}

			try {
				BufferController.server.sendMessageToPlayer(BufferController.next, afterbombcheck);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		}
		return true;
	}

}
