package itunibo.applUtil

import it.unibo.kactor.ActorBasic
import itunibo.planner.plannerUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import itunibo.planner.moveUtils

object applUtil {
	suspend fun changeDirection( actor : ActorBasic, toDir : String){
		var direction = plannerUtil.getDirection()
		println("		changeDirection to $toDir from $direction")
		if( direction.equals(toDir) ) return
 		when( toDir ){
			"upDir" ->  when( direction ) {
							"leftDir"  -> moveUtils.rotate(actor,"r")//forwardToResourcemodel(actor,"r")
							"rightDir" -> moveUtils.rotate(actor,"l") //forwardToResourcemodel(actor,"l")
					        "downDir"  -> turn180(actor)
					         else -> println("changeDirection NEVER HERE (upDir)")
 						} 
			"downDir" ->  when( direction ) {
							"leftDir"  -> moveUtils.rotate(actor,"l") //forwardToResourcemodel(actor,"l")
							"rightDir" -> moveUtils.rotate(actor,"r")//forwardToResourcemodel(actor,"r")
					        "upDir"    -> turn180(actor)
					        else -> println("changeDirection NEVER HERE (downDir)")
						} 
			"rightDir" ->  when( direction ) {
							"downDir"  -> moveUtils.rotate(actor,"l") //forwardToResourcemodel(actor,"l")
							"upDir"    -> moveUtils.rotate(actor,"r")//forwardToResourcemodel(actor,"r")
					        "leftDir"   -> turn180(actor)
					        else -> println("changeDirection NEVER HERE (rightDir)")
						} 
			"leftDir" ->  when( direction ) {
							"downDir"  -> moveUtils.rotate(actor,"r") //forwardToResourcemodel(actor,"r")
							"upDir"    -> moveUtils.rotate(actor,"l") //forwardToResourcemodel(actor,"l")
					        "rightDir"  -> turn180(actor)
					        else -> println("changeDirection NEVER HERE (leftDir)")
						} 
			 else -> println("changeDirection DIRECTION UNKNOWN")
		}
//		delay(1000) 
 
 		println("		changed Direction to  ${plannerUtil.getDirection()}" )
//		delay(5000) 
	}
	
	
//	suspend fun forwardToResourcemodel( actor : ActorBasic, move : String){
//		println("forwardToResourcemodel modelChange $move")
//		//actor.scope.launch{
//			actor.forward("modelChange", "modelChange(robot,$move)", "resourcemodel")
// 		//}		
//	}
	
	suspend fun turn180( actor : ActorBasic ){
		//actor.scope.launch{
			println("turn180  ")
			//actor.forward("modelChange", "modelChange(robot,r)", "resourcemodel")
			moveUtils.rotate(actor,"r")
			//delay(100)
			moveUtils.rotate(actor,"r")
			//actor.forward("modelChange", "modelChange(robot,r)", "resourcemodel")
		//}		
	}
	
}