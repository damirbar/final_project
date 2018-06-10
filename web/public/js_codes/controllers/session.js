wizerApp.controller('sessionController',
    function ($scope, $rootScope, $routeParams, $location, $window, $interval, AuthService, SessionService, $http, UploadService, socketIO) {

        console.log("Hello from sessionController");
        $scope.sessionID = "";
        $scope.defaultSessionUserName = "Anon";
        $scope.sessionUserName = "";
        $scope.loggedUser = {};
        $scope.isConnectedToSession = false;
        $scope.session = null;
        $scope.session = {};
        $scope.message = {type: "question", body: ""};

        $scope.sessionMessages = [];
        $scope.msgLikes = [];
        $scope.msgHates = [];

        $scope.reply = {type: "question", body: ""};
        $scope.messageToReply = {};
        $scope.repliesWindowOpen = false;

        $scope.firstConnectionTry = true;

        var ensureLogged = function () {
            if (!AuthService.isLoggedIn()) {
                $location.path('/');
            }
            else {
                $scope.loggedUser = $rootScope.loggedUser;
            }
        };

        ensureLogged();


        // SessionService.getSessionByID($routeParams.id)
        //     .then(function (data) {
        //         console.log("Looking for session " + $routeParams.id);
        //         $scope.profile = data;
        //         console.log(data);
        //     });


        $scope.connectSession = function () {
            console.log("SESSION ID = " + $scope.sessionID);
            SessionService.connectSession($scope.sessionID, $scope.sessionUserName !== "" ? $scope.sessionUserName : $scope.sessionUserName)
                .then(function (data) {

                    if (data.error) {

                        $scope.errorConnectionMessage = data.error;
                        $scope.firstConnectionTry = false;
                    }
                    else {
                        $scope.session = data;
                        console.log("Connected to session as " + $scope.sessionUserName !== "" ? $scope.sessionUserName : $scope.sessionUserName);// + JSON.stringify(data.session));
                        console.log("SESSION DATA = " + JSON.stringify(data));
                        io.connect();
                        if (data.session) {
                            $scope.isConnectedToSession = true;
                            $scope.session = data.session;
                            getting();
                        }
                        $scope.firstConnectionTry = false;
                    }
                })
                .catch(function (err) {
                    console.log("Error connection to session");
                    $scope.firstConnectionTry = false;
                });
        };

        $scope.sendMessage = function () {

            SessionService.sendMessage($scope.sessionID, $scope.message.type, $scope.message.body)
                .then(function (data) {
                    console.log("Sent message");
                    $scope.getMessages();
                    $scope.message = {body: ""};
                })
                .catch(function (err) {
                    console.log(err);
                });
        };


        $scope.createSession = function () {

            SessionService.createSession($scope.createSessionID, $scope.createSessionName, $scope.createSessionLocation)
                .then(function (data) {
                    if (data.error) {

                        $scope.errorCreationMessage = data.error;
                        $scope.firstCreationTry = false;

                    } else {
                        $scope.session = data;
                        console.log("Created session with ID = " + $scope.createSessionID);// + JSON.stringify(data.session));
                        console.log("SESSION DATA = " + JSON.stringify(data));
                        io.connect();
                        if (data.session) {
                            $scope.isConnectedToSession = true;
                            $scope.session = data.session;
                            getting();
                        }
                        $scope.firstCreationTry = false;
                    }
                })
                .catch(function (err) {
                    console.log(err);
                    $scope.firstConnectionTry = false;
                });

        };

        $scope.getMessages = function () {

            SessionService.getMessages($scope.sessionID)
                .then(function (data) {
                    // console.log(JSON.stringify(data));
                    var oldMessagesLength = $scope.sessionMessages.length;
                    $scope.sessionMessages = data;
                    if (oldMessagesLength != $scope.sessionMessages.length) {
                        $("#msg-cnt").animate({scrollTop: 0}, 1000);
                    }
                    $scope.msgLikes = [];
                    $scope.msgHates = [];
                    for (let i = 0; i < $scope.sessionMessages.length; ++i) {
                        if ($scope.sessionMessages[i].likers.includes($rootScope.loggedUser._id)) {
                            $scope.msgLikes.push(true);
                        } else {
                            $scope.msgLikes.push(false);
                        }
                        if ($scope.sessionMessages[i].dislikers.includes($rootScope.loggedUser._id)) {
                            $scope.msgHates.push(true);
                        } else {
                            $scope.msgHates.push(false);
                        }
                    }
                })
                .catch(function (err) {
                    console.log("Error with getting messages");
                });

        };


        $scope.getToken = function () {
            return $window.localStorage.getItem('token');
        };


        $scope.disconnect = function () {

            SessionService.disconnect($scope.sessionID)
                .then(function (data) {
                    $scope.isConnectedToSession = false;
                    console.log(JSON.stringify(data));
                })
                .catch(function (err) {
                    console.log("Error with disconnecting from session");
                });

        };


        $scope.$on('$locationChangeStart', function (event) {
            if ($scope.isConnectedToSession) {
                var answer = confirm("Are you sure you want to leave this page?");
                if (!answer) {
                    event.preventDefault();
                } else {
                    $scope.disconnect();
                }
            }
        });

        var getting = function () {
            // while(!$scope.isConnectedToSession);
            $scope.getMessages();
        };

        $scope.getSession = function () {
            SessionService.getSession($scope.sessionID)
                .then(function (data) {
                    $scope.session = data;
                    console.log(JSON.stringify(data));
                })
                .catch(function (err) {
                    console.log("Error with disconnecting from session");
                });
        };

        $scope.typeChoose = function (type) {
            $('.msg-type-dropdown-header').text(type.charAt(0).toUpperCase() + type.slice(1));
            $scope.message.type = type;
            $scope.reply.type = type;
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
        };

        $scope.rateReplyMessage = function (sid, origMsgId, mid, rate) {
            SessionService.rateReplyMessage(sid, origMsgId, mid, rate)
                .then(function (data) {
                    // console.log(JSON.stringify(data));
                    $scope.getMessages();
                    $scope.setMsgIdToReplyAndGetMessage(mid);
                })
                .catch(function (err) {
                    console.log("Error with getting messages");
                });
        };

        $scope.onTimeUpdate = function () {
            var currTime = $element[0].currentTime;
            if (currTime - $scope.videoCurrentTime > 0.5 ||
                $scope.videoCurrentTime - currTime > 0.5) {
                $element[0].currentTime = $scope.videoCurrentTime;
                console.log("CURRENT VIDEO TIME: " + $scope.videoCurrentTime);
            }
            $scope.$apply(function () {
                $scope.videoCurrentTime = $element[0].currentTime;
            });
        };

        $interval(function () {
            if ($scope.isConnectedToSession) {
                getting();
                $scope.getSession();
            }
        }, 3000);


        $scope.uploadFile = function () {

            var file = $scope.myFile;
            var uploadUrl = "/sessions/post-video?sid=" + $scope.sessionID;
            var fd = new FormData();
            fd.append('recfile', file);

            $http.post(uploadUrl, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
                .then(function (data) {
                        console.log("upload success!!!");
                    },
                    function () {
                        console.log("error!!");
                    });
        };


        $scope.setMsgIdToReplyAndGetMessage = function (id) {
            $scope.reply.msgID = id;

            SessionService.getMessage(id)
                .then(function (data) {
                    console.log("Got message: " + JSON.stringify(data));
                    $scope.messageToReply = data;
                    $scope.repliesWindowOpen = true;

                })
                .catch(function (err) {
                    console.log(err);
                });

        };


        $scope.sendReply = function () {

            SessionService.sendReply($scope.sessionID, $scope.reply.type, $scope.reply.body, $scope.reply.msgID)
                .then(function (data) {
                    console.log("Sent message");
                    $scope.getMessages();
                    $scope.setMsgIdToReplyAndGetMessage($scope.reply.msgID);
                    $scope.reply = {body: ""};
                })
                .catch(function (err) {
                    console.log(err);
                });

        };


        $scope.rateSession = function(val) {

            console.log("RATING SESSION");
            SessionService.rateSession(val, $scope.sessionID )
                .then(function (data) {
                    console.log("Sent rating = " + val + " successfully.");
                    $scope.session = data;
                })
                .catch(function (err) {
                    console.log(err);
                });

        };

    });

