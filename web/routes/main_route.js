var express = require('express');
var router = express.Router();
var path = require("path");
const jwt = require('jsonwebtoken');

//make sure that all request contain a valid token
router.all("*" , function (req, res, next) {

    if(req.url === '/' || req.url === '/favicon.ico' || req.url.includes('/auth/auth-login-user-pass') || req.url.includes('/auth/new-user') || req.url.includes('/sessions/post-video')){
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
                return next();
            }
        });
    }
});

module.exports = router;