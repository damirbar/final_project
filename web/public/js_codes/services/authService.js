wizerApp.service('AuthService', function($http, AuthToken) {

    this.auth = function(email, password) {
        return $http.post('/auth-login-user-pass?email=' + email +
        '&password=' + password)
            .then(function(data) {
                console.log(data.data.msg);

                AuthToken.setToken(data.data.token);
                return data.data.success;
            }, function(){
                console.log("Error Authenticating " + email);
            });
    };

    // Auth.isLoggedIn()
    this.isLoggedIn = function() {
        if (AuthToken.getToken()) {
            return true;
        } else {
            return false;
        }
    };


    this.logout = function() {
        AuthToken.setToken();
    };


})

.factory('AuthToken', function($window) {
    var authTokenFactory = {};

    // AuthToken.setToken(token);
    authTokenFactory.setToken = function(token) {

        if (token) {
            $window.localStorage.setItem('token', token);
        } else {
            $window.localStorage.removeItem('token');
        }
    };

    // AuthToken.getToken();
    authTokenFactory.getToken = function() {
        return $window.localStorage.getItem('token');
    };

    return authTokenFactory;
});