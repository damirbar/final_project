var mongoose = require('mongoose');
var SessionSchema = new mongoose.Schema({
    sid: {type: Number, unique: true, required: true},
    name: {type: String, default: ""},
    admin: {type: String, default: ""},
    teacher_id: {type: String, default: ""},
    location: {type: String, default: ""},
    creation_date: {type: Date, default: Date.now()},
    students: Array,
    curr_rating: {type: Number, default: 0},
    hidden: Boolean,
    messages: Array,
    videoID: {type: String, default: ""},
    picID: {type: String, default: "http://res.cloudinary.com/wizeup/image/upload/v1527096126/wizeup.jpg"}

}, {usePushEach: true});
module.exports = mongoose.model('Session', SessionSchema);
