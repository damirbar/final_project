let passport = require('passport');
let GoogleStrategy = require('passport-google-oauth').OAuth2Strategy;
let FacebookStrategy = require('passport-facebook').Strategy;
let LocalStrategy = require('passport-local').Strategy;

let User = require('../schemas/user');
let email = require('./email');
let emailMessages = require('./email-messages');
let config = require('../config/config');
const bcrypt = require('bcrypt-nodejs');
let File = require("../schemas/file");
let Q = require('q');
let validator = require("email-validator");

const cloudinary = require('cloudinary');

cloudinary.config({
    cloud_name: config.cloudniary.cloud_name,
    api_key: config.cloudniary.api_key,
    api_secret: config.cloudniary.api_secret
});

module.exports.init = function () {

    passport.serializeUser(function (user, done) {
        done(null, user.id);
    });

    passport.deserializeUser(function (id, done) {
        User.findById(id, function (err, user) {
            done(err, user);
        });
    });

    passport.use(new FacebookStrategy({
            clientID: config.passport.FACEBOOK_APP_ID,
            clientSecret: config.passport.FACEBOOK_APP_SECRET,
            callbackURL: "http://localhost:3000/auth/facebook/callback",
            profileFields: ['id', 'displayName', 'picture.type(large)', 'email', 'name', 'gender', 'birthday'],
        },
        function (accessToken, refreshToken, profile, done) {
            // console.log(profile);
            let query = {};
            query['facebookId'] = profile.id;
            if (profile._json.email) {
                query = {};
                query ["$or"] = [];
                let a = {};
                a['facebookId'] = profile.id;
                let b = {};
                b['email'] = profile._json.email;
                query ["$or"].push(a);
                query ["$or"].push(b);
            }
            User.findOne(query, function (err, user) {
                if (err) return done(err);
                if (user) {
                    uploadProfileImage(user._id, profile._json.picture.data.url).then(function (result) {
                        user.profile_img = result.url;
                        user.facebookId = profile.id;
                        if (profile._json.email && !user.email) {
                            user.email = profile._json.email;
                        }
                        user.save(function (err, user) {
                            if (err) return done(err);
                            return done(null, user);
                        });
                    }, function (err) {
                        return done(err);
                    });
                }
                else {
                    bcrypt.genSalt(10, function (err, salt) {
                        bcrypt.hash(profile.id, salt, null, function (err, hash) {
                            user = new User({
                                role: "student",
                                gender: profile.gender,
                                first_name: profile.name.givenName,
                                last_name: profile.name.familyName,
                                display_name: profile.displayName,
                                email: profile._json.email,
                                accessToken: hash,
                                facebookId: profile.id,
                            });
                            uploadProfileImage(user._id, profile._json.picture.data.url).then(function (result) {
                                user.profile_img = result.url;
                                const ans = new File({
                                    originalName: "profile image",
                                    uploaderid: user.id,
                                    url: result.url,
                                    type: result.format,
                                    size: result.bytes,
                                    hidden: false
                                });
                                ans.save(function (err, updated_file) {
                                    if (err) return done(err);
                                    user.profile_file_id = updated_file.id;
                                    user.save(function (err, user) {
                                        if (err) return done(err);
                                        console.log("new facebook user saved");
                                        if (user.email) {
                                            email.sendMail(user.email, 'Registration to wizeUp', emailMessages.registration(user));
                                        }
                                        return done(null, user);
                                    });
                                });
                            }, function (err) {
                                return done(err);
                            });
                        });
                    });
                }
            });
        }
    ));

    passport.use(new GoogleStrategy({
            clientID: config.passport.googleClientId,
            clientSecret: config.passport.googleClientSecret,
            callbackURL: "http://localhost:3000/auth/google/callback"
        },
        function (accessToken, refreshToken, profile, done) {
            // console.log(profile);
            User.findOne({$or: [{googleId: profile.id}, {email: profile.emails[0].value}]}, function (err, user) {
                if (err) return done(err);
                if (user) {
                    if (!user.googleId) {
                        user.googleId = profile.id;
                        user.save(function (err, user) {
                            if (err) return done(err);
                            return done(null, user);
                        });
                    }
                    else {
                        return done(null, user);
                    }
                }
                else {
                    bcrypt.genSalt(10, function (err, salt) {
                        bcrypt.hash(profile.id, salt, null, function (err, hash) {
                            user = new User({
                                role: "student",
                                first_name: profile.name.givenName,
                                last_name: profile.name.familyName,
                                display_name: profile.displayName,
                                email: profile.emails[0].value,
                                accessToken: hash,
                                googleId: profile.id
                            });
                            uploadProfileImage(user, profile._json.image.url).then(function (result) {
                                user.profile_img = result.url;
                                const ans = new File({
                                    originalName: "profile image",
                                    uploaderid: user.id,
                                    url: result.url,
                                    type: result.format,
                                    size: result.bytes,
                                    hidden: false
                                });
                                ans.save(function (err, updated_file) {
                                    if (err) console.log(err);
                                    user.profile_file_id = updated_file.id;
                                    user.save(function (err, user) {
                                        if (err) console.log(err);
                                        console.log("new google user saved");
                                        email.sendMail(user.email, 'Registration to wizeUp', emailMessages.registration(user));
                                        return done(null, user);
                                    });
                                });
                            }, function (err) {
                                return done(err);
                            });
                        });
                    });
                }
            });
        }
    ));

    //local
    passport.use('local-signup', new LocalStrategy({
            // by default, local strategy uses username and password, we will override with email
            usernameField: 'email',
            passwordField: 'password',
            passReqToCallback: true // allows us to pass back the entire request to the callback
        },
        function (req, email, password, done) {
            User.findOne({email: email}, function (err, user) {
                if (err) return done(err);
                if (user) {
                    if (req.body.first_name || req.body.last_name) {
                        return done({error: true, message: 'User already exists'}, null);
                    }
                    else {
                        if (user.password) {
                            if (bcrypt.compareSync(req.body.password, user.password)) {
                                return done(null, user);
                            }
                            else {
                                return done({error: true, message: 'Wrong password'}, null);
                            }
                        }
                        else {
                            return done({error: true, message: 'User has no password'}, null);
                        }
                    }
                } else {
                    if (req.body.first_name && req.body.last_name) {
                        const fname = req.body.first_name,
                            lname = req.body.last_name,
                            email = req.body.email.toLowerCase(),
                            password = req.body.password,
                            password_cnfrm = req.body.password_cnfrm,
                            role = req.body.role;


                        req.checkBody("first_name", "First Name is required").notEmpty();
                        req.checkBody("last_name", "Last Name is required").notEmpty();
                        req.checkBody("email", "Email is required").notEmpty();
                        req.checkBody("password", "Password is required").notEmpty();
                        req.checkBody("password_cnfrm", "Confirm password is required").notEmpty();
                        req.checkBody("role", "role is required").notEmpty();

                        const errors = req.validationErrors();

                        if (!validator.validate(email)) {
                            let err = {
                                location: "body",
                                msg: "Invalid Email",
                                param: 'email',
                                value: undefined
                            };
                            errors.push(err);
                        }
                        if (password !== password_cnfrm) {
                            let err = {
                                location: "body",
                                msg: "Passwords don't match",
                                param: 'password',
                                value: undefined
                            };
                            errors.push(err);
                        }
                        if (errors) {
                            let str = "";
                            for (let i = 0; i < errors.length; ++i) {
                                console.log(errors[i].msg);
                                str += errors[i].msg + "  ";
                            }
                            return done({error: true, message: str}, null);
                        }
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
                                        if (err.name === 'MongoError' && err.code === 11000) {
                                            // Duplicate username
                                            console.log(email + ' already exists!');
                                            return res.status(500).json({message: email + ' already exists!'});
                                        }
                                        else if (err.name === 'ValidationError') {
                                            let str = "";
                                            for (field in err.errors) {
                                                console.log("you must provide: " + field + " field");
                                                str += "you must provide: " + field + " field  ";
                                            }
                                            return res.status(500).json({message: str});
                                        }
                                        else {// Some other error
                                            console.log(err);
                                            return res.status(500).json({message: err});
                                        }
                                    }
                                    else {
                                        console.log(user);
                                        email.sendMail(user.email, 'Registration to wizeUp', emailMessages.registration(user));
                                        const newStudent = new Student({
                                            user_id: user._id
                                        });
                                        newStudent.save();
                                        res.status(200).json(user);
                                    }
                                });
                            });
                        });
                    }
                    else {
                        return done({error: true, message: 'No such user'}, null);
                    }
                }

            });
        }));
};

