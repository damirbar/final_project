var express = require('express');
var router = express.Router();
var path = require("path");
const jwt = require('jsonwebtoken');
const User = require("../schemas/user");


//make sure that all request contain a valid token
router.all("*", function (req, res, next) {

    if (req.url === '/' || req.url === '/favicon.ico' || req.url.includes('/auth/auth-login-user-pass') || req.url.includes('/auth/new-user')) {
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

                    if (req.url === "/get-all-messages?sid=1234") return;//remove this only for testing

                    console.log(req.url);
                    if (req.url === "/post-profile-image") {
                        let event = {
                            type: "personal",
                            event: "change profile image",
                            date: Date.now()
                        };
                        user.events.push(event);
                    }

                    if (req.url === "/edit-profile") {
                        let event = {
                            type: "personal",
                            event: "edit profile",
                            date: Date.now()
                        };
                        user.events.push(event);
                    }

                    if (req.url === "/connect-session") {
                        let event = {
                            type: "session",
                            event: "connect to session " + req.body.sid,
                            date: Date.now()
                        };
                        user.events.push(event);
                    }

                    if (req.url === "/messages") {
                        let event = {
                            type: "session",
                            event: "post a question in session " + req.body.sid,//can extract the message if wanted
                            date: Date.now()
                        };
                        user.events.push(event);
                    }

                    if (req.url.includes("/rate-message")) {
                        if (req.query.rating === "1") {
                            let event = {
                                type: "session",
                                event: "liked a question in session " + req.query.sid,//can extract the message if wanted
                                date: Date.now()
                            };
                            user.events.push(event);
                        }
                        else if(req.query.rating === "0") {
                            let event = {
                                type: "session",
                                event: "disliked a question in session " + req.query.sid,//can extract the message if wanted
                                date: Date.now()
                            };
                            user.events.push(event);
                        }

                    }

                    if (req.url === "/reply") {
                        let event = {
                            type: "session",
                            event: "replied to a question in session " + req.body.sid,//can extract the message if wanted
                            date: Date.now()
                        };
                        user.events.push(event);
                    }
                    user.save();
                });
                return next();
            }
        });
    }
});

module.exports = router;