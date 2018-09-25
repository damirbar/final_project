let express = require('express');
let router = express.Router();
let path = require("path");
let jwt = require('jsonwebtoken');
let User = require("../schemas/user");
let Session = require("../schemas/session");
let Session_Message = require("../schemas/session_message");
let Course = require("../schemas/course");
let config = require('../config/config');
let multer = require('multer');
let upload = multer({dest: 'upload/'});
let type = upload.single('recfile');

//make sure that all request contain a valid token
router.all("*", type, function (req, res, next) {
    if (req.url === '/' || req.url === '/favicon.ico'
        || req.url.includes('/auth/auth-login-user-pass')
        || req.url.includes('/auth/new-user')
        || req.url.includes('/auth/reset-pass-finish')
        || req.url.includes('/auth/reset-pass-init')
        || req.url.includes('/auth/google')
        || req.url.includes('/auth/google/callback')
        || req.url.includes('/auth/facebook')
        || req.url.includes('/auth/facebook/callback')
        //remove! this is only for testing
        || req.url.includes('/stream')
        || req.url.includes('/get-all-sessions')
        || req.url.includes('/auth/signup')) {

        return next();
    }
    if(req.url === '/students/post-profile-image'){
        console.log("\n\n\t\tHERE!!!!!\n\n");
    }
    let token = req.headers["x-access-token"] || req.query.token;
    if (!token) {
        res.redirect('/')
    }
    else {
        jwt.verify(token, config.email.secret, function (err, decoded) {
            if (err) {
                res.sendFile(path.join(__dirname + "/../index.html"));
                return res.status(401).json({success: false, message: 'Failed to authenticate token.'});
            } else {
                req.verifiedEmail = decoded.toLowerCase();

                User.findOne({email: decoded}, function (err, user) {

                    next();

                    if (user) {

                        let event= undefined;

                        switch (req.params[0]){
                            case "/students/post-profile-image":
                                if(req.file) {
                                    event = {
                                        type: "personal",
                                        event: "change profile image to "+ req.file.originalname,
                                    };
                                }
                                break;
                            case "/students/edit-profile":
                                event = {
                                    type: "personal",
                                    event: "edit profile",
                                };
                                break;
                            case "/sessions/connect-session":
                                Session.findOne({sid:req.body.sid},function (err, sess) {
                                    if(err) console.log(err);
                                    if(sess){
                                        event = {
                                            type: "session",
                                            event: "connected to session " + sess.name,
                                        };
                                        user.events.push(event);
                                        user.save();
                                        event = undefined
                                    }
                                });
                                break;
                            case "/sessions/messages":
                                Session.findOne({sid:req.body.sid},function (err, sess) {
                                    if (err) console.log(err);
                                    if (sess) {
                                        event = {
                                            type: "session",
                                            event: "posted the question: '" + req.body.body + "'  in session: " + sess.name,//can extract the message if wanted
                                            date : Date.now()
                                        };
                                        user.events.push(event);
                                        user.save();
                                        event = undefined
                                    }
                                });
                                break;
                            case "/sessions/reply":
                                Session.findOne({sid:req.body.sid},function (err, sess) {
                                    if (err) console.log(err);
                                    if (sess) {
                                    Session_Message.findOne({_id: req.body.mid},function (err, msg) {
                                        if (err) console.log(err);
                                        if(msg){
                                            User.findOne({email: msg.email},function (err, student) {
                                                if (err) console.log(err);
                                                if (student) {
                                                    event = {
                                                        type: "session",
                                                        event: "replied '"+ req.body.body +"' to " + student.first_name +"'s question: '" + msg.body + "' in session: " + sess.name,
                                                        date : Date.now()
                                                    };
                                                    user.events.push(event);
                                                    user.save();
                                                    event = undefined
                                                }
                                            });
                                        }
                                    });
                                    }
                                });
                                break;
                            case "/sessions/create-session":
                                event = {
                                    type: "create",
                                    event: "created session: " + req.body.name + " at: " + req.body.location,
                                };
                                break;
                            case "/courses/create-course":
                                event = {
                                    type: "create",
                                    event: "created course: '" + req.body.name + "' with teacher: " + req.body.teacher + " At: "+ req.body.location,
                                };
                                break;
                            // remove un-like and un-dislike
                            case "/sessions/rate-message":
                                Session_Message.findOne({_id: req.query.msgid},function (err, msg) {
                                    if (err) console.log(err);
                                    if (msg) {
                                        Session.findOne({sid: req.query.sid}, function (err, sess) {
                                            if (err) console.log(err);
                                            if (sess) {
                                                let type = req.query.rating === "1" ? "liked" : "disliked";
                                                event = {
                                                    type: "session",
                                                    event: type + " the question '" + msg.body + "' in session: " + sess.name,
                                                    date: Date.now()
                                                };
                                                user.events.push(event);
                                                user.save();
                                                event = undefined
                                            }
                                        });
                                    }
                                });
                                break;
                            case "/search/free-text-search":
                                event = {
                                    type: "search",
                                    event: "searched for: " + req.query.keyword
                                };
                                break;
                            case "/sessions/post-video":
                                if(req.file) {
                                    Session.findOne({sid: req.query.sid}, function (err, sess) {
                                        if (err) console.log(err);
                                        if (sess) {
                                            event = {
                                                type: "session",
                                                event: "added the video " + req.file.filename + "to session: " + sess.name,
                                                date: Date.now()
                                            };
                                            user.events.push(event);
                                            user.save();
                                            event = undefined
                                        }
                                    });
                                }
                                break;
                            //test this!!!
                            case "/courses/add-students-to-course":
                                    event = {
                                        type: "course",
                                        event: "added a student (" + req.query.student + ") to course: " + req.query.cid,
                                    };
                                break;
                            case "/courses/get-course":
                                Course.findOne({cid: req.query.cid},function (err, course) {
                                    if(err) console.log(err);
                                    if(course){
                                        event = {
                                            type: "course",
                                            event: "viewed course: " + course.name,
                                            date : Date.now()
                                        };
                                        user.events.push(event);
                                        user.save();
                                        event = undefined
                                    }
                                });
                                break;
                        }

                        if(event){
                            event.date = Date.now();
                            user.events.push(event);
                            user.save();
                        }
                    }
                });
            }
        });
    }
});

module.exports = router;