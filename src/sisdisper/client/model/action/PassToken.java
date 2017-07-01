package sisdisper.client.model.action;

import org.apache.el.parser.Token;

public class PassToken implements Action {
Token token = new Token();

public Token getToken() {
	return token;
}

public void setToken(Token token) {
	this.token = token;
}
}
