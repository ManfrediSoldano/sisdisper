package sisdisper.client.model.action;


public class MoveCLI  implements Action  {
 
	public enum Where {
		UP,
		DOWN,
		RIGHT,
		LEFT
	}
	
	private Where where;

	public Where getWhere() {
		return where;
	}

	public void setWhere(Where where) {
		this.where = where;
	}
	
}
