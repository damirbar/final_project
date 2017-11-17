wizerApp.service('AuthService', function($http) {

    this.auth = function(email, password) {
        return $http.post('/auth-login-user-pass?email=' + email +
        '&password=' + password)
            .then(function(data) {
                console.log(data.data.msg);
                return data.data.success;
                // if (!data.data.success) {
                //     data.data = data.data.msg;
                // }
            }, function(){
                console.log("Error Authenticating " + email);
            });
    };

});