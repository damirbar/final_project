let express = require("express");
let app = express();
let http = require('http').Server(app);
let mongoose = require("mongoose");
mongoose.Promise = require("bluebird");
let path = require("path");
let bodyParser = require("body-parser");
let logger = require('morgan');
app.use(logger('dev'));
let emailService = require('./tools/email');
emailService.init();


//passport eran
let cookieParser = require('cookie-parser');
let session = require('express-session');
let passport = require("passport");
let passportService = require('./tools/passport');
app.use(session({
    secret: '1234567890',
    cookie: { maxAge: 3600000*24*365 },
    resave: true,
    saveUninitialized: false}));
app.use(passport.initialize());
app.use(passport.session());
passportService.init();
app.use(cookieParser());
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


http.listen(3000, function () {
    console.log("listening...");
});


//eran
const io = require('socket.io')(http);

// app.set('views', __dirname + '/streamTest/views');
// app.set('view engine', 'ejs');
// let streams = require('./streamTest/streams.js')();
// require('./streamTest/stream_requests.js')(app, streams);

const socketIO = require('./tools/socketIO');
// require('./streamTest/socketHandler.js')(io, streams);



io.on('connection', function (socket){
    socket._id = 'stream_' + socket.id;
    console.log(socket.id + ' Connected');
    socket.emit('id', socket.id);
    socketIO.socketInit(socket);
});

// socketIO.setStreams(streams);
socketIO.setIO(io);
//eran


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


//The 404 Route (ALWAYS Keep this as the last route)
app.get('*', function (req, res) {
    res.sendFile(path.join(__dirname + "/index.html"));
});