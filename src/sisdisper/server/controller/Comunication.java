package sisdisper.server.controller;


import java.util.Observable;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;




import sisdisper.server.model.Game;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;

@Path("/RestServer")
public class Comunication extends Observable {
	private RestServer rest;
 
	public Comunication() {
		
		RestServer.getIstance();
		
	}
	  // This method is called if XML is request
	  
	  @GET
	  @Path("/get")
	  @Produces(MediaType.APPLICATION_XML)
	  public GetGames sayXMLHello() {

		return RestServer.getIstance().getGames();
	  }
	  
	  @POST
	  @Path("/post")
	  @Consumes(MediaType.APPLICATION_XML)
	  public String newGame(Game game){
		  return RestServer.getIstance().postNewGame(game);
		  
	  }
	  
	  @PUT
	  @Path("/put")
	  @Consumes(MediaType.APPLICATION_XML)
	  public ResponseAddToGame addMeOnAGame(AddToGame game){
		  return RestServer.getIstance().addMeOnAGame(game);
		  
	  }
	  
	  @DELETE
	  @Path("/delete/{userid}/{gameid}/{points}/{winner}")
	  @Consumes(MediaType.APPLICATION_XML)
	  public String deleteMeFromTheGame(@PathParam("userid") String userid, @PathParam("gameid") String gameid , @PathParam("points") String points , @PathParam("winner") String winner){
	 
		  return RestServer.getIstance().deleteMeFromTheGame(userid,gameid, points, winner);
		  
	  }
	  
	  //Analytics
	  
	  @POST
	  @Path("/analytics/{userid}/{port}")
	  @Consumes(MediaType.APPLICATION_XML)
	  public ResponseAddToGame newAnaliticalPLayer(@PathParam("userid") String userid, @PathParam("port") String ip){
		  return RestServer.getIstance().newAnalyst(userid,ip);
		  
	  }
	 
	  @GET
	  @Path("/analytics/{gameid}")
	  @Produces(MediaType.APPLICATION_XML)
	  public GetGames getGame(@PathParam("gameid") String gameid, @PathParam("userid") String userid) {

		return RestServer.getIstance().getGames();
	  }
	  
	

}
