package itunibo.robotReal

import itunibo.robot.GrabberSupport
import itunibo.robot.ClassifierSupport
import java.io.InputStreamReader
import java.io.BufferedReader


class ConsoleGrabberSupport(coapResourceName: String, coapHostname: String, coapPort: Int = 5683) : GrabberSupport,
	ClassifierSupport {

	var lastObstacle: String = "nothing"
	var handler: (String) -> Unit = { _ -> }
	var resource: itunibo.resource.CoapResourceSupport

	init {
		resource = itunibo.resource.CoapResourceSupport(coapResourceName, "coap://$coapHostname:$coapPort")
		resource.createProperty("cam", "")
	}

	override fun grab(): Boolean {
		val result = lastObstacle.contains("bottle")
		lastObstacle = "nothing"
		return result
	}

	override fun classify(handler: (String) -> Unit) {

		try {
			//TODO fix
			val p = Runtime.getRuntime().exec("raspistill -vf -hf -o - | base64 | tr -d '\n' ; echo -e \"\\n\"")
			val inputStream = BufferedReader(InputStreamReader(p!!.getInputStream()))
			p.waitFor()
			val img = inputStream.readLine(); //null
			resource.setProperty("cam", img)
		} catch (ex: Exception) {
			println(ex)
		}

		this.handler = handler
	}

	override fun suggest(obstacle: String) {
		println("A suggestion was received: obstacle is " + obstacle)
		handler(lastObstacle)
		handler = { _ -> }
	}
}
