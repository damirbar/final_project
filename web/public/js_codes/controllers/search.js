wizerApp.controller('searchController',
    function ($scope, $routeParams, ProfileService, SearchService) {

        $scope.results = {};
        SearchService.freeTextSearch($routeParams.keyword)
            .then(function (data) {
                $scope.results = data;
                console.log(data);
            }, function (err) {

            });


    });