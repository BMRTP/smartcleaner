const detector = require('./supports/detectorClient')
const coapClient = require('./supports/coap')
const app = require('express')();
const server = require('http').Server(app);
const io = require('socket.io')(server);

var clients = []

detector.init().then((res) => console.log("Connected to detector"))

app.get('/', function (req, res) {
    res.send('Hello World!')
})
  
app.listen(8080, function () {
    console.log('Example app listening on port 8080!')
})

io.on('connection', function (socket) {
    clients.push(socket)
    socket.on('command', function (data) {
      // data = { name: name }
      console.log("Command received: " + data)
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
      }
    })

    socket.on('disconnect', function() {    
        var i = clients.indexOf(socket);
        clients.splice(i, 1);
     });
})



coapClient.observeProperty("detector/RoomMap", coapUpdatesHandler)
coapClient.observeProperty("detector/SpaceAvailable", coapUpdatesHandler)
coapClient.observeProperty("detector/currentTask", coapUpdatesHandler)
coapClient.observeProperty("detector/waitingForSupervisor", coapUpdatesHandler)
coapClient.observeProperty("plasticbox/SpaceAvailable", coapUpdatesHandler)

function coapUpdatesHandler(resource, value) {
    console.log("Resource: " + resource + " value: " + value)
    clients.forEach(c => c.emit('update', { resource: resource, value: value}))
}
