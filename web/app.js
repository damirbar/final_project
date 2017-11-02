var express = require("express");
var mongoose = require("mongoose");
var path = require("path");
var Student = require("./schemas/student")
var Teacher = require("./schemas/teacher")

var bodyParser = require("body-parser");


var app =express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));


var mongoDB = 'mongodb://127.0.0.1:27017/main_db';
mongoose.connect(mongoDB)//, {
    // useMongoClient: true})
;

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error!'));


//var student = mongoose.model("Student", StudentSchema);

app.post("/student", function(req,res){
    var myData= new Student(req.body);
    myData.save()
        .then(function (item){
            res.send("successfully saved item to db");
    })
        .catch(function (error) {
            res.status(400).send("unable to save data");
        });
});

app.post("/teacher", function(req,res){
    var myData= new Teacher(req.body);
    myData.save()
        .then(function (item){
            res.send("successfully saved item to db");
        })
        .catch(function (error) {
            res.status(400).send("unable to save data");
        });
});

app.get("/",function(req,res){
     console.log(req.headers["x-forwarded-for"] || req.connection.remoteAddress);
     res.sendFile(path.join(__dirname + "/index.html"));
});

app.get("/profile/:id",function(req,res){
    console.log(req.headers["x-forwarded-for"] || req.connection.remoteAddress);
    res.sendFile(path.join(__dirname + "/profile.html"));
    console.log(req.params.id);
});


app.listen(3000, function(){
    console.log("listening...");
});

