package sisdisper.server.controller;


import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebServlet;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import sisdisper.server.model.Game;
import sisdisper.server.model.Player;
import sisdisper.server.model.comunication.AddToGame;
import sisdisper.server.model.comunication.GetGames;
import sisdisper.server.model.comunication.ResponseAddToGame;

@Path("/RestServer")
public class Comunication extends Observable {
	private RestServer rest;
	public Comunication() {
		
		RestServer.getIstance();
		
	}
	  // Match 
	  
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
	  @Path("/analytics")
	  @Consumes(MediaType.APPLICATION_XML)
	  public ResponseAddToGame newAnaliticalPLayer(Player player){
		  return RestServer.getIstance().newAnalyst(player.getId(),player.getIp());
		  
	  }
	 
	  @GET
	  @Path("/analytics/{gameid}")
	  @Produces(MediaType.APPLICATION_XML)
	  public Game getGame(@PathParam("gameid") String gameid) {

		return RestServer.getIstance().getGame(gameid);
	  }
	  
	  @GET
	  @Path("/analytics")
	  @Produces(MediaType.APPLICATION_XML)
	  public GetGames getGames() {

		return RestServer.getIstance().getAnalystGames();
	  }
	  
	  @GET
	  @Path("/analytics/players")
	  @Produces(MediaType.APPLICATION_XML)
	  public GetGames getPlayers() {

		return RestServer.getIstance().getAnalystGames();
	  }
	  
	  @GET
	  @Path("/analytics/live/{userid}")
	  @Produces(MediaType.APPLICATION_XML)
	  public GetGames liveStatus(@Suspended AsyncResponse asyncResp, @PathParam("userid") String userid) {
		try {
			RestServer.getIstance().waiters.put(userid, asyncResp);
		} catch (Exception e) {
			e.printStackTrace();
		}  
		return RestServer.getIstance().getGames();
	  }
	  
	

}
