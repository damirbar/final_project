
wizerApp.controller('navController', ['$scope', 'AuthService', '$location', '$timeout', 'ProfileService', '$rootScope', '$interval',
    function($scope, AuthService, $location, $timeout, ProfileService, $rootScope, $interval) {

    $scope.user = {};
    $scope.isLogged = false;


    $scope.checkLogin = function() {
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
                .then(function(data) {
                    $rootScope.user = data.data.student;
                    console.log(JSON.stringify(data.data.student));
                    $scope.isLogged = true;
                    if($rootScope.user) $('.profile-link').attr("href", "#/profile/" + $rootScope.user._id);
                    return true;
                }, function() {
                    console.log("Encountered an error!");
                    $scope.isLogged = false;
                    return false;
                });
        }
    };

    $scope.login = function() {
        AuthService.auth($scope.user.email, $scope.user.password)
            .then(function(data) {
                console.log("Got " + data + " from login function");
                data = data.data;
                $rootScope.user = data;
                $scope.user = data;
                console.log(data);
                $scope.checkLogin();

                $timeout(function() {
                    $location.path('/');
                    $scope.isLogged = false;
                }, 2000);
            }, function() {
                console.log("An error occurred");
            });
    };


    $scope.logout = function() {
        console.log("Activated logout function");
        $scope.isLogged = false;
        AuthService.logout();
        $location.path('/');
        $timeout(function() {
            $location.path('/');
            $scope.isLogged = false;
        }, 2000);
    };

    $scope.signup = function() {

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


    $scope.checkLogin();

    // $interval(function() {
    //     // console.log($rootScope.user);
    //     if($rootScope.user) $('.profile-link').attr("href", "#/profile/" + $rootScope.user._id);
    // }, 3000);

}]);