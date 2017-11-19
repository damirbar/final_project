var express = require('express');
var router = express.Router();
var path = require("path");
var mongoose = require("mongoose");

var Student = require("../schemas/student");
// var authCheck = function(req,res,next)
// {
//     if(!req.user)
//         res.send("ererer");
//     else
//         next();
// }


// router.post("/student", function (req, res) {
//     var myData = new Student(req.body);
//     myData.save(function (err) {
//         if (err) {
//             if (err.name === 'MongoError' && err.code === 11000) {
//                 // Duplicate username
//                 console.log('User ' + myData.first_name + " cannot be added " + myData.mail + ' already exists!');
//                 return res.status(500).send('User ' + myData.first_name + " cannot be added " + myData.mail + ' already exists!');
//             }
//             if (err.name === 'ValidationError') {
//                 //ValidationError
//                 var str = "";
//                 for (field in err.errors) {
//                     console.log("you must provide: " + field + " field");
//                     str += "you must provide: " + field + " field  ";
//                 }
//                 return res.status(500).send(str);
//             }
//             // Some other error
//             console.log(err);
//             return res.status(500).send(err);
//         }
//         res.send("successfully added " + myData.first_name + " to db");
//         console.log("successfully added " + myData.first_name + " to db");
//     });
// });

router.post("/new-student", function(req, res){

    // var first_name = req.query.first_name;
    // var last_name = req.query.last_name;
    // var mail = req.query.email;
    // var pass = req.query.pass;
    var first_name = req.body.fname;
    var last_name = req.body.lname;
    var mail = req.body.email;
    var pass = req.body.password;

    console.log("The query I got in new-student: " +
    first_name + ", " + last_name + ", " + mail
    + ", " + pass);

    // if (! ( first_name && last_name && mail && pass ) ) {
    //     console.log("Error posting to \"/new-student\"! Fields missing");
    //     return;
    // }


    var reg_student = new Student({
        first_name: first_name,
        last_name: last_name,
        mail: mail,
        password: pass
    });

    reg_student.save(function (err, student) {
        if (err) {
            if (err.name === 'MongoError' && err.code === 11000) {
                // Duplicate username
                console.log('User ' + reg_student.first_name + " cannot be added " + reg_student.mail + ' already exists!');
                return res.status(500).send('User ' + reg_student.first_name + " cannot be added " + reg_student.mail + ' already exists!');
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
            // console.log(err);
        }
        res.status(200).send({success: true, msg: "successfully added " + reg_student.first_name + " to db"});
        console.log("successfully added " + reg_student.first_name + " to db");
    });

    // res.send("some response");

});



router.get("/get-profile",function (req, res, next) {
    var id = req.query.id;

    Student.findOne({_id: id}, function (err, student) {
        if (err) return next(err);

        var ip = req.headers['x-forwarded-for'];
        // ip = ipaddr.process(ip + "");
        console.log("Got a " + req.method + " request from " + ip);
        res.json(student);
    });

});

router.get("/get-by-name", function (req, res, next) {
    var fname = req.query.fname;
    var lname = req.query.lname;

    console.log("Got a search for " + fname + " " + lname);
    var ip = req.headers["x-forwarded-for"] || req.connection.remoteAddress;
    console.log("Got a " + req.method + " request from " + ip);


    if (!lname || lname == "null")
        Student.find({first_name: fname}, function (err, student) {
            if (err) return next(err);
            console.log("No last name");
            console.log("Search result\n:" + student);

            if (student instanceof Array) {
                student.isarray = true;
            } else {
                student.isarray = false;
            }

            res.json(student);
        });

    else if(!lname || fname == "null")
        Student.find({last_name: lname}, function (err, student) {
            if (err) return next(err);
            console.log("No first name");

            if (student instanceof Array) {
                student.isarray = true;
            } else {
                student.isarray = false;
            }

            res.json(student);
        });
    else {
        Student.find({first_name: fname, last_name: lname}, function (err, student) {
            if (err) return next(err);
            console.log("Got first and last name");

            if (student instanceof Array) {
                student.isarray = true;
            } else {
                student.isarray = false;
            }

            res.json(student);
        });
    }
});

module.exports = router;