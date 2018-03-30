wizerApp.service('SessionService', function ($http) {

    this.getSessionByID = function (id) {
        return $http.post('/api/v1/sessions/connect-session', {internal_id: id})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error getting session with ID = " + id);
            });
    };
});