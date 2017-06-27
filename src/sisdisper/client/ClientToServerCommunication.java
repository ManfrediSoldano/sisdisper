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
import sisdisper.server.model.comunication.GetGames;




public class ClientToServerCommunication {

	public Game getGamesFromServer(){
		try {

			 ClientConfig config = new ClientConfig();
			 
			 
		      Client client = ClientBuilder.newClient(config);
		      WebTarget service = client.target(getBaseURI());
		      GetGames game = service.path("get").request().accept(MediaType.APPLICATION_XML).get(GetGames.class);
		      //String game = service.path("get").request().accept(MediaType.APPLICATION_XML).get(String.class);
		      System.out.println(game.getGames().get(1).getId());
		      
		  } catch (Exception e) {

			e.printStackTrace();

		  }
		return null;
	}
	
	private static URI getBaseURI() {
	    return UriBuilder.fromUri("http://localhost:8080/sisdisper/rest/RestServer").build();
	  }
	
	
}

