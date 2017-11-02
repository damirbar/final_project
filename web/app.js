var express = require("express");

var path = require("path");

var app =express();



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

