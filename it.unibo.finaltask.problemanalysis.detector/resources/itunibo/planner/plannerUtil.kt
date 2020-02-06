package itunibo.planner

import java.util.ArrayList
import aima.core.agent.Action
import aima.core.search.framework.SearchAgent
import aima.core.search.framework.problem.GoalTest
import aima.core.search.framework.problem.Problem
import aima.core.search.framework.qsearch.GraphSearch
import aima.core.search.uninformed.BreadthFirstSearch
import java.io.PrintWriter
import java.io.FileWriter
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.FileInputStream
import itunibo.planner.model.RobotState
import itunibo.planner.model.Functions
import itunibo.planner.model.RobotState.Direction
import itunibo.planner.model.RobotAction
import itunibo.planner.model.RoomMap
import itunibo.planner.model.Box
import kotlinx.coroutines.delay

object plannerUtil {
	private var initialState: RobotState? = null
	private var actions: List<Action>? = null
	private var search: BreadthFirstSearch? = null
	var goalTest: GoalTest = Functions()        //init
	private var timeStart: Long = 0

	@Throws(Exception::class)
	fun initAI() {
		println("plannerUtil initAI")
		initialState = RobotState(1, 1, RobotState.Direction.DOWN)
		search = BreadthFirstSearch(GraphSearch())
	}

	var currentGoalApplicable = true;

	@Throws(Exception::class)
	fun doPlan(): List<Action>? {
		if (!currentGoalApplicable) {
			println("plannerUtil doPlan cannot go into an obstacle")
			return null
		}

		val searchAgent: SearchAgent
		val problem = Problem(initialState, Functions(), Functions(), goalTest, Functions())


		searchAgent = SearchAgent(problem, search!!)
		actions = searchAgent.actions

		println("plannerUtil doPlan actions=$actions")

		if (actions == null || actions!!.isEmpty()) {
			println("plannerUtil doPlan NO MOVES !!!!!!!!!!!! $actions!!")
			if (!RoomMap.getRoomMap().isClean) RoomMap.getRoomMap().setObstacles()
			return actions
		} else if (actions!![0].isNoOp) {
			println("plannerUtil doPlan NoOp")
			return actions
		}
		return actions
	}
/*
* ------------------------------------------------
* MAP UPDATE
* ------------------------------------------------
*/

	fun getPosX(): Int {
		return initialState!!.x
	}

	fun getPosY(): Int {
		return initialState!!.y
	}
	
	fun getRobotPosition(): Pair<Int, Int> {
		return Pair(initialState!!.x, initialState!!.y)
	}

	fun doMove(move: String) {
		val dir = initialState!!.direction
		val dimMapx = RoomMap.getRoomMap().dimX
		val dimMapy = RoomMap.getRoomMap().dimY
		val x = initialState!!.x
		val y = initialState!!.y
		try {
			when (move) {
				"w" -> {
					RoomMap.getRoomMap().put(x, y, Box(false, false, false)) //clean the cell
					initialState = Functions().result(initialState!!, RobotAction(RobotAction.FORWARD)) as RobotState
					RoomMap.getRoomMap().put(initialState!!.x, initialState!!.y, Box(false, false, true))
				}
				"s" -> {
					initialState = Functions().result(initialState!!, RobotAction(RobotAction.BACKWARD)) as RobotState
					RoomMap.getRoomMap().put(initialState!!.x, initialState!!.y, Box(false, false, true))
				}
				"a" -> {
					initialState = Functions().result(initialState!!, RobotAction(RobotAction.TURNLEFT)) as RobotState
					RoomMap.getRoomMap().put(initialState!!.x, initialState!!.y, Box(false, false, true))
				}
				"l" -> {
					initialState = Functions().result(initialState!!, RobotAction(RobotAction.TURNLEFT)) as RobotState
					RoomMap.getRoomMap().put(initialState!!.x, initialState!!.y, Box(false, false, true))
				}
				"d" -> {
					initialState = Functions().result(initialState!!, RobotAction(RobotAction.TURNRIGHT)) as RobotState
					RoomMap.getRoomMap().put(initialState!!.x, initialState!!.y, Box(false, false, true))
				}
				"r" -> {
					initialState = Functions().result(initialState!!, RobotAction(RobotAction.TURNRIGHT)) as RobotState
					RoomMap.getRoomMap().put(initialState!!.x, initialState!!.y, Box(false, false, true))
				}
				"c"
				-> {
					RoomMap.getRoomMap().put(x, y, Box(false, false, false))
					initialState = Functions().result(initialState!!, RobotAction(RobotAction.FORWARD)) as RobotState
					RoomMap.getRoomMap().put(initialState!!.x, initialState!!.y, Box(false, false, true))
				}
				//Box(boolean isObstacle, boolean isDirty, boolean isRobot)
				"rightDir" -> RoomMap.getRoomMap().put(x + 1, y, Box(true, false, false))
				"leftDir" -> RoomMap.getRoomMap().put(x - 1, y, Box(true, false, false))
				"upDir" -> RoomMap.getRoomMap().put(x, y - 1, Box(true, false, false))
				"downDir" -> RoomMap.getRoomMap().put(x, y + 1, Box(true, false, false))

			}
		} catch (e: Exception) {
			println("plannerUtil doMove: ERROR:" + e.message)
		}
	}

