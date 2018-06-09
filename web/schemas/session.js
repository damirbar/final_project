var mongoose = require('mongoose');
var SessionSchema = new mongoose.Schema({
    sid: {type: Number, unique: true, required: true},
    name: {type: String, required: true},
    admin: {type: String, required: true},
    teacher_fname: {type: String, required: true},
    teacher_lname: {type: String, required: true},
    location: {type: String, required: true},
    creation_date: {type: Date, default: Date.now()},
    students: Array,
    curr_rating: {type: Number, default: 0},
    hidden: Boolean,
    messages: Array,
    videoUrl: {type: String, default: ""},
    video_file_id: {type: String, default: ""},
    picID: {type: String, default: "http://res.cloudinary.com/wizeup/image/upload/v1527096126/wizeup.jpg"},
    ongoing: {type: Boolean, default: true},
    // endTime: {type: Date, required: true}

}, {usePushEach: true});
module.exports = mongoose.model('Session', SessionSchema);
