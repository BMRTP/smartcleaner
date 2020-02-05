package itunibo.robotReal

import it.unibo.kactor.ActorBasic
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import it.unibo.kactor.ActorBasicFsm
import it.unibo.kactor.MsgUtil
import it.unibo.supports.serial.SerialPortConnSupport
import it.unibo.supports.serial.JSSCSerialComm
import itunibo.robot.RobotSupport

class RealRobotSupport(port: String) : RobotSupport {
	lateinit var conn: SerialPortConnSupport

	init {
		try {
			val serialConn = JSSCSerialComm(null)
			conn = serialConn.connect(port)
			println("RealRobotSupport connected at $port")
		} catch (e: Exception) {
			println("RealRobotSupport connection error: $e");
		}
	}


	override fun move(cmd: String) {
		println("RealRobotSupport handling move $cmd")
		when (cmd) {
			"cmd(w)", "w" -> conn.sendALine("w")
			"cmd(s)", "s" -> conn.sendALine("s")
			"cmd(a)", "a" -> conn.sendALine("a")
			"cmd(d)", "d" -> conn.sendALine("d")
			"cmd(h)", "h" -> conn.sendALine("h")


			"cmd(j)", "j" -> conn.sendALine("j") //set the current angle value as a full rotation angle value
			"cmd(k)", "k" -> conn.sendALine("k") //reset angle
			//"cmd(p)", "p" -> conn.sendALine("p") //set power, NEED A VALUE

			else -> println("RealRobotSupport command $cmd unsupported")
		}

	}
}