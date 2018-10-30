let express = require('express');
let router = express.Router();
let Session = require("../schemas/session");
let Session_Message = require("../schemas/session_message");
let Notification = require('../schemas/notification');
let User = require("../schemas/user");
ObjectID = require('mongodb').ObjectID;

const notificationsSystem = require('../tools/notificationsSystem');

const socketIOEmitter = require('../tools/socketIO');

router.post("/connect-session", function (req, res) {
    const decoded = req.verifiedEmail;
    const sid = req.body.sid;
    const name = req.body.name;
    Session.findOne({sid: sid}, function (err, sess) {
        if (err) {
            console.log('session ' + sid + 'dose not exist sorry');
            res.status(500).json({message: err});
        }
        else {
            if (sess) {
                if (sess.students.includes(decoded)) {
                    console.log('Welcome back to Class ' + decoded + '!');
                    res.status(200).json(sess);
                }
                else {
                    sess.update({$push: {students: decoded}}, function (err) {
                        if (err) {
                            console.log(err);
                            res.status(500).json({message: err});
                        }
                        else {
                            console.log("Saved " + decoded + " as " + name + " to session: " + sid);
                            res.status(200).json(sess);
                        }
                    });
                }
            }
            else {
                res.status(404).json({message: 'session ' + sid + ' dose not exist sorry'});
            }
        }
    });
});

router.get("/get-students-count", function (req, res) {
    const id = req.query.sid;
    Session.findOne({sid: id}, function (err, sess) {
        if (err) {
            console.log('session ' + sid + 'dose not exist sorry');
            res.status(500).json({message: err});
        }
        else {
            if(sess) {
                res.status(200).json({message: sess.students.length});
            }
            else{
                res.status(404).json({message: 'no such session ' + id});
            }
        }
    });
});


router.get("/get-students-rating", function (req, res) {
    const id = req.query.sid;
    Session.findOne({sid: id}, function (err, sess) {
        if (err) {
            console.log('session ' + sid + 'dose not exist sorry');
            res.status(500).json({message: err});
        }
        else {
            if(sess) {
                res.status(200).json({message: sess.curr_rating});
            }
            else{
                res.status(404).json({message: 'no such session ' + id});
            }
        }
    });
});


router.get("/change-val", function (req, res) { // Expect 0 or 1

    let decoded =  req.verifiedEmail;

    const val = req.query.val;
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) {
            console.log('session ' + sid + 'dose not exist sorry');
            res.status(500).json({message: err});
        }
        else {
            if (sess) {
                let likers = sess.likers;
                let dislikers = sess.dislikers;
                let liked = likers.indexOf(decoded) > -1; // true if the user has liked the message
                let disliked = dislikers.indexOf(decoded) > -1;// true if the user has disliked the message
                let ratingUpdate = {};
                let updateEvent = {likes: 0, dislikes: 0};

                if (val == 1) { // user likes the message
                    if (liked) { // user has already liked the message
                        ratingUpdate.$pull = {likers: decoded};//removes the user id from the likers array
                        ratingUpdate.$inc = {likes: -1};
                        updateEvent.likes = -1;
                        // console.log('user has already liked this message. mess id:' + mess_id);
                    } else if (disliked) { // user has already disliked the message.

                        ratingUpdate.$push = {likers: decoded};
                        ratingUpdate.$pull = {dislikers: decoded}; //removes the user id from the dislikers array
                        ratingUpdate.$inc = {likes: 1, dislikes: -1};

                        updateEvent.likes = 1;
                        updateEvent.dislikes = -1;

                    } else { // user is liking the message fo the first time
                        ratingUpdate.$push = {likers: decoded};
                        ratingUpdate.$inc = {likes: 1};
                        updateEvent.likes = 1;
                    }

                } else {// user likes the message
                    if (disliked) {// user has already disliked the message
                        ratingUpdate.$pull = {dislikers: decoded}; //removes the user id from the dislikers array
                        ratingUpdate.$inc = {dislikes: -1};
                        updateEvent.dislikes = -1;
                    } else if (liked) { // user has already liked the message.
                        ratingUpdate.$push = {dislikers: decoded};
                        ratingUpdate.$pull = {likers: decoded};//removes the user id from the likers array
                        ratingUpdate.$inc = {likes: -1, dislikes: 1};
                        updateEvent.likes = -1;
                        updateEvent.dislikes = 1;
                    } else {// user is disliking the message fo the first time
                        ratingUpdate.$push = {dislikers: decoded};
                        ratingUpdate.$inc = {dislikes: 1};
                        updateEvent.dislikes = 1;
                    }
                }
                sess.update(ratingUpdate, function(err){
                    if(err){
                        console.log(err);
                        res.status(500).json({message: err});
                    }else{
                        socketIOEmitter.emitEventToSessionRoom(sess.sid, 'updateSessionRating', updateEvent);
                        res.status(200).json(sess);
                    }
                });
            }
            else {
                res.status(404).json({message: "no such session " + req.query.sid});
            }
        }
    });
});


