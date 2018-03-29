var express = require('express');
var router = express.Router();
var path = require("path");
var mongoose = require("mongoose");

const auth = require('basic-auth');

var Session = require("../schemas/session");
var Student = require("../schemas/student");

router.post("/connect-session",function (req, res) {

    const id = req.body.internal_id;

    if (!id || !id.trim()) {

        res.status(500).json({message: 'Invalid Request !'});

    } else {

        Session.findOne({internal_id: id}, function (err, sess) {
            if (sess) {
                return res.status(200).json({message: 'Welcome to Class !'});
            }
            res.status(500).json({message: 'Session Not Found !'});

        });
    }
});

router.get("/get-students-count",function (req, res, next) {
    var id = req.query.id;

    Session.findOne({internal_id: id}, function (err, sess) {
        if (err) return next(err);

        res.status(200).json({message: sess.curr_rating});
    });
});

router.get("/change-val",function (req, res, next) { // Expect 0 or 1
    const id = req.query.id;
    const val = req.query.val;

    // TODO
    // Need to check what is the student's rating value!
    // We can't allow the user to change the rating more than one up or one down.

    const token = req.headers['x-access-token'];

    console.log("token = " + token);
    // console.log("cred.user = " + cred.user);
    Student.find({accessToken: token}, function(err, student) {
        if (err) next(err);

        console.log("The student is: " + JSON.stringify(student));

        Session.find({internal_id: id}, function(err, sess) {
            if (err) next(err);

            var studs = sess.students;
            for(var i = 0; i < studs.length; ++i) {
                if (studs[i].mail == student.mail && studs[i].rating_val != val) {
                    sess.students[i].rating_val = val;
                }
            }
        });
    });


    Session.findOne({internal_id: id}, function (err, sess) {
        if (err) return next(err);

        sess.curr_rating = (val == 1 ? (sess.curr_rating + 1) : (sess.curr_rating - 1));

        sess.save(function (err, updated_sess) {
            if (err) return next(err);
            console.log("Updates value successfully to " + updated_sess.curr_rating);
        });

        res.json(sess.curr_rating);
    });

});



router.post("/create-session", function (req, res) {
    var myData = new Session(req.body);
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
        res.send("successfully added session " + myData.name + " to db");
        console.log("successfully added session " + myData.name + " to db");
    });
});




module.exports = router;
