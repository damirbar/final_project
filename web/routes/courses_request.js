var express = require('express');
var router = express.Router();
var path = require("path");

var Course = require("../schemas/course");
var File = require("../schemas/file");

router.post("/add-course", function (req, res) {
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

router.get('/get-all-courses', function(req,res,next){
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


router.post('/get-all-courses-by-id', function(req,res,next){ // You get: Array of IDs

    var ObjectID     = require('mongodb').ObjectID;

    var coursesIds = req.body.courses;

    var ids = coursesIds.map(function(crs) {
        return ObjectID(String(crs));
    });

    var courses = [];

    function addToCourses(coursesToAdd) {
        courses = coursesToAdd;
        res.json({courses: courses});
    }

    Course.find({ '_id': { $in: ids } }, function(err, crses) {
        if(err) {
            console.log("Error in get-courses-by-id. error = " + err);
            error.message = err;
        } else {
            addToCourses(crses);
        }
    });

});

/////////////////////////////////////////////////////////////////////////////////////////////


router.get('/search-by-name', function(req,res,next){

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



router.get('/file-system', function(req,res){
        var id = req.query.id;
        console.log("Got " + id);
        Course.findOne({_id: id},function(err,course){
            console.log("finding one");
           if(err) throw err;
           File.find({},function(err,filesys){
               console.log("finding");
               if(err) throw err;
               console.log("filtering");
                var ans = filesys.filter(function(file){
                    return file.course_id === id;
                });
               console.log(ans);
               res.status(200).send(ans);
           });
        });
});

router.post('/upload-file',function(req,res){
    var course_id = req.query.course_id;
    var file = new File(req.body);
    file.course_id = course_id;
    file.save(function (err){
        if (err) {
            if (err.name === 'MongoError' && err.code === 11000) {
                // Duplicate username
                console.log('Failed adding file Error 11000 ' + file.name);
                return res.status(500).send('Failed adding course Error 11000 ' + file.name);
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
        res.send("successfully added " + file.name + " to db");
        console.log("successfully added " + file.name + " to db");
    });
});


router.get('/file-system/download-file',function(req,res){
        var file_id = req.query.file_id;
        var course_id = req.query.course_id;

        File.findOne({_id:file_id,course_id:course_id},function (err,file) {
                    if(err) throw err;
                    console.log("found " + file.url);
                    res.status(200).send(file.url);
        });
});

/////////////////////////////////////////////////////////////////////////////////////////////

module.exports = router;