package sisdisper.client.model;

import java.util.ArrayList;

import sisdisper.server.model.Player;

public class Token {
ArrayList<Player> testList = new ArrayList<Player>();
Boolean isMine;
public Boolean getIsMine() {
	return isMine;
}

public void setIsMine(Boolean isMine) {
	this.isMine = isMine;
}

public ArrayList<Player> getTestList() {
	return testList;
}

public void setTestList(ArrayList<Player> testList) {
	this.testList = testList;
}


}
