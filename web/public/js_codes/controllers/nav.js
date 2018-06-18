wizerApp.controller('navController', function ($scope, AuthService, $location, $timeout, $window, ProfileService, socketIO, SearchService, $rootScope, $interval, $http) {

        $scope.loadingNotifications = true;
        $scope.hasLoginError = false;
        $scope.loginErrorMessage = "";
        $scope.hasSignupError = false;
        $scope.signupErrorMessage = "";
        $scope.notifications = [];

        $scope.homeClick = function() {
            $('.search-nav-form').addClass('ng-hide');
        };
        $scope.nonHomeClick = function() {
            $('.search-nav-form').removeClass('ng-hide');
        };


        $scope.loggedUser = {};
        $scope.user = {};
        $scope.user.role = "student";
        $scope.isLogged = false;

        $scope.checkLogin = function () {
            if ($scope.isLogged)
                return true;
            console.log("CALLED CHECKLOGIN!");
            if (AuthService.isLoggedIn()) {
                AuthService.getUserByToken()
                    .then(function (data) {
                        console.log("DATA = " + JSON.stringify(data));
                        registerUserToSocketIO(data.data._id);
                        $rootScope.loggedUser = data.data;
                        $scope.loggedUser = $rootScope.loggedUser;

                        $scope.getNotifications();

                        console.log(JSON.stringify(data.data));
                        $scope.isLogged = true;
                        $("#loginModal").modal('hide');
                        if ($rootScope.loggedUser) $('.profile-link').attr("href", "/profile/" + $rootScope.loggedUser._id);
                        else console.log("$rootScope.loggedUser is null");
                        return true;
                    }, function (err) {
                        console.log("Encountered an error!");
                        $scope.isLogged = false;
                        console.log("Error: " + JSON.stringify(err));
                        $scope.hasLoginError = true;
                        $scope.loginErrorMessage = err.message;
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
                    // data = data.data;
                    // $rootScope.loggedUser = data;
                    // $scope.loggedUser = data;

                    // if (data.status !== 200) {
                    //     console.log("ffffffffffffffffffffffffffffffffffff!");
                    //     $scope.isLogged = false;
                    //     console.log("Error: " + JSON.stringify(data));
                    //     $scope.hasLoginError = true;
                    //     $scope.loginErrorMessage = data.data.message;
                    //     return false;
                    // }

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
            console.log(AuthService.user_id);
            unregisterUserFromSocketIO(AuthService.user_id);
            $scope.isLogged = false;
            AuthService.logout();
            // $("#loginModal").modal('show');
            $location.path('/');
            $timeout(function () {
                // $location.url('/');
                var url = "http://" + $window.location.host + "/Account/Login";
                $window.location.href = url;
                $scope.isLogged = false;
            }, 1000);
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
                        $("#loginModal").modal('hide');
                    } else {
                        console.log("An error occurred during sign up!");
                        console.log("kkkkkkkkkkkkkkkkkkkkkkkk!");
                        $scope.isLogged = false;
                        console.log("Error: " + JSON.stringify(data));
                        $scope.hasSignupError = true;
                        $scope.signupErrorMessage = data.data.message;
                        return false;
                    }
                });

        };

        $scope.getNotifications = function () {
            console.log("Called get notifications");
            ProfileService.getNotifications(0, 10)
                .then(function (data) {
                    console.log("Got " + data.length + " notifications");
                    console.log(data);
                    $scope.notifications = data;
                    $scope.loadingNotifications = false;
                }, function (err) {
                    console.log("An error occurred in facebookLogin()! " + JSON.stringify(err));
                });
        };


        $scope.onSignIn = function (googleUser) {
            var profile = googleUser.getBasicProfile();
            console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
            console.log('Name: ' + profile.getName());
            console.log('Image URL: ' + profile.getImageUrl());
            console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
        };


        $scope.roleChoose = function(role) {
            console.log("Called role choose");
            $('.dropdown-header').text(role.charAt(0).toUpperCase() + role.slice(1));
            $scope.user.role = role;
        };


        ///////socket.io registration
        function registerUserToSocketIO (user_id) {
            AuthService.user_id = user_id;
            socketIO.on('ackConnection', function (){
                console.log("successfully registered to socket.io");
            });
            socketIO.emit('registerClientToClients', user_id);
        }
        //////

        ///////socket.io unregistration
        function unregisterUserFromSocketIO (user_id) {
            socketIO.emit('unregisterClientFromClients', user_id);
        }
        //////


        var ctr = 0;
        $scope.isLoggedIn = function() {
            return $scope.isLogged;
        };

        $scope.checkLogin();


        //Free text search
        $scope.searchTerm = {keywords: ""}; //keywords for the free text search

        $scope.freeTextSearch = function(){
            console.log($scope.searchTerm.keywords);
            // SearchService.freeTextSearch($scope.searchTerm.keywords);
            $location.path("/search/" + $scope.searchTerm.keywords);
        };


    });