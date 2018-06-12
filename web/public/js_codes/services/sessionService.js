wizerApp.service('SessionService', function ($http, socketIO) {

    this.getSessionByID = function (id) {
        return $http.post('/sessions/connect-session', {sid: id})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error getting session with ID = " + id);
            });
    };

    this.connectSession = function (id, name) {
        console.log("ID = " + id);
        console.log('joined session');
        return $http.post('/sessions/connect-session', {sid: id, name: name})
            .then(function (data) {
                console.log(data);
                socketIO.emit('joinSession', data.data.sid);
                return data.data;
            }, function (err) {
                console.log("Error getting session with ID = " + id);
                console.log("ERROR: " + JSON.stringify(err));
                if (err.status === 404) {
                    console.log("SESSION NOT FOUND");
                    return {error: "Session not found"};
                } else {
                    return {error: "Unkown error connecting to session"};
                }
            });
    };

    this.createSession = function (sid, name, location) {
        console.log("ID = " + sid);
        return $http.post('/sessions/create-session', {sid: sid, name: name, location: location})
            .then(function (data) {
                socketIO.emit('createSession', sid);
                return data.data;
            }, function (err) {
                console.log("Error creating session with ID = " + id);
                return {error: "Error creating session"};
            });
    };

    this.sendMessage = function(senderID, sessionId, type, message) {
        return $http.post('/sessions/messages', {sender_id: senderID, sid: sessionId, type: type, body: message})
            .then(function (data) {
                socketIO.emit('postSessionMessage', );
                return data.data;
            }, function () {
                console.log("Error sending message to session with ID = " + sessionId);
            });
    };


    this.sendReply = function(sessionId, type, message, message_id) {
        return $http.post('/sessions/reply', {sid: sessionId, type: type, body: message, mid: message_id})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error sending message to session with ID = " + sessionId);
            });
    };


    this.getSession = function(sessionId) {
        return $http.get('/sessions/get-session?sid=' + sessionId)
            .then(function (data) {
                // console.log('joined session');
                return data.data;
            }, function () {
                console.log("Error getting session with ID = " + sessionId);
            });
    };

    this.getMessages = function(sessionId) {
        return $http.get('/sessions/get-all-messages?sid=' + sessionId)
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error getting messages from session with ID = " + sessionId);
            });
    };


    this.getMessage = function(msg_id) {
        return $http.get('/sessions/get-message?mid=' + msg_id)
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error getting messages from session with ID = " + sessionId);
            });
    };

    this.disconnect = function(sessionId) {
        return $http.get('/sessions/disconnect?sid=' + sessionId)
            .then(function (data) {
                socketIO.emit('leaveSession', sessionId);
                return data.data;
            }, function () {
                console.log("Error disconnecting from session with ID = " + sessionId);
            });
    };

    this.rateMessage = function(sessionId, messageId, rating) {
        return $http.get('/sessions/rate-message?sid=' + sessionId + "&msgid=" + messageId + "&rating=" + rating)
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error rating message in session with ID = " + sessionId);
            });
    };

    this.rateReplyMessage = function(sessionId, replytoId, messageId, rating) {
        return $http.get('/sessions/rate-reply-message?sid=' + sessionId + "&msgid=" + messageId + "&rating=" + rating)
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error rating message in session with ID = " + sessionId);
            });
    };

    this.rateSession = function(val, sessionId) {
        return $http.get('/sessions/change-val?sid=' + sessionId + "&val=" + val)
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error rating message in session with ID = " + sessionId);
            });
    }

    // this.getVideo = function(sessionId) {
    //     return $http.get('/sessions/video?sid=' + sessionId)
    //         .then(function (data) {
    //             return data.data;
    //         }, function () {
    //             console.log("Error rating message in session with ID = " + sessionId);
    //         });
    // }

});