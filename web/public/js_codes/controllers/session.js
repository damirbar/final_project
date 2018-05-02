wizerApp.controller('sessionController',
    function ($scope, $rootScope, $routeParams, $location, AuthService, SessionService) {

        console.log("Hello from sessionController");
        $scope.sessionID = "";
        $scope.loggedUser = {};
        $scope.isConnectedToSession = false;
        $scope.session = {}

        var ensureLogged = function() {
            if (!AuthService.isLoggedIn()) {
                $location.path('/');
            }
            else {
                $scope.loggedUser = $rootScope.loggedUser;
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
                    $scope.isConnectedToSession = true;
                    $scope.session = data.sess
                })
                .catch(function(err) {
                    console.log("Error connection to session");
                });
        }




    });