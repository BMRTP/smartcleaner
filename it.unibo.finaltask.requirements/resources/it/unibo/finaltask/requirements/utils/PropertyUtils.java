package it.unibo.finaltask.requirements.utils;

import it.unibo.finaltask.requirements.model.*;

public class PropertyUtils {
	public static WRoom waitUntilRoomIsExplored() {
		
		return new WRoom() {
			@Override
			public int width() {
				return 5;
			}

			@Override
			public int height() {
				return 5;
			}

			@Override
			public SurfaceType surface(int x, int y) {
				return SurfaceType.Clean;
			}
			
		};
	}
}
