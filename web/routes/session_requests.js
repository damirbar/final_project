let express = require('express');
let router = express.Router();
let Session = require("../schemas/session");
let Session_Message = require("../schemas/session_message");
let Notification = require('../schemas/notification');
let User = require("../schemas/user");
let File = require("../schemas/file");
ObjectID = require('mongodb').ObjectID;

const notificationsSystem = require('../tools/notificationsSystem');

const socketIOEmitter = require('../tools/socketIO');

router.post("/connect-session", function (req, res) {
    const decoded = req.verifiedEmail;
    const sid = req.body.sid;
    const name = req.body.name;
    let index = 0;

    Session.findOne({sid: sid}, function (err, sess) {
        if (err) {
            console.log('session ' + sid + 'dose not exist sorry');
            res.status(404).json({message: 'session ' + sid + 'dose not exist sorry'});
        }
        else {
            if (sess) {
                let exists = false;
                sess.students.forEach(function (user, i) {
                    if (user.email === decoded) {
                        exists = true;
                        if (name !== user.display_name) {
                            sess.students.splice(i, 1);
                            sess.students.push({
                                rating_val: "1",
                                email: decoded,
                                display_name: name
                            });
                            const newArray = sess.students;
                            sess.update({students: newArray}).then(function () {
                                console.log(decoded + " logged in as " + name + " to session " + sess.name);
                                res.status(200).json(sess);
                            }).catch(function () {
                                console.log("Unable to save the session with the new student " + name);
                            });
                        }
                        else {
                            console.log('Welcome back to Class ' + decoded + '!');
                            res.status(200).json(sess);
                        }
                    }
                });
                if (!exists) {
                    sess.students.push({
                        rating_val: "1",
                        email: decoded,
                        display_name: name
                    });
                    const newArray = sess.students;
                    sess.update({students: newArray}).then(function (item) {
                        console.log("Saved " + decoded + " as " + name + " to session: " + sid);
                        res.status(200).json(sess);
                    }).catch(function (err) {
                        console.log("Unable to save the session with the new student " + name);
                    });
                }
            }
            else {
                res.status(404).json({message: 'session' + sid + 'dose not exist sorry'});
            }
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

    const val = req.query.val;
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) next(err);
        if (sess) {
            sess.students.forEach(function (student) {
                if (student.email === req.verifiedEmail) {
                    if (val === '1') {
                        if (!sess.likers.includes(student.email)) {
                            sess.likers.push(student.email);
                            sess.likes += 1;
                        }
                        if (sess.dislikers.includes(student.email)) {
                            sess.dislikers.forEach(function (stud, i) {
                                if (stud === student.email) sess.dislikers.splice(i, 1);
                            });
                            sess.dislikes -= 1;
                        }
                    }
                    else {
                        if (!sess.dislikers.includes(student.email)) {
                            sess.dislikers.push(student.email);
                            sess.dislikes += 1;
                        }
                        if (sess.likers.includes(student.email)) {
                            sess.likers.forEach(function (stud, i) {
                                if (stud === student.email) sess.likers.splice(i, 1);
                            });
                            sess.likes -= 1;
                        }
                    }
                    sess.save().then(function () {
                        console.log("Updates rating value successfully ");
                        res.json(sess);
                    });
                    // if (student.rating_val != val) {
                    //     student.rating_val = val;
                    //     let newarray = sess.students;
                    //     let rating = (val === "1" ? (sess.curr_rating + 1) : (sess.curr_rating - 1));
                    //     sess.update({students: newarray, curr_rating: rating}).then(function () {
                    //         if (err) return next(err);
                    //         console.log("Updates rating value successfully ");
                    //         res.json(sess);
                    //     });
                    // }
                    // else {
                    //     res.json(sess);
                    // }
                }
            });
        }
        else {
            res.status(404).json({message: "no such session"});
        }
    });
});


