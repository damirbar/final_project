wizerApp.service('searchService', function ($http) {

    this.freeTextSearch = function (keyword){
        console.log('search service');
        $http.get('/search/free-text-search?keyword=' + keyword)
            .then(function (results){
                return results;
            });
    }
});