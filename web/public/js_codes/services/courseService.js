wizerApp.service('CourseService', function($http, socketIO) {

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


    this.getMessages = function(cid) {

        var query = '?cid=' + cid;
        return $http.get('courses/get-all-messages' + query)
            .then(function(data) {

                    socketIO.emit('joinCourseMessages', cid);
                    console.log("This is a print from get course's messages.\nI got: " + data.data);
                    return data.data;
                },
                function(err) {
                    console.log("Error getting the course messages. " + err);
                });
    };


    this.sendMessage = function(posterID, courseId, type, message) {
        return $http.post('/courses/messages', {poster_id: posterID, cid: courseId, type: type, body: message})
            .then(function (data) {
                socketIO.emit('postCourseMessage', );
                return data.data;
            }, function () {
                console.log("Error sending message to course with ID = " + courseId);
            });
    };


    this.sendReply = function(replierID, posterID,courseId, type, message, message_id) {
        return $http.post('/courses/reply', {poster_id: posterID, replier_id: replierID, cid: courseId, type: type, body: message, mid: message_id})
            .then(function (data) {
                return data.data;
            }, function () {
                console.log("Error sending reply to course with ID = " + courseId);
            });
    };


    this.getMessageReplies = function (message_id){
        return $http.get('/courses/get-message-replies?mid=' + message_id)
            .then(function(data){
                return data.data;
            }).catch(function(err){
                console.log("error getting replies");
            });
    };


    this.getCourseSessions = function (cid){
        return $http.get('/courses/get-all-sessions?cid=' + cid)
            .then(function(data){
                return data.data;
            }).catch(function(err){
                console.log("error getting replies");
            });
    };


    this.createSession = function(sess) {

        return $http.post('courses/create-session', sess)
            .then(function(data) {

                    console.log("This is a print from create course.\nI got: " + data.data);
                    return data.data;
                },
                function(err) {
                    console.log("Error creating the course courses with the name " + err);
                });
    };

});