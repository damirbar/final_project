
wizerApp.controller('profileController',
    function($scope, $routeParams, ProfileService) {

    $scope.profile = {};

    ProfileService.getProfileByID($routeParams.id)
        .then(function(data) {
            console.log("Looking for " + $routeParams.id);
            $scope.profile = data;
            console.log(data);
        });





});