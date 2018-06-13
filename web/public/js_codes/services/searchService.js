wizerApp.service('SearchService', function ($http) {

    this.freeTextSearch = function (keywords){
        console.log('search service');
        return $http.get('/search/free-text-search?keyword=' + keywords)
            .then(function (data){
                // console.log(data);
                return data.data;
            }, function() {
                console.log("Error getting results for: " + keywords);
                return [];
            });
    };
});

