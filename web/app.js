var express = require("express");
var app = express();
var HashMap = require("hashmap");
var http = require('http').Server(app);
var io = require('socket.io')(http);

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
var mainRequests    = require('./routes/main_route');
var sessionRequests = require('./routes/session_requests');
var searchRequests = require('./routes/search_routes');



app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.use(express.static(path.join(__dirname, 'public')));

var myLessCompiler = require("./tools/less_compiler");
myLessCompiler();


var mongoDB = 'mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin';
mongoose.connect(mongoDB, {
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

var sockets = new HashMap();
var clients = new HashMap();

io.on('connection', function(socket) {

    console.log(socket.id + ' Connected');

    socket.on('registerClientToClients', function (user_id){
        clients.set(user_id, socket);
        sockets.set(socket.id, user_id);
        socket.emit('ackConnection', "Hello from the other siiiiiiiide!!!!!!");
    });

    socket.on('disconnect', function() {

        var userID = sockets.get(socket.id);
        sockets.remove(socket.id);
        clients.remove(userID);

        console.log(userID + " disconnected");
    });
});


http.listen(3000, function () {
    console.log("listening...");
});


//The 404 Route (ALWAYS Keep this as the last route)
app.get('*', function(req, res){
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

