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

object plannerUtil { 
    private var initialState: RobotState? = null
	private var actions: List<Action>?    = null
/*
 * ------------------------------------------------
 * PLANNING
 * ------------------------------------------------
 */
    private var search: BreadthFirstSearch? = null
    var goalTest: GoalTest = Functions()		//init
    private var timeStart: Long = 0

    @Throws(Exception::class)
    fun initAI() {
        println("plannerUtil initAI")
        initialState = RobotState(1, 1, RobotState.Direction.DOWN)
        search       = BreadthFirstSearch(GraphSearch())
    }

	fun resetRobotPos(x: Int, y:Int, oldx: Int, oldy: Int, direction: String ){
        //println("plannerUtil resetRobotPos direction=$direction")
		RoomMap.getRoomMap().put(oldx,oldy, Box(false, false, false))	
		RoomMap.getRoomMap().put(x,y, Box(false, false, true) )	

		var dir     = RobotState.Direction.DOWN  //init
		when( direction ){
			"down"   -> dir = RobotState.Direction.DOWN
			"up"     -> dir = RobotState.Direction.UP
			"left"   -> dir = RobotState.Direction.LEFT
			"right"  -> dir = RobotState.Direction.RIGHT			
		}
        initialState = RobotState(x,y, dir)
        var canMove = RoomMap.getRoomMap().canMove( x,y, initialState!!.direction  );
        println("resetRobotPos $x,$y from: ${oldy},${oldy} direction=${getDirection()} canMove=$canMove")
	}
	
	fun resetGoal( x: String, y: String) {
		resetGoal( Integer.parseInt(x), Integer.parseInt(y))
	}
	
	var currentGoalApplicable = true;
	
