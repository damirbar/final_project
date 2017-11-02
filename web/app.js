var express = require("express");

var app =express();

app.get("/",function(req,res){
    console.log(req.headers["x-forwarded-for"] || req.connection.remoteAddress);
    res.end("eran");
});

app.listen(3000, function(){
    console.log("listening...");
});

