var express = require('express');
var router = express.Router();
var Course = require("../schemas/course");
var User = require("../schemas/user");
var File = require("../schemas/file");


let globalCid = "000001";
router.post("/create-course", function (req, res) {

    Course.findOne({}).sort({"cid": -1}).then(function (ans) {
        if (ans) {
            globalCid = ans.cid + 1;
        }
        User.findOne({email: req.body.teacher}, function (err, teacher) {
            if(teacher) {
                const course = new Course({
                    cid: globalCid,
                    name: req.body.name,
                    department: req.body.department,
                    teacher_fname: teacher.first_name,
                    teacher_lname: teacher.last_name,
                    location: req.body.location,
                    points: req.body.points,
                    students: [teacher.id]
                });

                course.save(function (err) {
                    if (err) {
                        if (err.name === 'MongoError' && err.code === 11000) {
                            // Duplicate username
                            console.log('Course ' + course.name + " cannot be added id " + course.cid + ' already exists!');
                            return res.status(500).json({message: 'Course ' + course.name + " cannot be added id " + course.cid + ' already exists!'});
                        }
                        if (err.name === 'ValidationError') {
                            //ValidationError
                            for (field in err.errors) {
                                console.log("you must provide: " + field + " field");
                                return res.status(500).json({message: "you must provide: " + field + " field"});
                            }
                        }
                        // Some other error
                        console.log(err);
                        return res.status(500).send(err);
                    }
                    res.status(200).json(course);
                    console.log("successfully added course " + course.name + " to db");
                });
            }
            else return res.status(404).json({message: "no teacher: " + req.body.teacher});
        });
    });
});


router.get('/get-all-courses', function (req, res, next) {
    Course.find({}, function (err, courses) {
        if (err) {
            console.log("Error while finding courses");
            return next(err);
        } else if (courses) {
            console.log(courses);
            return res.json(courses);
        } else {
            console.log("No Courses available");
            return res.json("No courses available");
        }
    });
});


router.get('/get-all-courses-by-id', function (req, res) { // You get: Array of IDs
    const id = req.query.id;
    Course.find({students: id}, function (err, courses) {
        if (err) return err;
        res.status(200).json(courses);
    });
});


router.get('/get-my-courses', function (req, res) {

    User.findOne({email: req.verifiedEmail}, function(err, user) {
        Course.find({students: user._id}, function (err, courses) {
            if (err) return err;
            res.status(200).json(courses);
        });
    });

});




/////////////////////////////////////////////////////////////////////////////////////////////


router.get('/search-by-name', function (req, res, next) {

    const ip = req.headers["x-forwarded-for"] || req.connection.remoteAddress;
    const course_name = req.query.course_name;

    console.log("Got a search for " + course_name);
    console.log("Got a " + req.method + " request from " + ip);

    Course.find({name: course_name}, function (err, courses) {
        if (err) {
            console.log("Error while finding courses");
            return next(err);
        } else if (courses) {
            console.log(courses);
            console.log("Found: " + courses);
            return res.json(courses);
        } else {
            console.log("No Courses available");
            return res.json("No courses available");
        }
    });
});



/////////////////////////////////////////////////////////////////////////////////////////////


const multer = require('multer');
const upload = multer({dest: 'upload/'});
const type = upload.single('recfile');
const cloudinary = require('cloudinary');
const fs = require('fs');
const config = require('../config/config');

cloudinary.config({
    cloud_name: config.cloudniary.cloud_name,
    api_key: config.cloudniary.api_key,
    api_secret: config.cloudniary.api_secret
});

let first = true;
router.post('/post-file', type, function (req, res) {
    if (!req.file) {
        res.status(400).json({message: 'no file'});
    }
    else {
        const path = req.file.path;
        if (first) {
            first = false;
            if (!req.file.originalname.match(/\.(pdf)$/)) {
                fs.unlinkSync(path);
                res.status(400).json({message: 'wrong file'});
            }
            else {
                res.status(200).json({message: 'received file'});
                console.log(path);
                Course.findOne({cid: req.query.cid}, function (err, course) {
                    if (err) return next(err);
                    if (course) {
                        console.log("starting to upload " + req.file.originalname);
                        cloudinary.v2.uploader.upload(path,
                            {
                                public_id: "courses/" + course.cid + "/" + req.file.filename
                            },
                            function (err, result) {
                                fs.unlinkSync(path);
                                if (err) return err;
                                console.log(result);//test this
                                User.findOne({email: req.verifiedEmail}, function (err, user) {
                                    if (err) return err;
                                    if (user) {
                                        const ans = new File({
                                            originalName: req.file.originalname,
                                            uploaderid: user.id,
                                            url: result.url,
                                            type: result.format,
                                            size: result.bytes,
                                            hidden: false
                                        });
                                        ans.save(function (err, updated_file) {
                                            if (err) return (err);
                                            course.files.push(updated_file.id);
                                            course.save();
                                        });
                                    }
                                });
                            });
                    }
                    else {
                        console.log("no such course");
                        fs.unlinkSync(path);
                    }
                });
            }
        }
        else {
            fs.unlinkSync(path);
        }
    }
});

router.get("/add-student-to-course", function (req, res) {
    Course.findOne({cid: req.query.cid}, function (err, course) {
        if (err) return err;
        if (course) {
            User.findOne({email: req.verifiedEmail}, function (err, user) {
                if (err) return err;
                if (user && user.role === "teacher") {
                    User.findOne({email: req.query.student}, function (err, student) {
                        if(student) {
                            course.students.push(student.id);
                            course.save();
                            res.status(200).json(course);

                            //notify users
                            //todo test this!!!
                            User.find({id: {$in :course.students}}, function (err, students) {
                                students.forEach(function(stud){
                                    let notify ={
                                        type:  "Course",
                                        body: student.first_name + " joined the course "+ course.name,
                                        date:  Date.now()
                                    };
                                    stud.notifications.push(notify);
                                    stud.save();
                                });
                            });
                            //
                        }
                        else res.status(404).json({message: "no student " +  req.query.student});

                    });
                }
                else res.status(404).json({message: "no such user" + req.verifiedEmail +" or not teacher"});
            });
        }
        else res.status(404).json({message: "no such course" + req.query.cid});
    })
});

router.get("/get-course", function (req, res) {
    Course.findOne({cid: req.query.cid}, function (err, course) {
        if (err) return err;
        if (course) res.status(200).json(course);
        else res.status(404).json({message: "no such course"});
    })
});

router.get("/get-course-files", function (req, res) {
    Course.findOne({cid: req.query.cid}, function (err, course) {
        if (err) return err;
        if (course) {
            File.find({_id: {$in : course.files}},function (err, files) {
                if(err) return err;
                res.status(200).json(files);
            });
        }
        else res.status(404).json({message: "no such course"});
    })
});
module.exports = router;