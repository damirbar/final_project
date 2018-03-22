var express = require('express');
var router = express.Router();
var path = require("path");
var mongoose = require("mongoose");

var Session = require("../schemas/session");


router.get("/get-students-count",function (req, res, next) {
    var id = req.query.id;

    Session.findOne({_id: id}, function (err, sess) {
        if (err) return next(err);

        res.json(sess.students.length);
    });

});

router.get("/change-val",function (req, res, next) { // Expect 0 or 1
    const id = req.query.id;
    const val = req.query.val;

    // TODO
    // Need to check what is the student's rating value!
    // We can't allow the user to change the rating more than one up or one down.

    Session.findOne({_id: id}, function (err, sess) {
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
