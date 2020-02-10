package itunibo.robotReal

import it.unibo.kactor.*
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import itunibo.robot.SonarSupport
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

class RealSonarSupport() : SonarSupport { //TODO to toughen
	var sonarStream: InputStream? = null
	var p: Process? = null
	private val distanceHandlers: MutableList<(Double) -> Unit> = mutableListOf()

	init {
		println("sonarAdapter starting!!")
		startProcess()
		startTheReader()
		println("sonarAdapter started!")
	}

	fun startProcess() {
		p = Runtime.getRuntime().exec("sudo ./SonarAlone")
		sonarStream = p!!.getInputStream()
	}

	fun stopProcess() {
		sonarStream!!.close()
		p!!.destroy()
	}

	override fun observe(distanceHandler: (Double) -> Unit) {
		distanceHandlers.add(distanceHandler)
	}

	fun readSonar(): Double {
		var data: Double? = null;
		while (data == null || sonarStream!!.available() > 0) {
			println("sonarAdapter is reading.")
			data = BufferedReader(InputStreamReader(sonarStream)).readLine().toDoubleOrNull();
		}
		return data;
	}


	private fun startTheReader() {
		GlobalScope.launch {
			while (true) {
				var value: Double? = null
				while (value == null) {
					value = withTimeoutOrNull(1000L) {
						readSonar()
					}
					if (value == null) {
						stopProcess()
						startProcess()
					}
				}

				println("sonar: $value")
				distanceHandlers.forEach({
					it(value)
				})
			}
		}
	}
}