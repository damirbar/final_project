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

    this.getCourseById = function(id) {
        return $http.get('/courses/get-course?cid=' + id)
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





    this.createCourse = function(course) {

        return $http.post('courses/create-course', course)
            .then(function(data) {

                    console.log("This is a print from create course.\nI got: " + data.data);
                    return data.data;
                },
                function(err) {
                    console.log("Error creating the course courses with the name " + err);
                });
    };


    this.addStudent = function(cid, student_id) {

        var query = '?cid=' + cid + '&id=' + student_id;
        return $http.get('courses/add-student-to-course' + query)
            .then(function(data) {

                    console.log("This is a print from add student to course.\nI got: " + data.data);
                    return data.data;
                },
                function(err) {
                    console.log("Error adding the student. " + err);
                });
    };


    this.getFiles = function(cid) {

        var query = '?cid=' + cid;
        return $http.get('courses/get-course-files' + query)
            .then(function(data) {

                    console.log("This is a print from get course's files.\nI got: " + data.data);
                    return data.data;
                },
                function(err) {
                    console.log("Error getting the course files. " + err);
                });
    };



});