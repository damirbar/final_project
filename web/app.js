let express = require("express");
let app = express();
let http = require('http').Server(app);

let mongoose = require("mongoose");
mongoose.Promise = require("bluebird");

let path = require("path");

let bodyParser = require("body-parser");

// var logger = require('morgan');

const router = express.Router();
// app.use(logger('dev'));

//passport login
let passport       = require("passport");
let passportService = require('./tools/passport');
app.use(passport.initialize());
app.use(passport.session());
passportService.init();
//

let teacherRequests = require('./routes/teacher_requests');
let studentRequests = require('./routes/students_request');
let coursesRequests = require('./routes/courses_request');
let mainRequests = require('./routes/main_route');
let sessionRequests = require('./routes/session_requests');
let searchRequests = require('./routes/search_routes');


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

require('./routes/routes')(router);
app.use('/api/v1', router);


app.use('/', mainRequests);
app.use('/teachers', teacherRequests);
app.use('/students', studentRequests);
app.use('/courses', coursesRequests);
app.use('/sessions', sessionRequests);
app.use('/search', searchRequests);


let chatRequests = require('./routes/chat_request');///////shay chat
app.use('/chat', chatRequests);////shay chat


let authRouts = require("./routes/login_requests");
app.use('/auth', authRouts);


//////////SOCKET.IO

const io = require('socket.io')(http);
const socketIO = require('./tools/socketIO');

socketIO.setIO(io);

io.on('connection', function (socket) {

    console.log(socket.id + ' Connected');
    socketIO.socketInit(socket);

    // pendingSockets.set(socket.id, socket);

    //On Authentication
    // socket.on('registerClientToClients', function (user_id) {
    //
    //     // pendingSockets.remove(socket.id);
    //     //
    //     // clients.set(user_id, socket);
    //     // sockets.set(socket.id, user_id);
    //     //
    //     // console.log(user_id + ' was registered');
    //     //
    //     // /*can be a boolean value to ensure that the user has been registered successfully*/
    //     // socket.emit('ackConnection', "Hello from the other siiiiiiiide!!!!!!");
    // });

    // socket.on('unregisterClientFromClients', function (user_id) {
    //
    //     socketIO.unregisterClientFromClients(user_id);
    //
    //     // console.log("removeing " + user_id);
    //     //
    //     // if(clients.has(user_id)) {
    //     //     clients.remove(user_id);
    //     //     if (sockets.has(socket.id)) {
    //     //         sockets.remove(socket.id);
    //     //     }
    //     //     pendingSockets.set(socket.id, socket);
    //     // }
    // });

    //On disconnection. Removes the user from the map
    // socket.on('disconnect', function () {
    //     socketIO.onDisconnect(socket);
    //     // var socketID = socket.id;
    //     //
    //     // if(pendingSockets.has(socketID)){
    //     //     pendingSockets.remove(socketID);
    //     //     console.log("unregistered socket "+ socket.id + " disconnected");
    //     //
    //     // }else{
    //     //     var userID = sockets.get(socketID);
    //     //     sockets.remove(socketID);
    //     //     clients.remove(userID);
    //     //     console.log(userID + " disconnected");
    //     // }
    // });


    // function isRegistered(user_id){
    //     return clients.has(user_id);
    // }

    //Utility module for other server's routes

    // exports.emitEvent = function (user_id, eventName, args) {
    //
    //     console.log('emitting event ' + eventName);
    //
    //     if(isRegistered(user_id)) {
    //         clients.get(user_id).emit(eventName, args);
    //         return true;
    //     }
    //     return false;
    // }

});


http.listen(3000, function () {
    console.log("listening...");
});


//The 404 Route (ALWAYS Keep this as the last route)
app.get('*', function (req, res) {
    res.sendFile(path.join(__dirname + "/index.html"));
});


let addChat = function () {
    let channel = new Channel(
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

let addMessage = function () {
    let message = new Message(
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

