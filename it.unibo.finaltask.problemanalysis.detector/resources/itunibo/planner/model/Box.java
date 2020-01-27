package itunibo.planner.model;

import java.io.Serializable;

public class Box implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean isObstacle;
	private boolean isDirty;
	private boolean isRobot;
	
	public Box(boolean isObstacle, boolean isDirty, boolean isRobot) {
		this.isObstacle = isObstacle;
		this.isDirty    = isDirty;
		this.isRobot    = isRobot;
	}
	
	public Box(boolean isObstacle, boolean isDirty) {
		this(isObstacle, isDirty, false);
	}
	
	public Box() {
		this(false, true);
	}
	
	public void setRobot(boolean isRobot) {
		this.isRobot = isRobot;
	}
	
	public boolean isRobot() {
		return this.isRobot;
	}
	
	public boolean isObstacle() {
		return this.isObstacle;
	}
	
	public void setObstacle(boolean isObstacle) {
		this.isObstacle = isObstacle;
	}
	
	public boolean isDirty() {
		return this.isDirty;
	}
	
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
}