	fun showMap() {
		println(RoomMap.getRoomMap().toString())
	}

	fun saveMap(fname: String): Pair<Int, Int> {
		println("saveMap in $fname")
		val pw = PrintWriter(FileWriter(fname + ".txt"))
		pw.print(RoomMap.getRoomMap().toString())
		pw.close()

		val os = ObjectOutputStream(FileOutputStream(fname + ".bin"))
		os.writeObject(RoomMap.getRoomMap())
		os.flush()
		os.close()
		return getMapDims()
	}

	fun loadRoomMap(fname: String): Pair<Int, Int> {
		try {
			val inps = ObjectInputStream(FileInputStream("${fname}.bin"))
			val map = inps.readObject() as RoomMap;
			println("loadRoomMap = $fname DONE")
			RoomMap.setRoomMap(map)
		} catch (e: Exception) {
			println("loadRoomMap = $fname FAILURE")
		}
		return getMapDims()
	}

	fun getMapDims(): Pair<Int, Int> {
		if (RoomMap.getRoomMap() == null) {
			return Pair(0, 0)
		}
		val dimMapx = RoomMap.getRoomMap().getDimX()
		val dimMapy = RoomMap.getRoomMap().getDimY()
		return Pair(dimMapx, dimMapy)
	}

	fun getMap(): String {
		return RoomMap.getRoomMap().toString()
	}

	fun setGoal(x: Int, y: Int, direction: Direction? = null) {
		try {
			println("setGoal $x,$y while robot in cell: ${getPosX()}, ${getPosY()} direction=${getDirection()}")
			RoomMap.getRoomMap().put(x, y, Box(false, true, false))
			goalTest = GoalTest { state: Any ->
				val robotState = state as RobotState
				(robotState.x == x && robotState.y == y && (direction == null || robotState.direction == direction))
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	fun startTimer() {
		timeStart = System.currentTimeMillis()
	}

	fun getDuration(): Int {
		val duration = (System.currentTimeMillis() - timeStart).toInt()
		println("DURATION = $duration")
		return duration
	}

	fun getDirection(): String {
		val direction = initialState!!.direction
		when (direction) {
			Direction.UP -> return "upDir"
			Direction.RIGHT -> return "rightDir"
			Direction.LEFT -> return "leftDir"
			Direction.DOWN -> return "downDir"
			else -> return "unknownDir"
		}
	}

/*
* ------------------------------------------------
* ADDED Code
* ------------------------------------------------
*/
	fun getNextDirtyCell(): Pair<Int, Int> {
		val lenght = RoomMap.getRoomMap().getDimX()
		val height = RoomMap.getRoomMap().getDimY()
		for (i in 1..lenght - 1) {
			for (j in 1..height - 1) {
				if (RoomMap.getRoomMap().isDirty(i, j)) {
					return Pair(i, j)
				}
			}
		}
		return Pair(1, 1) //default value
	}

	fun getPlanMoves(x: Int, y: Int, direction: Direction? = null): Iterator<String> {
		setGoal(x, y, direction)
		return doPlan()!!.map { it.toString() }.iterator()
	}

	fun getSafePlanMoves(x: Int, y: Int, direction: Direction? = null): Iterator<String> {
		val oldMap = RoomMap.getRoomMap()
		val newMap = RoomMap.mapFromString(oldMap.toString().replace('0', 'X'))

		newMap.put(x, y, Box(false, true, false))
		RoomMap.setRoomMap(newMap)
		val res = getPlanMoves(x, y, direction)
		RoomMap.setRoomMap(oldMap)
		return res
	}

	fun setObstacle(x: Int, y: Int) {
		RoomMap.getRoomMap().put(x, y, Box(true, false, false))
	}

	fun isRoomClean(): Boolean {
		return RoomMap.getRoomMap().isClean()
	}

	fun goHomeMoves(): Iterator<String> {
		if (initialState!!.getX() == 1 && initialState!!.getY() == 1) {
			return listOf<String>().iterator()
		} else {
			return getSafePlanMoves(1, 1, Direction.DOWN)
		}
	}

	fun goPlasticBoxMoves(): Iterator<String> {
		return goHomeMoves()
	}

	fun getAHeadPosition(): Pair<Int, Int> {
		val dir = initialState!!.direction
		var x = initialState!!.x
		var y = initialState!!.y
		when (dir) {
			Direction.UP -> y -= 1
			Direction.RIGHT -> x += 1
			Direction.LEFT -> x -= 1
			Direction.DOWN -> y += 1
		}
		return Pair(x, y)
	}

	fun rotateRight90() {
		plannerUtil.doMove("r")
	}

	fun rotateLeft90() {
		plannerUtil.doMove("l")
	}

	fun updateMapAfterAheadOk() {
		plannerUtil.doMove("w")
	}

	fun setObstacleOnCurrentDirection() {
		plannerUtil.doMove(plannerUtil.getDirection())
	}
}



