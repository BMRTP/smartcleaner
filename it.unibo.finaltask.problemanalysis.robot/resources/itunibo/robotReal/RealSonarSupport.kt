package itunibo.robotReal

import it.unibo.kactor.*
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import itunibo.robot.SonarSupport
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RealSonarSupport() : SonarSupport { //TODO to toughen
	var sonarStream: InputStream? = null
	private val distanceHandlers: MutableList<(Double) -> Unit> = mutableListOf()

	init {
		println("sonarAdapter starting!!")
		val p: Process = Runtime.getRuntime().exec("sudo ./SonarAlone")
		sonarStream = p.getInputStream()
		println("sonarAdapter started!")
	}

	override fun observe(distanceHandler: (Double) -> Unit) {
		distanceHandlers.add(distanceHandler)
	}

	fun readSonar(): Double {
		var data: Double? = null;
		while (data == null || sonarStream!!.available() > 0 || data > 2000) {
			println("sonarAdapter is reading.")
			data = BufferedReader(InputStreamReader(sonarStream)).readLine().toDoubleOrNull();
		}
		return data;
	}


	private fun startTheReader() {
		GlobalScope.launch {
			while (true) {
				val value = readSonar()
				distanceHandlers.forEach({
					it(value)
				})
			}
		}
	}
}