package itunibo.robot

import it.unibo.kactor.*
import alice.tuprolog.*
import itunibo.robotVirtual.VirtualRobotSupport
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
			val sol2 = pengine.solve("robot(ROBOT_TYPE, PORT).")
			if (sol2.isSuccess()) {
				val robotType = sol2.getVarValue("ROBOT_TYPE").toString()
				val port = sol2.getVarValue("PORT").toString()
				println("Robot adapter started with $robotType : $port")


				when (robotType) {
					"virtual" -> {
						val virtualSupport = VirtualRobotSupport("localhost", port)
						robotSupport = virtualSupport
						grabberSupport = virtualSupport
						sonarSupport = virtualSupport
						classifierSupport = virtualSupport
					}
					"real" -> {
						robotSupport = RealRobotSupport(port)
						//sonarSupport = RealSonarSupport()
						sonarSupport = object:SonarSupport {
							override fun observe(distanceHandler: (Double) -> Unit) {
								//nothing
							}
						}
						val grabber = RealGrabberSupport()
						classifierSupport = grabber
						grabberSupport = grabber
					}

					else -> println("robot unknown")
				}

				sonarSupport.observe({ distance ->
					runBlocking {
						mySelf.emit("sonar", "sonar($distance)");
					}
				})

			}
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
			val result = classifierSupport.classify()
			replyWith("obstacletype", result, msg);
		}

	}

	fun replyWith(messageId: String, value: String, request: ApplMessage) {
		val reply = MsgUtil.buildReply(mySelf.name, messageId, "$messageId($value)", request.msgSender())
		mySelf.scope.launch { mySelf.sendMessageToActor(reply, request.msgSender(), request.conn) }
	}
}






