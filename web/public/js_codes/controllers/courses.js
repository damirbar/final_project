wizerApp.controller('coursesController',
    function ($scope, $routeParams, $rootScope, $location, CourseService) {

    $scope.courses = [];
    $scope.loading = true;
    $scope.isTeacher = $rootScope.loggedUser.role === 'teacher';
    $scope.courseCreate = {};

    $scope.getMyCourses = function() {
        $scope.loading = true;
        CourseService.getMyCourses()
            .then(function (data) {
                $scope.courses = data;
                console.log("Courses = " + JSON.stringify(data));
                $scope.loading = false;
            }, function(){
                console.log("Error getting my courses");
                $scope.loading = false;
            });
    };

    $scope.getMyCourses();

    // console.log($location.path());


    $scope.createCourse = function() {
        CourseService.createCourse($scope.courseCreate)
            .then(function(data) {
                $scope.courseCreate = {};
                $('#collapseExample').collapse('hide');
                $scope.getMyCourses();
                console.log("data = " + data);
            }, function(err) {
                console.log("error = " + err);
            });
    };

    });

