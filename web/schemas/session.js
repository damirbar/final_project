var mongoose = require('mongoose');
var SessionSchema = new mongoose.Schema({
    sid: {type: Number, unique: true, required: true},
    name: String,
    admin: String,
    teacher_id:String,
    location: String,
    creation_date: Date,
    students: Array,
    curr_rating: {type: Number, default: 0},
    hidden: Boolean,
    messages: Array,
    videoID: {type: String, default: ""},
    picID: {type: String, default: "http://res.cloudinary.com/wizeup/image/upload/v1527096126/wizeup.jpg"}
});
module.exports = mongoose.model('Session', SessionSchema);
