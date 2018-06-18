wizerApp.controller('courseController',
    function ($scope, $routeParams, $rootScope, $http, $window, CourseService, socketIO) {

        $scope.loading                  = true;
        $rootScope.showSearchNav        = true;
        $scope.course                   = {};
        $scope.courseToCreate           = {};
        $scope.courseFiles              = [];
        $scope.message                  = {type: "question", body: ""};
        $scope.courseMessages           = [];
        $scope.courseMessagesMap        = {};
        $scope.reply                    = {type: "question", body: ""};
        $scope.messageToReply           = {};
        $scope.currentMessageReplies    = [];
        $scope.currentMessageRepliesMap = {};
        $scope.sessions                 = [];
        $scope.sessionCreate            = {};

        socketIO.emit()

        $scope.getCourse = function () {
            console.log("Fetching course...");
            CourseService.getCourseById($routeParams.id)
                .then(function (data) {
                    console.log("in controller Looking for " + $routeParams.id);
                    console.log(data);
                    $scope.course = data;
                    $scope.loading = false;
                }, function(err) {
                    $scope.loading = false;
                });
        };
        $scope.getCourse();



        // $scope.createCourse = function () {
        //     console.log("Fetching course...");
        //     CourseService.createCourse($scope.courseToCreate)
        //         .then(function (data) {
        //             console.log("called create-course with " + $scope.courseToCreate);
        //             console.log(data);
        //
        //         });
        // };


        $scope.addStudent = function (student_id) {
            CourseService.addStudent(student_id)
                .then(function (data) {
                    console.log("got data from addStudent to course");
                    console.log(data);
                });
        };

        $scope.getFiles = function () {
            CourseService.getFiles($routeParams.id)
                .then(function (data) {
                    console.log("got files from getFiles");
                    console.log(data);
                    $scope.courseFiles = data;
                });
        };


        $scope.uploadFile = function () {

            var file = $scope.myFile;
            var uploadUrl = "/courses/post-file?cid=" + $scope.course.cid;
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

        $scope.getMessages = function(){
            CourseService.getMessages($routeParams.id)
                .then(function (data) {
                    if (data) {
                        console.log("got messages from getMessages");
                        let index = 0;
                        $scope.courseMessages = data;
                        console.log($scope.courseMessages);
                        $scope.courseMessages.forEach(function(message){
                            $scope.courseMessagesMap[message._id] = index;
                            ++index;
                        });
                    }
                });
        };


        $scope.getCourseSessions = function(){
            CourseService.getCourseSessions($routeParams.id)
                .then(function (data) {
                    console.log("got sessions from getCourseSessions");
                    console.log(data);
                    $scope.sessions = data;
                });
        };
        $scope.getCourseSessions();

        $scope.sendMessage = function() {
            CourseService.sendMessage($rootScope.loggedUser._id, $scope.course.cid, $scope.message.type, $scope.message.body)
                .then(function (data) {
                    console.log("Sent message");
                    // $scope.getMessages();
                    // $scope.getMessages();
                    $scope.message = {body: ""};
                })
                .catch(function (err) {
                    console.log(err);
                });
        };


        $scope.sendReply = function () {
            CourseService.sendReply($rootScope.loggedUser._id, $scope.messageToReply.poster_id, $scope.course.cid, $scope.reply.type, $scope.reply.body, $scope.messageToReply._id)
                .then(function (data) {
                    console.log("Sent message");
                    $scope.reply = {body: ""};
                })
                .catch(function (err) {
                    console.log(err);
                });
        };

        $scope.setMsgToReply = function (msg) {
            console.log(msg);
            $scope.repliesWindowOpen = true;
            $scope.messageToReply = msg;
            $scope.getMessageReplies(msg._id);
        };



        $scope.getMessageReplies = function (message_id){
            CourseService.getMessageReplies(message_id)
                .then(function(data){

                    var index = 0;
                    console.log("trigerred getMessageReplies")
                    $scope.currentMessageReplies = data;

                        $scope.currentMessageReplies.forEach(function (message) {
                            $scope.currentMessageRepliesMap[message._id] = index;
                            ++index;
                        });


                });
        };

        $scope.openFile = function(link){
            $window.open(link, '_blank');
        };


        $scope.closeRepliesWindow = function(){
            $scope.repliesWindowOpen = false;
            $scope.flushCurrentMessageReplies();
        };


        $scope.flushCurrentMessageReplies = function(){
            $scope.currentMessageReplies = [];
            $scope.currentMessageRepliesMap = {};
        };

        $scope.createSession = function() {
            $scope.sessionCreate.cid = $scope.course.cid;
            CourseService.createSession($scope.sessionCreate)
                .then(function(data) {
                    $('#collapseSessionCreate').collapse('hide');
                    $scope.getCourseSessions();
                    $scope.sessionCreate = {};
                    $scope.sessionCreate.cid = $scope.course.cid;
                    socketIO.emit("joinSession",data.sid);
                    console.log("data = " + data);
                }, function(err) {
                    console.log("error = " + err);
                });
        };


        socketIO.on('newCourseMessageReply', function(message){
            if($scope.messageToReply._id === message.parent_id) {
                var message_id = message._id;
                var index = $scope.currentMessageReplies.length;
                $scope.currentMessageRepliesMap[message_id] = index;
                $scope.currentMessageReplies.push(message);
            }
            $scope.courseMessages[$scope.courseMessagesMap[message.parent_id]].num_of_replies++;
        });


        socketIO.on('newCourseMessage', function(message){
            console.log(message);
            var message_id = message._id;
            var index = $scope.courseMessages.length;
            $scope.courseMessagesMap[message_id] = index;
            $scope.courseMessages.push(message);
            console.log($scope.courseMessages);
            console.log("Pushing message (in course) " + message);
        });
    });