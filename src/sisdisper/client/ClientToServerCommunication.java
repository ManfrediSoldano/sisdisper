package sisdisper.client;
import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.DeleteMe;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;




public class ClientToServerCommunication {

	public GetGames getGamesFromServer(){
		try {

			 ClientConfig config = new ClientConfig();
			 
			 
		      Client client = ClientBuilder.newClient(config);
		      WebTarget service = client.target(getBaseURI());
		      GetGames game = service.path("get").request().accept(MediaType.APPLICATION_XML).get(GetGames.class);
		      //String game = service.path("get").request().accept(MediaType.APPLICATION_XML).get(String.class);
		     // System.out.println(game.getGames().get(1).getId());
		      return game;
		  } catch (Exception e) {

			e.printStackTrace();
			return null;

		  }
	}
	
	public String createNewGame(Game game){
		try {

			 ClientConfig config = new ClientConfig();
			 
			 
		      Client client = ClientBuilder.newClient(config);
		      WebTarget service = client.target(getBaseURI());
		      String game_received = service.path("post").request(MediaType.APPLICATION_XML).post(Entity.entity(game,MediaType.APPLICATION_XML),String.class);
		      //String game = service.path("get").request().accept(MediaType.APPLICATION_XML).get(String.class);
		      //System.out.println(game_received);
		      return game_received;
		   } catch (Exception e) {

			e.printStackTrace();
			return null;
		  }
		
	}
	
	public ResponseAddToGame putMeOnaGame(AddToGame game){
		try {

			 ClientConfig config = new ClientConfig();
			 
			 
		      Client client = ClientBuilder.newClient(config);
		      WebTarget service = client.target(getBaseURI());
		      ResponseAddToGame game_received = service.path("put").request(MediaType.APPLICATION_XML).put(Entity.entity(game,MediaType.APPLICATION_XML),ResponseAddToGame.class);
		      //String game = service.path("get").request().accept(MediaType.APPLICATION_XML).get(String.class);
		     
		      return game_received;
		   } catch (Exception e) {

			e.printStackTrace();
			return null;
		  }
		
	}
	
	
	public String deleteMe(String playerid, String gameid){
		try {

			 ClientConfig config = new ClientConfig();
			 
			 
		      Client client = ClientBuilder.newClient(config);
		      WebTarget service = client.target(getBaseURI());
		      String game_received = service.path("delete").path(playerid).path(gameid).request(MediaType.APPLICATION_XML).delete(String.class);
		      //String game = service.path("get").request().accept(MediaType.APPLICATION_XML).get(String.class);
		      System.out.println("###Client2ServerCom## "+game_received+ "####");
		      return game_received;
		   } catch (Exception e) {

			e.printStackTrace();
			return null;
		  }
		
	}
	
	
	
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("http://localhost:8080/sisdisper/rest/RestServer").build();
	  }

	
	
}

