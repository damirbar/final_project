wizerApp.service('SearchService', function ($http) {

    this.freeTextSearch = function (keywords){
        console.log('search service');
        $http.get('/search/free-text-search?keyword=' + keywords)
            .then(function (results){
                console.log(results);
                return results;
            }, function() {
                console.log("Error getting results for: " + keywords);
            });
    }
});