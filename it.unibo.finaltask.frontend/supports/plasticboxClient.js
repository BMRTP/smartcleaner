const client = require('./qakClient')

const socket = client.createSocket()

exports.init = () => {
    return client.start(socket, 8016, "localhost") 
}

exports.empty = () => {
    return client.sendMessage(socket, "msg(empty, dispatch, gui, plasticbox, empty(x), 1)")
}

exports.close = () => {
    client.close(socket)
}