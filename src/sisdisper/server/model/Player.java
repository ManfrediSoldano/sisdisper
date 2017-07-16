package sisdisper.server.model;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Player")
public class Player implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String ip;
	private int port;
	private int point;
	//private Boolean alive;
	public Area area=null;
	
	private Coordinate coordinate; 
	public String getId() {
		return id;
	}
	 @XmlElement

	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	 @XmlElement

	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	 @XmlElement

	public void setPort(int port) {
		this.port = port;
	}
	
	public Player(){
	
	}
	
	public Coordinate getCoordinate() {
		return coordinate;
	}
	 @XmlElement
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	public int getPoint() {
		return point;
	}
	 @XmlElement
	public void setPoint(int point) {
		this.point = point;
	}
	
	 public Area getArea(int dimension){
		 if(coordinate!=null){
			 int x = dimension/2;
			 int y = dimension/2;
			 int player_x = coordinate.getX();
			 int player_y = coordinate.getY();
			 if(player_x>=x && player_y>=y){
				 return Area.RED;
			 } else if(player_x<=x && player_y>=y){
				 return Area.GREEN;
			 } else if(player_x>=x && player_y<=y){
				 return Area.YELLOW;
			 } else if(player_x<=x && player_y<=y){
				 return Area.BLUE;
			 }
			 
		 }
		 return null;
	 }
	 
	
	
	
}
