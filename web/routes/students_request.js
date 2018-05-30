var express = require('express');
var router = express.Router();
var User = require("../schemas/user");

router.get("/get-profile",function (req, res, next) {
    const id = req.query.id;

    User.findOne({_id: id}, function (err, user) {
        if (err) return next(err);
        if(user) {
            res.status(200).json(user);
        }
        else {
            res.status(404).json({message: 'user' + id + 'dose not exist sorry'});
        }
    });
});

router.post("/edit-profile",function (req, res, next) {
    const verified = req.verifiedEmail;
    const updatedUser = req.body.user || req.body;
    User.findOne({email: verified}, function (err, user) {
        if (err) return next(err);
        if(user) {
            user.first_name = updatedUser.first_name ? updatedUser.first_name : user.first_name;
            user.last_name = updatedUser.last_name ? updatedUser.last_name : user.last_name;
            user.display_name = updatedUser.display_name;// ? updatedUser.display_name : user.display_name;
            user.country = updatedUser.country ;//? updatedUser.country : user.country;
            user.address = updatedUser.address ;//? updatedUser.address : user.address;
            user.age = updatedUser.age ? updatedUser.age : user.age;
            user.gender = updatedUser.gender ? updatedUser.gender : user.gender;
            user.about_me = updatedUser.about_me ;//? updatedUser.about_me : user.about_me;

            user.last_modified = Date.now();
            user.save();
            return res.status(200).json(user);
        }
        return res.status(404).json({message: 'user' + verified + 'dose not exist sorry'});
    });
});

// router.get("/get-by-name", function (req, res, next) {
//     var fname = req.query.fname;
//     var lname = req.query.lname;
//
//     console.log("Got a search for " + fname + " " + lname);
//     var ip = req.headers["x-forwarded-for"] || req.connection.remoteAddress;
//     console.log("Got a " + req.method + " request from " + ip);
//
//
//     if (!lname || lname == "null")
//         Student.find({first_name: fname}, function (err, student) {
//             if (err) return next(err);
//             console.log("No last name");
//             console.log("Search result\n:" + student);
//
//             if (student instanceof Array) {
//                 student.isarray = true;
//             } else {
//                 student.isarray = false;
//             }
//
//             res.json(student);
//         });
//
//     else if(!lname || fname == "null")
//         Student.find({last_name: lname}, function (err, student) {
//             if (err) return next(err);
//             console.log("No first name");
//
//             if (student instanceof Array) {
//                 student.isarray = true;
//             } else {
//                 student.isarray = false;
//             }
//
//             res.json(student);
//         });
//     else {
//         Student.find({first_name: fname, last_name: lname}, function (err, student) {
//             if (err) return next(err);
//             console.log("Got first and last name");
//
//             if (student instanceof Array) {
//                 student.isarray = true;
//             } else {
//                 student.isarray = false;
//             }
//
//             res.json(student);
//         });
//     }
// });

module.exports = router;