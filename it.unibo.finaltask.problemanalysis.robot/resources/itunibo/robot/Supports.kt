package itunibo.robot

interface RobotSupport {
	fun move(cmd: String)
}

interface GrabberSupport {
	fun grab(): Boolean
}

interface SonarSupport {
	fun observe(distanceHandler: (Double) -> Unit)
}

interface ClassifierSupport {
	fun classify(): String
}