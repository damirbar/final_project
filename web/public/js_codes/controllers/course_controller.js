wizerApp.controller('courseController',
    function ($scope, $routeParams, courseService) {

        console.log("Hello from profileController");
        $scope.courseArr = [];

        if($routeParams.course_name){

            courseService.searchCoursesByName($routeParams.course_name)
                .then(function (data) {
                    console.log("in controller Looking for " + $routeParams.course_name);
                    console.log(data);
                    $scope.courseArr = data;
                });
        }else {
            courseService.getCourses()
                .then(function (data) {
                    console.log("Looking for all courses");
                    $scope.courseArr = data;
                    console.log(data);
                });
        }
    });