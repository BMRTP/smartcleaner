package itunibo.robot

import it.unibo.kactor.*
import alice.tuprolog.*
import itunibo.robotVirtual.clientWenvObjTcp

class robotAdapterQa(name: String) : ActorBasic(name) {

	init {
		println("		--- robotAdapterQa | start")
		println("		--- robotAdapterQa | connecting to virtual robot")
		clientWenvObjTcp.initClientConn(this, "localhost")
	}

	override suspend fun actorBody(msg: ApplMessage) {
		println("	--- robotAdapterQa | received  msg= $msg ")
		if(msg.isDispatch()) {
			val move = (Term.createTerm(msg.msgContent()) as Struct).getArg(0).toString()
		    clientWenvObjTcp.sendMsg(move)
		} else if(msg.isRequest()) {
			clientWenvObjTcp.sendReq(msg)
		}
	}
}