wizerApp.controller('sessionController',
    function ($scope, $rootScope, $routeParams, $location, $window, $interval, AuthService, SessionService, $http, UploadService, socketIO) {

        console.log("Hello from sessionController");
        $scope.sessionID = $routeParams.id ? $routeParams.id : "";
        $scope.defaultSessionUserName = "Anon";
        $scope.sessionUserName = "";
        $scope.loggedUser = {};
        $scope.isConnectedToSession = false;
        $scope.session = {};
        $scope.message = {type: "question", body: ""};

        $scope.connectedUsers = 0;
        $scope.rate_positive = 0;
        $scope.rate_negative = 0;

        $scope.sessionMessages = [];
        $scope.sessionMessagesMap = {};
        $scope.msgLikes = [];
        $scope.msgHates = [];

        $scope.reply = {type: "question", body: ""};
        $scope.messageToReply = {};
        $scope.repliesWindowOpen = false;
        $scope.currentMessageReplies = [];
        $scope.currentMessageRepliesMap = {};
        $scope.firstConnectionTry = true;


        socketIO.on('newSessionMessage', function(message){
            var message_id = message._id;
            var index = $scope.sessionMessages.length;
            $scope.sessionMessagesMap[message_id] = index;
           $scope.sessionMessages.push(message);
        });


        socketIO.on('newSessionMessageReply', function(message){
            if($scope.messageToReply._id === message.parent_id) {
                var message_id = message._id;
                var index = $scope.currentMessageReplies.length;
                $scope.currentMessageRepliesMap[message_id] = index;
                $scope.currentMessageReplies.push(message);
            }

            $scope.sessionMessages[$scope.sessionMessagesMap[message.parent_id]].num_of_replies += 1;
        });

        socketIO.on('updateSessionConnectedUsers', function(update){
            console.log('updating');
            $scope.connectedUsers = update;
        });

        socketIO.on('updateMessageRating', function(update){
            var mess = $scope.sessionMessages[$scope.sessionMessagesMap[update.message_id]];
            mess.likes += update.likes;
            mess.dislikes += update.dislikes;
            });

        socketIO.on('updateMessageReplyRating', function(update){
            if($scope.currentMessageReplies.length === 0) return;
            var mess = $scope.currentMessageReplies[$scope.currentMessageRepliesMap[update.message_id]];
            mess.likes += update.likes;
            mess.dislikes += update.dislikes;
        });

        socketIO.on('updateSessionRating', function(update){
            console.log("updateSessionRating");
            console.log($scope.connected_users);
            console.log(update);
           $scope.rate_negative += update.dislikes;
           $scope.rate_positive += update.likes;

           console.log("neg");
           console.log($scope.rate_negative);
           console.log("pos");
           console.log($scope.rate_positive);
        });


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
            SessionService.connectSession($scope.sessionID, $scope.sessionUserName !== "" ? $scope.sessionUserName : "Anon")
                .then(function (data) {

                    if (data.error) {

                        $scope.errorConnectionMessage = data.error;
                        $scope.firstConnectionTry = false;
                    }
                    else {
                        $scope.session = data;
                        console.log("Connected to session as " + $scope.sessionUserName !== "" ? $scope.sessionUserName : $scope.sessionUserName);// + JSON.stringify(data.session));
                        console.log("SESSION DATA = " + JSON.stringify(data));
                        if (data._id) {
                            $scope.isConnectedToSession = true;
                            $scope.session = data;
                            getting();
                            $scope.isConnectedToSession = true;
                            $scope.rate_positive = data.likes;
                            $scope.rate_negative = data.dislikes;
                            console.log(JSON.stringify(data));
                            console.log("neg");
                            console.log($scope.rate_negative);
                            console.log("pos");
                            console.log($scope.rate_positive);
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
            SessionService.sendMessage($rootScope.loggedUser._id, $scope.sessionUserName, $scope.sessionID, $scope.message.type, $scope.message.body)
                .then(function (data) {
                    console.log("Sent message");
                    $scope.message = {body: ""};
                })
                .catch(function (err) {
                    console.log(err);
                });
        };

        $scope.getMessageReplies = function (message_id){
            SessionService.getMessageReplies(message_id)
                .then(function(data){

                    var index = 0;
                    $scope.currentMessageReplies = data;

                    $scope.currentMessageReplies.forEach(function(message){
                        message.liked = message.disliked = false;
                        if(message.likers.includes($rootScope.loggedUser._id)){
                            message.liked = true;
                        }else if(message.dislikers.includes($rootScope.loggedUser._id)){
                            message.disliked = true;
                        }
                        $scope.currentMessageRepliesMap[message._id] = index;
                        ++index;
                    });
                });
        };

        $scope.closeRepliesWindow = function(){
            $scope.repliesWindowOpen = false;
            $scope.flushCurrentMessageReplies();
        };

        $scope.flushCurrentMessageReplies = function(){
            $scope.currentMessageReplies = [];
            $scope.currentMessageRepliesMap = {};
        };

        $scope.createSession = function () {
            SessionService.createSession($scope.sessionID, $scope.createSessionName, $scope.createSessionLocation)
                .then(function (data) {
                    if (data.error) {
                        $scope.errorCreationMessage = data.error;
                        $scope.firstCreationTry = false;

                    } else {
                        $scope.session = data;
                        console.log("Created session with ID = " + $scope.createSessionID);// + JSON.stringify(data.session));
                        console.log("SESSION DATA = " + JSON.stringify(data));
                        socketIO.emit('joinSession', data.sid);
                        if (data) {
                            $scope.isConnectedToSession = true;
                            JSON.stringify(data);
                            $scope.session = data;
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

        $scope.getMessages = function (){
            SessionService.getMessages($scope.sessionID)
                .then(function (data) {

                    var index = 0;

                    $scope.sessionMessages = data;
                    $scope.sessionMessages.forEach(function(message){
                        message.liked = message.disliked = false;
                        if(message.likers.includes($rootScope.loggedUser._id)){
                            message.liked = true;
                        }else if(message.dislikers.includes($rootScope.loggedUser._id)){
                            message.disliked = true;
                        }

                        $scope.sessionMessagesMap[message._id] = index;
                        ++index;

                    });

                })
                .catch(function (err) {
                    console.log(err);
                    console.log("Error with getting messages");
                });

        };


        $scope.getToken = function () {
            return $window.localStorage.getItem('token');
        };


        $scope.disconnect = function () {
            // SessionService.disconnect($scope.sessionID)
                // .then(function (data) {
                    // $scope.isConnectedToSession = false;
                    // console.log(JSON.stringify(data));
                    socketIO.emit('leaveSession', $scope.session.sid);
                // })
                // .catch(function (err) {
                    // console.log("Error with disconnecting from session");
                // });
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
            $scope.getMessages();
        };

        $scope.getSession = function () {
            SessionService.getSession($scope.sessionID)
                .then(function (data) {
                    $scope.session = data;
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

        $scope.rateMessage = function (type, sid, mid, rate){

            SessionService.rateMessage(sid, mid, rate)
                .then(function (data) {

                    var messagesArray;
                    var messagesMap;

                    if(type === 1){
                         messagesArray = $scope.sessionMessages;
                         messagesMap = $scope.sessionMessagesMap;
                    }else{
                        messagesArray = $scope.currentMessageReplies;
                        messagesMap = $scope.currentMessageRepliesMap;
                    }


                    if(rate == 1){
                        if(messagesArray[messagesMap[mid]].disliked){
                            messagesArray[messagesMap[mid]].liked = true;
                            messagesArray[messagesMap[mid]].disliked = false;
                        }else if(messagesArray[messagesMap[mid]].liked){
                            messagesArray[messagesMap[mid]].liked = false;
                        }else{
                            messagesArray[messagesMap[mid]].liked = true;
                        }
                    }else{
                        if(messagesArray[messagesMap[mid]].liked){
                            messagesArray[messagesMap[mid]].liked = false;
                            messagesArray[messagesMap[mid]].disliked = true;
                        }else if(messagesArray[messagesMap[mid]].disliked){
                            messagesArray[messagesMap[mid]].disliked = false;
                        }else{
                            messagesArray[messagesMap[mid]].disliked = false;
                        }
                    }
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


        $scope.setMsgToReply = function (msg) {
            console.log(msg);
            $scope.repliesWindowOpen = true;
            $scope.messageToReply = msg;
            $scope.getMessageReplies(msg._id);
        };

        $scope.sendReply = function () {
            SessionService.sendReply($rootScope.loggedUser._id, $scope.sessionUserName, $scope.messageToReply.poster_id,
                $scope.sessionID, $scope.reply.type, $scope.reply.body, $scope.messageToReply._id)
                .then(function (data) {
                    console.log("Sent message");
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