router.post("/create-session", function (req, res) {
    User.findOne({email: req.verifiedEmail}, function (err, user) {
        if(err){
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (user) {
                const sess = new Session({
                    sid: req.body.sid,
                    name: req.body.name,
                    admin: req.verifiedEmail,
                    teacher_fname: user.first_name,
                    teacher_lname: user.last_name,
                    location: req.body.location,
                    endTime: req.body.finish,
                    students:[user.email]
                });

                if (err) {
                    if (err.name === 'MongoError' && err.code === 11000) {
                        console.log('Session ' + sess.name + " cannot be added id " + sess.sid + ' already exists!');
                        return res.status(500).json({message: 'Session ' + sess.name + " cannot be added id " + sess.sid + ' already exists!'});
                    }
                    else if (err.name === 'ValidationError') {
                        let str = "";
                        for (field in err.errors) {
                            console.log("you must provide: " + field + " field");
                            str += "you must provide: " + field + " field  ";
                        }
                        return res.status(500).json({message: str});
                    }

                else {
                        console.log(err);
                        return res.status(500).json({message: err});

                    }
                }
                else {
                    res.status(200).json(sess);
                    console.log("successfully added session " + sess.name + " to db");
                }
                sess.save(function (err) {
                });
            }
            else {
                return res.status(404).json({message: 'no such user ' + req.verifiedEmail});
            }
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

    User.findOne({email: decoded}, function (err, user) {
        if(err){
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if(user) {
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
                    if(err){
                        console.log(err);
                        res.status(500).json({message: err});
                    }
                    else {
                        if (message) {
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
                                if(err){
                                    console.log(err);
                                    res.status(500).json({message: err});
                                }
                                else {
                                    res.status(200).json({message: 'rated message successfully'});

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
                        }
                        else{
                            return res.status(404).json({message: 'no such message ' + mess_id});
                        }
                    }
                });
            }
            else{
                return res.status(404).json({message: 'no such user ' + decoded});
            }
        }
    });
});


router.get("/get-message-replies", function (req, res) {
    let message_id = req.query.mid;
    Session_Message.find({parent_id: message_id}, function (err, messages) {
        if(err){
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if(messages) {
                return res.status(200).json(messages);
            }
            else{
                return res.status(404).json({message: 'no such message '+ message_id});
            }
        }
    });
});

///DEPRECATED
// router.get("/rate-reply-message", function (req, res) {
//     const rating = Number(req.query.rating);
//     const mess_id = req.query.msgid;
//     const decoded = req.verifiedEmail;
//     let to = "";
//     let id = new ObjectID(mess_id);
//     // finds the user
//     User.findOne({email: decoded}, function (err, user) {
//         if (err) {
//             console.log(err);
//             next(err);
//         }
//         if (user) {
//             //finds the message and fetches its likers and dislikers arrays.
//             Session_Message.findOne({'replies._id': id}, function (err, message) {
//                 if (err) {
//                     console.log(err);
//                     next(err);
//                 }
//                 if (message) {
//                     message.replies.forEach(function (org, i) {
//                         if (org._id == mess_id) {
//                             if (err) return err;
//                             to = org.email;
//                             let likers = org.likers;
//                             let dislikers = org.dislikers;
//                             let liked = likers.indexOf(user.id) > -1; // true if the user has liked the message
//                             let disliked = dislikers.indexOf(user.id) > -1;// true if the user has disliked the message
//
//                             let newMesssage = org;
//                             message.replies.splice(i, 1);
//
//                             if (rating === 1) { // user likes the message
//                                 if (liked) { // user has already liked the message
//                                     newMesssage.likers.forEach(function (liker, i) {
//                                         if (liker === user.id) newMesssage.likers.splice(i, 1);
//                                     });
//                                     newMesssage.likes -= 1;
//                                 } else if (disliked) {
//                                     newMesssage.likers.push(user.id);
//                                     newMesssage.dislikers.forEach(function (disliker, i) {
//                                         if (disliker === user.id) newMesssage.dislikers.splice(i, 1);
//                                     });
//                                     newMesssage.dislikes -= 1;
//                                     newMesssage.likes += 1;
//                                 } else {
//                                     newMesssage.likers.push(user.id);
//                                     newMesssage.likes += 1;
//                                 }
//                             } else {
//                                 if (disliked) {
//                                     newMesssage.dislikers.forEach(function (disliker, i) {
//                                         if (disliker === user.id) newMesssage.dislikers.splice(i, 1);
//                                     });
//                                     newMesssage.dislikes -= 1;
//                                 } else if (liked) {
//                                     newMesssage.dislikers.push(user.id);
//                                     newMesssage.likers.forEach(function (liker, i) {
//                                         if (liker === user.id) newMesssage.likers.splice(i, 1);
//                                     });
//                                     newMesssage.likes -= 1;
//                                     newMesssage.dislikes += 1;
//                                 } else {
//                                     newMesssage.dislikers.push(user.id);
//                                     newMesssage.dislikes += 1;
//                                 }
//                             }
//                             let newArray = message.replies;
//                             newArray.push(newMesssage);
//                             message.update({replies: newArray}, function (err) {
//                                 console.log("done rating message");
//                                 console.log('updating session message');
//                                 if (err) {
//                                     console.log(err);
//                                     return err;
//                                 }
//                             });
//                         }
//                     });
//                 }
//                 else {
//                     res.status(404).json({message: "no message found"});
//                 }
//             });
//         }
//         else {
//             res.status(404).json({message: "no user found"});
//         }
//     });
// });

router.post("/messages", function (req, res) {
    const decoded = req.verifiedEmail;
    Session.findOne({sid: req.body.sid}, function (err, sess) {
        if(err){
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (sess) {
                const msg = new Session_Message({
                    poster_id: req.body.poster_id,
                    sid: req.body.sid,
                    type: req.body.type,
                    body: req.body.body,
                    email: decoded,
                    nickname: sess.admin === decoded ? sess.teacher_fname + " " + sess.teacher_lname : req.body.nickname,
                    date: Date.now()
                });
                msg.save(function (err) {
                    if(err){
                        console.log(err);
                        res.status(500).json({message: err});
                    }
                    else {
                        Session.update({sid: msg.sid}, {$push: {messages: msg._id}}, function (err) {
                            if(err){
                                console.log(err);
                                res.status(500).json({message: err});
                            }
                            else {
                                res.status(200).json({message: "successfully added message " + msg.body + " to db"});
                                console.log("successfully added message " + msg.body + " to db");
                                socketIOEmitter.emitEventToSessionRoom(msg.sid, 'newSessionMessage', msg);
                            }
                        });
                    }
                });
            }
            else {
                res.status(404).json({message: "no session " + req.body.sid});
            }
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
            if(err){
                console.log(err);
                res.status(500).json({message: err});
            }
            else {
                let response = list.length > 0 ? list[0].messages_list : [];
                return res.status(200).json(response);
            }
        }
    );
});

//test this
// router.get("/get-all-user-messages", function (req, res) {
//     let messages = [];
//     const decoded = req.verifiedEmail;
//     Session.findOne({sid: req.query.sid}, function (err, sess) {
//         if (err) return console.log(err);
//         User.findOne({email: decoded}, function (err, user) {
//             if (err) return err;
//             for (let i = 0; i < sess.messages.length; ++i) {
//                 for (let j = 0; j < sess.messages[i].ratings.length; ++j) {
//                     if (sess.messages[i].ratings[j].id == user.id) {
//                         messages.push(sess.messages[i]);
//                     }
//                 }
//             }
//             res.status(200).json({messages: messages});
//         });
//     });
// });


router.get("/disconnect", function (req, res) {
    let decoded = req.verifiedEmail;
    let sid = req.query.sid;
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (sess) {
                sess.update({$pull: {students: decoded}}, function (err) {
                    if (err) {
                        console.log(err);
                        res.status(500).json({message: err});
                    }
                    else {
                        if (sess.likers.includes(decoded)) {
                            sess.update({$pull: {likers: decoded}, $inc: {likes: -1}}, function (err) {
                                if (err) {
                                    console.log(err);
                                    res.status(500).json({message: err});
                                }
                                else{
                                    res.status(200).json({message: 'disconnected user '+ decoded});
                                }
                            });
                        }
                        else if (sess.dislikers.includes(decoded)) {
                            sess.update({$pull: {dislikers: decoded}, $inc: {dislikes: -1}}, function (err) {
                                if (err) {
                                    console.log(err);
                                    res.status(500).json({message: err});
                                }
                                else{
                                    res.status(200).json({message: 'disconnected user '+ decoded});
                                }
                            });
                        }
                        else{
                            res.status(200).json({message: 'disconnected user '+ decoded});
                        }
                    }
                });
            }
            else {
                res.status(404).json({message: 'no session ' + req.query.sid});
            }
        }
    });
});


