package sisdisper.client.model.action;

import org.apache.el.parser.Token;

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
}
