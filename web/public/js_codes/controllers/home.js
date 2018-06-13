wizerApp.controller('homeController', ['$scope', '$http', 'SessionService', 'SearchService', '$location',
    function ($scope, $http, SessionService, SearchService, $location) {

        //Free text search
        $scope.searchTerm = {keywords: ""}; //keywords for the free text search

        $scope.freeTextSearch = function () {
            console.log($scope.searchTerm.keywords);
            // SearchService.freeTextSearch($scope.searchTerm.keywords);
            $location.path("/search/" + $scope.searchTerm.keywords);
        };
    }]);




