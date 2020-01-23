package itunibo.tvocVirtual

import it.unibo.kactor.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import org.json.JSONObject
import alice.tuprolog.*
import kotlin.random.Random

    object tvocVirtual {
		private val spike = 300.0
		private val standard = 100.0
		
		private var value = 0.0
		private var fail = 0.0
		
        fun initSensor(fail: Double = 0.0) {
			this.fail = fail;
        }
		
		fun getTvocValue(): Double {
			val rnd = Random.nextDouble(0.0, 100.0)
			if (rnd < fail) {
				return spike
			} else {
				return standard
			}
		}
}


