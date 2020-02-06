package itunibo.planner

enum class PlanMove {
	FORWARD, RIGHT, LEFT, NONE
}

interface Plan {
	fun isDone(): Boolean
	fun isNotDone(): Boolean
	fun getNextMove(): PlanMove
}

class RobotPlan(val moves: Iterator<String>) : Plan {
	
	companion object {
		fun empty(): Plan =  RobotPlan(listOf<String>().iterator())
	}
	
	override fun isDone(): Boolean {
		return !moves.hasNext();
	}
	
	override fun isNotDone(): Boolean {
		return !isDone();
	}
	
	override fun getNextMove(): PlanMove {
		var planMove = PlanMove.NONE
		if(moves.hasNext()) {
			when(moves.next()) {
				"w" -> planMove = PlanMove.FORWARD
				"d" -> planMove = PlanMove.RIGHT
				"a" -> planMove = PlanMove.LEFT
				else -> planMove = PlanMove.NONE
			}
		}
		return planMove
	}
}