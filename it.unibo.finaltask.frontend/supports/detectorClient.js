const net = require('net');

const HOST = "localhost"
const PORT = 8022

let socket

function connect() {
    socket = new net.Socket();
    socket.connect(PORT, HOST, function() {
        console.log('Connected to detector: ' + HOST + ':' + PORT);
    });

    socket.on('error', function() {}); // need this line so it wont throw exception

    socket.on('close', function() {
        connect();
    });
}
exports.connect = connect

exports.explore = () => {
    socket.write("msg(explore, dispatch, gui, detector, explore(x), 1)\n")
}

exports.suspend = () => {
    socket.write("msg(suspend, dispatch, gui, detector, suspend(x), 1)\n")
}

exports.terminate = () => {
    socket.write("msg(terminate, dispatch, gui, detector, terminate(x), 1)\n")
}

exports.continuee = () => {
    socket.write("msg(continue, dispatch, gui, detector,continue(x), 1)\n")
}

exports.close = () => {
    socket.close()
}