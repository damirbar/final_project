var mongoose = require('mongoose');
var User = require('./user');
var SessionSchema = new mongoose.Schema({
    sid: Number, // Corresponds to course id
    name: String,
    admin:  {type: mongoose.Schema.ObjectId, ref: 'User'},
    teacher_id:String,
    location: String,
    creation_date: Date,
    students: Array,
    curr_rating: {type: Number, default: 0},
    hidden: Boolean,
    messages: Array,
    videoID: {type: String, default: ""}
});
module.exports = mongoose.model('Session', SessionSchema);
