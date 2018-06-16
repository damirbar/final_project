var mongoose = require('mongoose');
var SessionSchema = new mongoose.Schema({
    sid: {type: String, unique: true, required: true},
    name: {type: String, required: true},
    admin: {type: String, required: true},
    teacher_fname: {type: String, required: true},
    teacher_lname: {type: String, required: true},
    location: {type: String, required: true},
    creation_date: {type: Date, default: Date.now()},
    students: {type: Array, default: []},
    likes: {type : Number, default: 0},
    dislikes: {type : Number, default: 0},
    likers: [String],
    dislikers: [String],
    hidden: Boolean,
    messages: Array,
    videoUrl: {type: String, default: ""},
    video_file_id: {type: String, default: ""},
    picID: {type: String, default: "http://res.cloudinary.com/wizeup/image/upload/v1527096126/wizeup.jpg"},
    ongoing: {type: Boolean, default: true},
    cid: {type: String, default: ""},
    // endTime: {type: Date, required: true}

}, {usePushEach: true});
module.exports = mongoose.model('Session', SessionSchema);
