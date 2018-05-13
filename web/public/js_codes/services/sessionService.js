wizerApp.service('SessionService', function ($http) {

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
        return $http.post('/sessions/connect-session', {sid: id, name: name})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error getting session with ID = " + id);
            });
    };

    this.sendMessage = function(sessionId, type, message) {
        return $http.post('/sessions/messages', {sid: sessionId, type: type, body: message})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error sending message to session with ID = " + sessionId);
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

    this.disconnect = function(sessionId) {
        return $http.get('/sessions/disconnect?sid=' + sessionId)
            .then(function (data) {
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