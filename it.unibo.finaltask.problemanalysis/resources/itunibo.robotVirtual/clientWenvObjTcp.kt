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

    object clientWenvObjTcp {
        private var hostName = "localhost"
        private var port     = 8999
        private val sep      = ";"
        private var outToServer: PrintWriter?     = null
        private var inFromServer: BufferedReader? = null
		private var lastObstacle: String = "";
		private var actor: ActorBasic? = null;
		
        fun initClientConn(actor:ActorBasic, hostName: String = "localhost", portStr: String = "8999"  ) {
            port  = Integer.parseInt(portStr)
			this.actor = actor;
            try {
                val clientSocket = Socket(hostName, port)
                println("		--- clientWenvObjTcp |  CONNECTION DONE")
                inFromServer = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                outToServer  = PrintWriter(clientSocket.getOutputStream())
                startTheReader( actor )
            }catch( e:Exception ){
                println("		--- clientWenvObjTcp | ERROR $e")
            }
        }

        fun sendMsg(v: String) {
			var outS = "{'type': 'alarm', 'arg': 0 }"
			when( v ){
				"w"  -> outS = "{'type': 'moveForward',  'arg': -1  }"
				"a"  -> outS = "{'type': 'turnLeft',     'arg': -1  }"
 				"d"  -> outS = "{'type': 'turnRight',    'arg': -1  }"
   			    "h"  -> outS = "{'type': 'alarm',        'arg': 0   }"
 				"grab" -> outS = "{'type': 'remove',       'arg': \"$lastObstacle\"  }"
 			}
			val jsonObject = JSONObject(outS) 
			val msg= "$sep${jsonObject.toString()}$sep"
			outToServer?.println(msg)
            outToServer?.flush()
         }
		
		fun sendReq(req: ApplMessage) {
			var outS: String = ""
			var reply: String = ""
			when(req.msgId()){
 				"grab" -> {outS = "{'type': 'remove', 'arg': \"$lastObstacle\" }"; reply = "grabbed"}
 			}
			
			if(lastObstacle.contains("bottle", ignoreCase = true)) {
				val jsonObject = JSONObject(outS) 
				val msg= "$sep${jsonObject.toString()}$sep"
			    outToServer?.println(msg)
                outToServer?.flush()
				lastObstacle = ""
				MsgUtil.buildReplyReq(actor!!.name, reply, "$reply(true)",  req.msgSender())
			} else {
				MsgUtil.buildReplyReq(actor!!.name, reply, "$reply(false)",  req.msgSender())
			}
			
		}

        private fun startTheReader( actor:ActorBasic  ) {
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
								actor.emit("sonar", "sonar(1)" );
								lastObstacle = objectName;
								//Aggiungere in fase di progetto un attore che si occupa di capire se l'ostacolo è una bottiglia di plastica?
 								val vobstev= MsgUtil.buildEvent( "sonarsupport","virtualobstacle","virtualobstacle($objectName)")
 								println("clientWenvObjTcp | emit $vobstev")
								actor.emit( vobstev )
						    }
                        }
                    } catch (e: IOException) {
						println("		--- clientWenvObjTcp | ERROR $e   ")
						System.exit(1)
                    }
                }
            }
         }//startTheReader
}//clientWenvObjTcp


