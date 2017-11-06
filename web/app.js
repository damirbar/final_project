var express = require("express");
var mongoose = require("mongoose");
mongoose.Promise = require("bluebird");
var path = require("path");
var bodyParser = require("body-parser");
var Student = require("./schemas/student");
var Teacher = require("./schemas/teacher");

var feeder = require("./teacherStream");



var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));


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



