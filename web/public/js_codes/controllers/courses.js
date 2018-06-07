wizerApp.controller('coursesController',
    function ($scope, $routeParams, $location, CourseService) {

    $scope.courses = [];
    $scope.loading = true;

    $scope.getMyCourses = function() {
        CourseService.getMyCourses()
            .then(function (data) {
                $scope.courses = data;
                console.log("Courses = " + JSON.stringify(data));
                $scope.loading = false;
            }, function(){
                console.log("Error getting my courses");
                $scope.loading = false;
            })
    };

    $scope.getMyCourses();

    console.log($location.path())

    });

