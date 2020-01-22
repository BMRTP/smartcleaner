package itunibo.robot 
import it.unibo.kactor.*
import alice.tuprolog.*

class robotAdapterQa( name : String ) : ActorBasic( name ){
    
    init{
		println("		--- robotAdapterQa | start")
		val sol1 = pengine.solve( "consult('basicRobotConfig.pl')." )
		if( ! sol1.isSuccess() ){
			println("	--- robotAdapterQa | ERROR: basicRobotConfig.pl not found or wrong")
		}else{
	 		val sol2 = pengine.solve( "robot(R, PORT)." )
	 		if( sol2.isSuccess() ){
				val host =  sol2.getVarValue("R").toString()
				val port =  sol2.getVarValue("PORT").toString()
				println("	--- robotAdapterQa | USING $host : $port")
				itunibo.robot.robotSupport.create( this, host, port )
			}
		}		  		      
    }

    override suspend fun actorBody(msg : ApplMessage){
        println("	--- robotAdapterQa | received  msg= $msg "  )
		if( msg.isEvent() ) return
		val move = (Term.createTerm(msg.msgContent()) as Struct).getArg(0).toString()
		itunibo.robot.robotSupport.move( move  )
    }
}