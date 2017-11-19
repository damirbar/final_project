wizerApp.service('AuthService', function($http, AuthToken) {

    // "Login" function
    this.auth = function(email, password) {
        return $http.post('/auth-login-user-pass?email=' + email +
        '&password=' + password)
            .then(function(data) {
                console.log(data.data.msg);

                AuthToken.setToken(data.data.token);
                return data.data;
            }, function(){
                console.log("Error Authenticating " + email);
            });
    };

    // Auth.isLoggedIn()
    this.isLoggedIn = function(email) {
        if (AuthToken.getToken()) {
            // $http.get('/get-user-by-email', email)
            //     .then(function(data) {
            //         return true;
            //     }, function() {
            //
            //     });
            return true;
        }
        return false;
    };


    this.logout = function() {
        AuthToken.setToken();
    };

    // this.getUser = function() {
    //     if (AuthToken.getToken()) {
    //         return $http.post('');
    //     } else {
    //         $q.reject({message: "User has no token"});
    //     }
    // };


})

.factory('AuthToken', function($window) {
    var authTokenFactory = {};

    // AuthToken.setToken(token);
    authTokenFactory.setToken = function(token) {
        console.log("Inside setToken");
        if (token) {
            console.log("Token found in setToken");
            $window.localStorage.setItem('token', token);
        } else {
            console.log("Token NOT found in setToken");
            $window.localStorage.removeItem('token');
        }
    };

    // AuthToken.getToken();
    authTokenFactory.getToken = function() {
        return $window.localStorage.getItem('token');
    };

    // authTokenFactory.getUser = function() {
    //     if (AuthToken.getToken()) {
    //         return $http.post('');
    //     } else {
    //         $q.reject({ message: "User has no token"});
    //     }
    // };

    return authTokenFactory;
});