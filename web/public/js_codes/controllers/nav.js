
wizerApp.controller('navController', ['$scope', 'AuthService', function($scope, AuthService) {

    $scope.user = {};

    $scope.login = function() {
        AuthService.auth($scope.user.email, $scope.user.password)
            .then(function(data) {
                console.log("Got " + data + " from login function");
            }, function() {
                console.log("An error occurred");
            });
    }

}]);