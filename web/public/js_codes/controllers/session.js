wizerApp.controller('sessionController',
    function ($scope, $rootScope, $routeParams, $location, AuthService, SessionService) {

        console.log("Hello from sessionController");

        $scope.user = {};

        var ensureLogged = function() {
            if (!AuthService.isLoggedIn()) {
                $location.path('/');
            }
            else {
                $scope.user = $rootScope.user;
            }
        };

        ensureLogged();


        $scope.session = {};

        SessionService.getSessionByID($routeParams.id)
            .then(function (data) {
                console.log("Looking for session " + $routeParams.id);
                $scope.profile = data;
                console.log(data);
            });


    });