    fun resetGoal( x: Int, y: Int) {
        try {
//			var canMove = RoomMap.getRoomMap().canMove( x,y-1, initialState!!.direction  );
//            println("resetGoal $x,$y while robot in cell: ${getPosX()},${getPosY()} direction=${getDirection()} canMove=$canMove")
            
			if( RoomMap.getRoomMap().isObstacle(x,y) ) {
				println("ATTEMPT TO GO INTO AN OBSTACLE ")
				currentGoalApplicable = false
			}else currentGoalApplicable = true
            
			RoomMap.getRoomMap().put(x, y, Box(false, true, false))  //set dirty

			goalTest = GoalTest { state  : Any ->
                val robotState = state as RobotState
				(robotState.x == x && robotState.y == y)
            }
			showMap()
			//doPlan()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
	
	
	fun changeDirectionIfNextCellObstacle( x: Int, y: Int) {
		val direction = initialState!!.direction
		when (direction) {
			 Direction.UP     -> if( ! RoomMap.getRoomMap().canMoveUp( x-1, y ) ){
				 						initialState = RobotState(x, y, RobotState.Direction.LEFT )	}	 
			 Direction.RIGHT  -> if( ! RoomMap.getRoomMap().canMoveUp( x+1, y ) ){
				 						initialState = RobotState(x, y, RobotState.Direction.DOWN )	} 
			 Direction.DOWN  ->  if( ! RoomMap.getRoomMap().canMoveUp( x+1, y ) ){
				 						initialState = RobotState(x, y+1, RobotState.Direction.RIGHT )  }
			 Direction.LEFT  ->  if( ! RoomMap.getRoomMap().canMoveUp( x+1, y ) ){
				 						initialState = RobotState(x-1, y, RobotState.Direction.DOWN )  }
			 else -> throw IllegalArgumentException("Not a valid direction");
		}		
	}
	
	
    fun getActions() : List<Action>{
        return actions!!
    }
 
    @Throws(Exception::class)
    fun doPlan(): List<Action>? {
        //var actions: List<Action>?
		
		if( ! currentGoalApplicable ){
			println("plannerUtil doPlan cannot go into an obstacle")
			return null
		} 
		
        val searchAgent: SearchAgent
        //println("plannerUtil doPlan newProblem (A) $goalTest" );
		val problem = Problem(initialState, Functions(), Functions(), goalTest, Functions())
		
		
        //println("plannerUtil doPlan newProblem (A) search " );
        searchAgent = SearchAgent(problem, search!!)
        actions     = searchAgent.actions
		
		println("plannerUtil doPlan actions=$actions")
		
        if (actions == null || actions!!.isEmpty()) {
            println("plannerUtil doPlan NO MOVES !!!!!!!!!!!! $actions!!"   )
            if (!RoomMap.getRoomMap().isClean) RoomMap.getRoomMap().setObstacles()
            //actions = ArrayList()
            return null
        } else if (actions!![0].isNoOp) {
            println("plannerUtil doPlan NoOp")
            return null
        }
		
        //println("plannerUtil doPlan actions=$actions")
        return actions
    }
	
    fun executeMoves( ) {
		if( actions == null ) return
        val iter = actions!!.iterator()
        while (iter.hasNext()) {
            plannerUtil.doMove(iter.next().toString())
        }
    }


/*
* ------------------------------------------------
* MAP UPDATE
* ------------------------------------------------
*/
	
    fun getPosX() : Int{ return initialState!!.x }
    fun getPosY() : Int{ return initialState!!.y }
     
    fun doMove(move: String) {
        val dir = initialState!!.direction
        val dimMapx = RoomMap.getRoomMap().dimX
        val dimMapy = RoomMap.getRoomMap().dimY
        val x = initialState!!.x 
        val y = initialState!!.y
       // println("plannerUtil: doMove move=$move  dir=$dir x=$x y=$y dimMapX=$dimMapx dimMapY=$dimMapy")
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
                "a"  -> {
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
                "c"    //forward and  clean
                -> {
                    RoomMap.getRoomMap().put(x, y, Box(false, false, false))
                    initialState = Functions().result(initialState!!, RobotAction(RobotAction.FORWARD)) as RobotState
                    RoomMap.getRoomMap().put(initialState!!.x, initialState!!.y, Box(false, false, true))
                }
				//Box(boolean isObstacle, boolean isDirty, boolean isRobot)
                "rightDir" -> RoomMap.getRoomMap().put(x + 1, y, Box(true, false, false)) 
                "leftDir"  -> RoomMap.getRoomMap().put(x - 1, y, Box(true, false, false))
                "upDir"    -> RoomMap.getRoomMap().put(x, y - 1, Box(true, false, false))
                "downDir"  -> RoomMap.getRoomMap().put(x, y + 1, Box(true, false, false))

		   }//switch
 			//RoomMap.getRoomMap().setObstacles()	
       } catch (e: Exception) {
            println("plannerUtil doMove: ERROR:" + e.message)
        }

//        val newdir = initialState!!.direction.toString().toLowerCase() + "Dir"
//        val x1     = initialState!!.x
//        val y1     = initialState!!.y
        //update the kb
        //println("plannerUtil: doMove move=$move newdir=$newdir x1=$x1 y1=$y1")
    }
     
    fun showMap() {
        println(RoomMap.getRoomMap().toString())
    }
	
    fun saveMap(  fname : String) : Pair<Int,Int> {		
        println("saveMap in $fname")
		val pw = PrintWriter( FileWriter(fname+".txt") )
		pw.print( RoomMap.getRoomMap().toString() )
		pw.close()
		
		val os = ObjectOutputStream( FileOutputStream(fname+".bin") )
		os.writeObject(RoomMap.getRoomMap())
		os.flush()
		os.close()
		return getMapDims()
    }
	
	fun loadRoomMap( fname: String  ) : Pair<Int,Int> {
//	    var dimMapx = 0
//	    var dimMapy = 0
		try{
 			val inps = ObjectInputStream(FileInputStream("${fname}.bin"))
			val map  = inps.readObject() as RoomMap;
//			println(map.toString())
//	        dimMapx = map.getDimX()
//	        dimMapy = map.getDimY()
//			println("dimMapx = $dimMapx, dimMapy=$dimMapy")
			println("loadRoomMap = $fname DONE")
			RoomMap.setRoomMap( map )
		}catch(e:Exception){			
			println("loadRoomMap = $fname FAILURE")
		}
		return getMapDims()//Pair(dimMapx,dimMapy)
	}
	
	fun getMapDims() : Pair<Int,Int> {
		if( RoomMap.getRoomMap() == null ){
			return Pair(0,0)
		}
	    val dimMapx = RoomMap.getRoomMap().getDimX()
	    val dimMapy = RoomMap.getRoomMap().getDimY()
	    //println("getMapDims dimMapx = $dimMapx, dimMapy=$dimMapy")
		return Pair(dimMapx,dimMapy)	
	}
			
	fun getMap() : String{
		return RoomMap.getRoomMap().toString() 
	}
	fun getMapOneLine() : String{ 
		return  "'"+RoomMap.getRoomMap().toString().replace("\n","@").replace("|","").replace(",","") +"'" 
	}
	
	
/*
 * ---------------------------------------------------------
 */
    fun setGoalInit() {
        goalTest = Functions()
    }

	fun setGoal( x: String, y: String) {
		setGoal( Integer.parseInt(x), Integer.parseInt(y))
	}	

	//Box(boolean isObstacle, boolean isDirty, boolean isRobot)
    fun setGoal( x: Int, y: Int) {
        try {
            println("setGoal $x,$y while robot in cell: ${getPosX()}, ${getPosY()} direction=${getDirection()}")	
            RoomMap.getRoomMap().put(x, y, Box(false, true, false))
			//initialState = RobotState(getPosX(), getPosY(), initialState!!.direction ) 
            goalTest = GoalTest { state  : Any ->
                val robotState = state as RobotState
				(robotState.x == x && robotState.y == y)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startTimer() {
        timeStart = System.currentTimeMillis()
    }
	
    fun getDuration() : Int{
        val duration = (System.currentTimeMillis() - timeStart).toInt()
		println("DURATION = $duration")
		return duration
    }
	
	fun getDirection() : String{
		//val direction = initialState!!.direction.toString()
		val direction = initialState!!.direction 
		when( direction ){
			Direction.UP    -> return "upDir"
			Direction.RIGHT -> return "rightDir"
			Direction.LEFT  -> return "leftDir"
			Direction.DOWN  -> return "downDir"
			else            -> return "unknownDir"
 		}
  	}

/*
 * Direction
 */
    fun rotateDirection() {
        //println("before rotateDirection: " + initialState.getDirection() );
        initialState = Functions().result(initialState!!, RobotAction(RobotAction.TURNLEFT)) as RobotState
        initialState = Functions().result(initialState!!, RobotAction(RobotAction.TURNLEFT)) as RobotState
        //println("after  rotateDirection: " + initialState.getDirection() );
        //update the kb
        val x = initialState!!.x
        val y = initialState!!.y
        val newdir = initialState!!.direction.toString().toLowerCase() + "Dir"
    }
	
 
    fun setObstacles(   ){
		RoomMap.getRoomMap().setObstacles()
 	}
	
	fun setObstacleWall(  dir: Direction, x:Int, y:Int){
		when( dir ){
			Direction.DOWN  -> RoomMap.getRoomMap().put(x, y + 1, Box(true, false, false))
			//Direction.UP    -> RoomMap.getRoomMap().put(x, y - 1, Box(true, false, false)) 
			//Direction.LEFT  -> RoomMap.getRoomMap().put(x - 1, y, Box(true, false, false)) 
			Direction.RIGHT -> RoomMap.getRoomMap().put(x + 1, y, Box(true, false, false)) 
 		}
	}
	
	fun wallFound(){
 		 val dimMapx = RoomMap.getRoomMap().getDimX()
		 val dimMapy = RoomMap.getRoomMap().getDimY()
		 val dir = initialState!!.getDirection()
		 val x   = initialState!!.getX()
		 val y   = initialState!!.getY()
		 setObstacleWall( dir,x,y )
 		 println("wallFound dir=$dir  x=$x  y=$y dimMapX=$dimMapx dimMapY=$dimMapy");
		 doMove( dir.toString() )  //set cell
 		 if( dir == Direction.UP)    setWallRight(dimMapx,dimMapy,x,y)
		 if( dir == Direction.RIGHT) setWallDown(dimMapx,dimMapy,x,y)  
	}
	
	fun setWallDown(dimMapx: Int,dimMapy: Int,x: Int,y: Int ){
		 var k   = 0
		 while( k < dimMapx ) {
			RoomMap.getRoomMap().put(k, y+1, Box(true, false, false))
			k++
		 }
		
	}
	
	fun setWallRight(dimMapx: Int,dimMapy: Int, x: Int,y: Int){
 		 var k   = 0
		 while( k < dimMapy ) {
			RoomMap.getRoomMap().put(x+1, k, Box(true, false, false))
			k++
		 }
		
	}
	// ADDED
	fun getNextDirtyCell(): Pair<Int,Int> {
		val lenght = RoomMap.getRoomMap().getDimX()
		val height = RoomMap.getRoomMap().getDimY()
		for(i in 1..lenght) {
			for(j in 1..height) {
				if(RoomMap.getRoomMap().isDirty(i, j)) {
					return Pair(i,j)
				}
			}
		}
		return Pair(0,0) //TODO: fixit
	}
	
	fun getPlanMoves(): Iterator<String> {
		return doPlan()!!.map { it.toString() }.iterator()
	}
}


