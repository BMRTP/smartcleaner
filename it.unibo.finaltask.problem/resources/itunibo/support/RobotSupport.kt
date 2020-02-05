package itunibo.support

interface RobotSupport {
	fun executeStep(): Boolean
	fun executeBackStep(): Boolean
	fun getObstacleMaterial(): String
	fun grab(): Unit
	fun executeLeftRotation(): Unit
	fun executeRightRotation(): Unit
}

class DummySupport: RobotSupport {
	 override fun executeStep(): Boolean {
		 return true
	}
	
	override fun executeBackStep(): Boolean {
		 return true
	}
	
	override fun getObstacleMaterial(): String {
		return ""
	}
	
	override fun grab(): Unit{
		
	}
	
	override fun executeLeftRotation(): Unit {
		
	}
	
	override fun executeRightRotation(): Unit {
		
	}
}