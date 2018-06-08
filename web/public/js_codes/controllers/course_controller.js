wizerApp.controller('courseController',
    function ($scope, $routeParams, $rootScope, courseService) {

        // console.log("Hello from profileController");
        $rootScope.showSearchNav = true;
        $scope.course = {};


        courseService.searchCoursesByName($routeParams.course_name)
            .then(function (data) {
                console.log("in controller Looking for " + $routeParams.course_name);
                console.log(data);
                $scope.courseArr = data;
            });


    });