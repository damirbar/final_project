
wizerApp.service('ProfileService', function($http) {

    this.getProfileByID = function(id) {
        return $http.get('/students/get-profile?id=' + id)
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


    this.signupStudent = function(student) {
        // var query = "?first_name=" + student.fname +
        //     "&last_name=" + student.lname +
        //     "&email=" + student.email +
        //     "&pass=" + student.password;

        console.log("Signup function activated!");

        return $http.post("/auth/new-user", student)
            .then(function(data) {
                console.log("Successfully sent post with the query");
                if (data.data) {
                    return {success: true, data: data.data};
                } else {
                    return {success: false, data: data.data};
                }
                // return data.data;
            })
            .catch(function(err) {
                console.log("An error occurred in singup function :" + JSON.stringify(err));

                return {error: "Error"};
            });

    };


    this.editProfile = function(profile) {

        return $http.post('students/edit-profile', {user: profile})
            .then(function(data) {

                    console.log("This is a print from profileService.js.\nI got: " + data.data);

                    return data.data;
                },
                function() {
                    console.log("Error getting users with the name \"" + fname + " " + lname + "\"");
                });
    };

});