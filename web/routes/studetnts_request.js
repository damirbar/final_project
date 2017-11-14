var express = require('express');
var router = express.Router();
var path = require("path");


var Student = require("../schemas/student");



router.post("/student", function (req, res) {
    var myData = new Student(req.body);
    myData.save(function (err) {
        if (err) {
            if (err.name === 'MongoError' && err.code === 11000) {
                // Duplicate username
                console.log('User ' + myData.first_name + " cannot be added " + myData.mail + ' already exists!');
                return res.status(500).send('User ' + myData.first_name + " cannot be added " + myData.mail + ' already exists!');
            }
            if (err.name === 'ValidationError') {
                //ValidationError
                var str = "";
                for (field in err.errors) {
                    console.log("you must provide: " + field + " field");
                    str += "you must provide: " + field + " field  ";
                }
                return res.status(500).send(str);
            }
            // Some other error
            console.log(err);
            return res.status(500).send(err);
        }
        res.send("successfully added " + myData.first_name + " to db");
        console.log("successfully added " + myData.first_name + " to db");
    });
});