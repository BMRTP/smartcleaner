package itunibo.robotReal

import itunibo.robot.GrabberSupport
import itunibo.robot.ClassifierSupport

class ConsoleGrabberSupport : GrabberSupport, ClassifierSupport {

	var lastObstacle: String = "nothing"

	override fun grab(): Boolean {
		val result = lastObstacle.contains("bottle")
		lastObstacle = "nothing"
		return result
	}

	override fun classify(): String {
		print("Insert the obstacle type: ")
		return readLine() ?: "Unknown"
		//return "wall"
	}
}
