var express = require('express');
var router = express.Router();
var Session = require("../schemas/session");
var Session_Message = require("../schemas/session_message");
var Student = require("../schemas/student");
var User = require("../schemas/user");

router.post("/connect-session", function (req, res) {
    const decoded = req.verifiedEmail;
    const sid = req.body.sid;
    const name = req.body.name;

    Session.findOne({sid: sid}, function (err, sess) {
        if (err) throw err;
        if (sess) {
            let exists = false;
            sess.students.forEach(function (user) {
                if (user.email === decoded) {
                    exists = true;
                }
            });
                if (!exists) {
                    sess.students.push({
                        rating_val: 1,
                        email: decoded,
                        display_name: name,
                    });
                    const newArray = sess.students;
                    sess.update({students:newArray}).then(function (item) {
                        console.log("Saved " + decoded + " to session: " + sid);
                        res.status(200).json({message: 'Welcome to Class !', session: sess});
                    }).catch(function (err) {
                        console.log("Unable to save the session with the new student " + name);
                    });
                }
                else {
                    console.log('Welcome back to Class '+ decoded+'!');
                    res.status(200).json({message: 'Welcome back to Class !', session: sess});
                }
        }
        else {
            res.status(404).json({message: 'session' + sid + 'dose not exist sorry'});
        }
    });
});

router.get("/get-students-count", function (req, res, next) {
    const id = req.query.sid;
    Session.findOne({sid: id}, function (err, sess) {
        if (err) return next(err);
        res.status(200).json({message: sess.students.length});
    });
});


router.get("/get-students-rating", function (req, res, next) {
    const id = req.query.sid;
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
            let studs = sess.students;
            for (let i = 0; i < studs.length; ++i) {
                if (studs[i].email === student.email && studs[i].rating_val !== val) {
                    sess.students[i].rating_val = val;
                }
            }
        });
    });

    Session.findOne({sid: id}, function (err, sess) {
        if (err) return next(err);
        sess.curr_rating = (val === 1 ? (sess.curr_rating + 1) : (sess.curr_rating - 1));
        sess.save(function (err, updated_sess) {
            if (err) return next(err);
            console.log("Updates value successfully to " + updated_sess.curr_rating);
        });
        res.json(sess.curr_rating);
    });
});


router.post("/create-session", function (req, res) {
    const sess = new Session({
        sid: req.body.sid,
        name: req.body.name,
        admin: req.verifiedEmail,
        location: req.body.location,
        creation_date: Date.now()
    });


    sess.save(function (err) {
        if (err) {
            if (err.name === 'MongoError' && err.code === 11000) {
                // Duplicate username
                console.log('Session ' + sess.name + " cannot be added id" + sess.sid + ' already exists!');
                return res.status(200).json({message: 'Session ' + sess.name + " cannot be added id" + sess.sid + ' already exists!'});
            }
            if (err.name === 'ValidationError') {
                //ValidationError
                for (field in err.errors) {
                    console.log("you must provide: " + field + " field");
                    return res.status(200).json({message: "you must provide: " + field + " field"});
                }
            }
            // Some other error
            console.log(err);
            return res.status(200).send(err);
        }
        res.status(200).json({message: "successfully added session " + sess.name + " to db"});
        console.log("successfully added session " + sess.name + " to db");
    });
});


router.get("/rate-message", function (req, res) {

    const rating = Number(req.query.rating);
    const sess_id = req.query.sid;
    const mess_id = req.query.msgid;
    const decoded = req.verifiedEmail;

    // finds the user
    User.findOne({email: decoded}, function (err, user) {
        if (err) throw err;

        //finds the message and fetches its likers and dislikers arrays.
        Session_Message.findOne({_id: mess_id}, {_id: 0, likers: 1, dislikers: 1}, function (err, raters) {

            if (err) return err;
            let likers = raters.likers;
            let dislikers = raters.dislikers;
            let liked = likers.indexOf(user._id) > -1; // true if the user has liked the message
            let disliked = dislikers.indexOf(user._id) > -1;// true if the user has disliked the message
            let ratingUpdate = {};

            if (rating === 1) { // user likes the message
                if (liked) { // user has already liked the message
                    console.log('user has already liked this message. mess id:' + mess_id);
                    return res.status(200).json({message: 'user has already liked this message. mess id:' + mess_id});
                } else if (disliked) { // user has already disliked the message.
                    ratingUpdate.$push = {likers: user._id};
                    ratingUpdate.$pull = {dislikers: user._id}; //removes the user id from the dislikers array
                    ratingUpdate.$inc = {likes: 1, dislikes: -1};
                } else { // user is liking the message fo the first time
                    ratingUpdate.$push = {likers: user._id};
                    ratingUpdate.$inc = {likes: 1};
                }
            } else {// user likes the message
                if (disliked) {// user has already disliked the message
                    console.log('user has already disliked this message. mess id:' + mess_id);
                    return res.status(200).json({message: 'user has already disliked this message. mess id:' + mess_id});
                } else if (liked) { // user has already liked the message.
                    ratingUpdate.$push = {dislikers: user._id};
                    ratingUpdate.$pull = {likers: user._id};//removes the user id from the likers array
                    ratingUpdate.$inc = {likes: -1, dislikes: 1};
                } else {// user is disliking the message fo the first time
                    ratingUpdate.$push = {dislikers: user._id};
                    ratingUpdate.$inc = {dislikes: 1};
                }
            }
            // updates the message rating with the ratingUpdate object
            Session_Message.update({_id: mess_id}, ratingUpdate, function (err) {
                console.log('updating session message');
                if (err) {
                    console.log(err);
                    return err;
                }
            });
        });
    });
});

