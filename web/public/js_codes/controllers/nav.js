
wizerApp.controller('navController', ['$scope', 'AuthService', '$location', '$timeout', 'ProfileService', '$rootScope', '$interval',
    function($scope, AuthService, $location, $timeout, ProfileService, $rootScope, $interval) {

    $scope.user = {};
    $scope.hasToken = false;


    $scope.checkLogin = function() {
        if (AuthService.isLoggedIn()) {
            console.log("Success! User is logged in.");
            $scope.hasToken = true;
            // $('.profile-link').attr("href", "#/profile/" + $rootScope.user._id);
        } else {
            console.log("Failure! User is NOT logged in.");
            $scope.hasToken = false;
        }
    };

    $scope.checkLogin();

    $scope.login = function() {
        AuthService.auth($scope.user.email, $scope.user.password)
            .then(function(data) {
                console.log("Got " + data + " from login function");
                $rootScope.user = data.student;
                $scope.user = data.student;
                $location.path('/');
                $scope.checkLogin();
            }, function() {
                console.log("An error occurred");
            });
    };


    $scope.logout = function() {
        console.log("Activated logout function");
        AuthService.logout();
        $location.path('/');
        $timeout(function() {
            $location.path('/');
            $scope.hasToken = false;
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

    $interval(function() {
        // console.log($rootScope.user);
        $('.profile-link').attr("href", "#/profile/" + $rootScope.user._id);
    }, 3000);

}]);