let express = require('express');
let router = express.Router();
let Course = require("../schemas/course");
let User = require("../schemas/user");
let File = require("../schemas/file");
let Session = require("../schemas/session");
let Notification = require("../schemas/notification");
let Course_Message = require("../schemas/course_message");
const socketIOEmitter = require('../tools/socketIO');
const notificationsSystem = require("../tools/notificationsSystem");

router.post("/create-course", function (req, res) {

    let Gcid = 1;

    Course.findOne({}).sort({cid: -1}).then(function (ans) {
            if(ans){
                Gcid = ans.cid + 1;
            }
            User.findOne({email: req.body.teacher.toLowerCase()}, function (err, teacher) {
                if (err) {
                    console.log("Error while finding courses");
                    res.status(500).json({message: err});
                }
                else {
                    if (teacher) {
                        const course = new Course({
                            cid: Gcid,
                            name: req.body.name,
                            department: req.body.department,
                            teacher_fname: teacher.first_name,
                            teacher_lname: teacher.last_name,
                            location: req.body.location,
                            points: req.body.points,
                            students: [teacher.id],
                            teacher_email: teacher.email
                        });
                        course.save(function (err) {
                            if (err) {
                                if (err.name === 'MongoError' && err.code === 11000) {
                                    console.log('Course ' + course.name + " cannot be added id " + course.cid + ' already exists!');
                                    return res.status(500).json({message: 'Course ' + course.name + " cannot be added id " + course.cid + ' already exists!'});
                                }
                                else if (err.name === 'ValidationError') {
                                    let str = "";
                                    for (field in err.errors) {
                                        console.log("you must provide: " + field + " field");
                                        str += "you must provide: " + field + " field  ";
                                    }
                                    return res.status(500).json({message: str});
                                }
                                else {
                                    console.log(err);
                                    return res.status(500).json({message: err});
                                }
                            }
                            else {
                                socketIOEmitter.addCourseToCoursesRooms(String(course.cid));
                                res.status(200).json(course);
                                console.log("successfully added course " + course.name + " to db");
                            }
                        });
                    }
                    else return res.status(404).json({message: "no teacher: " + req.body.teacher});
                }
            });
    });
});


router.get('/get-all-courses', function (req, res) {
    Course.find({}, function (err, courses) {
        if (err) {
            console.log("Error while finding courses");
            res.status(500).json({message: err});
        } else if (courses) {
            return res.status(200).json(courses);
        } else {
            console.log("No Courses available");
            return res.status(200).json({message: "No courses available"});
        }
    });
});


router.get('/get-all-courses-by-id', function (req, res) { // You get: Array of IDs
    const id = req.query.id;
    Course.find({students: id}, function (err, courses) {
        if (err) {
            console.log("Error while finding courses");
            res.status(500).json({message: err});
        }
        else {
            res.status(200).json(courses);
        }
    });
});


router.get('/get-my-courses', function (req, res) {
    User.findOne({email: req.verifiedEmail}, function (err, user) {
        if (err) {
            console.log("Error while finding user");
            res.status(500).json({message: err});
        }
        else {
            Course.find({students: user._id}, function (err, courses) {
                if (err) {
                    console.log("Error while finding course");
                    res.status(500).json({message: err});
                }
                else {
                    res.status(200).json(courses);
                }
            });
        }
    });
});

//
// router.get('/search-by-name', function (req, res, next) {
//
//     const ip = req.headers["x-forwarded-for"] || req.connection.remoteAddress;
//     const course_name = req.query.course_name;
//
//     console.log("Got a search for " + course_name);
//     console.log("Got a " + req.method + " request from " + ip);
//
//     Course.find({name: course_name}, function (err, courses) {
//         if (err) {
//             console.log("Error while finding courses");
//             return next(err);
//         } else if (courses) {
//             console.log(courses);
//             console.log("Found: " + courses);
//             return res.json(courses);
//         } else {
//             console.log("No Courses available");
//             return res.json("No courses available");
//         }
//     });
// });


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

