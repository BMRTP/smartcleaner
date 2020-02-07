package itunibo.robotReal

import itunibo.tvoc.TvocSupport
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RealTvocSupport : TvocSupport {
	var tvocStream: InputStream? = null
	var p: Process? = null
	private val handlers: MutableList<(Pair<Int, Int>) -> Unit> = mutableListOf()

	init {
		println("RealTvocSupport starting!!")
		startProcess()
		startTheReader()
		println("RealTvocSupport started!")
	}

	fun startProcess() {
		p = Runtime.getRuntime().exec("./ccs811demo")
		tvocStream = p!!.getInputStream()
	}

	fun stopProcess() {
		tvocStream!!.close()
		p!!.destroy()
	}


	fun readTvoc(): Pair<Int, Int> {
		var data: Pair<Int, Int>? = null;
		while (data == null) {
			val line = BufferedReader(InputStreamReader(tvocStream)).readLine()
			if (line != null) {
				val split = line.split('/')
				if (split.size >= 2) {
					data = (split[0].toIntOrNull() ?: 400) to (split[1].toIntOrNull() ?: 0) //400/0 default value
				}
			}
		}
		return data;
	}


	private fun startTheReader() {
		GlobalScope.launch {
			while (true) {
				var value: Pair<Int, Int>? = null
				while (value == null) {
					value = withTimeoutOrNull(10000L) {
						readTvoc()
					}
					
					if (value == null) {
						println("Tvoc process timedout! Restarting...")
						stopProcess()
						startProcess()
					}
				}

				//println("sonar: $value")
				handlers.forEach({
					it(value)
				})
			}
		}
	}

	override fun observe(handleData: (Pair<Int, Int>) -> Unit) {
		handlers.add(handleData)
	}
}