wizerApp.controller('sessionController',
    function ($scope, $rootScope, $routeParams, $location, AuthService, SessionService) {

        console.log("Hello from sessionController");
        $scope.sessionID = "";
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

        // SessionService.getSessionByID($routeParams.id)
        //     .then(function (data) {
        //         console.log("Looking for session " + $routeParams.id);
        //         $scope.profile = data;
        //         console.log(data);
        //     });


        $scope.connectSession = function() {
            SessionService.connectSession($scope.sessionID)
                .then(function(data) {
                    $scope.session = data;
                    console.log("Connected to session " + JSON.stringify(data));
                })
                .catch(function(err) {
                    console.log("Error connection to session");
                });
        }


    });