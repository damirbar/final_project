var express = require('express');
var router = express.Router();
var path = require("path");

var Course = require("../schemas/course");


router.post("/courses", function (req, res) {
    var course = new Course(req.body);
    course.save(function (err) {
        if (err) {
            if (err.name === 'MongoError' && err.code === 11000) {
                // Duplicate username
                console.log('Failed adding course Error 11000' + course.name);
                return res.status(500).send('Failed adding course Error 11000' + course.name);
            }
            if (err.name === 'ValidationError') {
                //ValidationError
                for (field in err.errors) {
                    console.log("Validation Error!");
                    return res.status(500).send("validation Error!");
                }
            }
            // Some other error
            console.log(err);
            return res.status(500).send(err);
        }
        res.send("successfully added " + course.name + " to db");
        console.log("successfully added " + course.name + " to db");
    });
});





/////////////////////////////////////////////////////////////////////////////////////////////


router.get('/courses', function(req,res,next){
    var ip = req.headers["x-forwarded-for"] || req.connection.remoteAddress;
    Course.find({}, function(err,courses){
        if(err) {
            console.log("Error while finding courses");
            return next(err);
        }else if(courses){
            console.log(courses);
            return res.json(courses);
        }else{
            console.log("No Courses available");
            return res.json("No courses available");
        }
    });
});



/////////////////////////////////////////////////////////////////////////////////////////////


router.get('/courses/search-by-name', function(req,res,next){

    var ip = req.headers["x-forwarded-for"] || req.connection.remoteAddress;
    var course_name = req.query.course_name;

    console.log("Got a search for " + course_name);
    console.log("Got a " + req.method + " request from " + ip);

    Course.find({name: course_name}, function(err,courses){
        if(err) {
            console.log("Error while finding courses");
            return next(err);
        }else if(courses){
            console.log(courses);
            console.log("Found: " + courses);
            return res.json(courses);
        }else{
            console.log("No Courses available");
            return res.json("No courses available");
        }
    });
});



router.get('/courses/get-fs', function(req,res){
        var course_id = req.query.id;
        console.log("Got " + course_id);
        Course.findOne({course_no: course_id},function(err,course){
            console.log("here");
           if(err) throw err;
           console.log(course);
           res.status(200).send(course);
        });



});


/////////////////////////////////////////////////////////////////////////////////////////////

module.exports = router;