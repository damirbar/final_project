
wizerApp.service('CourseService', function($http) {

    this.getCourses = function() {
        return $http.get('/courses')
            .then(function(data) {
                return data.data;
            }, function(){
                console.log("Error getting courses");
            });
    };

    this.getCoursesById = function(id) {
        return $http.get('/courses/get-all-courses-by-id?id=' + id)
            .then(function(data) {
                return data.data;
            }, function() {
                console.log("Error getting courses by ids");
            });
    };

    this.getMyCourses = function() {
        return $http.get('/courses/get-my-courses')
            .then(function(data) {
                return data.data;
            }, function() {
                console.log("Error getting my courses");
            });
    };


    this.searchCoursesByName = function(courseName) {

        var query = '?course_name=' + courseName;

        return $http.get('courses/search-by-name' + query)
            .then(function(data) {

                    console.log("This is a print from courseService.js.\nI got: " + data.data);
                    return data.data;
                },
                function() {
                    console.log("Error getting courses with the name " + courseName);
                });
    };

});