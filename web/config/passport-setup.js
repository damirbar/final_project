var passport = require('passport');
var GoogleStrategy = require('passport-google-oauth20');
var FacebookStrategy = require('passport-facebook');//.Strategy;
var LocalStrategy = require('passport-local');//.Strategy;
var configAuth = require('./auth');
var Student = require('../schemas/student');


passport.serializeUser(function (user, done) {
    done(null, user.id);
});

passport.deserializeUser(function (id, done) {
    Student.findById(id, function (err, user) {
        done(err, user);
    })
});

passport.use(
    new GoogleStrategy({
            clientID: configAuth.googleAuth.clientID,
            clientSecret: configAuth.googleAuth.clientSecret,
            callbackURL: configAuth.googleAuth.callbackURL

        }, function (accessToken, refreshToken, profile, done) {
            process.nextTick(function () {
                Student.findOne({"googleid": profile.id}, function (err, user) {
                    if (err)
                        return done(err);
                    if (user)
                        return done(null, user);
                    else {
                        // var newStudent = new Student();
                        var newStudent = new Student({

                            first_name: profile.name.givenName,
                            last_name: profile.name.familyName,
                            display_name: profile.displayName,
                            mail: profile.emails[0].value,
                            about_me: '',
                            country: '',
                            city: '',
                            age: 0,
                            uni: {},
                            gender: profile.gender,
                            courses: [],
                            photos: [],
                            // profile_img: profile._json.picture.data.url,
                            fs: {},
                            grades: [],
                            cred: 0,
                            fame: 0,
                            msg: [],
                            register_date: Date.now(),
                            last_update: Date.now(),
                            notifications: [],
                            facebookid: '',
                            accessToken: '',
                            googleid: profile.id

                        });
                        //  newStudent.googleid = profile.id;
                        //  newStudent.first_name = profile.name.givenName;
                        //  newStudent.last_name = profile.name.familyName;
                        //  newStudent.gender = profile.gender;
                        // // newStudent.facebook.token = accessToken;
                        //  newStudent.display_name = profile.displayName;
                        //  newStudent.mail = profile.emails[0].value;


                        newStudent.save(function (err) {
                            if (err)
                                throw (err);
                            return done(null, newStudent);
                        }).then(function (newStudent) {
                            console.log('new user created' + newStudent);
                        });
                    }
                });
            });
        }
    ));


passport.use(new FacebookStrategy({
        clientID: configAuth.facebookAuth.clientID,
        clientSecret: configAuth.facebookAuth.clientSecret,
        callbackURL: configAuth.facebookAuth.callbackURL,
        profileFields: ["id", "birthday", "emails", "first_name", "gender", "last_name"]
    }, function (accessToken, refreshToken, profile, done) {
        process.nextTick(function () {
            Student.findOne({"facebookid": profile.id}, function (err, user) {
                if (err)
                    return done(err);
                if (user)
                    return done(null, user);
                else {
                    var newStudent = new Student({

                        first_name: profile.name.givenName,
                        last_name: profile.name.familyName,
                        display_name: profile.displayName,
                        //temporary fix
                        mail: profile.name.givenName + "@gmail.com//fix!",
                        //mail: profile._json.email,
                        about_me: '',
                        country: '',
                        city: '',
                        age: 0,
                        uni: {},
                        gender: profile.gender,
                        courses: [],
                        photos: [],
                        // profile_img: profile._json.picture.data.url,
                        fs: {},
                        grades: [],
                        cred: 0,
                        fame: 0,
                        msg: [],
                        register_date: Date.now(),
                        last_update: Date.now(),
                        notifications: [],
                        facebookid: profile.id,
                        accessToken: '',
                        googleid: ''

                    });
                    // newStudent.facebookid = profile.id;
                    // newStudent.first_name = profile.name.givenName;
                    // newStudent.last_name = profile.name.familyName;
                    // // newStudent.facebook.token = accessToken;
                    // newStudent.display_name = profile.displayName;
                    // if(newStudent.mail = profile.emails)
                    //     newStudent.mail = profile.emails[0].value;


                    newStudent.save(function (err) {
                        if (err)
                            throw (err);
                        return done(null, newStudent);
                    }).then(function (newStudent) {
                        console.log('new user created' + newStudent);
                    });
                }
            });
        });
    }
));
