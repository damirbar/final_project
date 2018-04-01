wizerApp.controller('profileController',
    function ($scope, $routeParams, ProfileService) {

        console.log("Hello from profileController");
        var defaultProfilePicture = "https://images.youtrendit.com/1487525287/desktop/avatar-600/youtrendit_Australopitekus.jpg";
        $scope.userArr = [];
        $scope.profile = {};
        
        ProfileService.getProfileByID($routeParams.id)
            .then(function (data) {
                console.log("Looking for " + $routeParams.id);
                $scope.profile = data;
                console.log(data);
            });

        $scope.getPhoto = function() {
            return $scope.profile ? ($scope.profile.photos.length == 0 ? defaultProfilePicture : $scope.profile.photos[0]) : defaultProfilePicture;
        }

        // ProfileService.getProfileByName("eran")
        //     .then(function (data) {
        //         console.log("Looking for " + $routeParams.first_name);
        //         console.log(data);
        //         $scope.userArr = data;
        //     });

    });