router.post('/post-file', type, function (req, res) {
    if (!req.file) {
        res.status(400).json({message: 'no file'});
    }
    else {
        const path = req.file.path;
        let type = "raw";
        if (!req.file.originalname.toLowerCase().match(/\.(pdf|doc|docx|ppt|pptx|jpeg|jpg|png)$/)) {
            fs.unlinkSync(path);
            res.status(400).json({message: 'wrong file'});
        }
        else {
            if (req.file.originalname.toLowerCase().match(/\.(jpeg|jpg|png)$/)) {
                type = "image";
            }
            let extension = req.file.originalname.split('.').pop();
            Course.findOne({cid: req.query.cid}, function (err, course) {
                if (err) {
                    console.log("Error while finding course");
                    fs.unlinkSync(path);
                    res.status(500).json({message: err});
                }
                else {
                    if (course) {
                        User.findOne({email: req.verifiedEmail}, function (err, user) {
                            if (err) {
                                console.log(err);
                                fs.unlinkSync(path);
                                res.status(500).json({messsage: err});
                            }
                            else {
                                if (user) {
                                    res.status(200).json({message: 'received file'});
                                    console.log("starting to upload " + req.file.originalname);
                                    cloudinary.v2.uploader.upload(path,
                                        {
                                            resource_type: type,
                                            public_id: "courses/" + course.cid + "/" + req.file.filename + "." + extension
                                        },
                                        function (err, result) {
                                            fs.unlinkSync(path);
                                            if (err) console.log(err);
                                            else {
                                                const ans = new File({
                                                    publicid: result.public_id,
                                                    originalName: req.file.originalname,
                                                    uploaderid: user.id,
                                                    url: result.url,
                                                    type: extension,
                                                    size: result.bytes,
                                                    hidden: false
                                                });
                                                ans.save(function (err, updated_file) {
                                                    if (err) console.log(err);
                                                    else {
                                                        course.update({$push: {files: updated_file.id}}, function (err) {
                                                            if (err) console.log(err);
                                                            else {
                                                                console.log("finished uploading " + req.file.originalname);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                }
                                else {
                                    fs.unlinkSync(path);
                                    res.status(404).json({message: 'no such user ' + req.verifiedEmail});
                                }
                            }
                        });
                    }
                    else {
                        fs.unlinkSync(path);
                        res.status(404).json({message: 'no such course ' + req.query.cid});
                    }
                }
            });
        }
    }
});

router.delete('/remove-file', function (req, res) {

    let publicid = req.query.publicid;
    let id = req.query.id;
    cloudinary.v2.uploader.destroy(publicid,
        function (err, result) {
            if (err) {
                console.log(err);
            }
            console.log(result);
            File.remove({publicid: publicid}, function (err) {
                if (err) {
                    console.log(err);
                    res.status(500).json({message: err});
                }
                else {
                    Course.update({cid: req.query.cid}, {$pull: {files: id}}, function (err) {
                        if (err) {
                            console.log(err);
                            res.status(500).json({message: err});
                        }
                        else {
                            res.status(200).json({message: "file deleted"})
                        }
                    });
                }
            })
        });
});

router.get("/add-student-to-course", function (req, res) {
    Course.findOne({cid: req.query.cid}, function (err, course) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (course) {
                User.findOne({email: req.verifiedEmail}, function (err, user) {
                    if (err) {
                        console.log(err);
                        res.status(500).json({message: err});
                    }
                    else {
                        if (user && user.role === "teacher") {
                            User.findOne({email: req.query.student}, function (err, student) {
                                if (err) {
                                    console.log(err);
                                    res.status(500).json({message: err});
                                }
                                else {
                                    if (student) {
                                        if(course.students.includes(student.id)){
                                            res.status(200).json({message: student.email + " already in course"});
                                        }
                                        else {
                                            course.update({$push: {students: student.id}}, function (err) {
                                                if (err) {
                                                    console.log(err);
                                                    res.status(500).json({message: err});
                                                }
                                                else {
                                                    res.status(200).json({message: "added " + student.email + " to course"});
                                                }
                                            });
                                        }
                                    }
                                    else res.status(404).json({message: "no student " + req.query.student});
                                }
                            });
                        }
                        else res.status(404).json({message: "no such user " + req.verifiedEmail + " or not teacher"});
                    }
                });
            }
            else res.status(404).json({message: "no such course " + req.query.cid});
        }
    })
});

router.get("/get-course", function (req, res) {
    Course.findOne({cid: req.query.cid}, function (err, course) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (course) res.status(200).json(course);
            else res.status(404).json({message: "no such course " + req.query.cid});
        }
    })
});

router.get("/get-course-files", function (req, res) {
    Course.findOne({cid: req.query.cid}, function (err, course) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (course) {
                File.find({_id: {$in: course.files}}, function (err, files) {
                    if (err) {
                        console.log(err);
                        res.status(500).json({message: err});
                    }
                    else {
                        res.status(200).json(files);
                    }
                });
            }
            else res.status(404).json({message: "no such course " + req.query.cid});
        }
    })
});

router.post("/messages", function (req, res) {

    const decoded = req.verifiedEmail;

    User.findOne({email: decoded}, function (err, user) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (user) {
                Course.findOne({cid: req.body.cid}, function (err, course) {
                    if (err) {
                        console.log(err);
                        res.status(500).json({message: err});
                    }
                    else {
                        if (course) {
                            const msg = new Course_Message({
                                poster_id: req.body.poster_id,
                                cid: req.body.cid,
                                type: req.body.type,
                                body: req.body.body,
                                email: decoded,
                                date: Date.now(),
                                name: user.first_name + " " + user.last_name,
                                image: user.profile_img,
                            });
                            msg.save(function (err) {
                                if (err) {
                                    console.log(err);
                                    return res.status(500).json({message: err});
                                }
                                course.update({$push: {messages: msg._id}}, function (err) {
                                    console.log('pushing message to messages');
                                    if (err) {
                                        console.log(err);
                                        res.status(500).json({message: err});
                                    } else {
                                        res.status(200).json(msg);
                                        console.log("successfully added message " + msg.body + " to db");
                                        socketIOEmitter.emitEventToCourse(msg.cid, 'newCourseMessage', msg);
                                    }
                                });
                            });
                        }
                        else {
                            return res.status(404).json({message: "no such course " + req.body.cid});
                        }
                    }
                });
            }
            else {
                return res.status(404).json({message: "no such user " + decoded});
            }
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
                    "cid": course_id,
                    "messages_list.reply": false
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
                return res.status(500).json({message: err});
            } else {
                if (list[0]) {
                    return res.status(200).json(list[0].messages_list);
                } else {
                    return res.status(200).json([]); // what is this?
                }
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
        if (err) {
            console.log(err);
            return res.status(500).json({message: err});
        }
        else {
            if (user) {
                let newReply = new Course_Message({
                        poster_id: req.body.poster_id,
                        parent_id: req.body.mid,
                        cid: req.body.cid,
                        type: req.body.type,
                        reply: true,
                        body: req.body.body,
                        date: Date.now(),
                        name: user.first_name + " " + user.last_name,
                        image: user.profile_img,
                    }
                );
                newReply.save(function (err, reply) {
                    if (err) {
                        console.log(err);
                        return res.status(500).json({message: err});
                    }
                    else {
                        Course_Message.update({_id: reply.parent_id}, {
                            $push: {replies: reply._id},
                            $inc: {num_of_replies: 1}
                        }, function (err) {
                            if (err) {
                                console.log(err);
                                return res.status(500).json({message: err});
                            }
                            else {
                                res.status(200).json({message: "added reply"});
                                socketIOEmitter.emitEventToSessionRoom(reply.cid, 'newCourseMessageReply', reply);

                                let notification = new Notification({
                                    receiver_id: notified_id,
                                    sender_id: replier_id, // the user who replied
                                    action: 4,
                                    subject: 'course message',
                                    subject_id: subject_id
                                });
                                console.log(notification);
                                notificationsSystem.saveAndEmitNotification(notification);
                            }
                        });
                    }
                });
            }
            else {
                res.status(404).json({message: "no such user " + decoded});
            }
        }
    });
});

router.post("/create-session", function (req, res) {

    Course.findOne({cid: req.body.cid}, function (err, course) {
        if (err) {
            console.log(err);
            return res.status(500).json({message: err});
        }
        else {
            if (course) {
                User.findOne({email: req.verifiedEmail}, function (err, user) {
                    if (err) {
                        console.log(err);
                        return res.status(500).json({message: err});
                    }
                    else {
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
                                        let str = "";
                                        for (field in err.errors) {
                                            console.log("you must provide: " + field + " field");
                                            str += "you must provide: " + field + " field  ";
                                        }
                                        return res.status(500).json({message: str});
                                    }
                                    // Some other error
                                    console.log(err);
                                    return res.status(500).json({message: err});
                                }
                                else {
                                    course.update({$push: {sessions: sess._id}}, function (err) {
                                        if (err) {
                                            console.log(err);
                                            return res.status(500).json({message: err});
                                        }
                                        else {
                                            res.status(200).json(sess);
                                            socketIOEmitter.addSessionToSessionRooms(sess.sid);
                                            console.log("successfully added session " + sess.name + " to course:" + course.cid);
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            return res.status(404).json({message: 'User ' + user.name + " cannot open a session "});
                        }
                    }
                });
            }
            else {
                return res.status(404).json({message: 'no such course: ' + course.cid});
            }
        }
    });
});


router.get("/get-all-sessions", function (req, res) {
    let course_id = req.query.cid;
    Course.findOne({cid: course_id}, function (err, course) {
        if (err) {
            console.log(err);
            return res.status(500).json({message: err});
        }
        else {
            if (course) {
                Session.find({_id: {$in: course.sessions}}, function (err, sessions) {
                    if (err) {
                        console.log(err);
                        return res.status(500).json({message: err});
                    }
                    else {
                        res.status(200).json(sessions);
                    }
                });
            }
            else {
                return res.status(404).json({message: 'no such course: ' + course.cid});
            }
        }
    });
});

router.get("/get-message", function (req, res) {
    let msg_id = req.query.mid;
    Course_Message.findOne({_id: msg_id}, function (err, msg) {
        if (err) {
            console.log(err);
            return res.status(500).json({message: err});
        }
        else {
            if (msg) {
                res.status(200).json(msg);
            }
            else {
                res.status(404).json({message: 'no such message ' + msg_id});
            }
        }
    });
});


router.get("/get-message-replies", function (req, res) {
    let message_id = req.query.mid;
    Course_Message.find({parent_id: message_id}, function (err, messages) {
        if (err) {
            console.log(err);
            return res.status(500).json({message: err});
        }
        else {
            return res.status(200).json(messages);
        }
    });
});

module.exports = router;