const multer = require('multer');
const upload = multer({dest: 'upload/'});
const type = upload.single('recfile');
let uploader = require('../tools/uploader');



router.post('/post-video', type, function (req, res) {
    if (!req.file) {
        res.status(400).json({message: 'no file'});
    }
    else {
        const path = req.file.path;
        if (!req.file.originalname.match(/\.(mp4)$/)) {
            fs.unlinkSync(path);
            res.status(400).json({message: 'wrong file'});
        }
        else {
            Session.findOne({sid: req.query.sid}, function (err, sess) {
                if (err) {
                    console.log(err);
                    fs.unlinkSync(path);
                    res.status(500).json({message: err});
                }
                else {
                    if (sess) {
                        User.findOne({email: req.verifiedEmail}, function (err, user) {
                            if (err) {
                                console.log(err);
                                fs.unlinkSync(path);
                                res.status(500).json({message: err});
                            }
                            else {
                                if (user) {
                                    res.status(200).json({message: 'received file'});
                                    uploader.uploadVideo(req.file, path, sess, user._id);
                                }
                                else{
                                    fs.unlinkSync(path);
                                    res.status(404).json({message: 'no such user ' + req.verifiedEmail});
                                }
                            }
                        });
                    }
                    else {
                        fs.unlinkSync(path);
                        res.status(404).json({message: 'no such session ' + req.query.sid});
                    }
                }
            });
        }
    }
});


