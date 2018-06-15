let express = require("express");
let app = express();
let http = require('http').Server(app);
let mongoose = require("mongoose");
mongoose.Promise = require("bluebird");
let path = require("path");
let bodyParser = require("body-parser");
let logger = require('morgan');
app.use(logger('dev'));


//passport login
let passport       = require("passport");
let passportService = require('./tools/passport');
app.use(passport.initialize());
app.use(passport.session());
passportService.init();

//email service
let emailService = require('./tools/email');
emailService.init();
//

let teacherRequests = require('./routes/teacher_requests');
let studentRequests = require('./routes/students_request');
let coursesRequests = require('./routes/courses_request');
let mainRequests = require('./routes/main_route');
let sessionRequests = require('./routes/session_requests');
let searchRequests = require('./routes/search_routes');
let authRouts = require("./routes/login_requests");


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.use(express.static(path.join(__dirname, 'public')));

let myLessCompiler = require("./tools/less_compiler");
myLessCompiler();


let mongoDB = 'mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin';
mongoose.connect(mongoDB, {
    useMongoClient: true
    }
);

let db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error!\n'));


app.use('/', mainRequests);
app.use('/teachers', teacherRequests);
app.use('/students', studentRequests);
app.use('/courses', coursesRequests);
app.use('/sessions', sessionRequests);
app.use('/search', searchRequests);
app.use('/auth', authRouts);



//////////SOCKET.IO

const io = require('socket.io')(http);
const socketIO = require('./tools/socketIO');

socketIO.setIO(io);

io.on('connection', function (socket) {
    console.log(socket.id + ' Connected');
    socketIO.socketInit(socket);
});


http.listen(3000, function () {
    console.log("listening...");
});


//The 404 Route (ALWAYS Keep this as the last route)
app.get('*', function (req, res) {
    res.sendFile(path.join(__dirname + "/index.html"));
});

//
// let chatRequests = require('./routes/chat_request');///////shay chat
// app.use('/chat', chatRequests);////shay chat

//
// let addChat = function () {
//     let channel = new Channel(
//         {
//             guid: "money",
//             name: "bank2"
//         }
//     );
//     channel.save()
//         .then(function (item) {
//             console.log("Saved a channel to the DB");
//         })
//         .catch(function (err) {
//             console.log("\nCouldn't save the channel to the DB\nError: " + err + "\n");
//         })
// };
//
// //addChat();
//
// let addMessage = function () {
//     let message = new Message(
//         {
//             guid: "shay",
//             channel_guid: "money",
//             user_guid: "avi",
//             content: "bye",
//             timestamp: Date.now()
//         }
//     );
//     message.save()
//         .then(function (item) {
//             console.log("Saved a message to the DB");
//         })
//         .catch(function (err) {
//             console.log("\nCouldn't save the message to the DB\nError: " + err + "\n");
//         })
// };
// addMessage();

