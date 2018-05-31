wizerApp.controller('profileController',
    function ($scope, $routeParams, $http, ProfileService, CourseService) {

        console.log("Hello from profileController");
        var defaultProfilePicture = "https://images.youtrendit.com/1487525287/desktop/avatar-600/youtrendit_Australopitekus.jpg";
        $scope.userArr = [];
        $scope.profile = {};
        $scope.readMore = false;
        $scope.courses = [];
        $scope.editUser = {};

        var initPage = function() {
            CourseService.getCoursesById($routeParams.id)
                .then(function (data) {
                    console.log("got courses");
                    $scope.courses = data;
                    console.log(JSON.stringify(data));
                });
        };

        ProfileService.getProfileByID($routeParams.id)
            .then(function (data) {
                console.log("Looking for " + $routeParams.id);
                $scope.profile = data;
                $scope.editUser = data;
                console.log(data);
                initPage();
            });

        $scope.getPhoto = function() {
            return $scope.profile.photos ? ($scope.profile.profile_img == '' ? defaultProfilePicture : $scope.profile.profile_img) : defaultProfilePicture;
        };

        $scope.readMoreButton = function() {
            $scope.readMore = !$scope.readMore;
        };


        $scope.editProfile = function() {
            ProfileService.editProfile($scope.editUser)
                .then(function (data) {
                    console.log("GOT FROM EDIT PROFILE: " + JSON.stringify(data))
                })
                .catch(function (err) {
                    console.log("GOT ERROR FROM EDIT PROFILE: " + err);
                });
        };

        $scope.uploadPhoto = function(){

            var file = $scope.myFile;
            var uploadUrl = "/students/post-profile-image";
            var fd = new FormData();
            fd.append('recfile', file);

            $http.post(uploadUrl,fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
                .then(function(data){
                        console.log("upload success!!!");
                    },
                    function(){
                        console.log("error!!");
                    });
        };

    });