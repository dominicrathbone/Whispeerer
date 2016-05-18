var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var bodyParser = require('body-parser');
var randomString = require("randomstring");
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

var baseUrl = "/users/"
var users = [];

function User(name) {
    this.namespace = io.of(baseUrl.concat(name));
    this.name = name;
    this.id = null;
    this.currentGuest = "";
    this.busy = false;
    var user = this;

    this.namespace.on('connection', function(socket) {
        if(user.id == null) {
            user.id = socket.client.id;
            console.log(user.name + " joined their room");
        }

        if(socket.client.id !== user.id && user.busy == true) {
            socket.disconnect();
            console.log(user.name + "is busy...disconnecting");
        } else if(socket.client.id !== user.id && user.busy == true) {
            user.busy = true;
            console.log(user.name + "is free...connecting");
        }

        socket.on('disconnect', function(){
            if(socket.client.id === user.id) {
                removeUser(user.id);
                console.log(user.name + " disconnected from their room")
            } else {
                console.log(user.currentGuest + " disconnected from : " + user.name);
                user.currentGuest = "";
                user.busy = false;
            }
        });

        socket.on('offer', function(data) {
            user.currentGuest = JSON.parse(data).from;
            if(data.chatStatus == "CANCELLED") {
                console.log(user.currentGuest + " cancelled on: " + user.name);
                user.currentGuest = "";
            }
            socket.broadcast.emit("offer", data);
            console.log(user.currentGuest + " rang user: " + user.name);
        });

        socket.on('answer', function(data) {
            data = JSON.parse(data);
            if(data.chatStatus == "DECLINED") {
                console.log(user.name + " declined user: " + user.currentGuest);
                user.currentGuest = "";
            } else {
                console.log(user.name + " accepted user: " + user.currentGuest);
            }
            socket.broadcast.emit("answer", data);
        });

        socket.on('sdp', function(sdp) {
            console.log("SDP");
            console.log(sdp);
            socket.broadcast.emit("sdp", sdp);
        });

        socket.on('candidate', function(candidate) {
            console.log("CANDIDATE");
            console.log(candidate);
            socket.broadcast.emit("candidate", candidate);
        });
    });
}

function getUser(name) {
    for(var i = 0; i < users.length; i++) {
        if(users[i].name == name) {
            return name;
        }
    }
    return null;
}

function removeUser(id) {
    for(var i = 0; i < users.length; i++) {
        if(users[i].id == id) {
            users.splice(i, 1);
        }
    }
}

app.get('/users/:user', function(req, res){
    var username = req.params.user;
    if(getUser(username) !== null) {
        console.log(username + " exists");
        res.sendStatus(200);
    } else {
        res.sendStatus(404);
    }
});

app.post('/user', function(req, res){
    var username = randomString.generate({
        length: 10,
        readable:true,
        capitalization:"uppercase"
    });
    console.log(username + " has created their room");
    if(getUser(username) === null) {
        users.push(new User(username));
        res.status(201);
        res.json({"username": username})
    } else {
        res.sendStatus(304);
    }
});

http.listen(8080, function(){
    console.log('listening on *:8080');
});