router.post("/create-session", function (req, res) {

    User.findOne({email: req.verifiedEmail}, function (err, user) {
        if (err) return err;
        if (user) {
            const sess = new Session({
                sid: req.body.sid,
                name: req.body.name,
                admin: req.verifiedEmail,
                teacher_fname: user.first_name,
                teacher_lname: user.last_name,
                location: req.body.location,
                endTime: req.body.finish
            });


            sess.save(function (err) {
                if (err) {
                    if (err.name === 'MongoError' && err.code === 11000) {
                        // Duplicate username
                        console.log('Session ' + sess.name + " cannot be added id " + sess.sid + ' already exists!');
                        return res.status(500).json({message: 'Session ' + sess.name + " cannot be added id " + sess.sid + ' already exists!'});
                    }
                    if (err.name === 'ValidationError') {
                        //ValidationError
                        for (field in err.errors) {
                            console.log("you must provide: " + field + " field");
                            return res.status(500).json({message: "you must provide: " + field + " field"});
                        }
                    }
                    // Some other error
                    console.log(err);
                    return res.status(500).send(err);
                }
                res.status(200).json(sess);
                console.log("successfully added session " + sess.name + " to db");
            });
        }
        else {
            return res.status(500).json({message: 'User ' + user.name + " cannot open a session "});
        }
    });
});


router.get("/rate-message", function (req, res) {

    const rating = Number(req.query.rating);
    const mess_id = req.query.msgid;
    const sess_id = req.query.sid;
    const decoded = req.verifiedEmail;
    let updateEvent = {
        message_id: mess_id,
        likes: 0,
        dislikes: 0
    };

    // finds the user
    User.findOne({email: decoded}, function (err, user) {
        if (err) throw err;

        //finds the message and fetches its likers and dislikers arrays.
        Session_Message.findOne({_id: mess_id}, {
            _id: 0,
            sid: 1,
            likers: 1,
            dislikers: 1,
            poster_id: 1,
            email: 1,
            reply: 1
        }, function (err, message) {

            if (err) return err;
            let likers = message.likers;
            let dislikers = message.dislikers;
            let liked = likers.indexOf(user._id) > -1; // true if the user has liked the message
            let disliked = dislikers.indexOf(user._id) > -1;// true if the user has disliked the message
            let ratingUpdate = {};

            if (rating === 1) { // user likes the message
                if (liked) { // user has already liked the message
                    ratingUpdate.$pull = {likers: user._id};//removes the user id from the likers array
                    ratingUpdate.$inc = {likes: -1};
                    updateEvent.likes = -1;
                    // console.log('user has already liked this message. mess id:' + mess_id);
                } else if (disliked) { // user has already disliked the message.

                    ratingUpdate.$push = {likers: user._id};
                    ratingUpdate.$pull = {dislikers: user._id}; //removes the user id from the dislikers array
                    ratingUpdate.$inc = {likes: 1, dislikes: -1};

                    updateEvent.likes = 1;
                    updateEvent.dislikes = -1;

                } else { // user is liking the message fo the first time
                    ratingUpdate.$push = {likers: user._id};
                    ratingUpdate.$inc = {likes: 1};
                    updateEvent.likes = 1;
                }

            } else {// user likes the message
                if (disliked) {// user has already disliked the message
                    ratingUpdate.$pull = {dislikers: user._id}; //removes the user id from the dislikers array
                    ratingUpdate.$inc = {dislikes: -1};
                    updateEvent.dislikes = -1;
                } else if (liked) { // user has already liked the message.
                    ratingUpdate.$push = {dislikers: user._id};
                    ratingUpdate.$pull = {likers: user._id};//removes the user id from the likers array
                    ratingUpdate.$inc = {likes: -1, dislikes: 1};
                    updateEvent.likes = -1;
                    updateEvent.dislikes = 1;
                } else {// user is disliking the message fo the first time
                    ratingUpdate.$push = {dislikers: user._id};
                    ratingUpdate.$inc = {dislikes: 1};
                    updateEvent.dislikes = 1;
                }
            }
            // updates the message rating with the ratingUpdate object
            Session_Message.update({_id: mess_id}, ratingUpdate, function (err) {
                if (err) console.log(err);
                else {
                    let eventName = message.reply ? 'updateMessageReplyRating' : 'updateMessageRating';
                    socketIOEmitter.emitEventToSessionRoom(sess_id, eventName, updateEvent);
                    let notification = new Notification({
                        receiver_id: message.poster_id,
                        sender_id: user._id,
                        action: rating,
                        subject: 'message',
                        subject_id: mess_id
                    });

                    notificationsSystem.saveAndEmitNotification(notification);
                }
            });

            if (err) {
                console.log(err);
                return err;
            }
        });
    });
});


