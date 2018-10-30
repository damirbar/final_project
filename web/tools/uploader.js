let File = require("../schemas/file");
const cloudinary = require('cloudinary');
const fs = require('fs');
const config = require('../config/config');
cloudinary.config({
    cloud_name: config.cloudniary.cloud_name,
    api_key: config.cloudniary.api_key,
    api_secret: config.cloudniary.api_secret
});

let uploadService = {

    uploadProfileImage: function(file, path, user, res){
        console.log("starting to upload " + file.originalname);
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
                if (err) {
                    console.log(err);
                    res.status(500).json({message: err});
                }
                else {
                    console.log("uploaded " + file.originalname);
                    const ans = new File({
                        originalName: file.originalname,
                        uploaderid: user.id,
                        url: result.url,
                        type: result.format,
                        size: result.bytes,
                        hidden: false
                    });
                    ans.save(function (err, updated_file) {
                        if (err) {
                            console.log(err);
                            res.status(500).json({message: err});
                        }
                        else {
                            user.update({
                                profile_file_id: updated_file.id,
                                profile_img: result.url
                            }, function (err) {
                                if (err) {
                                    console.log(err);
                                    res.status(500).json({message: err});
                                }
                                else {
                                    res.status(200).json({message: 'changed user profile image'});
                                }
                            });
                        }
                    });
                }
            });
    },

    uploadVideo: function(file, path, sess, userid) {
        console.log("starting to upload " + file.originalname);
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
                if (err) console.log(err);
                else {
                    const ans = new File({
                        originalName: file.originalname,
                        uploaderid: userid,
                        url: result.url,
                        type: result.format,
                        size: result.bytes,
                        hidden: false
                    });
                    ans.save(function (err, updated_file) {
                        if (err) console.log(err);
                        else {
                            sess.update({video_file_id: updated_file.id, videoUrl: result.url}, function (err) {
                                if (err) console.log(err);
                                else {
                                    console.log("finished uploading " + file.originalname);
                                }
                            });
                        }
                    });
                }
            });
    },

    uploadDoc: function(file, path, extension, userid, course, type) {
        console.log("starting to upload " + file.originalname);
        cloudinary.v2.uploader.upload(path,
            {
                resource_type: type,
                public_id: "courses/" + course.cid + "/" + file.filename + "." + extension
            },
            function (err, result) {
                fs.unlinkSync(path);
                if (err) console.log(err);
                else {
                    const ans = new File({
                        publicid: result.public_id,
                        originalName: file.originalname,
                        uploaderid: userid,
                        url: result.url,
                        type: extension,
                        size: result.bytes,
                        hidden: false
                    });
                    ans.save(function (err, updated_file) {
                        if (err) console.log(err);
                        else {
                            course.update({$push: {files: updated_file.id}}, function (err) {
                                if (err) console.log(err);
                                else {
                                    console.log("finished uploading " + file.originalname);
                                }
                            });
                        }
                    });
                }
            });

    },

    deleteDoc: function(publicid, cid, fileid, res) {
        cloudinary.v2.uploader.destroy(publicid,
            function (err, result) {
                if (err) {
                    console.log(err);
                    res.status(500).json({message: err});
                }
                else {
                    File.remove({publicid: publicid}, function (err) {
                        if (err) {
                            console.log(err);
                            res.status(500).json({message: err});
                        }
                        else {
                            Course.update({cid: cid}, {$pull: {files: fileid}}, function (err) {
                                if (err) {
                                    console.log(err);
                                    res.status(500).json({message: err});
                                }
                                else {
                                    res.status(200).json({message: "file deleted"})
                                }
                            });
                        }
                    })
                }
            });
    }
};

module.exports = uploadService;