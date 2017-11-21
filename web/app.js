var express = require("express");
var app = express();
var mongoose = require("mongoose");
mongoose.Promise = require("bluebird");
var path = require("path");
var bodyParser = require("body-parser");

var cookieSession = require('cookie-session');
var passport=require('passport');
var passportSetup = require('./config/passport-setup');

var Student = require("./schemas/student");
var Teacher = require("./schemas/teacher");


//shay
// const router  = express.Router();
// var logger = require('morgan');
// app.use(logger('dev'));
// require('./routes')(router);
// app.use('/api/v1', router);
// require('./routes')(router);
// app.use('/api/v1', router);


var teacherRequests = require('./routes/teacher_requests');
var studentRequests = require('./routes/students_request');
var coursesRequests = require('./routes/courses_request');
var uniRequest = require('./routes/uni_requests');
var departmentRequest = require('./routes/department_request');



app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.use(express.static(path.join(__dirname, 'public')));

var myLessCompiler = require("./tools/less_compiler");
myLessCompiler();


var mongoDB = 'mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin';
mongoose.connect(mongoDB, {
        useMongoClient: true
    }
);

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error!\n'));


app.use('/', teacherRequests);
app.use('/',studentRequests);
app.use('/',coursesRequests);
app.use('/',uniRequest);
app.use('/',departmentRequest);

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


//erans work authentication session


app.use(passport.initialize());
app.use(passport.session());

var keys = require('./config/auth');
var authRouts = require("./routes/login_requests");


app.use(cookieSession({
    maxAge: 24 * 60 * 60 * 1000, //one day
    keys:[keys.session.cookieKey]

}));

// initialize passport

app.use('/auth',authRouts);


/////////////////////////








app.listen(3000, function () {
    console.log("listening...");
});


