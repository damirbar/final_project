var express = require('express');
var router = express.Router();
var path = require("path");

var Teacher = require("../schemas/teacher");




router.post("/teacher", function (req, res) {
    var myData = new Teacher(req.body);
    myData.save(function (err) {
        if (err) {
            if (err.name === 'MongoError' && err.code === 11000) {
                // Duplicate username
                console.log('User ' + myData.first_name + " cannot be added " + myData.mail + ' already exists!');
                return res.status(500).send('User ' + myData.first_name + " cannot be added " + myData.mail + ' already exists!');
            }
            if (err.name === 'ValidationError') {
                //ValidationError
                for (field in err.errors) {
                    console.log("you must provide: " + field + "field");
                    return res.status(500).send("you must provide: " + field + "field");
                }
            }
            // Some other error
            console.log(err);
            return res.status(500).send(err);
        }
        res.send("successfully added " + myData.first_name + " to db");
        console.log("successfully added " + myData.first_name + " to db");
    });
});



router.get("/", function (req, res) {
    console.log(req.headers["x-forwarded-for"] || req.connection.remoteAddress);
    res.sendFile(path.join(__dirname + "/../index.html"));
});

router.get("/teacher", function (req, res) {
    res.sendFile(path.join(__dirname + "/../TestTeacherPage.html"));
});



module.exports = router;