router.get("/get-message-replies", function (req, res) {

    let message_id = req.query.mid;

    console.log(message_id);

    Session_Message.find({parent_id: message_id}, function (err, messages) {
        if (err) return console.log(err);
        console.log(messages);
        return res.status(200).json(messages);
    });
});

router.get("/rate-reply-message", function (req, res) {
    const rating = Number(req.query.rating);
    const mess_id = req.query.msgid;
    const decoded = req.verifiedEmail;
    let to = "";
    let id = new ObjectID(mess_id);
    // finds the user
    User.findOne({email: decoded}, function (err, user) {
        if (err) throw err;

        //finds the message and fetches its likers and dislikers arrays.
        Session_Message.findOne({'replies._id': id}, function (err, message) {
            message.replies.forEach(function (org, i) {
                if (org._id == mess_id) {
                    if (err) return err;
                    to = org.email;
                    let likers = org.likers;
                    let dislikers = org.dislikers;
                    let liked = likers.indexOf(user.id) > -1; // true if the user has liked the message
                    let disliked = dislikers.indexOf(user.id) > -1;// true if the user has disliked the message

                    let newMesssage = org;
                    message.replies.splice(i, 1);

                    if (rating === 1) { // user likes the message
                        if (liked) { // user has already liked the message
                            newMesssage.likers.forEach(function (liker, i) {
                                if (liker === user.id) newMesssage.likers.splice(i, 1);
                            });
                            newMesssage.likes -= 1;
                        } else if (disliked) {
                            newMesssage.likers.push(user.id);
                            newMesssage.dislikers.forEach(function (disliker, i) {
                                if (disliker === user.id) newMesssage.dislikers.splice(i, 1);
                            });
                            newMesssage.dislikes -= 1;
                            newMesssage.likes += 1;
                        } else {
                            newMesssage.likers.push(user.id);
                            newMesssage.likes += 1;
                        }
                    } else {
                        if (disliked) {
                            newMesssage.dislikers.forEach(function (disliker, i) {
                                if (disliker === user.id) newMesssage.dislikers.splice(i, 1);
                            });
                            newMesssage.dislikes -= 1;
                        } else if (liked) {
                            newMesssage.dislikers.push(user.id);
                            newMesssage.likers.forEach(function (liker, i) {
                                if (liker === user.id) newMesssage.likers.splice(i, 1);
                            });
                            newMesssage.likes -= 1;
                            newMesssage.dislikes += 1;
                        } else {
                            newMesssage.dislikers.push(user.id);
                            newMesssage.dislikes += 1;
                        }
                    }
                    let newArray = message.replies;
                    newArray.push(newMesssage);
                    message.update({replies: newArray}, function (err) {
                        console.log("done rating message");
                        console.log('updating session message');
                        if (err) {
                            console.log(err);
                            return err;
                        }
                    });
                }
            });
        });
    });
});

router.post("/messages", function (req, res) {

    const decoded = req.verifiedEmail;

    Session.findOne({sid: req.body.sid}, function (err, sess) {
        if (err) console.log(err);
        if (sess) {
            sess.students.forEach(function (stud) {
                if (stud.email === decoded) {
                    const msg = new Session_Message({
                            poster_id: req.body.poster_id,
                            sid: req.body.sid,
                            type: req.body.type,
                            body: req.body.body,
                            email: decoded,
                            nickname: stud.display_name,
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
                            } else {
                                socketIOEmitter.emitEventToSessionRoom(msg.sid, 'newSessionMessage', msg);
                            }
                        });
                        res.status(200).json({message: "successfully added message " + msg.body + " to db"});
                        console.log("successfully added message " + msg.body + " to db");
                    });
                }
            });
        }
    });
});


