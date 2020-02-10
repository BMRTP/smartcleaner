package itunibo.resource

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.coap.MediaTypeRegistry

class CoapResourceSupport(resourceName: String, val serverAddr: String) : ResourceSupport(resourceName) {

	private fun setClientPath(path : String){
	}

	override fun createProperty(name: String, init: String) {
	}

	override fun setProperty(name: String, value: String) {
	}

	override fun getProperty(name: String): String {
		return ""
	}
}