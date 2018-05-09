var express = require('express');
var router = express.Router();
var path = require("path");
var mongoose = require("mongoose");
var fs = require('fs');
const jwt = require('jsonwebtoken');

const auth = require('basic-auth');

var Session = require("../schemas/session");
var Session_Message = require("../schemas/session_message");
var Student = require("../schemas/student");
var User = require("../schemas/user");

router.post("/connect-session", function (req, res) {

    var id = req.body.sid;

    Session.findOne({sid: id}, function (err, sess) {
        if (err) throw err;

        if (sess) {

            User.findOne({email: decoded}, function (err, user) {
                if (err) throw err;

                var exists = false;
                for (var i = 0; i < sess.students.length; ++i) {
                    if (sess.students[i].id_num == user.id) {
                        exists = true;
                        // res.status(409).json({message: 'Conflict!'});
                        break;
                    }

                }

                if (!exists) {
                    sess.students.push({
                        rating_val: 1,
                        email: user.email,
                        display_name: user.display_name,
                        id_num: user._id
                    });
                    sess.save()
                        .then(function (item) {
                            console.log("Saved a the session with the new student " + user.display_name);
                            return res.status(200).json({message: 'Welcome to Class !', session: sess});
                        })
                        .catch(function (err) {
                            console.log("Unable to save the session with the new student " + user.display_name);
                        });
                }
                else {
                    return res.status(200).json({message: 'Welcome back to Class !', session: sess});
                }

            });


        }
        return res.status(404).json({message: 'session' +id + 'dose not exist sorry'});
    });
        // console.log("The token is: " + token);
});

router.get("/get-students-count", function (req, res, next) {
    var id = req.query.id;

    Session.findOne({sid: id}, function (err, sess) {
        if (err) return next(err);

        res.status(200).json({message: sess.students.length});
    });
});


router.get("/get-students-rating", function (req, res, next) {
    var id = req.query.id;

    Session.findOne({sid: id}, function (err, sess) {
        if (err) return next(err);

        res.status(200).json({message: sess.curr_rating});
    });
});

router.get("/change-val", function (req, res, next) { // Expect 0 or 1
    const id = req.query.id;
    const val = req.query.val;

    // TODO
    // Need to check what is the student's rating value!
    // We can't allow the user to change the rating more than one up or one down.

    const token = req.headers['x-access-token'];

    console.log("token = " + token);
    // console.log("cred.user = " + cred.user);
    Student.find({accessToken: token}, function (err, student) {
        if (err) next(err);

        console.log("The student is: " + JSON.stringify(student));

        Session.find({sid: id}, function (err, sess) {
            if (err) next(err);

            var studs = sess.students;
            for (var i = 0; i < studs.length; ++i) {
                if (studs[i].email == student.email && studs[i].rating_val != val) {
                    sess.students[i].rating_val = val;
                }
            }
        });
    });


    Session.findOne({sid: id}, function (err, sess) {
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
                console.log('User ' + myData.first_name + " cannot be added " + myData.email + ' already exists!');
                return res.status(500).send('User ' + myData.first_name + " cannot be added " + myData.email + ' already exists!');
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

// TODO -> This is currently not updating the database
router.get("/rate-message", function (req, res) {
    var sess_id = req.query.sid;
    var mess_id = req.query.msgid;
    var rating = req.query.rating;

    Session.findOne({sid: sess_id}, function (err, sess) {
        if (err) return next(err);

        var found = false;
        for (var i = 0; i < sess.messages.length; ++i) {
            if (sess.messages[i]._id == mess_id) {
                found = true;
                if (rating == 1) {
                    console.log("INCREMENTING THE MESSAGE " + sess.messages[i].body[1] + " to " + parseInt(sess.messages[i].rating) + 1);
                    sess.messages[i].rating = parseInt(sess.messages[i].rating) + 1;
                    console.log("CURRENT RATING = " + sess.messages[i].rating);

                } else {
                    console.log("DECREMENTING THE MESSAGE " + sess.messages[i].body[1] + " to " + parseInt(sess.messages[i].rating) - 1);
                    sess.messages[i].rating = parseInt(sess.messages[i].rating) - 1;
                    console.log("CURRENT RATING = " + sess.messages[i].rating);
                }
                break;
            }
        }
        if (!found) {
            res.status(404).json({message: "Message not found within session " + sess_id});
        } else {
            sess.markModified('object');
            sess.save(function (err, updated_sess) {
                if (err) return next(err);
                res.status(200).json({message: "Updated message rating successfully"});
            });
        }

    });
});

//erans work receiving messages (Q/A) in session
router.post("/messages", function (req, res) {

    var token = req.headers["x-access-token"];

    if (!token) {
        res.status(401).json({message: 'Unauthorized!'});
    }
    else {
        var decoded = jwt.verify(token, "Wizer", function (err, decoded) {
            if (err) {
                return res.json({success: false, message: 'Failed to authenticate token.'});
            } else {
                var msg = new Session_Message({
                        sid: req.body.sid,
                        type: req.body.type,
                        body: req.body.body,
                        email: decoded
                    }
                );


                Session.findOne({sid: msg.sid}, function (err, sess) {
                    if (err) return next(err);

                    sess.messages.push(msg);
                    sess.save(function (err, updated_sess) {
                        if (err) return next(err);
                        console.log("Updates value successfully to " + updated_sess.curr_rating);
                    });

                });

                msg.save(function (err) {
                    if (err) {
                        console.log(err);
                        return res.status(500).send(err);
                    }
                    res.status(200).json({message: "successfully added message " + msg.body + " to db"});
                    console.log("successfully added message " + msg.body + " to db");
                });
            }
        });
    }
});


router.get("/get-all-messages", function (req, res) {
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return next(err);
        res.status(200).json({messages: sess.messages});
    });
});


router.get("/disconnect", function (req, res) {
    console.log("\t\t\n\n\t " + req.verifiedEmail);
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return next(err);
        var found = false;
        for (var i = 0; i < sess.students.length; ++i) {
            if (sess.students[i].email === req.verifiedEmail) {
                found = true;
                sess.students.splice(i, 1);
                sess.save();
                res.status(200).json({message: "Disconnected " + req.verifiedEmail + " from session " + req.query.sid})
            }
        }
        if (!found) {
            res.status(404).json({message: "User " + req.verifiedEmail + " not found in session " + req.query.sid})
        }
    });
});


