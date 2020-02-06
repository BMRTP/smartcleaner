const detector = require('./supports/detectorClient')
const plasticbox = require('./supports/plasticboxClient')
const coapClient = require('./supports/coap')
const app = require('express')();
const server = require('http').Server(app);
const io = require('socket.io')(server);


var clients = []
var updates = new Map()


detector.connect()
plasticbox.connect()

app.get('/', function (req, res) {
    res.sendFile(__dirname + '/public/index.html');
})
  
server.listen(8080, function () {
    console.log('Example app listening on port 8080!')
})

io.on('connection', function (socket) {
    clients.push(socket)
    socket.on('command', function (data) {
      // data = { name: name }
      console.log("Command received: " + data.name)
      switch(data.name) {
        case 'explore':
            detector.explore()
            break
        case 'terminate':
            detector.terminate()
            break
        case 'suspend':
            detector.suspend()
            break
        case 'continue':
            detector.continuee()
            break
        case 'empty':
            plasticbox.empty()
            break
      }
    })
    updates.forEach((value, key, map) => socket.emit('update', {resource : key, value: value}))

    socket.on('disconnect', function() {    
        var i = clients.indexOf(socket);
        clients.splice(i, 1);
     });
})


setInterval(observeProperties, 2000);

function observeProperties() {
    coapClient.observeProperty("detector/RoomMap", coapUpdatesHandler)
    coapClient.observeProperty("detector/SpaceAvailable", coapUpdatesHandler)
    coapClient.observeProperty("detector/currentTask", coapUpdatesHandler)
    coapClient.observeProperty("detector/waitingForSupervisor", coapUpdatesHandler)
    coapClient.observeProperty("plasticbox/SpaceAvailable", coapUpdatesHandler)
}

observeProperties()

function coapUpdatesHandler(resource, value) {
    //console.log("Resource: " + resource + " value: " + value)
    updates.set(resource, value)
    clients.forEach(c => c.emit('update', { resource: resource, value: value}))
}
