var express = require('express');
var router = express.Router();
var path = require("path");
var passport = require('passport');


var cookieParser =require('cookie-parser');
var session = require('express-session');
const MongoStore = require('connect-mongo')(session);

var app = express();


app.use(cookieParser());
app.use(session({
    secret: "cookie_secret",
    name: "cookie_name",
    store: new MongoStore({ mongooseConnection: mongoose.connection }), // connect-mongo session store
    proxy: true,
    resave: true,
    saveUninitialized: true
}));

app.post("/login", passport.authenticate('facebook',
    { failureRedirect: '/login',
        failureFlash: true }), function(req, res) {
    if (req.body.remember) {
        req.session.cookie.maxAge = 30 * 24 * 60 * 60 * 1000; // Cookie expires after 30 days
    } else {
        req.session.cookie.expires = false; // Cookie expires at end of session
    }
    res.redirect('/');
});

router.get("/login", function (req, res) {
    res.sendFile(path.join(__dirname + "/../login.html"));
});

router.get('/auth/facebook',passport.authenticate("facebook", {scope: ['email']}));

router.get('/auth/facebook/callback',passport.authenticate("facebook",
    { failureRedirect: '/login', failureFlash: true , successRedirect: "/#/", successFlash: true }));
    // , function(req, res) {
    // res.redirect('/#/');
// });

module.exports = router;
