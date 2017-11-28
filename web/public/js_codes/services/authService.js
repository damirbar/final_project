wizerApp.service('AuthService', function($http, AuthToken, $rootScope) {


    this.googleLogin = function() {
        return $http.get("/auth/google");
    };

    this.facebookLogin = function() {
        return $http.get("/auth/facebook");
    };

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
    this.isLoggedIn = function() {
        // console.log("Sending POST to /get-user-by-token");
        if (AuthToken.getToken()) {
            // var token = AuthToken.getToken();
            // return $http.get('/get-user-by-token?token=' + token);
                // .then(function(data) {
                //     $rootScope.user = data.data.student;
                //     console.log(data.data);
                //     return true;
                // }, function() {
                //
                // });
            return true;
        }
        return false;
    };

    this.getUserByToken = function() {
        var token = AuthToken.getToken();
        return $http.get('/get-user-by-token?token=' + token);
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