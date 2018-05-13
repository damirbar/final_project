var express = require('express');
var router = express.Router();
var mongoose = require("mongoose");

var Session = require("../schemas/session");
var Session_Message = require("../schemas/session_message");
var Student = require("../schemas/student");
var User = require("../schemas/user");

router.post("/connect-session", function (req, res) {
    const decoded = req.verifiedEmail;
    const id = req.body.sid;
    const name = req.body.name;

    Session.findOne({sid: id}, function (err, sess) {
        if (err) throw err;
        if (sess) {
            User.findOne({email: decoded}, function (err, user) {
                if (err) throw err;
                let exists = false;
                for (let i = 0; i < sess.students.length; ++i) {
                    if (sess.students[i].id_num == user.id) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    sess.students.push({
                        rating_val: 1,
                        email: user.email,
                        display_name: name,
                        id_num: user.id
                    });
                    sess.save()
                        .then(function (item) {
                            console.log("Saved " + user.email + " to session: " + id);
                            res.status(200).json({message: 'Welcome to Class !', session: sess});
                        })
                        .catch(function (err) {
                            console.log("Unable to save the session with the new student " + user.display_name);
                        });
                }
                else {
                    res.status(200).json({message: 'Welcome back to Class !', session: sess});
                }
            });
        }
        else {
            res.status(404).json({message: 'session' + id + 'dose not exist sorry'});
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
    let myData = new Session(req.body);
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



router.get("/rate-message", function (req, res) {

    const rating = Number(req.query.rating);
    const sess_id = req.query.sid;
    const mess_id = req.query.msgid;
    const decoded = req.verifiedEmail;

    // finds the user
    User.findOne({email: decoded}, function (err, user) {
        if (err) throw err;

        //finds the message and fetches its likers and dislikers arrays.
        Session_Message.findOne({_id: mess_id},{_id: 0 , likers: 1, dislikers: 1}, function(err,raters){

            if(err) return err;
            let likers = raters.likers;
            let dislikers = raters.dislikers;
            let liked = likers.indexOf(user._id) > -1; // true if the user has liked the message
            let disliked = dislikers.indexOf(user._id) > -1;// true if the user has disliked the message
            let ratingUpdate = {};

            if (rating == 1){ // user likes the message
                if(liked) { // user has already liked the message
                    console.log('user has already liked this message. mess id:' + mess_id);
                    return res.status(200).json({message: 'user has already liked this message. mess id:' + mess_id});
                }else if(disliked){ // user has already disliked the message.
                        ratingUpdate.$push = {likers: user._id};
                        ratingUpdate.$pull = {dislikers: user._id}; //removes the user id from the dislikers array
                        ratingUpdate.$inc = {likes: 1, dislikes: -1};
                }else { // user is liking the message fo the first time
                    ratingUpdate.$push = {likers: user._id};
                    ratingUpdate.$inc = {likes: 1};
                }
            }else{// user likes the message
                if(disliked) {// user has already disliked the message
                    console.log('user has already disliked this message. mess id:' + mess_id);
                    return res.status(200).json({message: 'user has already disliked this message. mess id:' + mess_id});
                }else if(liked){ // user has already liked the message.
                    ratingUpdate.$push = {dislikers: user._id};
                    ratingUpdate.$pull = {likers: user._id};//removes the user id from the likers array
                    ratingUpdate.$inc = {likes: -1, dislikes: 1};
                }else {// user is disliking the message fo the first time
                    ratingUpdate.$push = {dislikers: user._id};
                    ratingUpdate.$inc = {dislikes: 1};
                }
            }
            // updates the message rating with the ratingUpdate object
            Session_Message.update({_id: mess_id}, ratingUpdate, function(err){
                console.log('updating session message');
                if(err){
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
        Session.update({sid: msg.sid}, {$push: {messages: msg._id}}, function(err){
            console.log('pushing message to messages');
           if(err){
               return console.log(err);
           }
        });
        res.status(200).json({message: "successfully added message " + msg.body + " to db"});
        console.log("successfully added message " + msg.body + " to db");
    });
});




router.get("/get-all-messages", function (req, res) {
    var sess_id = Number(req.query.sid);

    console.log('session id: ' + sess_id);

    console.log('starting to aggregate');

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
                "messages_list" : 1
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
        User.findOne({email:decoded}, function (err, user) {
          if (err) return err;
          for(let i=0; i<sess.messages.length;++i){
            for(let j =0;j<sess.messages[i].ratings.length;++j){
                if(sess.messages[i].ratings[j].id == user.id){
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
                sess.save();
                res.status(200).json({message: "Disconnected " + req.verifiedEmail + " from session " + req.query.sid})
            }
        }
        if (!found) {
            res.status(404).json({message: "User " + req.verifiedEmail + " not found in session " + req.query.sid})
        }
    });
});



const cloudinary = require('cloudinary');

cloudinary.config({
    cloud_name: 'wizeup',
    api_key: '472734726483424',
    api_secret: 'A2ZBcQsnU72oh7p9JI415BOR1ws'
});

router.get('/postVideo', function (req, res) {
    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return next(err);
        cloudinary.v2.uploader.upload("/home/eran/projects/WebstormProjects/final_project/web/routes/good.mp4",
            { resource_type: "video",
                public_id: "sessions/" +req.query.sid + 'video',
                eager: [
                    { width: 300, height: 300,
                        crop: "pad", audio_codec: "none" },
                    { width: 160, height: 100,
                        crop: "crop", gravity: "south",
                        audio_codec: "none" } ],
                eager_async: true,
                eager_notification_url: "http://mysite/notify_endpoint"},
            function(error, result) {
                console.log(result);
                sess.videoID = result.url;
                sess.save();
            });
    });


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

    // const ObjectID = require('mongodb').ObjectID,
    //     GridStore = require('mongodb').GridStore,
    //     mongoDB = 'mongodb://damir:damiri@cluster0-shard-00-00-00hhm.mongodb.net:27017,cluster0-shard-00-01-00hhm.mongodb.net:27017,cluster0-shard-00-02-00hhm.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin',
    //     mongo = require('mongodb');
    //
    // mongo.MongoClient.connect(mongoDB, function (err, db) {
    //     // Our file ID
    //     const fileId = new ObjectID();
    //
    //     // Open a new file
    //     const gridStore = new GridStore(db, fileId, 'session:' + req.query.sid + ' video.mp4', 'w');
    //     const source = '/home/eran/projects/WebstormProjects/final_project/web/routes/good.mp4';
    //
    //     // Open the new file
    //     gridStore.open(function (err, gridStore) {
    //
    //         // Write the file to gridFS
    //         console.log("starting to upload file " + source + " as: " + fileId);
    //         gridStore.writeFile(source, function (err, doc) {
    //             Session.findOne({sid: req.query.sid}, function (err, sess) {
    //                 if (err) return next(err);
    //                 console.log('saved %s to GridFS file %s', source, fileId);
    //                 sess.videoID = fileId;
    //                 sess.save();
    //             });
    //         });
    //     });
    // });
});


router.get('/getVideo', function (req, res) {


    Session.findOne({sid: req.query.sid}, function (err, sess) {
        if (err) return (err);
        if (sess) {
            return sess.videoID;
        }
    });

    // const GridStore = require('mongodb').GridStore;
    // const db = mongoose.connection;
    // const ObjectID = require('mongodb').ObjectID;
    // db.options = 'PRIMARY';
    // Session.findOne({sid: req.query.sid}, function (err, sess) {
    //     if (err) return next(err);
    //     if (sess.videoID !== "") {
    //         new GridStore(db, new ObjectID(sess.videoID), 'r').open(function (err, GridFile) {
    //             if (!GridFile) {
    //                 console.log("video" + " not found!!!");
    //                 res.status(404).json({message: "video" + " not found"})
    //             }
    //             else {
    //                 console.log("starting to stream file " + GridFile.filename);
    //                 StreamGridFile(req, res, GridFile)
    //             }
    //         });
    //     }
    //     else {
    //         console.log("video" + " not found!!!");
    //         res.status(404).json({message: "video" + " not found"})
    //     }
    // });

});

// function StreamGridFile(req, res, GridFile) {
//     if (req.headers['range']) {
//         // Range request, partialle stream the file
//         console.log('Range Reuqest');
//         const parts = req.headers['range'].replace(/bytes=/, "").split("-");
//         const partialstart = parts[0];
//         const partialend = parts[1];
//         let start = parseInt(partialstart, 10);
//         const end = partialend ? parseInt(partialend, 10) : GridFile.length - 1;
//         const chunksize = (end - start) + 1;
//         console.log('Range ', start, '-', end);
//         res.writeHead(206, {
//             'Content-Range': 'bytes ' + start + '-' + end + '/' + GridFile.length,
//             'Accept-Ranges': 'bytes',
//             'Content-Length': chunksize,
//             'Content-Type': GridFile.contentType
//         });
//         // Set filepointer
//         GridFile.seek(start, function () {
//             // get GridFile stream
//             const stream = GridFile.stream(true);
//             // write to response
//             stream.on('data', function (buff) {
//                 // count data to abort streaming if range-end is reached
//                 // perhaps theres a better way?
//                 start += buff.length - 1;
//                 if (start >= end) {
//                     // enough data send, abort
//                     GridFile.close();
//                     res.end();
//                 } else {
//                     res.write(buff);
//                 }
//             });
//         });
//     } else {
//         // stream back whole file
//         console.log('No Range Request');
//         res.header('Content-Type', GridFile.contentType);
//         res.header('Content-Length', GridFile.length);
//         const stream = GridFile.stream(true);
//         stream.pipe(res);
//     }
// }


module.exports = router;
