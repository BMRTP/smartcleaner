package itunibo.model;

import java.lang.SuppressWarnings

abstract class WRoomMap {
	
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

public class RoomMap: WRoomMap() {

	override fun width():Int {
		return 1;
	}
	override fun height():Int {
		return 1;
	}
	override fun surface(cell: Pair<Int, Int>): SurfaceType {
		return SurfaceType.Unknow;
	}
	
	//From problem analysis
	fun setCell(cell: Pair<Int, Int>, state: SurfaceType): Unit {
		
	}
	companion object {
		fun mapFromString(stringMap: String): RoomMap {
			return RoomMap();
		}
	}
	override fun toString(): String {
		return ""
	}
	
}

enum class SurfaceType {
	Unknow,
	Clean,
	Dirty,
	Robot,
	Obstacle
}