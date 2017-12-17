package sisdisper.client.model.action;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.el.parser.Token;

import com.fasterxml.jackson.core.JsonProcessingException;

import sisdisper.client.BombManager;
import sisdisper.client.BombObservable;
import sisdisper.client.BufferController;
import sisdisper.client.model.Buffer;
import sisdisper.server.model.Coordinate;

public class PassToken extends Action {
/**
	 * 
	 */
private static final long serialVersionUID = 1L;
Token token = new Token();
public int i=0;

public Token getToken() {
	return token;
}

public void setToken(Token token) {
	this.token = token;
}

public Boolean execute() {
	
	
return true;
}

}
