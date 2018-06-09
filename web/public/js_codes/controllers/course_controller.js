wizerApp.controller('courseController',
    function ($scope, $routeParams, $rootScope, CourseService) {

        // console.log("Hello from profileController");
        $rootScope.showSearchNav = true;
        $scope.course = {};


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


    });