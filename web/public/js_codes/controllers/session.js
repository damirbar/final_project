wizerApp.controller('sessionController',
    function ($scope, $rootScope, $routeParams, $location, AuthService, SessionService) {

        console.log("Hello from sessionController");
        $scope.sessionID = "";
        $scope.loggedUser = {};
        $scope.isConnectedToSession = false;
        $scope.session = {};
        $scope.message = "";

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
            console.log("SESSION ID = " + $scope.sessionID);
            SessionService.connectSession($scope.sessionID)
                .then(function(data) {
                    $scope.session = data;
                    console.log("Connected to session " + JSON.stringify(data.session));
                    $scope.isConnectedToSession = true;
                    $scope.session = data.sess
                    getting();

                })
                .catch(function(err) {
                    console.log("Error connection to session");
                });
        }

        $scope.sendMessage = function() {

            SessionService.sendMessage($scope.sessionID, "question", ["Q: blahblah", "A: " + $scope.message])
                .then(function(data) {
                    console.log("Sent message");
                })
                .catch(function(err) {
                    console.log("Error with sending message");
                });
        };;

        $scope.getMessages = function () {

            SessionService.getMessages("1234")
                .then(function(data) {
                    console.log(JSON.stringify(data));
                })
                .catch(function(err) {
                    console.log("Error with getting messages");
                });

        }

        var getting = function() {
            // while(!$scope.isConnectedToSession);
            $scope.getMessages();
        };







    });