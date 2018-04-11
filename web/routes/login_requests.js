var router = require('express').Router();
var path = require("path");
var passport = require('passport');
var expressValidator = require('express-validator');
var User = require("../schemas/user");
var Student = require("../schemas/student");
var Teacher = require("../schemas/teacher");
var jwt = require('jsonwebtoken');

var bcrypt = require('bcrypt-nodejs');
const auth = require('basic-auth');

router.post("/auth-login-user-pass", function (req, res) {

    var role = req.role;
    var credentials = auth(req);
    console.log(credentials);

    if(role === 'teacher'){
        User.aggregate([{
                $lookup: {
                    from: "teachers",
                    localField: "_id",
                    foreignField: "user_id",
                    as: "teacher"
                }
            },{
                $match: {
                    "email": credentials.name
                }
            },{
                $project: {
                    "temp_password": 0,
                    "temp_password_time": 0,
                    "last_modified": 0,
                    "accessToken": 0
                }
            }
            ],
            function (err, list) {
                if (err) {
                    console.log("Error while finding models");
                    console.log("The error: " + err);
                    return err;
                } else {
                    console.log(list);
                    console.log("Found the user " + credentials.name);

                    const token = jwt.sign(credentials.name, "Wizer");

                    // list[0].accessToken = token;
                    // list[0].save();

                    Object.assign(list[0], list[0].student[0]);
                    delete list[0].teacher;

                    return res.status(200).json({message: "Welcome to WizeUp!", token: token, teacher: list[0]});
                }
            }
        );
    }
    else {
        User.aggregate([{
                $lookup: {
                    from: "students",
                    localField: "_id",
                    foreignField: "user_id",
                    as: "student"
                }
            },{
                $match: {
                    "email": credentials.name
                }
            },{
                    $project: {
                        "temp_password": 0,
                        "temp_password_time": 0,
                        "last_modified": 0,
                        "accessToken": 0
                    }
                }
            ],
            function (err, list) {
                if (err) {
                    console.log("Error while finding student");
                    console.log("The error: " + err);
                    return err;
                } else {
                    console.log(list);
                    console.log("Found the user " + credentials.name);

                    if (bcrypt.compareSync(credentials.pass, list[0].password)) {
                        console.log("Found the user " + credentials.name);

                        const token = jwt.sign(credentials.name, "Wizer");
                        //  const token = jwt.sign({name: credentials.name}, "Wizer", {expiresIn: '1d'});

                        //TODO find one student and set his token and save

                        User.update({email: credentials.name}, {accessToken: token}, function(err){
                            if(err){
                                console.log(err);
                            }else{
                                console.log("access token was successfully updated");
                            }
                        });


                        // list[0].accessToken = token;
                        // list[0].save();


                        Object.assign(list[0], list[0].student[0]);
                        delete list[0].student;
                        return res.status(200).json({message: "Welcome to WizeUp!", token: token, student: list[0]});

                    } else {
                        console.log("An error occurred!");
                        console.log("Your pass: " + credentials.pass
                            + ",\nThe expected encrypted pass: " + list[0].password);
                        res.status(401).send({message: 'Invalid Credentials!'})
                    }
                }
            }
        );

        // Student.findOne({mail: credentials.name}, function (err, student) {
        //     if (err) return next(err);
        //
        //     if (bcrypt.compareSync(credentials.pass, student.password)) {
        //         console.log("Found the user " + credentials.name);
        //
        //         const token = jwt.sign(credentials.name, "Wizer");
        //
        //         student.accessToken = token;
        //         student.save();
        //
        //         res.status(200).send({message: "Welcome to WizeUp!", token: token, student: student});
        //     } else {
        //         console.log("An error occurred!");
        //         console.log("Your pass: " + credentials.pass
        //             + ",\nThe expected encrypted pass: " + student.password);
        //         res.status(401).send({message: 'Invalid Credentials!'})
        //     }
        // });
    }
});

router.get("/get-user-by-token", function (req, res) {
    var token = req.body.token || req.query.token || req.headers['x-access-token'];
    // decode token
    if (token) {
        // verifies secret and checks exp
        jwt.verify(token, "Wizer", function (err, decoded) {
            if (err) {
                return res.json({success: false, message: 'Failed to authenticate token.'});
            } else {
                // if everything is good, save to request for use in other routes
                Student.findOne({email: decoded}, function (err, student) {
                    if (err) return next(err);
                    res.status(200).send(student);
                });
                //return res.status(200).send()
                //req.decoded = decoded;
                // next();
            }
        });
    } else {
        // if there is no token
        // return an error
        return res.status(403).send({
            success: false,
            message: 'No token provided.'
        });
    }
});

