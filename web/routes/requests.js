var express = require('express');
var router = express.Router();
var path = require("path");

var Student = require("../schemas/student");
var Teacher = require("../schemas/teacher");

var feeder = require("../teacherStream");


router.post("/student", function (req, res) {
    var myData = new Student(req.body);
    myData.save()
        .then(function (item) {
            res.send("successfully saved item to db");
            console.log("successfully added " + myData.first_name + " to db");
        })
        .catch(function (error) {
            res.status(400).send("unable to save data. Error: " + error);
            console.log("unable to save data");
        });
});

router.post("/teacher", function (req, res) {
    var myData = new Teacher(req.body);
    myData.save()
        .then(function (item) {
            res.send("successfully saved item to db");
            console.log("successfully added " + myData.first_name + " to db");
        })
        .catch(function (error) {
            res.status(400).send("unable to save data");
            console.log("unable to save data");
        });
});

router.get("/", function (req, res) {
    console.log(req.headers["x-forwarded-for"] || req.connection.remoteAddress);
    res.sendFile(path.join(__dirname + "/../index.html"));
});

router.post("/teachers", function (req, res) {
    feeder.func(req.body, res);
});

router.get("/teacher", function (req, res) {
    res.sendFile(path.join(__dirname + "/../TestTeacherPage.html"));
});


module.exports = router;
