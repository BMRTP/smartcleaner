package it.unibo.finaltask.requirements.utils;

import it.unibo.finaltask.requirements.model.*;

public class PropertyUtils {
	public static WRoomMap waitUntilRoomIsExplored() {
		
		return new WRoomMap() {
			@Override
			public int width() {
				return 9;
			}

			@Override
			public int height() {
				return 7;
			}

			@Override
			public SurfaceType surface(int x, int y) {
				return SurfaceType.Clean;
			}
			
			@Override
			public String toString() {
				return String.join(
						   "\n", 
						   "X, X, X, X, X, X, X, X,   ",
						   "X, r, 1, 1, 1, 1, 1, 1, X,",
						   "X, 1, 1, 1, 1, 1, 1, 1, X,",
						   "X, 1, 1, 1, 1, 1, 1, 1, X,",
						   "X, 1, 1, 1, 1, 1, 1, 1, X,",
						   "X, 1, 1, 1, 1, 1, 1, 1, X,",
						   "X, X, X, X, X, X, X, X, X,");
			}
			
		};
	}
	
	public static WRoomMap getInitialState() {
		return new WRoomMap() {
			@Override
			public int width() {
				return 9;
			}

			@Override
			public int height() {
				return 7;
			}

			@Override
			public SurfaceType surface(int x, int y) {
				return SurfaceType.Clean;
			}
			
			@Override
			public String toString() {
				return String.join(
						   "\n", 
						   "X, X,",
						   "X, r,");
			}
			
		};
	}
}
