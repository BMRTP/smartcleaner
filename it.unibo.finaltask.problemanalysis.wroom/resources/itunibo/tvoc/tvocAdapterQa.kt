package itunibo.tvoc

import it.unibo.kactor.*
import alice.tuprolog.*
import itunibo.tvocVirtual.tvocVirtual

class tvocAdapterQa(name: String) : ActorBasic(name) {

	init {
		println("		--- tvocAdapterQa | start")
		println("		--- tvocAdapterQa | init virtual tvoc sensor")
		tvocVirtual.initSensor(1.0)
	}

	override suspend fun actorBody(msg: ApplMessage) {
		if(msg.isRequest()) {
			val value = tvocVirtual.getTvocValue()
			val m = MsgUtil.buildReply(this.name, "tvoc", "tvoc($value)",  msg.msgSender())
			sendMessageToActor( m, msg.msgSender(), msg.conn )
		}
	}
}