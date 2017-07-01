package sisdisper.server.model;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Player")
public class Player {
	
	private String id;
	private String ip;
	private int port;
	private int point;
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
	
	
	

}
