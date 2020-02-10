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
			conn = serialConn.connect(port.trim('\''))
			println("RealRobotSupport connected at $port")
		} catch (e: Exception) {
			println("RealRobotSupport connection error: $e");
		}
	}


	override fun move(cmd: String) {
		println("RealRobotSupport handling move $cmd")
		when (cmd) {
			"w" -> conn.sendALine("w")
			"s" -> conn.sendALine("s")
			"a" -> conn.sendALine("a")
			"d" -> conn.sendALine("d")
			"h" -> conn.sendALine("h")


			"j" -> conn.sendALine("j") //set the current angle value as a full rotation angle value
			"k" -> conn.sendALine("k") //reset angle


			else -> {
				if (cmd.length > 0 && cmd[0] == 'r') { //rotation power
					val power = cmd.trim('r').toIntOrNull()
					if(power != null) {
						conn.sendALine("r" + power)
					}
				} else if (cmd.length > 0 && cmd[0] == 'f') { //forward power
					val power = cmd.trim('f').toIntOrNull()
					if(power != null) {
						conn.sendALine("f" + power)
					}
				} else {
					println("RealRobotSupport command $cmd unsupported")
				}
			}
		}

	}
}