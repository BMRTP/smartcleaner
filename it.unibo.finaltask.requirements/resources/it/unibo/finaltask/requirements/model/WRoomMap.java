package it.unibo.finaltask.requirements.model;

public abstract class WRoomMap {

	public boolean isExplored() {
		for (int x = 0; x < width(); x++) {
			for (int y = 0; y < height(); y++) {
				if (surface(x, y) == SurfaceType.Dirty || surface(x, y) == SurfaceType.Unknow) {
					return false;
				}
			}
		}
		return true;
	}

	public abstract int width();

	public abstract int height();

	public abstract SurfaceType surface(int x, int y);

}