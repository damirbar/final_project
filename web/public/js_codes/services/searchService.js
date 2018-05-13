wizerApp.service('SearchService', function ($http) {
    this.freeTextSearch = function (keywords){
        console.log('search service');
        $http.get('/search/free-text-search?keyword=' + keywords)
            .then(function (results){
                return results;
            });
    }
});