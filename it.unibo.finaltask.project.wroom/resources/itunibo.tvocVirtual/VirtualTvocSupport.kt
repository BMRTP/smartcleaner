package itunibo.robotVirtual

import itunibo.tvoc.TvocSupport
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class VirtualTvocSupport : TvocSupport {

	private val handlers: MutableList<(Pair<Int, Int>) -> Unit> = mutableListOf()

	init {
		GlobalScope.launch {
			forgeVirtualData();
		}
	}

	override fun observe(handleData: (Pair<Int, Int>) -> Unit) {
		handlers.add(handleData)
	}

	private suspend fun forgeVirtualData() {
		while (true) {
			handlers.forEach({
				it(200 to 0)
			})
			delay(5000);
		}
	}

}