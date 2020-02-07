package itunibo.robotVirtual

import it.unibo.kactor.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import org.json.JSONObject
import alice.tuprolog.*
import itunibo.robot.*

class VirtualRobotSupport(hostName: String = "localhost", portStr: String = "8999") : RobotSupport, SonarSupport,
	GrabberSupport, ClassifierSupport {
	private var hostName = "localhost"
	private var port = 8999
	private val sep = ";"
	private var outToServer: PrintWriter? = null
	private var inFromServer: BufferedReader? = null
	private var lastObstacle: String = "nothing";
	private val distanceHandlers: MutableList<(Double) -> Unit> = mutableListOf()

	init {
		port = Integer.parseInt(portStr)
		try {
			val clientSocket = Socket(hostName, port)
			println("VirtualRobotSupport has connected")
			inFromServer = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
			outToServer = PrintWriter(clientSocket.getOutputStream())
			startTheReader()
		} catch (e: Exception) {
			println("VirtualRobotSupport error while connecting $e")
		}
	}

	override fun move(cmd: String) {
		sendMsg(cmd)
	}

	override fun grab(): Boolean {
		if (lastObstacle.contains("bottle", ignoreCase = true)) {
			sendMsg("grab")
			lastObstacle = "nothing"
			return true
		} else {
			return false
		}
	}

	override fun classify(): String {
		return lastObstacle
	}

	override fun observe(handleData: (Double) -> Unit) {
		distanceHandlers.add(handleData)
	}

	fun sendMsg(v: String) {
		var outS = "{'type': 'alarm', 'arg': 0 }"
		when (v) {
			"w" -> outS = "{'type': 'moveForward',  'arg': -1  }"
			"s" -> outS = "{'type': 'moveBackward', 'arg': -1  }"
			"a" -> outS = "{'type': 'turnLeft',     'arg': 400  }"
			"d" -> outS = "{'type': 'turnRight',    'arg': 400  }"
			"h" -> outS = "{'type': 'alarm',        'arg': 0   }"
			"grab" -> outS = "{'type': 'remove',     'arg': \"$lastObstacle\"  }"
		}
		val jsonObject = JSONObject(outS)
		val msg = "$sep${jsonObject.toString()}$sep"
		outToServer?.println(msg)
		outToServer?.flush()
	}


	private fun startTheReader() {
		GlobalScope.launch {
			while (true) {
				try {
					val inpuStr = inFromServer?.readLine()
					val jsonMsgStr =
						inpuStr!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
					val jsonObject = JSONObject(jsonMsgStr)
					when (jsonObject.getString("type")) {
						"collision" -> {
							val jsonArg = jsonObject.getJSONObject("arg")
							val objectName = jsonArg.getString("objectName")
							lastObstacle = objectName;
							distanceHandlers.forEach {
								it(1.0)
							}
						}
					}
				} catch (e: IOException) {
					println("VirtualRobotSupport connection error: $e")
					System.exit(1)
				}
			}
		}
	}
}


