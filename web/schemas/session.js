var mongoose = require('mongoose');
var SessionSchema = new mongoose.Schema({
    internal_id: Number, // Corresponds to course id
    name: String,
    teacher_id:String,
    location: String,
    creation_date: Date,
    students: Array,
    curr_rating: {type: Number, default: 0},
    hidden: Boolean,
    messages: Array
});
module.exports = mongoose.model('Session', SessionSchema);
