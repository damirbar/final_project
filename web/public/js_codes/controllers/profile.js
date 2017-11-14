wizerApp.controller('profileController',
    function ($scope, $routeParams, ProfileService) {

        console.log("Hello from profileController");
        $scope.userArr = [];
        $scope.profile = {};

        ProfileService.getProfileByID($routeParams.id)
            .then(function (data) {
                console.log("Looking for " + $routeParams.id);
                $scope.profile = data;
                console.log(data);
            });


        ProfileService.getProfileByName("eran")
            .then(function (data) {
                console.log("Looking for " + $routeParams.first_name);
                console.log(data);
                $scope.userArr = data.data;
            });

    });