wizerApp.controller('searchController',
    function ($scope, $routeParams, $rootScope, $window, ProfileService, SearchService) {

        $rootScope.showSearchNav = true;
        $scope.loading = true;
        $scope.results = {};
        SearchService.freeTextSearch($routeParams.keyword)
            .then(function (data) {
                $scope.results = data;
                $scope.loading = false;
                console.log(data);
            }, function (err) {

            });

        $scope.openFile = function(link){
            $window.open(link, '_blank');
        };


    });