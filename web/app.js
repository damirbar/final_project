var express = require("express");
var mongoose = require("mongoose");
var path = require("path");

var app =express();

var mongoDB = 'mongodb://127.0.0.1/main_db';
mongoose.connect(mongoDB, {
    useMongoClien: true
});

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'MongoDB connection error!'));

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

