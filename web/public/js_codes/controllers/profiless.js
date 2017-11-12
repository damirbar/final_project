wizerApp.controller('getProfilesController',
    function ($scope, $routeParams, ProfileService) {

        $scope.userArr = [];

        console.log("Hello from getprofilecontroller");

        // ProfileService.getProfileByName($routeParams.fname, $routeParams.lname)
        //     .then(function (data) {
        //         console.log("Looking for " + $routeParams.first_name + " " + $routeParams.last_name);
        //         $scope.userArr = data;
        //         console.log(data);
        //     });

        ProfileService.getProfileByName($routeParams.fname)
            .then(function (data) {
                console.log("Looking for " + $routeParams.first_name);
                console.log(data);
            });

    });