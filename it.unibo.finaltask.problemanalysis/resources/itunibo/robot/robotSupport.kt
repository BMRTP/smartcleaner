package itunibo.robot

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ActorBasicFsm
import itunibo.robotVirtual.clientWenvObjTcp
 
object robotSupport{
	lateinit var robotKind  :  String
	var endPipehandler      :  ActorBasic? = null 
	
	fun create( actor: ActorBasic, robot : String, port: String, endPipe: ActorBasic? = null ){
		robotKind           = robot
		endPipehandler      =  endPipe
		println( "		--- robotSupport | CREATED for $robotKind" )
		when( robotKind ){
			"virtual"    ->  { clientWenvObjTcp.initClientConn( actor, "localhost", port) }
			//"realmbot"   ->  { itunibo.robotMbot.mbotSupport.create( actor, port  ) }
			//"realnano"   ->  { it.unibo.robotRaspOnly.nanoSupport.create(actor, true ) }
			else -> println( "		--- robotSupport | robot unknown" )
		}
	}
	
	fun move( cmd : String ){
		when( robotKind ){
			"virtual"  -> { clientWenvObjTcp.sendMsg(  cmd ) }	
			//"realmbot" -> { itunibo.robotMbot.mbotSupport.move( cmd ) }
			//"realnano" -> { it.unibo.robotRaspOnly.nanoSupport.move( cmd ) }
			else       -> println( "		--- robotSupport | robot unknown" )
		}		
	}
}