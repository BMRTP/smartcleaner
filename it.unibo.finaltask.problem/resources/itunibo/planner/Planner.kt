package itunibo.planner

interface Planner {
	fun generateSafePlanForHome(): List<String> // List["w", "a", "w"...]
	fun generateSafePlanForPlasticBox(): List<String>
	fun generateSafePlanForDestination(destination: Pair<Int, Int>): List<String>
	fun generatePlanForExplore(): List<String>
}