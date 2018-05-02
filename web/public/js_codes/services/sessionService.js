wizerApp.service('SessionService', function ($http) {

    this.getSessionByID = function (id) {
        return $http.post('/sessions/connect-session', {internal_id: id})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error getting session with ID = " + id);
            });
    };

    this.connectSession = function (id) {
        console.log("ID = " + id);
        return $http.post('/sessions/connect-session', {internal_id: id})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error getting session with ID = " + id);
            });
    };

    this.sendMessage = function(sessionId, type, message) {
        return $http.post('/session/messages', {sid: sessionId, type: type, body: message})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error sending message to session with ID = " + sessionId);
            });
    }
});