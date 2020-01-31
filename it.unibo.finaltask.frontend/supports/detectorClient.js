const client = require('./qakClient')

exports.init = () => {
    return client.start(8022, "localhost") 
}

exports.explore = () => {
    return client.sendMessage("msg(explore, dispatch, gui, detector, explore(x), 1)")
}

exports.suspend = () => {
    return client.sendMessage("msg(suspend, dispatch, gui, detector, suspend(x), 1)")
}

exports.terminate = () => {
    return client.sendMessage("msg(terminate, dispatch, gui, detector, terminate(x), 1)")
}

exports.continuee = () => {
    return client.sendMessage("msg(continue, dispatch, gui, detector,continue(x), 1)")
}

exports.close = () => {
    client.close()
}