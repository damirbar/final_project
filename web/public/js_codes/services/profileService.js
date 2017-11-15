
wizerApp.service('ProfileService', function($http) {

    this.getProfileByID = function(id) {
        return $http.get('/get-profile?id=' + id)
            .then(function(data) {
                return data.data;
            }, function(){
                console.log("Error getting user with ID = " + id);
            });
    };


    this.getProfileByName = function(fname, lname) {

        var qfname = fname ? fname : "null";
        var qlname = lname ? lname : "null";

        var query = '?fname=' + qfname + '&lname=' + qlname;


        return $http.get('students/search-by-name' + query)
            .then(function(data) {

                console.log("This is a print from profileService.js.\nI got: " + data.data);

                return data.data;
            },
                function() {
                    console.log("Error getting users with the name \"" + fname + " " + lname + "\"");
                });
    };

});