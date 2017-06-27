package sisdisper.server.controller;


import java.util.ArrayList;
import java.util.Observable;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
	
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;




import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.DeleteMe;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;

@Path("/RestServer")
public class Comunication extends Observable {
	private RestServer rest;

	public Comunication(){
		rest = new RestServer();
	}
	  // This method is called if XML is request
	  
	  @GET
	  @Path("/get")
	  @Produces(MediaType.APPLICATION_XML)
	  public GetGames sayXMLHello() {

		return rest.getGames();
	  }
	  
	  @POST
	  @Path("/post")
	  @Consumes(MediaType.APPLICATION_XML)
	  public String newGame(Game game){
		  return rest.postNewGame(game);
		  
	  }
	  
	  @PUT
	  @Path("/put")
	  @Consumes(MediaType.APPLICATION_XML)
	  public ResponseAddToGame addMeOnAGame(AddToGame game){
		  return rest.addMeOnAGame(game);
		  
	  }
	  
	  @DELETE
	  @Path("/delete")
	  @Consumes(MediaType.APPLICATION_XML)
	  public String deleteMeFromTheGame(DeleteMe player){
		  return rest.deleteMeFromTheGame(player);
		  
	  }
	  

	 
	  
	

}