router.post("/reply", function (req, res) {
    const decoded = req.verifiedEmail;
    let notified_id = req.body.poster_id; //The user to be notified
    let replier_id = req.body.replier_id; // The user who replied
    let subject_id = req.body.mid;
    Session.findOne({sid: req.body.sid}, function (err, sess) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (sess) {
                let newReply = new Session_Message({
                    poster_id: req.body.poster_id,
                    parent_id: req.body.mid,
                    sid: req.body.sid,
                    type: req.body.type,
                    reply: true,
                    email: decoded,
                    nickname: sess.admin === decoded ? sess.teacher_fname + " " + sess.teacher_lname : req.body.nickname,
                    body: req.body.body,
                    date: Date.now()
                });
                newReply.save(function (err, reply) {
                    if (err) return console.log(err);
                    Session_Message.update({_id: reply.parent_id}, {
                        $push: {replies: reply._id},
                        $inc: {num_of_replies: 1}
                    }, function (err) {
                        if (err) {
                            console.log(err);
                            res.status(500).json({message: err});
                        }
                        else {
                            res.status(200).json({message: "added reply"});
                            socketIOEmitter.emitEventToSessionRoom(reply.sid, 'newSessionMessageReply', reply);

                            let notification = new Notification({
                                receiver_id: notified_id,
                                sender_id: replier_id, // the user who replied
                                action: 4,
                                subject: 'message',
                                subject_id: subject_id
                            });
                            notificationsSystem.saveAndEmitNotification(notification);
                        }
                    });
                });
            }
            else{
                res.status(404).json({message: 'no such session ' + req.body.sidd});
            }
        }
    });
});


router.get("/get-message", function (req, res) {
    let msg_id = req.query.mid;
    Session_Message.findOne({_id: msg_id}, function (err, msg) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if(msg){
                res.status(200).json(msg);
            }
            else{
                res.status(404).json({message: 'no such message '+ msg_id});
            }
        }
    });
});

router.get("/get-session", function (req, res) {
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (sess){
                res.status(200).json(sess);
            }
            else {
                res.status(404).json({message: "no such session " + req.query.sid});
            }
        }
    })
});

module.exports = router;