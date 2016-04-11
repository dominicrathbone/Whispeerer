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

function User(name) {
    this.namespace = io.of(baseUrl.concat(name));
    this.name = name;
    this.id = null;
    this.currentCaller = "";
    this.busy = false;
    var user = this;

    this.namespace.on('connection', function(socket) {

        if(user.id == null) {
            user.id = socket.client.id;
        }

        if(user.busy == true) {
            socket.disconnect();
        } else {
            user.busy = true;
        }

        console.log(socket.client.id + " rang user: " + user.name);

        socket.on('disconnect', function(){
            if(socket.client.id == user.id) {
                removeUser(user.id)
            } else {
                user.currentCaller = "";
                user.busy = false;
            }
            console.log(socket.client.id + " hung up on: " + user.name);
        });

        socket.on('offer', function(data) {
            var caller = JSON.parse(data);
            user.currentCaller = caller.username;
            socket.broadcast.emit("offer", JSON.stringify({"from": username}));
        });

        socket.on('answer', function(data) {
            var callStatus = JSON.parse(data);
            if(callStatus == "DECLINED") {
                user.currentCaller = "";
            }
            socket.broadcast.emit("answer", JSON.stringify({"callStatus": callStatus}));
        });

        socket.on('signal', function(signal) {
            var parsedSignal = JSON.parse(signal);
            console.log(parsedSignal);
        });
    });
}

function removeUser(id) {
    for(var i = 0; i < users.length; i++) {
        if(users[i].id == id) {
            users.splice(i, 1);
        }
    }
}

app.post('/user', function(req){
    var username = req.body.username;
    users.push(new User(username));
    req.sendStatus(200);
});

http.listen(8080, function(){
    console.log('listening on *:8080');
});
