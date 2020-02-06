const net = require('net');

const HOST = "localhost"
const PORT = 8016

let socket

function connect() {
    socket = new net.Socket();
    socket.connect(PORT, HOST, function() {
        console.log('Connected to plasticBox: ' + HOST + ':' + PORT);
    });

    socket.on('error', function() {}); // need this line so it wont throw exception

    socket.on('close', function() {
        connect();
    });
}
exports.connect = connect

exports.empty = () => {
    socket.write("msg(empty, dispatch, gui, plasticbox, empty(x), 1)\n")
}

exports.close = () => {
    socket.close()
}