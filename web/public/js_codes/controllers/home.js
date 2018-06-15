wizerApp.controller('homeController', ['$scope', '$http', '$rootScope', 'SessionService', 'SearchService', '$location',
    function ($scope, $http, $rootScope, SessionService, SearchService, $location) {

        $rootScope.showSearchNav = false;

        //Free text search
        $scope.searchTerm = {keywords: ""}; //keywords for the free text search

        $scope.freeTextSearch = function () {
            console.log($scope.searchTerm.keywords);
            // SearchService.freeTextSearch($scope.searchTerm.keywords);
            $location.path("/search/" + $scope.searchTerm.keywords);
        };
    }]);




