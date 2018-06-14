wizerApp.controller('courseController',
    function ($scope, $routeParams, $rootScope, $http, CourseService, socketIO) {

        // console.log("Hello from profileController");
        $rootScope.showSearchNav = true;
        $scope.course = {};
        $scope.courseToCreate = {};
        $scope.courseFiles = [];
        $scope.message = {type: "question", body: ""};
        $scope.messages = [];


        $scope.getCourse = function () {
            console.log("Fetching course...");
            CourseService.getCourseById($routeParams.id)
                .then(function (data) {
                    console.log("in controller Looking for " + $routeParams.id);
                    console.log(data);
                    $scope.course = data;
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
                    console.log("got messages from getMessages");
                    console.log(data);
                    $scope.messages = data;
                });
        };

        $scope.sendMessage = function() {
            CourseService.sendMessage($rootScope.loggedUser._id, $scope.course.cid, $scope.message.type, $scope.message.body)
                .then(function (data) {
                    console.log("Sent message");
                    // $scope.getMessages();
                    $scope.message = {body: ""};
                })
                .catch(function (err) {
                    console.log(err);
                });
        };


        socketIO.on('newCourseMessage', function(message){
            var message_id = message._id;
            var index = $scope.messages.length;
            $scope.messages[message_id] = index;
            $scope.messages.push(message);
            console.log("Pushing message (in course) " + message);

        });
    });