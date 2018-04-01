
wizerApp.service('CourseService', function($http) {

    this.getCourses = function() {
        return $http.get('/courses')
            .then(function(data) {
                return data.data;
            }, function(){
                console.log("Error getting courses");
            });
    };

    this.getCoursesById = function(arr) {
        return $http.post('/courses/get-all-courses-by-id', {courses: arr})
            .then(function(data) {
                return data.data;
            }, function() {
                console.log("Error getting courses by ids");
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