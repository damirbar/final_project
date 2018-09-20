/**
 * Module dependencies.
 */
var express = require('express')
    , http = require('http')
    , path = require('path')
    , streams = require('./app/streams.js')();

var app = express()
    , server = http.createServer(app)
    , io = require('socket.io').listen(server);

// all environments
app.set('port', 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
let logger = require('morgan');
app.use(logger('dev'));
let bodyParser = require("body-parser");
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(express.static(path.join(__dirname, 'public')));

// routing
require('./app/routes.js')(app, streams);

/**
 * Socket.io event handling
 */
require('./app/socketHandler.js')(io, streams);

server.listen(app.get('port'), function(){
    console.log('listening...');
});