router.use(expressValidator());

router.post("/new-student", function (req, res) {
    var fname = req.body.fname;
    var lname = req.body.lname;
    var email = req.body.email;
    var password = req.body.password;
    req.checkBody("fname", "First Name is required").notEmpty();
    req.checkBody("lname", "Last Name is required").notEmpty();
    req.checkBody("email", "Email is required").notEmpty();
    req.checkBody("email", "Email is not valid").isEmail();
    req.checkBody("password", "Password is required").notEmpty();
    req.checkBody("password_cnfrm", "Bouth passwords are required").notEmpty();
    req.checkBody("password_cnfrm", "Passwords do not match").equals(req.body.password);


    var errors = req.validationErrors();

    if (errors) {
        res.status(400).send("erans error");
    }
    else {
        //TODO var user = ...

        var newUser = new User({
            first_name: fname,
            last_name: lname,
            email: email,
            register_date: Date.now(),
            last_update: Date.now(),
            password: password
        });

        bcrypt.genSalt(10, function(err, salt) {
            bcrypt.hash(newUser.password, salt, null, function(err, hash) {
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
                            var str = "";
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
                    var newStudent = new Student({
                        user_id: user._id
                    });
                    newStudent.save();
                    res.status(200).send(user);
                });
            });
        });
    }
});

router.put("/switch_password", function (req, res) {
    var token = req.body.token || req.query.token || req.headers['x-access-token'];
    if (token) {
        // verifies secret and checks exp
        jwt.verify(token, "Wizer", function (err, decoded) {
            if (err) {
                return res.json({success: false, message: 'Failed to authenticate token.'});
            } else {
                Student.findOne({accessToken: token}, function (err, student) {
                    if (err) return next(err);

                    var oldPass = req.body.oldPassword;
                    var newPass = req.body.newPassword;
                    if (bcrypt.compareSync(oldPass, student.password)) {
                        student.password = newPass;
                        Student.createStudent(newStudent, function (err, user) {
                            if (err) return next(err);
                            res.status(200).send(student);
                        });
                    }
                    else {
                        console.log("An error occurred!");
                        console.log("Your pass: " + credentials.pass
                            + ",\nThe expected encrypted pass: " + student.password);
                        res.status(401).send({message: 'Invalid Credentials!'})
                    }
                });
            }
        });
    } else {
        // if there is no token
        // return an error
        return res.status(403).send({
            success: false,
            message: 'No token provided.'
        })
    }
});

router.post("/reset-pass-init", function (req, res) {

    Student.findOne({email: req.body.email}, function (err, student) {
        if (err) return next(err);

        const salt = bcrypt.genSaltSync(10);
        const hash = bcrypt.hashSync(random, salt);

        student.temp_password = hash;
        student.temp_password_time = new Date();
    }).save().then(function (student) {
        process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        const transporter = nodemailer.createTransport(`smtps://${config.email}:${config.password}@smtp.gmail.com`);
        const emailOptions = {
            from: `"${config.name}" <${config.email}>`,
            to: email,
            subject: 'Reset Password Request ',
            html: `Hello ${student.display_name},
                <br><br>
    			&nbsp;&nbsp;&nbsp;&nbsp; Your reset password token is <b>${random}</b>. 
    			If you are viewing this email from a Android Device click this <a href = "http://localhost:3000/${random}">link</a>. 
    			The token is valid for only 2 minutes.<br><br>
    			Thanks,<br>
    			wizer.`
        };
        return transporter.sendEmail(emailOptions);
    }).then(function (info) {
        console.log(info);
        resolve({status: 200, message: 'Check email for instructions'})
    }).catch(function (err) {
        console.log(err);
        reject({status: 500, message: 'Internal Server Error !'});
    })
});

router.post("/reset-pass-finish", function (req, res) {
    Student.findOne({email: req.body.email}, function (err, student) {
        const diff = new Date() - new Date(student.temp_password_time);
        const seconds = Math.floor(diff / 1000);
        console.log("Seconds :" + seconds);
        if (seconds < 600) {
            return student;
        } else {
            reject({ status: 401, message: 'Time Out ! Try again' });
        }
    }).then(function(student){
        if (bcrypt.compareSync(token, student.temp_password)) {

            const salt = bcrypt.genSaltSync(10);
            const hash = bcrypt.hashSync(password, salt);

            student.password = hash;
            student.temp_password = undefined;
            student.temp_password_time = undefined;

            return student.save();
        } else {
            reject({status: 401, message: 'Invalid Token !'});
        }
    }).then(function(student){resolve({ status: 200, message: 'Password Changed Sucessfully !'
    })
    })
});

module.exports = router;