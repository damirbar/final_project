wizerApp.controller('navController', ['$scope', 'AuthService', '$location', '$timeout', 'ProfileService', '$rootScope', '$interval',
    '$http',
    function ($scope, AuthService, $location, $timeout, ProfileService, $rootScope, $interval, $http) {

        $scope.user = {};
        $scope.isLogged = false;


        $scope.checkLogin = function () {
            // if (AuthService.isLoggedIn()) {
            //     console.log("Success! User is logged in.");
            //     $scope.isLogged = true;
            //
            //     // $('.profile-link').attr("href", "#/profile/" + $rootScope.user._id);
            // } else {
            //     console.log("Failure! User is NOT logged in.");
            //     $scope.isLogged = false;
            // }
            if (AuthService.isLoggedIn()) {
                AuthService.getUserByToken()
                    .then(function (data) {
                        $rootScope.user = data.data.student;
                        console.log(JSON.stringify(data.data.student));
                        $scope.isLogged = true;
                        if ($rootScope.user) $('.profile-link').attr("href", "#/profile/" + $rootScope.user._id);
                        return true;
                    }, function () {
                        console.log("Encountered an error!");
                        $scope.isLogged = false;
                        return false;
                    });
            }
        };

        $scope.login = function () {
            AuthService.auth($scope.user.email, $scope.user.password)
                .then(function (data) {
                    console.log("Got " + data + " from login function");
                    data = data.data;
                    $rootScope.user = data;
                    $scope.user = data;
                    console.log(data);
                    $scope.checkLogin();

                    $timeout(function () {
                        $location.path('/');
                        $scope.isLogged = false;
                    }, 2000);
                }, function () {
                    console.log("An error occurred");
                });
        };


        $scope.logout = function () {
            console.log("Activated logout function");
            $scope.isLogged = false;
            AuthService.logout();
            $location.path('/');
            $timeout(function () {
                $location.path('/');
                $scope.isLogged = false;
            }, 2000);
        };

        $scope.signup = function () {

            console.log("Activated ctrl's signup function!");

            ProfileService.signupStudent($scope.user)
                .then(function (data) {
                    if (data.success) {
                        $scope.login();
                    } else {
                        console.log("An error occurred during sign up!");
                    }
                });

        };

        $scope.googleLogin = function () {
            // AuthService.googleLogin()
            //     .then(function (data) {
            //         if (data.data.user) {
            //             console.log("Login succeeded with Google! The user is: " + JSON.stringify(data.data.user));
            //             $rootScope.user = data.data.user;
            //             $scope.isLogged = true;
            //         } else {
            //             console.log("Error logging in with Google! " + JSON.stringify(data));
            //             $scope.isLogged = false;
            //         }
            //
            //     });
            console.log("Clicked google login");
            $http.get("/auth/google")
                .then(function(data) {
                    console.log("Successsssssssssssssss");
                }, function(err) {
                    console.log("An error occurred in googleLogin()! " + JSON.stringify(err));
                });

        };
        $scope.onSignIn = function(googleUser) {
            var profile = googleUser.getBasicProfile();
            console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
            console.log('Name: ' + profile.getName());
            console.log('Image URL: ' + profile.getImageUrl());
            console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
        };

        function onSignIn(googleUser) {
            var profile = googleUser.getBasicProfile();
            console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
            console.log('Name: ' + profile.getName());
            console.log('Image URL: ' + profile.getImageUrl());
            console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
        }

        $scope.facebookLogin = function () {
            // AuthService.facebookLogin()
            //     .then(function (data) {
            //         if (data.data.user) {
            //             console.log("Login succeeded with Facebook! The user is: " + JSON.stringify(data.data.user));
            //             $rootScope.user = data.data.user;
            //             $scope.isLogged = true;
            //         } else {
            //             console.log("Error logging in with Facebook! " + JSON.stringify(data));
            //             $scope.isLogged = false;
            //         }
            //
            //     });
            console.log("Clicked facebook login");
            $http.get("/auth/facebook")
                .then(function(data) {
                    console.log("Successsssssssssssssss");
                }, function(err) {
                    console.log("An error occurred in facebookLogin()! " + JSON.stringify(err));
                });
        };


        $scope.checkLogin();

        // $interval(function() {
        //     // console.log($rootScope.user);
        //     if($rootScope.user) $('.profile-link').attr("href", "#/profile/" + $rootScope.user._id);
        // }, 3000);

    }]);