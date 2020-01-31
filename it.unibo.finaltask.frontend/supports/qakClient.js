const net = require('net');
const {PromiseSocket} = require("promise-socket")

const socket = new net.Socket()
var promiseSocket = new PromiseSocket(socket)

exports.start = (port, address) => {
    return promiseSocket.connect(port, address)
}

exports.sendMessage = (message) => {
    return promiseSocket.write(message + "\n")
}

exports.close = () => {
    promiseSocket.destroy()
}