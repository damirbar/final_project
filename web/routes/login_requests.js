const router = require('express').Router();
const expressValidator = require('express-validator');
const User = require("../schemas/user");
const Student = require("../schemas/student");
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt-nodejs');
const auth = require('basic-auth');

router.use(expressValidator());

router.post("/auth-login-user-pass", function (req, res) {

    const credentials = auth(req);
    console.log(credentials);

    if (credentials.name === "undefined" || credentials.pass === "undefined") {
        return res.status(401).send({message: 'Invalid Credentials!'})
    }

    User.findOne({email: credentials.name}, function (err, user) {
        if (err) return err;
        if (!user) {
            return res.status(400).send({message: 'no such user!'})
        }
        console.log(user);
        console.log("Found the user " + credentials.name);
        if (bcrypt.compareSync(credentials.pass, user.password)) {
            console.log("Found the user " + credentials.name);
            const token = jwt.sign(credentials.name, "Wizer");

            User.update({email: credentials.name}, {accessToken: token}, function (err) {
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
                        id_num: user.id_num,
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
    User.findOne({email: req.verifiedEmail}, function (err, user) {
        if (err) return next(err);
        res.status(200).send({
            message: 'welcome to Wizer!',
            email: user.email,
            first_name: user.first_name,
            last_name: user.last_name,
            accessToken: user.accessToken,
            role: user.role,
            photos: user.photos,
            id_num: user.id_num,
            display_name: user.display_name
        })
    });
});


router.post("/new-user", function (req, res) {

    const fname = req.body.first_name,
        lname = req.body.last_name,
        email = req.body.email,
        password = req.body.password,
        role = req.body.role;

    req.checkBody("first_name", "First Name is required").notEmpty();
    req.checkBody("last_name", "Last Name is required").notEmpty();
    req.checkBody("email", "Email is required").notEmpty();
    req.checkBody("email", "Email is not valid").isEmail();
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
            role: role
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

    const token = req.headers['x-access-token'];

    if (token) {
        // verifies secret and checks exp
        jwt.verify(token, "Wizer", function (err, decoded) {
            if (err) {
                return res.json({success: false, message: 'Failed to authenticate token.'});
            } else {
                User.findOne({accessToken: token}, function (err, user) {
                    if (err) return next(err);
                    if (user !== {}) {
                        const oldPass = req.body.password;
                        const newPass = req.body.newPassword;
                        if (bcrypt.compareSync(oldPass, user.password)) {
                            bcrypt.genSalt(10, function (err, salt) {
                                bcrypt.hash(user.password, salt, null, function (err, hash) {
                                    if (err) next(err);

                                    User.update({accessToken: token}, {password: hash}, function (err) {
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
            reject({status: 401, message: 'Time Out ! Try again'});
        }
    }).then(function (student) {
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
    }).then(function (student) {
        resolve({
            status: 200, message: 'Password Changed Successfully !'
        })
    })
});

module.exports = router;