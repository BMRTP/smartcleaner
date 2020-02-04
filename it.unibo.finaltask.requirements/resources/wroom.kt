package it.unibo.finaltask.requirements


enum class SurfaceType {
	Unknow,
	Clean,
	Dirty,
	Robot,
	Obstacle
}

abstract class WRoom {
	
	fun isClean(): Boolean {
		for (x in 0..width()) {
			for (y in 0..height()) {
				if (surface(x to y) == SurfaceType.Dirty ||
					surface(x to y) == SurfaceType.Unknow) {
					return false
				}
			}
		}
		return true
	}

	abstract fun width(): Int
	
	abstract fun height(): Int
	
	abstract fun surface(cell: Pair<Int, Int>): SurfaceType

}