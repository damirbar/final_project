var express = require('express');
var router = express.Router();
var Course = require("../schemas/course");
var User = require("../schemas/user");
var File = require("../schemas/file");
var Session = require("../schemas/session");
let Course_Message = require("../schemas/course_message");
const socketIOEmitter = require('../tools/socketIO');


let globalCid = "000001";

router.post("/create-course", function (req, res) {

    Course.findOne({}).sort({"cid": -1}).then(function (ans) {
        if (ans) {
            globalCid = String(Number(ans.cid) + 1);
        }
        User.findOne({email: req.body.teacher}, function (err, teacher) {
            if (teacher) {
                const course = new Course({
                    cid: globalCid,
                    name: req.body.name,
                    department: req.body.department,
                    teacher_fname: teacher.first_name,
                    teacher_lname: teacher.last_name,
                    location: req.body.location,
                    points: req.body.points,
                    students: [teacher.id],
                    teacher_email: req.body.teacher
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

    User.findOne({email: req.verifiedEmail}, function (err, user) {
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
            if (!req.file.originalname.match(/\.(pdf|doc|docx|ppt|pptx)$/)) {
                fs.unlinkSync(path);
                res.status(400).json({message: 'wrong file'});
            }
            else {
                Course.findOne({cid: req.query.cid}, function (err, course) {
                    if (err) return next(err);
                    if (course) {
                        console.log("starting to upload " + req.file.originalname);
                        cloudinary.v2.uploader.upload(path,
                            {
                                resource_type: 'raw',
                                public_id: "courses/" + course.cid + "/" + req.file.filename
                            },
                            function (err, result) {
                                fs.unlinkSync(path);
                                if (err) console.log(err);
                                console.log(result);//test this
                                User.findOne({email: req.verifiedEmail}, function (err, user) {
                                    if (err) return err;
                                    if (user) {
                                        const ans = new File({
                                            publicid: result.public_id,
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
                                            course.save().then(function () {
                                                res.status(200).json({message: 'received file'});
                                            });
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

router.delete('/remove-file',function (req, res) {

    let publicid = req.body.publicid;

    cloudinary.v2.uploader.destroy(publicid,
        function(err, result) {
            console.log(result);
            File.remove({publicid: publicid},function (err) {
                if(err) console.log(err);
                else console.log("deleted file");
                res.status(200).json({message: "file deleted"})
            })
        });
});

router.get("/add-student-to-course", function (req, res) {
    Course.findOne({cid: req.query.cid}, function (err, course) {
        if (err) return err;
        if (course) {
            User.findOne({email: req.verifiedEmail}, function (err, user) {
                if (err) return err;
                if (user && user.role === "teacher") {
                    User.findOne({email: req.query.student}, function (err, student) {
                        if (student) {
                            course.students.push(student.id);
                            course.save();
                            res.status(200).json({message: "added" + student.email + " to course"});
                        }
                        else res.status(404).json({message: "no student " + req.query.student});

                    });
                }
                else res.status(404).json({message: "no such user" + req.verifiedEmail + " or not teacher"});
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
            File.find({_id: {$in: course.files}}, function (err, files) {
                if (err) return err;
                res.status(200).json(files);
            });
        }
        else res.status(404).json({message: "no such course"});
    })
});

router.post("/messages", function (req, res) {

    const decoded = req.verifiedEmail;

    User.findOne({email: decoded}, function (err, user) {
        if (err) return err;
        if (user) {
            Course.findOne({cid: req.body.cid}, function (err, course) {
                if (err) return err;
                if (course) {
                    const msg = new Course_Message({
                        poster_id: req.body.poster_id,
                        cid: req.body.cid,
                        type: req.body.type,
                        body: req.body.body,
                        email: decoded,
                        date: Date.now(),
                        name: user.first_name + " " + user.last_name
                    });
                    msg.save(function (err) {
                        if (err) {
                            console.log(err);
                            return res.status(500).send(err);
                        }
                        course.update({$push: {messages: msg._id}}, function (err) {
                            console.log('pushing message to messages');
                            if (err) {
                                return console.log(err);
                            } else {
                                socketIOEmitter.emitEventToCourse(msg.cid, 'newCourseMessage', msg);
                            }
                        });
                        res.status(200).json({message: "successfully added message " + msg.body + " to db"});
                        console.log("successfully added message " + msg.body + " to db");
                    });
                }
            });
        }
    });
});

router.get("/get-all-messages", function (req, res) {
    let course_id = req.query.cid;
    Course.aggregate([
            {
                $lookup: {
                    from: "course_messages",
                    localField: "messages",
                    foreignField: "_id",
                    as: "messages_list"
                }
            }, {
                $match: {
                    "cid": course_id
                }
            },
            {
                $project: {
                    "_id": 0,
                    "messages_list": 1
                }
            }
        ],
        function (err, list) {
            if (err) {
                console.log(err);
                return err;
            } else {
                return res.status(200).json(list[0].messages_list);
            }
        }
    );
});


router.post("/reply", function (req, res) {

    let notified_id = req.body.poster_id; //The user to be notified
    let replier_id = req.body.replier_id; // The user who replied
    let subject_id = req.body.mid;
    const decoded = req.verifiedEmail;
    User.findOne({email: decoded}, function (err, user) {
        if (err) return err;
        if (user) {
            let newReply = new Course_Message({
                    poster_id: req.body.poster_id,
                    parent_id: req.body.mid,
                    cid: req.body.cid,
                    type: req.body.type,
                    reply: true,
                    body: req.body.body,
                    date: Date.now(),
                    name: user.first_name + " " + user.last_name
                }
            );
            newReply.save(function (err, reply) {
                if (err) return console.log(err);
                Course_Message.update({_id: reply.parent_id}, {
                    $push: {replies: reply._id},
                    $inc: {num_of_replies: 1}
                }, function (err) {
                    if (err) return console.log(err);
                    // socketIOEmitter.emitEventToSessionRoom(reply.cid, 'newCourseMessageReply', reply);
                    //
                    // let notification = new Notification({
                    //     receiver_id: notified_id,
                    //     sender_id: replier_id, // the user who replied
                    //     action: 4,
                    //     subject: 'message',
                    //     subject_id: subject_id
                    // });
                    // notificationsSystem.saveAndEmitNotification(notification);
                });
            });
        }
    });
});

router.post("/create-session", function (req, res) {

    Course.findOne({cid: req.body.cid}, function (err, course) {
        if (err) return err;
        if (course) {
            User.findOne({email: req.verifiedEmail}, function (err, user) {
                if (err) return err;
                if (user) {
                    const sess = new Session({
                        sid: req.body.sid,
                        name: req.body.name,
                        admin: req.verifiedEmail,
                        teacher_fname: user.first_name,
                        teacher_lname: user.last_name,
                        location: req.body.location,
                        endTime: req.body.finish,
                        cid: req.body.cid
                    });
                    sess.save(function (err) {
                        if (err) {
                            if (err.name === 'MongoError' && err.code === 11000) {
                                // Duplicate username
                                console.log('Session ' + sess.name + " cannot be added id " + sess.sid + ' already exists!');
                                return res.status(500).json({message: 'Session ' + sess.name + " cannot be added id " + sess.sid + ' already exists!'});
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
                        course.update({$push: {sessions: sess._id}}, function () {
                            res.status(200).json(sess);
                            console.log("successfully added session " + sess.name + " to course:" + course.cid);
                        });
                    });
                }
                else {
                    return res.status(500).json({message: 'User ' + user.name + " cannot open a session "});
                }
            });
        }
        else {
            return res.status(500).json({message: 'no such course: ' + course.cid});
        }
    });
});


router.get("/get-all-sessions", function (req, res) {

    let course_id = req.query.cid;

    Course.findOne({cid: course_id}, function (err, course) {
        if (err) console.log(err);
        if (course) {
            Session.find({_id: {$in: course.sessions}}, function (err, sessions) {
                if (err) console.log(err);
                res.status(200).json(sessions);
            });
        }
        else {
            return res.status(500).json({message: 'no such course: ' + course.cid});
        }
    });
});

router.get("/get-message", function (req, res) {

    let msg_id = req.query.mid;

    Course_Message.findOne({_id: msg_id}, function (err, msg) {
        if (err) return console.log(err);
        if(msg) {
            res.status(200).json(msg);
        }
        else{
            res.status(404).json({message: 'no such message'});
        }
    });
});


router.get("/get-message-replies", function(req,res){

    let message_id = req.query.mid;

    console.log(message_id);

    Course_Message.find({parent_id: message_id}, function(err, messages){
        if(err) return console.log(err);
        console.log(messages);
        return res.status(200).json(messages);
    });
});

module.exports = router;