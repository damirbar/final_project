wizerApp.controller('profileController',
    function ($scope, $routeParams, ProfileService, CourseService) {

        console.log("Hello from profileController");
        var defaultProfilePicture = "https://images.youtrendit.com/1487525287/desktop/avatar-600/youtrendit_Australopitekus.jpg";
        $scope.userArr = [];
        $scope.profile = {};
        $scope.readMore = false;
        $scope.courses = [];

        var initPage = function() {
            CourseService.getCoursesById($scope.profile.courses)
                .then(function (data) {
                    console.log("got courses");
                    $scope.courses = data.courses;
                    console.log(JSON.stringify(data));
                });
        };

        ProfileService.getProfileByID($routeParams.id)
            .then(function (data) {
                console.log("Looking for " + $routeParams.id);
                $scope.profile = data;
                console.log(data);
                initPage();
            });

        $scope.getPhoto = function() {
            return $scope.profile.photos ? ($scope.profile.photos.length == 0 ? defaultProfilePicture : $scope.profile.photos[0]) : defaultProfilePicture;
        };

        $scope.readMoreButton = function() {
            $scope.readMore = !$scope.readMore;
        };



    });