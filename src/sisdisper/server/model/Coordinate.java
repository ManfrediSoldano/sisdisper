package sisdisper.server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Coordinate")
public class Coordinate {
private int x=-5;
private int y=-5;
public int getX() {
	return x;
}
public void setX(int x) {
	this.x = x;
}
public int getY() {
	return y;
}
public void setY(int y) {
	this.y = y;
}
public Boolean equal(Coordinate other){
	if(other.x == x &&  other.y == y){
		return true;
	}
	return false;
}

}
