package sisdisper.server.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Area")
public enum Area implements Serializable {
	
		GREEN,
		RED,
		BLUE,
		YELLOW
	
	
}
