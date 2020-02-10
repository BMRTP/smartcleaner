package itunibo.robot

import it.unibo.kactor.*
import alice.tuprolog.*
import itunibo.robotVirtual.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import itunibo.robotReal.*

class RobotAdapterQa(name: String) : ActorBasic(name) {

	lateinit var robotSupport: RobotSupport
	lateinit var grabberSupport: GrabberSupport
	lateinit var sonarSupport: SonarSupport
	lateinit var classifierSupport: ClassifierSupport

	val mySelf = this

	init {
		val sol1 = pengine.solve("consult('basicRobotConfig.pl').")
		if (!sol1.isSuccess()) {
			println("Robot adapter failed while loading prolog.")
		} else {
			val sol2 = pengine.solve("robotType(TYPE).")
			val TYPE = sol2.getVarValue("TYPE").toString().trim('\'')
			if (TYPE == "virtual") {
				val solVirtual = pengine.solve("virtualRobot(VIRTUAL_PORT).")
				val port = solVirtual.getVarValue("VIRTUAL_PORT").toString()
				val virtualSupport = VirtualRobotSupport("localhost", port)

				robotSupport = virtualSupport
				grabberSupport = virtualSupport
				sonarSupport = virtualSupport
				classifierSupport = virtualSupport

				println("Virtual robot started")

			} else if (TYPE == "real") {
				val solReal = pengine.solve("realRobot(SERIAL_PORT, FRONTEND_URL).")
				val SERIAL_PORT = solReal.getVarValue("SERIAL_PORT").toString().trim('\'')
				val FRONTEND_URL = solReal.getVarValue("FRONTEND_URL").toString().trim('\'')
				val RASPISTILL_PARAM = solReal.getVarValue("RASPISTILL_PARAM").toString().trim('\'')
				
				robotSupport = RealRobotSupport(SERIAL_PORT)
				//sonarSupport = RealSonarSupport()
				sonarSupport = object : SonarSupport { //th sonar should be run externally
					override fun observe(distanceHandler: (Double) -> Unit) {
						//nothing
					}
				}
				val grabber = ConsoleGrabberSupport(FRONTEND_URL, RASPISTILL_PARAM)
				classifierSupport = grabber
				grabberSupport = grabber

				println("Real robot started")

			} else {
				println("robot unknown")
			}


			sonarSupport.observe({ distance ->
				runBlocking {
					mySelf.emit("sonar", "sonar($distance)");
				}
			})
		}
	}


	suspend fun handleDistance(distance: Double) {
		mySelf.emit("sonar", "sonar($distance)");
	}

	override suspend fun actorBody(msg: ApplMessage) {
		if (msg.isDispatch() && msg.msgId() == "cmd") {
			val move = (Term.createTerm(msg.msgContent()) as Struct).getArg(0).toString()
			robotSupport.move(move)
		}

		if (msg.isRequest() && msg.msgId() == "grab") {
			val result = grabberSupport.grab().toString()
			replyWith("grabbed", result, msg);
		}

		if (msg.isRequest() && msg.msgId() == "getobstacletype") {
			classifierSupport.classify({
				replyWith("obstacletype", it, msg);
			})
		}

		if (msg.isRequest() && msg.msgId() == "suggestobstacletype") {
			val suggestion = (Term.createTerm(msg.msgContent()) as Struct).getArg(0).toString()
			classifierSupport.suggest(suggestion)
		}

	}

	fun replyWith(messageId: String, value: String, request: ApplMessage) {
		val reply = MsgUtil.buildReply(mySelf.name, messageId, "$messageId($value)", request.msgSender())
		mySelf.scope.launch { mySelf.sendMessageToActor(reply, request.msgSender(), request.conn) }
	}
}






