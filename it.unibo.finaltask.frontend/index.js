const detector = require('./supports/detectorClient')
const plasticbox = require('./supports/plasticboxClient')
const robot = require('./supports/robotClient')
const coapClient = require('./supports/coap')
const express = require('express')
const app = express();
const server = require('http').Server(app);
const io = require('socket.io')(server);
const defaultimg = require('./defaultObstacleImage');

let bodyParser = require('body-parser');
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb', extended: true}));

let clients = []
let updates = new Map()

let obstacle = defaultimg.img


detector.connect()
plasticbox.connect()
robot.connect()

app.get('/style.css', function (req, res) {
    res.sendFile(__dirname + '/public/style.css');
})
app.get('/', function (req, res) {
    res.sendFile(__dirname + '/public/index.html');
})

app.post('/obstacle', function(req, res){
	console.log(req.body.img);
	if (req.body.img) {
		obstacle = req.body.img
		res.sendStatus(200)
		clients.forEach(c => c.emit('obstacle', { url: "/obstacle" }))
	} else {
		res.sendStatus(400)
	}
});

app.get('/obstacle', function(req, res){
	let img = Buffer.from(obstacle, 'base64');
    res.writeHead(200, {
		'Content-Type': "image/jpeg",
        'Content-Length': img.length
    });
    res.end(img);
});
  
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
        case 'isbottle':
            robot.isbottle()
            break
        case 'notbottle':
            robot.notbottle()
            break
      }
    })
    updates.forEach((value, key, map) => socket.emit('update', {resource : key, value: value}))
	socket.emit('obstacle', { url: "/obstacle" })

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