router.post("/messages", function (req, res) {
    const decoded = req.verifiedEmail;
    const msg = new Session_Message({
            sid: req.body.sid,
            type: req.body.type,
            body: req.body.body,
            email: decoded,
            date: Date.now()
        }
    );
    msg.save(function (err) {
        if (err) {
            console.log(err);
            return res.status(500).send(err);
        }
        Session.update({sid: msg.sid}, {$push: {messages: msg._id}}, function (err) {
            console.log('pushing message to messages');
            if (err) {
                return console.log(err);
            }
        });
        res.status(200).json({message: "successfully added message " + msg.body + " to db"});
        console.log("successfully added message " + msg.body + " to db");
    });
});


router.get("/get-all-messages", function (req, res) {
    var sess_id = Number(req.query.sid);
    Session.aggregate([
            {
                $lookup: {
                    from: "session_messages",
                    localField: "messages",
                    foreignField: "_id",
                    as: "messages_list"
                }
            }, {
                $match: {
                    "sid": sess_id
                }
            },
            {
                $project: {
                    "_id": 0,
                    "messages_list": 1
                }
            }
        ],
        function (err, list) {
            if (err) {
                console.log(err);
                return err;
            } else {
                return res.status(200).json(list[0].messages_list);
            }
        }
    );
});

//test this
router.get("/get-all-user-messages", function (req, res) {
    let messages = [];
    const decoded = req.verifiedEmail;
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return next(err);
        User.findOne({email: decoded}, function (err, user) {
            if (err) return err;
            for (let i = 0; i < sess.messages.length; ++i) {
                for (let j = 0; j < sess.messages[i].ratings.length; ++j) {
                    if (sess.messages[i].ratings[j].id == user.id) {
                        messages.push(sess.messages[i]);
                    }
                }
            }
            res.status(200).json({messages: messages});
        });
    });
});


router.get("/disconnect", function (req, res) {
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return next(err);
        let found = false;
        for (let i = 0; i < sess.students.length; ++i) {
            if (sess.students[i].email === req.verifiedEmail) {
                found = true;
                sess.students.splice(i, 1);
                sess.save().then(function () {
                    console.log("Disconnected " + req.verifiedEmail + " from session " + req.query.sid);
                    res.status(200).json({message: "Disconnected " + req.verifiedEmail + " from session " + req.query.sid})
                });
            }
        }
        if (!found) {
            res.status(404).json({message: "User " + req.verifiedEmail + " not found in session " + req.query.sid})
        }
    });
});


const multer = require('multer');
const upload = multer({dest: 'upload/'});
const type = upload.single('recfile');
const cloudinary = require('cloudinary');
const fs = require('fs');
cloudinary.config({
    cloud_name: 'wizeup',
    api_key: '472734726483424',
    api_secret: 'A2ZBcQsnU72oh7p9JI415BOR1ws'
});

let first = true;
router.post('/post-video', type, function (req, res) {
    if (!req.file) {
        res.status(400).json({message: 'no file'});
    }
    else {
        const path = req.file.path;
        if (first) {
            first = false;
            if (!req.file.originalname.match(/\.(mp4)$/)) {
                fs.unlinkSync(path);
                res.status(400).json({message: 'wrong file'});
            }
            else {
                res.status(200).json({message: 'received file'});
                console.log(path);
                Session.findOne({sid: req.body.sid}, function (err, sess) {
                    if (err) return next(err);
                    console.log("starting to upload " + req.file.originalname);
                    cloudinary.v2.uploader.upload(path,
                        {
                            resource_type: "video",
                            public_id: sess.sid + 'video',
                            eager: [
                                {
                                    width: 300, height: 300,
                                    crop: "pad", audio_codec: "none"
                                },
                                {
                                    width: 160, height: 100,
                                    crop: "crop", gravity: "south",
                                    audio_codec: "none"
                                }],
                            eager_async: true,
                            eager_notification_url: "http://mysite/notify_endpoint"
                        },
                        function (err, result) {
                            fs.unlinkSync(path);
                            if (err) return err;
                            console.log("uploaded " + req.file.originalname);
                            console.log(result);
                            sess.videoID = result.url;
                            sess.save();
                            first = true;
                        });
                });
            }
        }
        else {
            fs.unlinkSync(path);
        }
    }
});
//
// router.get('/get-video', function (req, res) {
//     Session.findOne({sid: req.query.sid}, function (err, sess) {
//         if (err) return err;
//         if (sess) {
//             res.status(200).json({url: sess.videoID});
//         }
//         else {
//             res.status(400).json({error: 'no such session'});
//         }
//     });
//
// });
module.exports = router;
