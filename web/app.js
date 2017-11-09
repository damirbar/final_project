var express = require("express");
var app = express();

var mongoose = require("mongoose");
mongoose.Promise = require("bluebird");

var path = require("path");

// JSON Parsing
var bodyParser = require("body-parser");
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));
// End JSON Parsing


var Student = require("./schemas/student");
var Teacher = require("./schemas/teacher");

var requests = require('./routes/requests');

var feeder = require("./teacherStream");


var mongoDB = 'mongodb://127.0.0.1:27017/main_db';
mongoose.connect(mongoDB, {
     useMongoClient: true}
);

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error!\n'));

//query to dataBase
// Student.findOne({'first_name':"eran"}, function(err,student){
//     if(err) return "error";
//     console.log("u got "+ student);
// });

app.use('/', requests);

app.listen(3000, function(){
    console.log("listening...");
});