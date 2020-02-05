package itunibo.planner

import itunibo.model.RoomMap
import itunibo.model.SurfaceType

interface Planner {
	fun generateSafePlanForHome(): List<String> // List["w", "a", "w"...]
	fun generateSafePlanForPlasticBox(): List<String>
	fun generateSafePlanForDestination(destination: Pair<Int, Int>): List<String>
	fun generatePlanForExplore(): List<String>
}

class DummyPlanner: Planner {
	var wroomMap = RoomMap()
	override fun generateSafePlanForHome(): List<String> {
		return listOf<String>()
	}
	
	override fun generateSafePlanForPlasticBox(): List<String> {
		return listOf<String>()
	}
	
	override fun generateSafePlanForDestination(destination: Pair<Int, Int>): List<String>{
		val oldMap = wroomMap
		val newMap = RoomMap.mapFromString(oldMap.toString().replace('0', 'X'))
        wroomMap = newMap
        val res = generatePlanForDestination(destination)
        wroomMap = oldMap
        return res
	}
	
	private fun generatePlanForDestination(destination: Pair<Int, Int>): List<String>{
		return listOf<String>()
	}
	
	override fun generatePlanForExplore(): List<String>{
		return listOf<String>()
	}
	
	//From problem analysis
	
	fun getStringMap(): String {
		return wroomMap.toString()
	}
	
	fun setObstacle(cell: Pair<Int, Int>): Unit {
		wroomMap.setCell(cell, SurfaceType.Obstacle)
	}
	
	fun stepDone(): Unit {
		
	}
	
	fun rotationLeftDone(): Unit {
		
	}
	
	fun rotationRightDone(): Unit {
		
	}
	
	fun getRobotAheadPosition(): Pair<Int, Int> {
		return Pair(2,2);
	}
}