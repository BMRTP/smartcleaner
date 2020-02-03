const client = require('./qakClient')

const socket = client.createSocket()

exports.init = () => {
    return client.start(socket, 8022, "localhost") 
}

exports.explore = () => {
    return client.sendMessage(socket, "msg(explore, dispatch, gui, detector, explore(x), 1)")
}

exports.suspend = () => {
    return client.sendMessage(socket, "msg(suspend, dispatch, gui, detector, suspend(x), 1)")
}

exports.terminate = () => {
    return client.sendMessage(socket, "msg(terminate, dispatch, gui, detector, terminate(x), 1)")
}

exports.continuee = () => {
    return client.sendMessage(socket, "msg(continue, dispatch, gui, detector,continue(x), 1)")
}

exports.close = () => {
    client.close(socket)
}