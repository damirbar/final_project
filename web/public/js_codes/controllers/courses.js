wizerApp.controller('coursesController',
    function ($scope, $routeParams, $location, CourseService) {

    $scope.courses = [];

    $scope.getMyCourses = function() {
        CourseService.getMyCourses()
            .then(function (data) {
                $scope.courses = data;
                console.log("Courses = " + JSON.stringify(data));
            }, function(){
                console.log("Error getting my courses");
            })
    };

    $scope.getMyCourses();

    console.log($location.path())

    });

