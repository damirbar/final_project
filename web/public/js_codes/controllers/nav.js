wizerApp.controller('navController', ['$scope', 'AuthService', '$location', '$timeout', 'ProfileService', '$rootScope', '$interval',
    '$http', function ($scope, AuthService, $location, $timeout, ProfileService, $rootScope, $interval, $http) {

        $scope.loggedUser = {};
        $scope.user = {};
        // $scope.user.role = "student";
        $scope.isLogged = false;

        $scope.checkLogin = function () {
            if ($scope.isLogged)
                return true;
            console.log("CALLED CHECKLOGIN!");
            if (AuthService.isLoggedIn()) {
                AuthService.getUserByToken()
                    .then(function (data) {
                        console.log("DATA = " + JSON.stringify(data))
                        $rootScope.loggedUser = data.data;
                        $scope.loggedUser = $rootScope.loggedUser;
                        console.log(JSON.stringify(data.data));
                        $scope.isLogged = true;
                        if ($rootScope.loggedUser) $('.profile-link').attr("href", "/profile/" + $rootScope.loggedUser._id);
                        else console.log("$rootScope.loggedUser is null");
                        return true;
                    }, function () {
                        console.log("Encountered an error!");
                        $scope.isLogged = false;
                        return false;
                    });
            }
            else {
                console.log("User is not logged in");
            }
        };

        $scope.login = function () {
            AuthService.auth($scope.user.email, $scope.user.password)
                .then(function (data) {
                    console.log("Got " + JSON.stringify(data) + " from login function");
                    data = data.data;
                    // $rootScope.loggedUser = data;
                    // $scope.loggedUser = data;
                    console.log(data);
                    $scope.checkLogin();
                    // $scope.$apply();
                    $timeout(function () {
                    //     $location.path('/');
                    //     $scope.isLogged = false;
                    //     $scope.$apply();


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
                    console.log(JSON.stringify(data));
                    if (data.success) {

                        $rootScope.loggedUser = data.data;
                        $scope.login();
                        console.log(JSON.stringify(data.data));
                        $scope.isLogged = true;
                    } else {
                        console.log("An error occurred during sign up!");
                    }
                });

        };

        $scope.googleLogin = function () {
            console.log("Clicked google login");
            AuthService.googleLogin();
            // .then(function (data) {
            //     if (data.data.loggedUser) {
            //         console.log("Login succeeded with Google! The loggedUser is: " + JSON.stringify(data.data.loggedUser));
            //         $rootScope.loggedUser = data.data.loggedUser;
            //         $scope.isLogged = true;
            //     } else {
            //         console.log("Error logging in with Google! " + JSON.stringify(data));
            //         $scope.isLogged = false;
            //     }
            //
            // })
            // .catch(function(err) {
            //     console.log("An error occurred! " + JSON.stringify(err));
            // });
            // console.log("Clicked google login");
            // $http.get("/auth/google")
            //     .then(function(data) {
            //         console.log("Successsssssssssssssss");
            //     }, function(err) {
            //         console.log("An error occurred in googleLogin()! " + JSON.stringify(err));
            //     });

        };
        $scope.onSignIn = function (googleUser) {
            var profile = googleUser.getBasicProfile();
            console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
            console.log('Name: ' + profile.getName());
            console.log('Image URL: ' + profile.getImageUrl());
            console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
        };

        // function onSignIn(googleUser) {
        //     var profile = googleUser.getBasicProfile();
        //     console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
        //     console.log('Name: ' + profile.getName());
        //     console.log('Image URL: ' + profile.getImageUrl());
        //     console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
        // }

        $scope.facebookLogin = function () {
            // AuthService.facebookLogin()
            //     .then(function (data) {
            //         if (data.data.loggedUser) {
            //             console.log("Login succeeded with Facebook! The loggedUser is: " + JSON.stringify(data.data.loggedUser));
            //             $rootScope.loggedUser = data.data.loggedUser;
            //             $scope.isLogged = true;
            //         } else {
            //             console.log("Error logging in with Facebook! " + JSON.stringify(data));
            //             $scope.isLogged = false;
            //         }
            //
            //     });
            console.log("Clicked facebook login");
            $http.get("/auth/facebook")
                .then(function (data) {
                    console.log("Successsssssssssssssss");
                }, function (err) {
                    console.log("An error occurred in facebookLogin()! " + JSON.stringify(err));
                });
        };


        $scope.roleChoose = function(role) {
            console.log("Called role choose");
            $('.dropdown-header').text(role.charAt(0).toUpperCase() + role.slice(1));
            $scope.user.role = role;
        };


        var ctr = 0;
        $scope.isLoggedIn = function() {
            return $scope.isLogged;
        };

        $scope.checkLogin();

    }]);