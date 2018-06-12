const HashMap = require('hashmap');
const Session = require('../schemas/session');

var clients = new HashMap();
var sockets = new HashMap();
var pendingSockets = new HashMap();
var sessionsRooms = new HashMap();

var IO = null;

getAllSessions();


exports.socketInit = function (socket){

    pendingSockets.set(socket.id, socket);

    socket.on('disconnect', function(){
        onDisconnect(socket);
    });

    socket.on('registerClientToClients', function(user_id){
        pendingSockets.remove(socket.id);
        clients.set(user_id, socket);
        sockets.set(socket.id, user_id);
        console.log(user_id + ' was registered');
        socket.emit('ackConnection', "Hello from the other siiiiiiiide!!!!!!");
    });

    socket.on('unregisterClientFromClients', function(user_id){
        unregisterClientFromClients(user_id, socket);
    });

    socket.on('createSession', function(session_id){
        let room = {
            connected_users: 0,
        };
        sessionsRooms.set(session_id, room);
    });

    socket.on('joinSession', function(session_id){
        console.log('joined session ' + session_id);
        if(sessionsRooms.has(session_id)){
            socket.join(session_id, function (){
                sessionsRooms.get(session_id).connected_users++;
                console.log(sessionsRooms.get(session_id));
            });
        }
    });

    socket.on('leaveSession', function(session_id){
        socket.leave(session_id, function(){
            console.log('somebody left session ' + session_id);
            sessionsRooms.get(session_id).connected_users--;
            console.log(sessionsRooms.get(session_id));
        });
    });

    socket.on('postSessionMessage', function(message){
        console.log('posting');
        console.log(message);
    });

    socket.on('', function(){

    });

    socket.on('', function(){

    });

    socket.on('', function(){

    });

    socket.on('', function(){

    });

    socket.on('', function(){

    });

    socket.on('', function(){

    });

    socket.on('', function(){

    });

}


function onDisconnect(socket){

    var socketID = socket.id;

    if(pendingSockets.has(socketID)){
        pendingSockets.remove(socketID);
        console.log("unregistered socket "+ socket.id + " disconnected");

    }else{
        var userID = sockets.get(socketID);
        sockets.remove(socketID);
        clients.remove(userID);
        console.log(userID + " disconnected");
    }
}

function unregisterClientFromClients(user_id, socket){
    console.log("removeing " + user_id);
    if(clients.has(user_id)) {
        clients.remove(user_id);
        if (sockets.has(socket.id)) {
            sockets.remove(socket.id);
        }
        pendingSockets.set(socket.id, socket);
    }
}

exports.setIO = function (io){
    if(IO == null) IO = io;
}


function getAllSessions(){
    Session.find({},{sid: 1} ,function(err,sessions){
        if(err){
            console.log(err);
        }else{
            sessions.forEach(function(session){
                sessionsRooms.set(session.sid, {connected_users: 0});
            });
        }
    });
}

exports.emitEvent = function (user_id, eventName, args) {

    console.log('emitting event ' + eventName);
    if(exports.isRegistered(user_id)) {
        clients.get(user_id).emit(eventName, args);
        return true;
    }
    return false;
}

exports.emitEventToSessionRoom = function (room_id, eventName, args) {
    console.log('emitting "' + eventName + '" to session room ' + room_id);
    IO.to(room_id).emit(eventName, args);
}

exports.isRegistered = function(user_id){
    return clients.has(user_id);
}