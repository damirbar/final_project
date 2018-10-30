const HashMap = require('hashmap');
const Session = require('../schemas/session');
const Course = require('../schemas/course');
const User = require('../schemas/user');

let clients = new HashMap();
let sockets = new HashMap();
let pendingSockets = new HashMap();
let sessionsRooms = new HashMap();
let coursesRooms = new HashMap();

let IO = null;
let streams = null;


getAllSessions();
getAllCourses();

exports.socketInit = function (client) {

    pendingSockets.set(client.id, client);

    client.on('message', function (details) {
        let id = details.to.slice(7);
        let otherClient = IO.sockets.sockets[id];

        if (!otherClient) {
            return;
        }
        delete details.to;
        details.from = client.id;
        otherClient.emit('message', details);
    });

    client.on('readyToStream', function () {
        console.log(client.id + ' is ready to stream!!!');

        streams.addStream(client.id);
    });
    client.on('stopStreaming', function () {
        console.log(client.id + ' stopped streaming!!!');

        streams.removeStream(client.id);
    });

    client.on('update', function () {
        streams.update(client.id);
    });


    client.on('disconnect', function () {
        onDisconnect(client);
    });

    client.on('leave', function () {
        onDisconnect(client);
    });

    client.on('registerClientToClients', function (user_id) {
        client.user_id = user_id;
        pendingSockets.remove(client.id);
        clients.set(user_id, client);
        sockets.set(client.id, user_id);
        console.log('user ' + user_id + ' was registered to socket: ' + client.id);
        // client.emit('ackConnection', "Hello from the other siiiiiiiide!!!!!!");
    });

    client.on('unregisterClientFromClients', function (user_id) {
        unregisterClientFromClients(user_id, client);
    });

    client.on('createSession', function (session_id) {
        let room = {
            connected_users: 0,
        };
        sessionsRooms.set(session_id, room);
    });

    client.on('joinSession', function (session_id) {
        console.log(client.user_id + ' joined session ' + session_id);
        if (sessionsRooms.has(session_id)) {
            client.join(session_id, function () {
                client.current_room = session_id;
                let connectedUsers = sessionsRooms.get(session_id).connected_users++;
                exports.emitEventToSessionRoom(session_id, 'updateSessionConnectedUsers', connectedUsers + 1);
                console.log(sessionsRooms.get(session_id));
            });
        }
    });

    client.on('leaveSession', function (session_id) {
        client.leave(session_id, function () {
            // let connectedUsers = sessionsRooms.get(session_id).connected_users--;
            // exports.emitEventToSessionRoom(session_id, 'updateSessionConnectedUsers', connectedUsers - 1);
            emitAndUpdateDisconnection(client);
            console.log(sessionsRooms.get(session_id));
        });
    });

    client.on('joinCourseMessages', function (cid) {
        if (client.current_room !== cid) {
            console.log('joined course ' + cid);
            client.join(cid, function () {
                client.current_room = cid;
                coursesRooms.get(cid).connected_users++;
                // console.log(coursesRooms.get(Number(cid)).connected_users);
            });
        }
    });

    // socket.on('rateSession', function (sessionRating) {
    //     let session = sessionsRooms.get(sessionRating.sess_id);
    //     if (rating == 1) {
    //         session.rate_positive++;
    //     } else {
    //         session.rate_negative++;
    //     }
    //     let connectedUsers = session.connected_users;
    //
    //     // exports.emitEventToSessionRoom(sessionRating.sess_id, 'updateSessionRating', {positive:});
    // });

};

exports.setIO = function (io) {
    if (!IO) IO = io;
};

exports.setStreams = function (s) {
    if (!streams) streams = s;
};


function onDisconnect(client) {

    streams.removeStream(client.id);
    console.log(client.id + " disconnected");

    let socketID = client.id;

    if (pendingSockets.has(socketID)) {
        pendingSockets.remove(socketID);
        // console.log("unregistered socket " + client.id + " disconnected");
    } else {
        let userID = sockets.get(socketID);
        sockets.remove(socketID);
        clients.remove(userID);
        if (client.current_room) {
            console.log("socket has current room " + client.current_room);
            emitAndUpdateDisconnection(client);
        }
        console.log(userID + " disconnected");
    }
}

function unregisterClientFromClients(user_id, client) {
    console.log("removeing " + user_id);
    if (clients.has(user_id)) {
        clients.remove(user_id);
        if (sockets.has(client.id)) {
            sockets.remove(client.id);
        }
        pendingSockets.set(client.id, client);
    }
}


