
wizerApp.service('ProfileService', function($http) {

    this.getProfileByID = function(id) {
        return $http.get('/get-profile?id=' + id)
            .then(function(data) {
                return data.data;
            }, function(){
                console.log("Error getting user with ID = " + id);
            });
    };


    this.getProfileByName = function(name) {
        var query = '?name=' + name;
        return $http.get('/get-user-by-name' + query)
            .then(function(data) {
                return data;
            },
                function() {
                    console.log("Error getting users with the name \"" + name + "\"");
                });
    };

});