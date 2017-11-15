var express = require("express");
var app = express();


var passport = require('passport');
var FacebookStrategy = require('passport-facebook').Strategy;
var cookieParser =require('cookie-parser');
var session = require('express-session');
app.use(passport.initialize());
app.use(passport.session());


var mongoose = require("mongoose");
mongoose.Promise = require("bluebird");
var path = require("path");
var bodyParser = require("body-parser");
var Student = require("./schemas/student");
var Teacher = require("./schemas/teacher");
var loger = require("./routes/login_requests");


var teacherRequests = require('./routes/teacher_requests');
var studentRequests = require('./routes/students_request');
var coursesRequests = require('./routes/courses_request');




app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.use(express.static(path.join(__dirname, 'public')));

var myLessCompiler = require("./less_compiler");
myLessCompiler();


// var mongoDB = 'mongodb://127.0.0.1:27017/main_db';
var mongoDB = 'mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin';
mongoose.connect(mongoDB, {
        useMongoClient: true
    }
);

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error!\n'));

// Student.find({},{last_name:true,first_name:1,_id:0}, function(err,student){
//     if(err) return "error";
//     console.log(student);
// });

app.use('/', teacherRequests);
app.use('/',studentRequests);
app.use('/',coursesRequests);


var feedStudents = function (dataArr) {
    for (var i = 0; i < dataArr.length; ++i) {
        var dat = new Student(dataArr[i]);
        dat.save()
            .then(function (item) {
                console.log("Saved a student to the DB");
            })
            .catch(function (err) {
                console.log("\nCouldn't save student to the DB\nError: " + err.errmsg + "\n");
            })
    }
};

var studentList = require('./studentsArr');

// feedStudents(studentList);




//erans work authentication session


var configAuth = require('./auth')


app.use('/', loger);
app.use(cookieParser());
// app.use(session({
//     secret: "1234567890",
//     cookie: {maxAge:600000},
//     resave : true,
//     saveUninitialized:false}));
passport.serializeUser(function (user,done) {
    done(null,user.id);
});
passport.deserializeUser(function (user,done) {
    Student.findById(id,function (err,user) {
        done(err,user);
    })
})
passport.use(new FacebookStrategy({
        clientID: configAuth.facebookAuth.clientID,
        clientSecret: configAuth.facebookAuth.clientSecret,
        callbackURL: configAuth.facebookAuth.callbackURL
    }, function(accessToken, refreshToken, profile, done) {
        process.nextTick(function () {
            Student.findOne({"facebook.id":profile.id},function (err,user) {
               if(err)
                   return done(err);
               if(user)
                   return done(null,user);
               else{
                   var newStudent = new Student({facebook: {}});
                   profile.emails = profile.emails ? profile.emails : [];
                   profile.emails[0] = profile.emails[0] ? profile.emails[0] : "mail@mail.mail";
                   newStudent.facebook.id = profile.id;
                   newStudent.facebook.token = accessToken;
                   newStudent.facebook.name = profile.name.givenName + " " + profile.name.familyName;
                   newStudent.facebook.email = profile.emails[0].value;

                   newStudent.save(function (err) {
                      if(err)
                          throw (err);
                      return done(null,newStudent);
                   });
               }
            });
            });
        }
));
/////////////////////////








app.listen(3000, function () {
    console.log("listening...");
});


