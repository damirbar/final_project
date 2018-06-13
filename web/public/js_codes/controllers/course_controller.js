wizerApp.controller('courseController',
    function ($scope, $routeParams, $rootScope, $http, CourseService) {

        // console.log("Hello from profileController");
        $rootScope.showSearchNav = true;
        $scope.course = {};
        $scope.courseToCreate = {};
        $scope.courseFiles = [];


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


    });