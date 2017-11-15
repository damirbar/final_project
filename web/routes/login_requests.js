var express = require('express');
var router = express.Router();
var path = require("path");
var passport = require('passport');

router.get("/login", function (req, res) {
    res.sendFile(path.join(__dirname + "/../login.html"));
});

router.get('/auth/facebook',passport.authenticate("facebook"));

router.get('/auth/facebook/callback',passport.authenticate("facebook",
        {successRedirect : "/",
        failureRedirect  : "/login"}));

module.exports = router;
