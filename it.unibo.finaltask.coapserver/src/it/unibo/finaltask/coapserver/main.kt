package it.unibo.finaltask.coapserver
import org.eclipse.californium.core.CoapServer
import org.eclipse.californium.core.coap.CoAP.ResponseCode.CHANGED;
import org.eclipse.californium.core.coap.CoAP.ResponseCode.CREATED;
import org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED;
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.server.resources.CoapExchange
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.MediaTypeRegistry


object coapServer {
	val server = CoapServer()

	fun start() {
		server.start()
		server.add(resourceCreator())
	}

	class resourceCreator : CoapResource("resources") {
		override fun handlePOST(exchange: CoapExchange) {
			val res = exchange.getRequestText()
			server.add(Resource(res))
			changed()    // notify all CoAp observers
			exchange.respond(CREATED)
		}
	}

	class Resource(name: String) : CoapResource(name) {
		override fun handlePOST(exchange: CoapExchange) {
			val res = exchange.getRequestText() //name|initial
			this.add(Property(res))
			changed()    // notify all CoAp observers
			exchange.respond(CREATED)
		}
	}

	class Property(name: String) : CoapResource(name) {
		var value = "NULL";

		init {
			setObservable(true)
		}

		override fun handleGET(exchange: CoapExchange) {
			exchange.respond(value);
		}

		override fun handlePUT(exchange: CoapExchange) {
			value = exchange.getRequestText()
			changed()    // notify all CoAp observers
			exchange.respond(CHANGED)
		}
	}
}

fun main() {
	println("Start coap server!")
	coapServer.start()
}