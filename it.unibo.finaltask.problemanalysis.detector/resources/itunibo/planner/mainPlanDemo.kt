package itunibo.planner

import aima.core.agent.Action


object mainPlanDemo {
    fun demo() {
        println("===== demo")

        try {
			plannerUtil.startTimer()

			plannerUtil.initAI()
            //plannerUtil.cleanQa()
            println("===== initial map")
            plannerUtil.showMap()
            doSomeMOve()
            println("===== map after some move")
            plannerUtil.showMap()
            val actions = plannerUtil.doPlan()
            println("===== plan actions: " + actions!!)
			
            executeMoves(plannerUtil.doPlan()!!)			
            println("===== map after plan")
            plannerUtil.showMap()
 

            //plannerUtil.cell0DirtyForHome()
            plannerUtil.setGoal(0,0);

            executeMoves(plannerUtil.doPlan()!!)
            println("===== map after plan for home")
            plannerUtil.showMap()

			plannerUtil.getDuration()
		
		} catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Throws(Exception::class)
    internal fun doSomeMOve() {
        plannerUtil.doMove("w")
        plannerUtil.doMove("a")
        plannerUtil.doMove("w")
        plannerUtil.doMove("w")
        plannerUtil.doMove("d")
        plannerUtil.doMove("w")
        plannerUtil.doMove("a")
        plannerUtil.doMove("obstacleOnRight")
    }


    @Throws(Exception::class)
    internal fun executeMoves(actions: List<Action>) {
        val iter = actions.iterator()
        while (iter.hasNext()) {
            plannerUtil.doMove(iter.next().toString())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        mainPlanDemo.demo()
    }

}
