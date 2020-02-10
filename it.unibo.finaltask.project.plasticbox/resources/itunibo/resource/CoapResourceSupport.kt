package itunibo.resource

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.coap.MediaTypeRegistry

class CoapResourceSupport(resourceName: String, val serverAddr: String) : ResourceSupport(resourceName) {
	lateinit var client : CoapClient
	
	init {
		setClientPath("resources")
		client.post(resourceName, MediaTypeRegistry.TEXT_PLAIN)
 	}
	
	private fun setClientPath(path : String){
		val url = serverAddr + "/" + path
		client = CoapClient(url)
		client.setTimeout(1000L)
	}

	override fun createProperty(name: String, init: String) {
		setClientPath(resourceName)
		client.post(name, MediaTypeRegistry.TEXT_PLAIN)
		setProperty(name, init)
	}

	override fun setProperty(name: String, value: String) {
		setClientPath("${resourceName}/${name}")
		client.put(value, MediaTypeRegistry.TEXT_PLAIN)
	}

	override fun getProperty(name: String): String {
		setClientPath("${resourceName}/${name}")
		return client.get().getResponseText()
	}
}

fun main() {
	val sup = CoapResourceSupport("robot", "localhost")
	sup.createProperty("position", "1")
	println(sup.getProperty("position"))
	sup.setProperty("position", "200")
	println(sup.getProperty("position"))
}