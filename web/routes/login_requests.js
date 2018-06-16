const router = require('express').Router();
const expressValidator = require('express-validator');
const User = require("../schemas/user");
const Student = require("../schemas/student");
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt-nodejs');
const auth = require('basic-auth');
let validator = require("email-validator");
let emailService = require('./../tools/email');
let emailMessages = require('./../tools/email-messages');
let randomstring = require("randomstring");
let config = require('../config/config');

router.use(expressValidator());

router.post("/auth-login-user-pass", function (req, res) {

    const credentials = auth(req);
    console.log(credentials);

    if (credentials.name === "undefined" || credentials.pass === "undefined") {
        return res.status(401).send({message: 'Invalid Credentials!'})
    }

    User.findOne({email: credentials.name.toLowerCase()}, function (err, user) {
        if (err) return err;
        if (!user) {
            return res.status(400).send({message: 'no such user!'})
        }
        console.log(user);
        console.log("Found the user " + credentials.name);
        if (bcrypt.compareSync(credentials.pass, user.password)) {
            console.log("Found the user " + credentials.name);
            const token = jwt.sign(credentials.name.toLowerCase(), config.email.secret);

            User.update({email: credentials.name.toLowerCase()}, {accessToken: token}, function (err) {
                if (err) {
                    console.log(err);
                } else {
                    console.log("access token was successfully updated");
                    res.status(200).send({
                        message: 'welcome to Wizer!',
                        email: user.email,
                        first_name: user.first_name,
                        last_name: user.last_name,
                        accessToken: token,
                        role: user.role,
                        photos: user.photos,
                        _id: user._id,
                        display_name: user.display_name
                    })
                }
            });
        } else {
            console.log("Your pass: " + credentials.pass + ",\nThe expected encrypted pass: " + user.password);
            res.status(401).send({message: 'Invalid Credentials!'})
        }
    });
});

router.get("/get-user-by-token", function (req, res) {

    const projection = {
        email: 1,
        first_name: 1,
        last_name: 1,
        accessToken: 1,
        role: 1,
        photos: 1,
        _id: 1,
        display_name: 1
    };

    User.findOne({email: req.verifiedEmail}, projection, function (err, user) {
        if (err) return next(err);
        if (user) {
            user.message = "welcome to Wizer";
            res.status(200).send(user)
        }
        else {
            console.log("no such student");
            res.status(404).json({message: "no such student"});
        }
    });
});


router.get("/get-user-from-google", function (req, res) {
    User.findOne({accessToken: req.query.token}, function (err, user) {
        if (err) return next(err);
        if (user) {
            req.verifiedEmail = user.email;

        }
        else {
            console.log("no such student");
            res.status(404).json({message: "no such student"});
        }
    });
});

router.post("/new-user", function (req, res) {

    const fname = req.body.first_name,
        lname = req.body.last_name,
        email = req.body.email.toLowerCase(),
        password = req.body.password,
        role = req.body.role;


    req.checkBody("first_name", "First Name is required").notEmpty();
    req.checkBody("last_name", "Last Name is required").notEmpty();
    req.checkBody("email", "Email is required").notEmpty();
    if (!validator.validate(email)) {
        console.log("Email is not valid");
        res.status(400).json({message: "Email is not valid"});
    }
    req.checkBody("password", "Password is required").notEmpty();
    req.checkBody("role", "role is required").notEmpty();

    const errors = req.validationErrors();

    if (errors) {
        console.log(errors);
        res.status(400).send(errors[0].msg);
    }
    else {
        const newUser = new User({
            first_name: fname,
            last_name: lname,
            email: email,
            register_date: Date.now(),
            last_update: Date.now(),
            password: password,
            role: role,
            events:
                {
                    event: "sing up to wizeUp",
                }
        });

        bcrypt.genSalt(10, function (err, salt) {
            bcrypt.hash(newUser.password, salt, null, function (err, hash) {
                newUser.password = hash;
                newUser.save(function (err, user) {
                    if (err) {
                        console.log(err);
                        if (err.name === 'MongoError' && err.code === 11000) {
                            // Duplicate username
                            console.log(email + ' already exists!');
                            return res.status(500).send(email + ' already exists!');
                        }
                        if (err.name === 'ValidationError') {
                            //ValidationError
                            let str = "";
                            for (field in err.errors) {
                                console.log("you must provide: " + field + " field");
                                str += "you must provide: " + field + " field  ";
                            }
                            return res.status(500).send(str);
                        }
                        // Some other error
                        console.log(err);
                        return res.status(500).send(err);
                    }
                    console.log(user);
                    emailService.sendMail(user.email, 'Registration to wizeUp', emailMessages.registration(user));
                    const newStudent = new Student({
                        user_id: user._id
                    });
                    newStudent.save();
                    res.status(200).send(user);
                });
            });
        });
    }
});

router.put("/change-password", function (req, res) {
    User.findOne({email: req.verifiedEmail}, function (err, user) {
        if (err) return next(err);
        if (user) {
            const oldPass = req.body.password;
            const newPass = req.body.temp_password;
            if (bcrypt.compareSync(oldPass, user.password)) {
                bcrypt.genSalt(10, function (err, salt) {
                    bcrypt.hash(newPass, salt, null, function (err, hash) {
                        if (err) next(err);
                        user.update({password: hash}, function (err) {
                            if (err) {
                                console.log(err);
                                return res.json({success: false, message: "DB connection error"});
                            } else {
                                console.log("password was successfully updated");
                                return res.status(200).json({
                                    success: true,
                                    message: "password was successfully updated"
                                });
                            }
                        });
                    });
                });
            } else {
                console.log("An error occurred!");
                res.status(401).send({message: "Wrong password"});
            }
        }
    });
});

router.post("/reset-pass-init", function (req, res) {

    User.findOne({email: req.body.email.toLowerCase()}, function (err, user) {
        if (err) return next(err);
        if(user){
        const salt = bcrypt.genSaltSync(10);
        const hash = bcrypt.hashSync("random", salt);

        let random = randomstring.generate(7);

        user.temp_password = random;
        user.temp_password_time = new Date();
        user.save(function () {
            emailService.init();
            emailService.sendMail(user.email, 'Reset Password', emailMessages.reset_password(user, random));
            res.status(200).json({message: 'see mail for more information'});
        });
        }
        else{
            res.status(404).json({message: 'no such user'});
            }
    });
});

router.post("/reset-pass-finish", function (req, res) {
    let token = req.body.accessToken;
    User.findOne({email: req.body.email.toLowerCase()}, function (err, user) {
        const diff = new Date() - new Date(user.temp_password_time);
        const seconds = Math.floor(diff / 1000);
        console.log("Seconds :" + seconds);
        if (seconds < 6000) {
            if (token === user.temp_password){

                const salt = bcrypt.genSaltSync(10);
                const hash = bcrypt.hashSync(req.body.password, salt);

                user.password = hash;
                user.temp_password = undefined;
                user.temp_password_time = undefined;

                user.save().then(function () {
                    res.status(200).json({message: 'Password reset successfully'});
                });
            } else {
                res.status(401).json({message: 'Invalid Token !'});
            }
        } else {
            res.status(404).json({message: 'time out try again'});
        }
    });
});

let passport = require('passport');

router.get('/google',
    passport.authenticate('google', {scope: ['profile email']}));

router.get('/google/callback',
    passport.authenticate('google', {failureRedirect: '/'}), function (req, res) {
        // Successful authentication, redirect home.
        res.status(200).redirect('http://localhost:3000/');
        // res.send('/', {userId: req.session.passport.user});
    }
);

module.exports = router;