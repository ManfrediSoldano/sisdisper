package sisdisper.client;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.model.Buffer;
import sisdisper.client.model.action.AdviceBomb;
import sisdisper.client.model.action.ExplodingBomb;
import sisdisper.client.socket.Server;
import sisdisper.server.model.Area;
import sisdisper.server.model.Player;

public class BombManager implements Runnable  {
	private Thread t;
	public Server server;
	private Player me;
	private Area area;
	
	public void start() {
		t = new Thread(this);
		t.start();

		
	}
	
	public BombManager(Server server, Player player, Area area){
		this.server = server;
		me = player;
		this.area = area;
	}
	
	@Override
	public void run() {
		AdviceBomb advicebomb = new AdviceBomb();
		advicebomb.player = me;
		advicebomb.area = area;
		try {
			server.sendMessageToAll(advicebomb);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ExplodingBomb explodingBomb = new ExplodingBomb();
		explodingBomb.player = me;
		explodingBomb.area = area;
		
		try {
			server.sendMessageToAll(explodingBomb);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		
		try {
			Buffer.addAction(explodingBomb, null);
		} catch (JsonProcessingException | JAXBException | InterruptedException e) {
			
			e.printStackTrace();
		}
		
		
		
	}

}
