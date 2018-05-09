var express = require("express");
var app = express();

var http = require('http').Server(app);
var io = require('socket.io')(http);

var mongoose = require("mongoose");
mongoose.Promise = require("bluebird");

var path = require("path");

var bodyParser = require("body-parser");


const Student = require("./schemas/student");
const Session = require("./schemas/session");
const Channel = require("./schemas/channel");///shay chat
const Message = require("./schemas/message");///shay chat



//shay
var logger = require('morgan');

//shay
const router = express.Router();
app.use(logger('dev'));


var teacherRequests = require('./routes/teacher_requests');
var studentRequests = require('./routes/students_request');
var coursesRequests = require('./routes/courses_request');
var mainRequests    = require('./routes/main_route');
var sessionRequests = require('./routes/session_requests');



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

//shay
require('./routes/routes')(router);
app.use('/api/v1', router);


app.use('/', mainRequests);
app.use('/teachers', teacherRequests);
app.use('/students', studentRequests);
app.use('/courses', coursesRequests);
app.use('/sessions', sessionRequests);

var chatRequests = require('./routes/chat_request');///////shay chat
app.use('/chat', chatRequests);////shay chat


var authRouts = require("./routes/login_requests");
app.use('/auth', authRouts);


/////////////////////////


io.on('connect', function(socket) {
    console.log("SOMEBODY CONNECTED!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    socket.on('disconnect', function() {
        console.log("ERAN IS FUCKING GAY");
    });
});


http.listen(3000, function () {
    console.log("listening...");
});


//The 404 Route (ALWAYS Keep this as the last route)
app.get('*', function(req, res){
    res.sendFile(path.join(__dirname + "/index.html"));
});


// For session testing:

var addSession = function () {
    var session = new Session(
        {
            sid: 1234,
            name: "Test Session",
            teacher_id: "WizeGuy1234",
            location: "10.4.2",
            creation_date: Date.now(),
            students: [
                {
                    id_num: "d1234",
                    display_name: "Damir",
                    mail: "damir@wizeup.com",
                    rating_val: 1
                },
                {
                    id_num: "sh1234",
                    display_name: "Shay",
                    mail: "shay@wizeup.com",
                    rating_val: 1
                },
                {
                    id_num: "se1234",
                    display_name: "Sefi",
                    mail: "sefi@wizeup.com",
                    rating_val: 1
                },
                {
                    id_num: "e1234",
                    display_name: "Eran",
                    mail: "eran@wizeup.com",
                    rating_val: 1
                }
            ],
            curr_rating: 0
        }
    );
    session.save()
        .then(function (item) {
            console.log("Saved a session to the DB");
        })
        .catch(function (err) {
            console.log("\nCouldn't save the session to the DB\nError: " + err + "\n");
        })


    // Session.findOne({internal_id: 1234}).then(function(sess) {

    // });
};
// addSession();



//can delete?

// var feedStudents = function (dataArr) {
//     for (var i = 0; i < dataArr.length; ++i) {
//         var dat = new Student(dataArr[i]);
//         dat.save()
//             .then(function (item) {
//                 console.log("Saved a student to the DB");
//             })
//             .catch(function (err) {
//                 console.log("\nCouldn't save student to the DB\nError: " + err.errmsg + "\n");
//             })
//     }
// };

//var studentList = require('./studentsArr');



var addChat = function () {
    var channel = new Channel(
        {
            guid: "money",
            name: "bank2"
        }
    );
    channel.save()
        .then(function (item) {
            console.log("Saved a channel to the DB");
        })
        .catch(function (err) {
            console.log("\nCouldn't save the channel to the DB\nError: " + err + "\n");
        })
};

//addChat();

var addMessage = function () {
    var message = new Message(
        {
            guid: "shay",
            channel_guid: "money",
            user_guid: "avi",
            content: "bye",
            timestamp: Date.now()
        }
    );
    message.save()
        .then(function (item) {
            console.log("Saved a message to the DB");
        })
        .catch(function (err) {
            console.log("\nCouldn't save the message to the DB\nError: " + err + "\n");
        })
};


// addMessage();