function uploadProfileImage(user, newImage) {
    let dfr = Q.defer();

    cloudinary.v2.uploader.upload(newImage, {

            public_id: "profiles/" + user.id + "profile",
            width: 1000,
            height: 1000,
            crop: 'thumb',
            gravity: 'face',
            radius: 20
        },
        function (err, result) {
            if (err) {
                return dfr.reject(err);
            }
            dfr.resolve(result);

        });
    return dfr.promise;


    // console.log("starting to upload profile image");
    // cloudinary.v2.uploader.upload(newImage,
    //     {
    //         public_id: "profiles/" + user.id + "profile",
    //         width: 1000,
    //         height: 1000,
    //         crop: 'thumb',
    //         gravity: 'face',
    //         radius: 20
    //     },
    //
    //     function (err, result) {
    //         if (err) return dfr.reject(err);
    //         console.log("uploaded profile image");
    //         // console.log(result);
    //         const ans = new File({
    //             originalName: "profile image",
    //             uploaderid: user.id,
    //             url: result.url,
    //             type: result.format,
    //             size: result.bytes,
    //             hidden: false
    //         });
    //         ans.save(function (err, updated_file) {
    //             if (err) return dfr.reject(err);
    //             user.profile_file_id = updated_file.id;
    //             user.profile_img = result.url;
    //             dfr.resolve(result);
    //         });
    //     });
    // return dfr.promise;
}

// function getGender(gender) {
//     if (gender) {
//         if (gender.toLowerCase() === 'female')
//             return 1;
//         if (gender.toLowerCase() === 'male')
//             return 2;
//     }
//     return 3;
// }