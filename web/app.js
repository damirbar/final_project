var express = require("express");
var mongoose = require("mongoose");
mongoose.Promise = require("bluebird");
var path = require("path");
var bodyParser = require("body-parser");
var Student = require("./schemas/student");
var Teacher = require("./schemas/teacher");

<<<<<<< HEAD
var requests = require('./routes/requests');
=======
var feeder = require("./teacherStream");



var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));

>>>>>>> 1a11db5666e5349bd8f38a29e726928ba084ad23

var mongoDB = 'mongodb://127.0.0.1:27017/main_db';
mongoose.connect(mongoDB, {
     useMongoClient: true}
);

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error!\n'));

Student.find({},{last_name:true,first_name:1,_id:0}, function(err,student){
    if(err) return "error";
    console.log(student);
});



app.post("/student", function(req,res){
    var myData= new Student(req.body);
    myData.save()
        .then(function (item){
            res.send("successfully saved item to db");
            console.log("successfully added " +myData.first_name +  " to db");
    })
        .catch(function (error) {
            res.status(400).send("unable to save data");
            console.log("unable to save data");
        });
});

app.post("/teacher", function(req,res){
    var myData= new Teacher(req.body);
    myData.save()
        .then(function (item){
            res.send("successfully saved item to db");
            console.log("successfully added " + myData.first_name +  " to db");
        })
        .catch(function (error) {
            res.status(400).send("unable to save data");
            console.log("unable to save data");
        });
});

app.get("/",function(req,res){
     console.log(req.headers["x-forwarded-for"] || req.connection.remoteAddress);
     res.sendFile(path.join(__dirname + "/index.html"));
});

app.listen(3000, function(){
    console.log("listening...");
});

app.post("/teachers", function(req,res){
        feeder.func(req.body,res);

});



