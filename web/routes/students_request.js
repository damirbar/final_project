var express = require('express');
var router = express.Router();
var User = require("../schemas/user");
var File = require("../schemas/file");


router.get("/get-profile", function (req, res, next) {
    const id = req.query.id;

    User.findOne({_id: id}, function (err, user) {
        if (err) return next(err);
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
    });
});

router.post("/edit-profile", function (req, res, next) {
    const verified = req.verifiedEmail;
    const updatedUser = req.body.user || req.body;
    User.findOne({email: verified}, function (err, user) {
        if (err) return next(err);
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
router.post('/post-profile-image', type, function (req, res) {
    if (!req.file) {
        res.status(400).json({message: 'no file'});
    }
    else {
        const path = req.file.path;
        if (first) {
            first = false;
            if (!req.file.originalname.match(/\.(jpg|jpeg|png)$/)) {
                fs.unlinkSync(path);
                res.status(400).json({message: 'wrong file'});
            }
            else {
                res.status(200).json({message: 'received file'});
                console.log(path);
                User.findOne({email: req.verifiedEmail}, function (err, user) {
                    if (err) return next(err);
                    console.log("starting to upload " + req.file.originalname);
                    cloudinary.v2.uploader.upload(path,
                        {
                            public_id: "profiles/" + user.id + "profile",
                            width: 1000,
                            height: 1000,
                            crop: 'thumb',
                            gravity: 'face',
                            radius: 20
                        },

                        function (err, result) {
                            fs.unlinkSync(path);
                            if (err) return err;
                            console.log("uploaded " + req.file.originalname);
                            console.log(result);
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
                                user.update({profile_file_id: updated_file.id}).then(function () {
                                   console.log("added file to collection");
                                });
                            });
                            user.update({profile_img: result.url}).then(function () {
                                console.log("updated user profile image");
                            });
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


router.get("/get-events", function (req, res) {
    // check if there is a more efficient way to do this
    User.findOne({email: req.verifiedEmail}, function (err, user) {
        if (err) return err;
        if (user) {
            res.status(200).json(user.events.reverse().slice(req.query.start, req.query.start + req.query.end))
        }
        else {
            res.status(404).json({message: "user not found"})
        }
    })
});

module.exports = router;