function getAllSessions() {
    Session.find({}, {sid: 1}, function (err, sessions) {
        if (err) {
            console.log(err);
        } else {
            sessions.forEach(function (session) {
                sessionsRooms.set(session.sid, {connected_users: 0, rate_positive: 0, rate_negative: 0});
            });
        }
    });
}

function getAllCourses() {
    Course.find({}, {cid: 1}, function (err, courses) {
        if (err) {
            console.log(err);
        } else {
            courses.forEach(function (course) {
                coursesRooms.set(course.cid, {connected_users: 0});
            });
        }
    });
}

exports.emitEvent = function (user_id, eventName, args) {

    console.log('emitting event ' + eventName);

    if (exports.isRegistered(user_id)) {
        clients.get(user_id).emit(eventName, args);
        return true;
    }
    return false;
};

function emitAndUpdateDisconnection(client) {
    console.log("disconnecting at emitAndUpdateDisconnection: ");

    if (client.user_id) {

        User.findOne({_id: client.user_id}, {email: 1, _id: 0}, function (err, user) {
            if (err) return console.log(err);
            if (user.email) {

                console.log(user.email);

                Session.findOne({sid: client.current_room}, {likers: 1, dislikers: 1}, function (err, session) {
                    if (err) return console.log(err);
                    console.log("The session");
                    console.log(session);
                    if (session) {
                        let likers = session.likers;
                        let dislikers = session.dislikers;
                        console.log("user liked? ");
                        console.log(likers.includes(user.email));
                        console.log("user disliked? ");
                        console.log(dislikers.includes(user.email));

                        if (likers.includes(user.email)) {
                            session.update({
                                $pull: {likers: user.email, students: user.email},
                                $inc: {likes: -1}
                            }, function (err) {
                                if (err) return console.log(err);
                                let connectedUsers = sessionsRooms.get(client.current_room).connected_users--;
                                exports.emitEventToSessionRoom(client.current_room, 'updateSessionConnectedUsers', connectedUsers - 1);
                                exports.emitEventToSessionRoom(client.current_room, 'updateSessionRating', {
                                    likes: -1,
                                    dislikes: 0
                                });
                                console.log("updated liked");
                            })
                        }
                        else if (dislikers.includes(user.email)) {
                            session.update({
                                $pull: {dislikers: user.email},
                                $pull: {students: user.email},
                                $inc: {dislikes: -1}
                            }, function (err) {
                                if (err) return console.log(err);
                                if (client.current_room) {
                                    let connectedUsers = sessionsRooms.get(client.current_room).connected_users--;
                                    exports.emitEventToSessionRoom(client.current_room, 'updateSessionConnectedUsers', connectedUsers - 1);
                                    exports.emitEventToSessionRoom(client.current_room, 'updateSessionRating', {
                                        likes: 0,
                                        dislikes: -1
                                    });
                                    console.log("updated disliked");
                                }
                            });
                        } else {
                            let connectedUsers = sessionsRooms.get(client.current_room).connected_users--;
                            exports.emitEventToSessionRoom(client.current_room, 'updateSessionConnectedUsers', connectedUsers - 1);
                        }
                    }
                })
            }
        });
    }
}

exports.decreaseSessionConnectedUsers = function (session_id) {
    let connectedUsers = sessionsRooms.get(session_id).connected_users--;
    exports.emitEventToSessionRoom(socket.current_room, 'updateSessionConnectedUsers', connectedUsers - 1);
};

exports.addCourseToCoursesRooms = function (course_id) {
    coursesRooms.set(course_id, {connected_users: 0});
};

exports.addSessionToSessionRooms = function (session_id) {
    sessionsRooms.set(session_id, {connected_users: 0});
};

exports.emitEventToSessionRoom = function (room_id, eventName, args) {
    console.log('emitting "' + eventName + '" to session room ' + room_id);
    IO.to(room_id).emit(eventName, args);
};

exports.emitEventToCourseRoom = function (room_id, eventName, args) {
    console.log('emitting "' + eventName + '" to session room ' + room_id);
    IO.to(room_id).emit(eventName, args);
};

exports.emitEventToCourse = function (cid, eventName, args) {
    console.log('emitting "' + eventName + '" to course ' + cid);
    IO.to(cid).emit(eventName, args);
};

exports.isRegistered = function (user_id) {
    return clients.has(user_id);
};