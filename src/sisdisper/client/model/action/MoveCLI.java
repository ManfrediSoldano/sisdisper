package sisdisper.client.model.action;


public class MoveCLI  extends Action  {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
