var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var uuid = require('node-uuid');

var baseUrl = "/room/"
var rooms = [];

function Room(id) {
    this.namespace = io.of(baseUrl.concat(id));
    this.id = id;
    this.uploader = null;
    var room = this;

    this.namespace.on('connection', function(socket) {
        if(!room.uploader) {
            room.uploader = socket.id;
        }

        console.log(socket.client.id + " connected to room: " + room.id);

        socket.on('disconnect', function(){
            console.log(this.client.id + " disconnected from room: " + room.id);
        });

        socket.on('signal', function(signal) {
            var parsedSignal = JSON.parse(signal);
            console.log(signal);
            if(parsedSignal.toUser) {
                room.namespace.to(room.namespace.name.concat("#",parsedSignal.toUser.userId)).emit('signal',signal);
            } else {
                room.namespace.to(room.uploader).emit('signal',signal);
            }
        });
    });
}

app.get('/addRoom', function(req, res){
    var roomId = uuid.v1();
    rooms.push(new Room(roomId));
    res.json('{"roomId":"' + roomId + '"}');
});

http.listen(8080, function(){
    console.log('listening on *:8080');
});/**
 * Created by Dominic on 25/03/2016.
 */
