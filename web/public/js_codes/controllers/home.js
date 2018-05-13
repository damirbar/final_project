wizerApp.controller('homeController', ['$scope', '$http', 'searchService', 'SessionService',
    function ($scope, $http, searchService ,SessionService) {

    $scope.keyword = "";

    //free-text-search

    $scope.search = function (keyword){

        $scope.searchResults = searchService.freeTextSearch(ketword);
        console.log($scope.searchResults);

    }



}]);




