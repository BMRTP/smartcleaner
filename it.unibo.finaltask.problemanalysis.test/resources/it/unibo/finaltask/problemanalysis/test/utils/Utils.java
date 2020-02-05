package it.unibo.finaltask.problemanalysis.test.utils;

import itunibo.planner.model.RoomMap;

public class Utils {
	public static final boolean robotIsInSubArea(int minX, int maxX, int minY, int maxY, RoomMap map) {
		Boolean cond = false;
		for(int x = minX; x <= maxX; x++) {
			for(int y = minY; y <= maxY; y++) {
				cond |= map.isRobot(x, y);
			}
		}
		return cond;
	}
	
	public static final boolean robotIsAtDiscoveryHome(RoomMap map) {
		return map.isRobot(1,1);
	}
	
}
