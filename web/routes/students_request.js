let express = require('express');
let router = express.Router();
let User = require("../schemas/user");
let File = require("../schemas/file");
const Notification = require("../schemas/notification");


router.get("/get-profile", function (req, res) {
    const id = req.query.id;

    User.findOne({_id: id}, function (err, user) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (user) {
                let ansUser = {
                    role: user.role,
                    first_name: user.first_name,
                    last_name: user.last_name,
                    display_name: user.display_name,
                    email: user.email,
                    about_me: user.about_me,
                    country: user.country,
                    address: user.address,
                    birthday: user.birthday,
                    gender: user.gender,
                    profile_img: user.profile_img,
                    register_date: user.register_date,
                    last_modified: user.last_modified,
                };
                res.status(200).json(ansUser);
            }
            else {
                res.status(404).json({message: 'user' + id + 'dose not exist sorry'});
            }
        }
    });
});

router.post("/edit-profile", function (req, res) {
    const verified = req.verifiedEmail;
    const updatedUser = req.body.user || req.body;
    User.findOne({email: verified}, function (err, user) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (user) {
                user.first_name = updatedUser.first_name ? updatedUser.first_name : user.first_name;
                user.last_name = updatedUser.last_name ? updatedUser.last_name : user.last_name;
                user.display_name = updatedUser.display_name;// ? updatedUser.display_name : user.display_name;
                user.country = updatedUser.country;//? updatedUser.country : user.country;
                user.address = updatedUser.address;//? updatedUser.address : user.address;
                user.birthday = updatedUser.birthday ? updatedUser.birthday : user.birthday;
                user.gender = updatedUser.gender ? updatedUser.gender : user.gender;
                user.about_me = updatedUser.about_me;//? updatedUser.about_me : user.about_me;
                user.last_modified = Date.now();
                user.save();
                return res.status(200).json(user);
            }
            else {
                return res.status(404).json({message: 'user' + verified + 'dose not exist sorry'});
            }
        }
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


const multer = require('multer');
const upload = multer({dest: 'upload/'});
const type = upload.single('recfile');
let uploader = require('../tools/uploader');

router.post('/post-profile-image', type, function (req, res) {
    if (!req.file) {
        console.log("no file");
        res.status(400).json({message: 'no file'});
    }
    else {
        const path = req.file.path;
        if (!req.file.originalname.toLowerCase().match(/\.(jpg|jpeg|png)$/)) {
            fs.unlinkSync(path);
            res.status(400).json({message: 'wrong file'});
            console.log("wrong file type");
        }
        else {
            User.findOne({email: req.verifiedEmail}, function (err, user) {
                if (err) {
                    console.log(err);
                    fs.unlinkSync(path);
                    res.status(500).json({message: err});
                }
                else {
                    if (user) {
                        File.remove({url: user.profile_img}, function (err) {
                            if (err) {
                                console.log(err);
                                fs.unlinkSync(path);
                                res.status(500).json({message: err});
                            }
                            else {
                                uploader.uploadProfileImage(req.file, path, user, res)
                            }
                        });
                    }
                    else {
                        fs.unlinkSync(path);
                        res.status(404).json({message: 'no such user ' + req.verifiedEmail})
                    }
                }
            });
        }
    }
});


router.get("/get-events", function (req, res) {
    // check if there is a more efficient way to do this
    User.findOne({email: req.verifiedEmail}, function (err, user) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (user) {
                res.status(200).json(user.events.reverse().slice(req.query.start, (Number(req.query.start) + Number(req.query.end))))
            }
            else {
                res.status(404).json({message: "no such user " + req.verifiedEmail})
            }
        }
    })
});

router.get("/get-notifications", function (req, res) {
    // check if there is a more efficient way to do this
    User.findOne({email: req.verifiedEmail}, function (err, user) {
        if (err) {
            console.log(err);
            res.status(500).json({message: err});
        }
        else {
            if (user) {
                Notification.find({receiver_id: user.id}, function (err, notifications) {
                    if (err) {
                        console.log(err);
                        res.status(500).json({message: err});
                    }
                    else {
                        res.status(200).json(notifications.reverse().slice(req.query.start, (Number(req.query.start) + Number(req.query.end))));
                    }
                });
            }
            else {
                res.status(404).json({message: "no such user " + req.verifiedEmail})
            }
        }
    })
});

module.exports = router;