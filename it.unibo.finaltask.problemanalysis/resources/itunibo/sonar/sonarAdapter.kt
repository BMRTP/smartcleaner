package sonar
import it.unibo.kactor.*
import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader

class sonarAdapter(name: String) : ActorBasic(name) {
	var sonarStream: InputStream? = null
	
	init {
		println("sonarAdapter starting!!")
		val p : Process = Runtime.getRuntime().exec("sudo ./SonarAlone")
		sonarStream =  p.getInputStream()
		println("sonarAdapter started!")
	}
	
	
	fun readSonar(): String {
		var data: String? = null;
		while (data == null || sonarStream!!.available() > 0) {
			println("sonarAdapter is reading.")
			data = BufferedReader(InputStreamReader(sonarStream)).readLine().toDoubleOrNull()?.toString();
		}
		return data;
	}
	
	
	override suspend fun actorBody(msg: ApplMessage) {
		if (msg.isRequest() && msg.msgId() == "getDistance") {
			println("sonarAdapter received a request!")
			val Distance = readSonar()
			println("sonarAdapter answering with $Distance!")
			val destName = msg.msgSender()
			val m = MsgUtil.buildReply(name, "distance", "distance($Distance)", destName)
			sendMessageToActor( m, destName, msg.conn )
		}
	}
}