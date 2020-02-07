package itunibo.robot

interface ObservableSupport<T> {
	fun observe(handleData: (T) -> Unit)
}

interface RobotSupport {
	fun move(cmd: String)
}

interface GrabberSupport {
	fun grab(): Boolean
}

interface SonarSupport : ObservableSupport<Double> {
	
}

interface ClassifierSupport {
	fun classify(): String
}