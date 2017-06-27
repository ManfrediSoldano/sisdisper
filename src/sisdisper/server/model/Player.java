package sisdisper.server.model;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Player")
public class Player {
	
	private String id;
	private String ip;
	private int port;
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
	
	
	

}
