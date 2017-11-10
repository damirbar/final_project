
wizerApp.controller('profileController',
    function($scope, $routeParams, ProfileService) {

    $scope.profile = {};

    ProfileService.getProfileByID($routeParams.id)
        .then(function(data) {
            $scope.profile = data.data;
        });



});