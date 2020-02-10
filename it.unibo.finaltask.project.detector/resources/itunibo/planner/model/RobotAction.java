package itunibo.planner.model;

import aima.core.agent.Action;

public class RobotAction implements Action {
	
	public static final int FORWARD   = 0;
	public static final int TURNRIGHT = 1;
	public static final int BACKWARD  = 2;
	public static final int TURNLEFT  = 3;
	
	private int action;
	
	public RobotAction(int action) {
		if (action < FORWARD || action > TURNLEFT)
			throw new IllegalArgumentException();
		this.action = action;
	}
	
	public int getAction() {
		return this.action;
	}

	@Override
	public boolean isNoOp() {
		return false;
	}
	
	@Override
	public String toString() {
		switch(action) {
		case FORWARD:   return "w";//"forward";
		case BACKWARD:  return "s";//"backward";
		case TURNRIGHT: return "d";//"turnRight";
		case TURNLEFT:  return "a";//"turnLeft";
		default: throw new IllegalArgumentException("Not a valid action");
		}
	}

}
