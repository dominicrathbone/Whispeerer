var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var uuid = require('node-uuid');
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

var baseUrl = "/users/"
var users = [];

function User(id) {
    this.namespace = io.of(baseUrl.concat(id));
    this.id = id;
    var room = this;

    this.namespace.on('connection', function(socket) {

        console.log(socket.client.id + " connected to room: " + room.id);

        socket.on('disconnect', function(){
            console.log(this.client.id + " disconnected from room: " + room.id);
        });

        socket.on('signal', function(signal) {
            var parsedSignal = JSON.parse(signal);
            console.log(signal);
        });
    });
}

app.post('/user', function(req){
    var username = req.body.username;
    users.push(new User(username));
});

http.listen(8080, function(){
    console.log('listening on *:8080');
});
