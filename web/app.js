var express = require("express");
var app = express();
var http = require('http').Server(app);

var mongoose = require("mongoose");
mongoose.Promise = require("bluebird");

var path = require("path");

var bodyParser = require("body-parser");

var logger = require('morgan');

const router = express.Router();
app.use(logger('dev'));


var teacherRequests = require('./routes/teacher_requests');
var studentRequests = require('./routes/students_request');
var coursesRequests = require('./routes/courses_request');
var mainRequests = require('./routes/main_route');
var sessionRequests = require('./routes/session_requests');
var searchRequests = require('./routes/search_routes');


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

require('./routes/routes')(router);
app.use('/api/v1', router);


app.use('/', mainRequests);
app.use('/teachers', teacherRequests);
app.use('/students', studentRequests);
app.use('/courses', coursesRequests);
app.use('/sessions', sessionRequests);
app.use('/search', searchRequests);


var chatRequests = require('./routes/chat_request');///////shay chat
app.use('/chat', chatRequests);////shay chat


var authRouts = require("./routes/login_requests");
app.use('/auth', authRouts);


//////////SOCKET.IO

const io = require('socket.io')(http);
const HashMap = require("hashmap");
// const socketIO = require('./tools/socketIO');

const sockets = new HashMap();
const clients = new HashMap();
const pendingSockets = new HashMap();

io.on('connection', function (socket) {

    console.log(socket.id + ' Connected');

    pendingSockets.set(socket.id, socket);

    //On Authentication
    socket.on('registerClientToClients', function (user_id) {

        pendingSockets.remove(socket.id);

        clients.set(user_id, socket);
        sockets.set(socket.id, user_id);

        console.log(user_id + ' was registered');

        /*can be a boolean value to ensure that the user has been registered successfully*/
        socket.emit('ackConnection', "Hello from the other siiiiiiiide!!!!!!");
    });

    socket.on('sendNotification', function(subject_id){});

    socket.on('unregisterClientFromClients', function (user_id) {

        console.log("removeing " + user_id);

        if(clients.has(user_id)) {
            clients.remove(user_id);
            if (sockets.has(socket.id)) {
                sockets.remove(socket.id);
            }
            pendingSockets.set(socket.id, socket);
        }
    });

    //On disconnection. Removes the user from the map
    socket.on('disconnect', function () {

        var socketID = socket.id;

        if(pendingSockets.has(socketID)){
            pendingSockets.remove(socketID);
            console.log("unregistered socket "+ socket.id + " disconnected");

        }else{
            var userID = sockets.get(socketID);
            sockets.remove(socketID);
            clients.remove(userID);
            console.log(userID + " disconnected");
        }
    });


    function isRegistered(user_id){
        return clients.has(user_id);
    }

    //Utility module for other server's routes
    var socketIOEmitter = {};

    socketIOEmitter.emitEvent = function (user_id, eventName, args) {

        if(isRegistered(user_id)) {
            clients.get(user_id).emit(eventName, args);
            return true;
        }
        return false;
    }

    module.exports = socketIOEmitter;

});


http.listen(3000, function () {
    console.log("listening...");
});


//The 404 Route (ALWAYS Keep this as the last route)
app.get('*', function (req, res) {
    res.sendFile(path.join(__dirname + "/index.html"));
});


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