router.get('/postVideo', function (req, res) {

    // good
    // var mongoDB = 'mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin';
    // var mongo = require('mongodb');
    // var Grid = require('gridfs');
    //
    // mongo.MongoClient.connect(mongoDB, function (err, db) {
    //     var gfs = Grid(db, mongo);
    //     var source = '/home/eran/projects/WebstormProjects/final_project/web/routes/good.mp4';
    //     Session.findOne({sid: req.query.sid}, function (err, sess) {
    //         if (err) return next(err);
    //         console.log("starting to upload file "+ source);
    //         gfs.fromFile({filename: 'session:' +req.query.sid+' video.mp4'}, source, function (err, file) {
    //             console.log('saved %s to GridFS file %s', source, file._id);
    //             sess.videoID = file._id;
    //             sess.save();
    //         });
    //     });
    // });
    //    good

    var ObjectID = require('mongodb').ObjectID,
        GridStore = require('mongodb').GridStore,
        assert = require('assert');

    var mongoDB = 'mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin';
    var mongo = require('mongodb');

    mongo.MongoClient.connect(mongoDB, function (err, db) {
        // Our file ID
        var fileId = new ObjectID();

        // Open a new file
        var gridStore = new GridStore(db, fileId, 'session:' +req.query.sid+' video.mp4', 'w');
        var source = '/home/eran/projects/WebstormProjects/final_project/web/routes/good.mp4';

        // Open the new file
        gridStore.open(function (err, gridStore) {

            // Write the file to gridFS
            console.log("starting to upload file " + source + " as: " + fileId);
            gridStore.writeFile(source, function (err, doc) {
                Session.findOne({sid: req.query.sid}, function (err, sess) {
                    if (err) return next(err);
                    console.log('saved %s to GridFS file %s', source, fileId);
                    sess.videoID = fileId;
                    sess.save();
                });
            });
        });
    });
});


router.get('/getVideo', function (req, res) {

    var GridStore = require('mongodb').GridStore;
    var mongoDB = 'mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin';
    var db = mongoose.connection;

    var ObjectID = require('mongodb').ObjectID;
    db.options = 'PRIMARY';
    console.log('GET request');
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return next(err);
        new GridStore(db, new ObjectID(sess.videoID), 'r').open(function (err, GridFile) {
            if (!GridFile) {
                console.log("video" + " not found!!!");
                res.status(404).json({message: "video" + " not found"})
            }
            else {
                console.log("starting to stream file " + GridFile.filename);
                StreamGridFile(req, res, GridFile)
            }
        });
    });

});

function StreamGridFile(req, res, GridFile) {
    if (req.headers['range']) {
        // Range request, partialle stream the file
        console.log('Range Reuqest');
        var parts = req.headers['range'].replace(/bytes=/, "").split("-");
        var partialstart = parts[0];
        var partialend = parts[1];
        var start = parseInt(partialstart, 10);
        var end = partialend ? parseInt(partialend, 10) : GridFile.length -1;
        var chunksize = (end - start) + 1;
        console.log('Range ', start, '-', end);
        res.writeHead(206, {
            'Content-Range': 'bytes ' + start + '-' + end + '/' + GridFile.length,
            'Accept-Ranges': 'bytes',
            'Content-Length': chunksize,
            'Content-Type': GridFile.contentType
        });
        // Set filepointer
        GridFile.seek(start, function () {
            // get GridFile stream
            var stream = GridFile.stream(true);
            // write to response
            stream.on('data', function (buff) {
                // count data to abort streaming if range-end is reached
                // perhaps theres a better way?
                start += buff.length-1;
                if (start >= end) {
                    // enough data send, abort
                    GridFile.close();
                    res.end();
                } else {
                    res.write(buff);
                }
            });
        });
    } else {
        // stream back whole file
        console.log('No Range Request');
        res.header('Content-Type', GridFile.contentType);
        res.header('Content-Length', GridFile.length);
        var stream = GridFile.stream(true);
        stream.pipe(res);
    }
}


module.exports = router;
