const coap = require("node-coap-client").CoapClient

const coapAddr = "coap://localhost:5683/"

exports.observeProperty = (resourceAddress, handler) => {
    const payloadHandler = (resource) => handler(resourceAddress, resource.payload.toString())
    return coap.observe(coapAddr + resourceAddress, "get", payloadHandler)
}