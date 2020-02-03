const net = require('net');
const {PromiseSocket} = require("promise-socket")


exports.createSocket = () => {
    const socket = new net.Socket()
    return new PromiseSocket(socket)
}

exports.start = (socket, port, address) => {
    return socket.connect(port, address)
}

exports.sendMessage = (socket, message) => {
    return socket.write(message + "\n")
}

exports.close = (socket) => {
    socket.destroy()
}