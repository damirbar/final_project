let passport = require('passport');
let GoogleStrategy = require('passport-google-oauth').OAuth2Strategy;
let User = require('../schemas/user');
let email = require('./email');
let emailMessages = require('./email-messages');
let config = require('../config/config');
let File = require("../schemas/file");

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

    passport.use(new GoogleStrategy({
            clientID: config.passport.googleClientId,
            clientSecret: config.passport.googleClientSecret,
            callbackURL: "http://localhost:3000/auth/google/callback"
        },
        function (accessToken, refreshToken, profile, done) {
            console.log(profile);
            User.findOne({email: profile.emails[0].value}, function (err, user) {
                if (err) return done(err);
                if (user) {
                    return done(null, user);
                }
                else {
                    user = new User({
                        role: "student",
                        first_name: profile.name.givenName,
                        last_name: profile.name.familyName,
                        display_name: profile.displayName,
                        email: profile.emails[0].value,
                        birthday: profile._json.birthday,
                        accessToken: profile.id,
                    });
                    console.log("starting to upload google image");
                    cloudinary.v2.uploader.upload(profile._json.image.url,
                        {
                            public_id: "profiles/" + user.id + "profile",
                            width: 1000,
                            height: 1000,
                            crop: 'thumb',
                            gravity: 'face',
                            radius: 20
                        },

                        function (err, result) {
                            if (err) return err;
                            console.log("uploaded google image");
                            console.log(result);
                            const ans = new File({
                                originalName: "google profile image",
                                uploaderid: user.id,
                                url: result.url,
                                type: result.format,
                                size: result.bytes,
                                hidden: false
                            });
                            ans.save(function (err, updated_file) {
                                if (err) return (err);
                                user.profile_file_id = updated_file.id;
                                user.profile_img = result.url;
                                if (user.email) {
                                    email.sendMail([user.email], 'Registration to wizeUp', emailMessages.registration(user));
                                }
                                user.save();
                                return done(user);
                            });
                        });
                }
            });
        }
    ));
};

function getGender(gender) {
    if (gender) {
        if (gender.toLowerCase() === 'female')
            return 1;
        if (gender.toLowerCase() === 'male')
            return 2;
    }
    return 3;
}
