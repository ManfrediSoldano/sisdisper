package sisdisper.client.model.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sisdisper.server.model.Game;

public class Action implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	
	public Boolean execute(Game game){
		return true;
	}

	public String serialize(){
		 try {	     
		     ByteArrayOutputStream bo = new ByteArrayOutputStream();
		     ObjectOutputStream so = new ObjectOutputStream(bo);
		     so.writeObject(this);
		     so.flush();
		     return bo.toString("ISO-8859-1");
		    
		 } catch (Exception e) {
		     System.err.println(e);
		     return null;
		 }
	}
	
	public Action deserialize(String scommand){
		 try {
			  byte b[] = scommand.getBytes("ISO-8859-1");  
		      ByteArrayInputStream bi = new ByteArrayInputStream(b);
		      ObjectInputStream si = new ObjectInputStream(bi);
		      Action obj = (Action) si.readObject();
		      return obj;
		     } catch (Exception e) {
		    	 
			     System.err.println(e + scommand);	    
	    
		     return null;
		 }
	}

	
}