router.get("/get-all-messages", function (req, res) {
    let sess_id = req.query.sid;
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
                    "sid": sess_id,
                    "messages_list.reply": false
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
                let response = list.length > 0 ? list[0].messages_list : [];
                return res.status(200).json(response);
            }
        }
    );
});

//test this
router.get("/get-all-user-messages", function (req, res) {
    let messages = [];
    const decoded = req.verifiedEmail;
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return console.log(err);
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
const config = require('../config/config');
cloudinary.config({
    cloud_name: config.cloudniary.cloud_name,
    api_key: config.cloudniary.api_key,
    api_secret: config.cloudniary.api_secret
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
                console.log(path);
                Session.findOne({sid: req.query.sid}, function (err, sess) {
                    if (err) return next(err);
                    console.log("starting to upload " + req.file.originalname);
                    cloudinary.v2.uploader.upload(path,
                        {
                            resource_type: "video",
                            public_id: "sessionVideos/" + sess.sid + 'video',
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
                            User.findOne({email: req.verifiedEmail}, function (err, user) {
                                if (err) return err;
                                if (user) {
                                    const ans = new File({
                                        originalName: req.file.originalname,
                                        uploaderid: user.id,
                                        url: result.url,
                                        type: result.format,
                                        size: result.bytes,
                                        hidden: false
                                    });
                                    ans.save(function (err, updated_file) {
                                        if (err) return (err);
                                        sess.update({video_file_id: updated_file.id}).then(function () {
                                            console.log("added file to collection");
                                        });
                                    });
                                }
                            });
                            sess.update({videoUrl: result.url}).then(function (err, updated_file) {
                                console.log("updated session video");
                            });
                            first = true;
                            res.status(200).json({message: 'received file'});
                        });
                });
            }
        }
        else {
            fs.unlinkSync(path);
        }
    }
});


router.post("/reply", function (req, res) {

    const decoded = req.verifiedEmail;

    let notified_id = req.body.poster_id; //The user to be notified
    let replier_id = req.body.replier_id; // The user who replied
    let subject_id = req.body.mid;
    Session.findOne({sid: req.body.sid}, function (err, sess) {
        if (err) console.log(err);
        if (sess) {
            sess.students.forEach(function (stud) {
                if (stud.email === decoded) {
                    let newReply = new Session_Message({
                        poster_id: req.body.poster_id,
                        parent_id: req.body.mid,
                        sid: req.body.sid,
                        type: req.body.type,
                        reply: true,
                        email: decoded,
                        nickname: stud.display_name,
                        body: req.body.body,
                        date: Date.now()
                    });
                    newReply.save(function (err, reply) {

                        if (err) return console.log(err);

                        Session_Message.update({_id: reply.parent_id}, {
                            $push: {replies: reply._id},
                            $inc: {num_of_replies: 1}
                        }, function (err) {
                            if (err) return console.log(err);

                            socketIOEmitter.emitEventToSessionRoom(reply.sid, 'newSessionMessageReply', reply);

                            let notification = new Notification({
                                receiver_id: notified_id,
                                sender_id: replier_id, // the user who replied
                                action: 4,
                                subject: 'message',
                                subject_id: subject_id
                            });
                            notificationsSystem.saveAndEmitNotification(notification);
                        });
                    });

                }
            });
        }
    });
});


router.get("/get-message", function (req, res) {

    let msg_id = req.query.mid;

    Session_Message.findOne({_id: msg_id}, function (err, msg) {
        if (err) return console.log(err);

        res.status(200).json(msg);
    });
});

router.get("/get-session", function (req, res) {
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return err;
        if (sess) res.status(200).json(sess);
        else res.status(404).json({message: "no such session"});
    })
});

module.exports = router;
