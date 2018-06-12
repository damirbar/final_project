let express = require('express');
let router = express.Router();
let path = require("path");
let jwt = require('jsonwebtoken');
let User = require("../schemas/user");
let config = require('../config/config');


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
                req.verifiedEmail = decoded;

                User.findOne({email: decoded}, function (err, user) {

                    next();

                    if (user) {

                        let event= undefined;

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

                            // change so self rating wont count
                            // and unlike and undislike as well
                            case "/sessions/rate-message":
                                let type = req.query.rating === "1" ? "liked" : "disliked";
                                event = {
                                    type: "session",
                                    event: type + " a question (" + req.query.msgid + ") in session " + req.query.sid,//can extract the message if wanted
                                };
                                break;
                            //test this
                            case "/free-text-search":
                                event = {
                                    type: "search",
                                    event: "searched for: " + req.query.keyword
                                };
                                break;
                            //test this
                            case "/sessions/post-video":
                                if(req.file) {
                                    event = {
                                        type: "session",
                                        event: "added video (" + req.file.originalname + ") to session: " + req.query.sid,
                                    };
                                }
                                break;
                            //test this
                            case "/courses/add-students-to-course":
                                    event = {
                                        type: "course",
                                        event: "added a student (" + req.query.student + ") to course: " + req.query.cid,
                                    };
                                break;
                            //test this
                            case "/courses/get-course":
                                event = {
                                    type: "course",
                                    event: "viewed course " + req.query.cid,
                                };
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