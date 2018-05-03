wizerApp.controller('sessionController',
    function ($scope, $rootScope, $routeParams, $location, $window, AuthService, SessionService/*,socketIO*/) {

        console.log("Hello from sessionController");
        $scope.sessionID = "";
        $scope.loggedUser = {};
        $scope.isConnectedToSession = false;
        $scope.session = {};
        $scope.message = {type: "question", body: "", replyTo: ""};
        $scope.sessionMessages = [];

        var ensureLogged = function () {
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


        $scope.connectSession = function () {
            console.log("SESSION ID = " + $scope.sessionID);
            SessionService.connectSession($scope.sessionID)
                .then(function (data) {
                    $scope.session = data;
                    console.log("Connected to session ");// + JSON.stringify(data.session));
                    // var sock = socketIO();
                    io.connect();
                     // console.log("The io looks like this: " + JSON.stringify(a));
                    // io.emit('send:message', {
                    //     message: "BLAHBLIHBLAH"
                    // }, function(result) {
                    //     if (!result) {
                    //         console.log("NO RESULT");
                    //     } else {
                    //         console.log("SOCKET RESULT = " + result)
                    //     }
                    // });
                    $scope.isConnectedToSession = true;
                    $scope.session = data.sess;
                    getting();

                })
                .catch(function (err) {
                    console.log("Error connection to session");
                });
        };

        $scope.sendMessage = function () {

            SessionService.sendMessage($scope.sessionID, $scope.message.type, [$scope.message.replyTo, $scope.message.body])
                .then(function (data) {
                    console.log("Sent message");
                    $scope.getMessages();
                })
                .catch(function (err) {
                    console.log("Error with sending message");
                });
        };

        $scope.getMessages = function () {

            SessionService.getMessages("1234")
                .then(function (data) {
                    // console.log(JSON.stringify(data));
                    $scope.sessionMessages = data.messages;
                    $("#msg-cnt").animate({scrollTop: 999999999}, 1000);
                })
                .catch(function (err) {
                    console.log("Error with getting messages");
                });

        };

        $scope.getVideo = function () {

            SessionService.getVideo("1234")
                .then(function (data) {
                    console.log('\n\n\t\t\t'+JSON.stringify(data)+'\n\n');
                })
                .catch(function (err) {
                    console.log("Error with getting video");
                });

        };
        // $scope.getVideo();

        $scope.disconnect = function () {

            SessionService.disconnect($scope.sessionID)
                .then(function(data) {
                    $scope.isConnectedToSession = false;
                    console.log(JSON.stringify(data));
                })
                .catch(function(err) {
                    console.log("Error with disconnecting from session");
                });

        };

        // $scope.$on("$destroy", function() {
        //     console.log("DISCONNECTING FROM SESSION");
        //     if($scope.isConnectedToSession) {
        //         $scope.isConnectedToSession = false;
        //         $scope.session = null;
        //         // $scope.disconnect();
        //     }
        // });
        //
        // $scope.$$applicationDestroyed("$destroy", function() {
        //     console.log("DISCONNECTING FROM SESSION");
        //     if($scope.isConnectedToSession) {
        //         $scope.isConnectedToSession = false;
        //         $scope.session = null;
        //         $scope.disconnect();
        //     }
        // });

        var getting = function () {
            // while(!$scope.isConnectedToSession);
            $scope.getMessages();
        };


        $scope.typeChoose = function (type) {
            $('.msg-type-dropdown-header').text(type.charAt(0).toUpperCase() + type.slice(1));
            $scope.message.type = type;
        };

        $scope.rateMessage = function (sid, mid, rate) {
            SessionService.rateMessage(sid, mid, rate)
                .then(function (data) {
                    // console.log(JSON.stringify(data));
                    $scope.getMessages();
                })
                .catch(function (err) {
                    console.log("Error with getting messages");
                });
        }

        // $scope.assignReply = function(msg) {
        //     console.log("MSG TO REPLY:" + JSON.stringify(msg))
        //     $scope.message.replyTo = msg.type.charAt(0).toUpperCase() + msg.type.slice(1) + ': ' + msg.body[0]
        // };

    });