var passport = require('passport');
var GoogleStratgy = require('passport-google-oauth20');
var FacebookStrategy = require('passport-facebook').Strategy;
var configAuth = require('./auth');
var Student = require('../schemas/student');


passport.serializeUser(function (user,done) {
    done(null,user.id);
});

passport.deserializeUser(function (id,done) {
    Student.findById(id,function (err,user) {
        done(err,user);
    })
});

passport.use(
    new GoogleStratgy({
        clientID:configAuth.googleAuth.clientID,
        clientSecret:configAuth.googleAuth.clientSecret,
        callbackURL:configAuth.googleAuth.callbackURL

},function (accessToken, refreshToken, profile, done) {
            process.nextTick(function () {
                Student.findOne({"googleid":profile.id},function (err,user) {
                    if(err)
                        return done(err);
                    if(user)
                        return done(null,user);
                    else{
                        var newStudent = new Student();
                        newStudent.googleid = profile.id;
                        newStudent.first_name = profile.name.givenName;
                        newStudent.last_name = profile.name.familyName;
                        newStudent.gender = profile.gender;
                       // newStudent.facebook.token = accessToken;
                        newStudent.display_name = profile.displayName;
                        newStudent.mail = profile.emails[0].value;
                        newStudent.save(function (err) {
                            if(err)
                                throw (err);
                            return done(null,newStudent);
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
        profileFields: ['id', 'name','gender', "displayName",'about','email']
    }, function(accessToken, refreshToken, profile, done) {
        process.nextTick(function () {
            Student.findOne({"facebookid":profile.id},function (err,user) {
               if(err)
                   return done(err);
               if(user)
                   return done(null,user);
               else{
                   var newStudent = new Student();
                   newStudent.facebookid = profile.id;
                   newStudent.first_name = profile.name.givenName;
                   newStudent.last_name = profile.name.familyName;
                   // newStudent.facebook.token = accessToken;
                   newStudent.display_name = profile.displayName;
                   if(newStudent.mail = profile.emails)
                   newStudent.mail = profile.emails[0].value;

                   newStudent.save(function (err) {
                      if(err)
                          throw (err);
                      return done(null,newStudent);
                   }).then(function (newStudent) {
                       console.log('new user created' + newStudent);
                   });
               }
            });
            });
        }
));
