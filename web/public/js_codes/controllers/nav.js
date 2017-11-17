
wizerApp.controller('navController', ['$scope', 'AuthService', '$location', '$timeout',
    function($scope, AuthService, $location, $timeout) {

    $scope.user = {};
    $scope.hasToken = false;

    if (AuthService.isLoggedIn()) {
        console.log("Success! User is logged in.");
        $scope.hasToken = true;
    } else {
        console.log("Failure! User is NOT logged in.");
        $scope.hasToken = false;
    }

    $scope.login = function() {
        AuthService.auth($scope.user.email, $scope.user.password)
            .then(function(data) {
                console.log("Got " + data + " from login function");
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

}]);