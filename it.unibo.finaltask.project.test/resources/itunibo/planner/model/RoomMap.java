package itunibo.planner.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomMap implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static RoomMap singletonRoomMap;
	public static RoomMap getRoomMap() {
		if (singletonRoomMap == null)
			singletonRoomMap = new RoomMap();
		return singletonRoomMap;
	}
	public static void setRoomMap( RoomMap map ) { 
		singletonRoomMap = map;
 	}
	
	public static RoomMap mapFromString(String map) {
		int x = 0, y = 0;
		RoomMap room = new RoomMap();
		for(int i = 0; i < map.length(); i++) {
			if(map.charAt(i) == 'X') {
				room.put(x, y, new Box(true, false, false));
				x++;
			}
			if(map.charAt(i) == '1') {
				room.put(x, y, new Box(false, false, false));
				x++;
			}
			if(map.charAt(i) == '0') {
				room.put(x, y, new Box(false, true, false));
				x++;
			}
			if(map.charAt(i) == 'r') {
				room.put(x, y, new Box(false, false, true));
				x++;
			}
			if(map.charAt(i) == '\n') {
				x = 0;
				y++;
			}
		}
		return room;
	}
	
	private List<ArrayList<Box>> roomMap = new ArrayList<ArrayList<Box>>();
	
	private RoomMap() {
		super();
		for (int i=0; i<1; i++) {
			roomMap.add(new ArrayList<Box>());
			for (int j=0; j<1; j++) {
				roomMap.get(i).add(null);
			}
		}
		this.put(0, 0, new Box(true, false, false)); //top left angle
		this.put(1, 0, new Box(true, false, false)); //plasticbox
	}
	
//	public Map<Coordinate, Box> getMapClone() {
//		return new HashMap<>(this.roomMap);
//	}


	public void put(int x, int y, Box box) {
		try {
			roomMap.get(y);
		} catch (IndexOutOfBoundsException e) {
			for (int i=roomMap.size(); i<y; i++) {
				roomMap.add(new ArrayList<Box>());
			}
			roomMap.add(y, new ArrayList<Box>());
		}
		try {
			roomMap.get(y).get(x);
			roomMap.get(y).remove(x);
			roomMap.get(y).add(x, box);
		} catch (IndexOutOfBoundsException e) {
			for (int j=roomMap.get(y).size(); j<x; j++) {
				roomMap.get(y).add(new Box(false, true, false));
			}
			roomMap.get(y).add(x, box);
		}
	}
	
	public boolean isObstacle(int x, int y) {
		try {
			Box box = roomMap.get(y).get(x);
			//System.out.println(" ... RoomMap  isObstacle " + box.isObstacle());
			if  (box == null)
				return false;
			if (box.isObstacle())
				return true;
			else
				return false;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	
	public boolean isDirty(int x, int y) {
		try {
			Box box = roomMap.get(y).get(x);
			if  (box == null)
				return true;
			if (box.isDirty())
				return true;
			else
				return false;
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}
	
	public boolean isRobot(int x, int y) {
		try {
			Box box = roomMap.get(y).get(x);
			if  (box == null)
				return false;
			if (box.isRobot())
				return true;
			else
				return false;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	
	public boolean canMove(int x, int y, RobotState.Direction direction) {
		switch (direction) {
		case UP: return canMoveUp(x, y);
		case RIGHT: return canMoveRight(x, y);
		case DOWN: return canMoveDown(x, y);
		case LEFT: return canMoveLeft(x, y);
		default: throw new IllegalArgumentException("Not a valid direction");
		}
	}
	
	public boolean canMoveUp(int x, int y) {
		if (y<=0)
			return false;
		try {
			Box box = roomMap.get(y-1).get(x);
			if  (box == null)
				return true;
			if (box.isObstacle())
				return false;
			return true;
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}
	
	public boolean canMoveRight(int x, int y) {
		try {
			Box box = roomMap.get(y).get(x+1);
			if  (box == null)
				return true;
			if (box.isObstacle())
				return false;
			return true;
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}

	public boolean canMoveDown(int x, int y) {
		try {
			Box box = roomMap.get(y+1).get(x);
			if  (box == null)
				return true;
			if (box.isObstacle())
				return false;
			return true;
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}
	
	public boolean canMoveLeft(int x, int y) {
		if (x<=0)
			return false;
		try {
			Box box = roomMap.get(y).get(x-1);
			if  (box == null)
				return true;
			if (box.isObstacle())
				return false;
			return true;
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (ArrayList<Box> a : roomMap) {
			builder.append("|");
			for (Box b : a) {
				if (b == null)
					break;
				if (b.isRobot())
					builder.append("r, ");
				else if (b.isObstacle())
					builder.append("X, ");
				else if (b.isDirty())
					builder.append("0, ");
				else
					builder.append("1, ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}
	
	public int getDimX() {
		int result=0;
		for (int i=0; i<roomMap.size(); i++) {
			if (result<roomMap.get(i).size())
				result = roomMap.get(i).size();
		}
		return result;
	}
	
	public int getDimY() {
		return roomMap.size();
	}
	
	public boolean isClean() {
		for (ArrayList<Box> row : roomMap) {
			for (Box b : row)
				if (b.isDirty())
					return false;
		}
		return true;
	}
	
	public void setObstacles() {
		for (ArrayList<Box> row : roomMap) {
			for (Box b : row) {
				if (!b.isObstacle() && b.isDirty()) {
					b.setDirty(false);
					b.setObstacle(true);
				}
			}
		}
	}
	
	public void setDirty() {
		for (ArrayList<Box> row : roomMap) {
			for (Box b : row) {
				if (!b.isObstacle() && !b.isDirty() && !b.isRobot()) //Robot is always clean
					b.setDirty(true);
			}
		}
	}
	
}