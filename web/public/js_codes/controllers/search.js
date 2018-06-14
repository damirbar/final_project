wizerApp.controller('searchController',
    function ($scope, $routeParams, ProfileService, SearchService) {

    $scope.loading = true;
        $scope.results = {};
        SearchService.freeTextSearch($routeParams.keyword)
            .then(function (data) {
                $scope.results = data;
                $scope.loading = true;
                console.log(data);
            }, function (err) {

            });


    });