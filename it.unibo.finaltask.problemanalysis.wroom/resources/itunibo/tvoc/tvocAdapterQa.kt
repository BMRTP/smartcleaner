package itunibo.tvoc

import it.unibo.kactor.*
import alice.tuprolog.*
import itunibo.robotVirtual.VirtualTvocSupport
import itunibo.robotReal.RealTvocSupport
import kotlinx.coroutines.runBlocking

class tvocAdapterQa(name: String) : ActorBasic(name) {

	lateinit var tvocSupport: TvocSupport
	val mySelf = this
	
	init {
		val sol1 = pengine.solve("consult('basicRobotConfig.pl').")
		if (!sol1.isSuccess()) {
			println("Tvoc adapter failed while loading prolog.")
		} else {
			val sol2 = pengine.solve("robot(ROBOT_TYPE, PORT).")
			if (sol2.isSuccess()) {
				val robotType = sol2.getVarValue("ROBOT_TYPE").toString()
				val port = sol2.getVarValue("PORT").toString()
				println("Tvoc adapter started with $robotType : $port")

				when (robotType) {
					"virtual" -> {
						tvocSupport = VirtualTvocSupport()
					}
					"real" -> {
						tvocSupport = RealTvocSupport()
					}

					else -> println("robot unknown")
				}
				
				tvocSupport.observe({ value ->
					runBlocking {
						println("Emitting TVOC: ${value.first}")
						mySelf.emit("tvocvalue", "tvocvalue(${value.first})");
					}
				})

			}
		}
	}

	override suspend fun actorBody(msg: ApplMessage) {
		
	}
}