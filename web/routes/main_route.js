var express = require('express');
var router = express.Router();
var path = require("path");
const jwt = require('jsonwebtoken');
const User = require("../schemas/user");


//make sure that all request contain a valid token
router.all("*", function (req, res, next) {
    if (req.url === '/' || req.url === '/favicon.ico'
        || req.url.includes('/auth/auth-login-user-pass')
        || req.url.includes('/auth/new-user')
        || req.url.includes('/auth/reset-pass-finish')
        || req.url.includes('/auth/reset-pass-init')
        || req.url.includes('/auth/google')
        || req.url.includes('/auth/google/callback')
        || req.url.includes('/auth/get-user-from-google')) {
        return next();
    }
    var token = req.headers["x-access-token"] || req.query.token;
    if (!token) {
        res.redirect('/')
    }
    else {
        jwt.verify(token, "Wizer", function (err, decoded) {
            if (err) {
                res.sendFile(path.join(__dirname + "/../index.html"));
                return res.status(401).json({success: false, message: 'Failed to authenticate token.'});
            } else {
                req.verifiedEmail = decoded;

                User.findOne({email: decoded}, function (err, user) {

                    next();

                    if (user) {

                        let event= undefined;

                        console.log(req.url);

                        switch (req.params[0]){
                            case "/students/post-profile-image":
                                event = {
                                    type: "personal",
                                    event: "change profile image",
                                };
                                break;
                            case "/students/edit-profile":
                                event = {
                                    type: "personal",
                                    event: "edit profile",
                                };
                                break;
                            case "/sessions/connect-session":
                                event = {
                                    type: "session",
                                    event: "connect to session " + req.body.sid,
                                };
                                break;
                            case "/sessions/messages":
                                event = {
                                    type: "session",
                                    event: "posted the question " + req.body.body +"  in session " + req.body.sid,//can extract the message if wanted
                                };
                                break;
                            case "/sessions/reply":
                                event = {
                                    type: "session",
                                    event: "replied "+ req.body.body +" to a question in session " + req.body.sid,//can extract the message if wanted
                                };
                                break;
                            case "/sessions/create-session":
                                event = {
                                    type: "create",
                                    event: "created session: " + req.body.name + " at: " + req.body.location,
                                };
                                break;
                            //test this!!!
                            case "/course/create-course":
                                event = {
                                    type: "create",
                                    event: "created course: " + req.body.name + " with teacher: " + req.body.teacher,
                                };
                                break;
                            case "/sessions/rate-message":
                                let type = req.query.rating === "1" ? "liked" : "disliked";
                                event = {
                                    type: "session",
                                    event: type + " a question (" + req.query.msgid + ") in session " + req.query.sid,//can extract the message if wanted
                                };
                        }
                        if(event){
                            event.date = Date.now();
                            user.events.push(event);
                            user.save();
                            return;
                        }

                        // if (req.url.includes("/rate-message")) {
                        //     if (req.query.rating === "1") {
                        //         let event = {
                        //             type: "session",
                        //             event: "liked a question (" + req.query.msgid + ") in session " + req.query.sid,//can extract the message if wanted
                        //             date: Date.now()
                        //         };
                        //         user.events.push(event);
                        //     }
                        //     else if (req.query.rating === "0") {
                        //         let event = {
                        //             type: "session",
                        //             event: "disliked a question (" + req.query.msgid + ") in session " + req.query.sid,//can extract the message if wanted
                        //             date: Date.now()
                        //         };
                        //         user.events.push(event);
                        //     }
                        //
                        // }


                        if (req.url.includes("/free-text-search")) {
                            let event = {
                                type: "search",
                                event: "searched for: " + req.query.keyword,
                                date: Date.now()
                            };
                            user.events.push(event);
                        }

                        if (req.file && req.url.includes("/post-video")) {
                            let event = {
                                type: "session",
                                event: "added video (" + req.file.originalname + ") to session: " + req.query.sid,
                                date: Date.now()
                            };
                            user.events.push(event);
                        }

                        if (req.file && req.url.includes("/post-file")) {
                            let event = {
                                type: "course",
                                event: "added file (" + req.file.originalname + ") to course: " + req.query.cid,
                                date: Date.now()
                            };
                            user.events.push(event);
                        }

                        if (req.url.includes("/courses/add-students-to-course")) {
                            let event = {
                                type: "course",
                                event: "added a student (" + req.query.student + ") to course: " + req.query.cid,
                                date: Date.now()
                            };
                            user.events.push(event);
                        }

                        if (req.url.includes("/get-course")) {
                            let event = {
                                type: "course",
                                event: "looked at course " + req.query.cid,
                                date: Date.now()
                            };
                            user.events.push(event);
                        }

                        user.save();
                    }
                });
            }
        });
    }
});

module.exports = router;