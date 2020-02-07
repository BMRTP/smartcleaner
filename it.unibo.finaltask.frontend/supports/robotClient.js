const net = require('net');

const HOST = "192.168.1.177"
const PORT = 8018

let socket

function connect() {
    socket = new net.Socket();
    socket.connect(PORT, HOST, function() {
        console.log('Connected to robot: ' + HOST + ':' + PORT);
    });

    socket.on('error', function() {}); // need this line so it wont throw exception

    socket.on('close', function() {
        connect();
    });
}
exports.connect = connect

exports.isbottle = () => {
    socket.write("msg(suggestobstacletype, dispatch, gui, robotadapter, suggestobstacletype(bottle), 1)\n")
}

exports.notbottle = () => {
    socket.write("msg(suggestobstacletype, dispatch, gui, robotadapter, suggestobstacletype(obstacle), 1)\n")
}

exports.close = () => {
    socket.close()
}