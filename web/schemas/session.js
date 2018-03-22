var mongoose = require('mongoose');
var SessionSchema = new mongoose.Schema({
    internal_id: Number, // Corresponds to course id
    name: String,
    teacher_id:String,
    location: String,
    creation_date: Date,
    students: {type: Array, required: true},
    curr_rating: {type: Number, default: 0}
    // msg: Array
});
module.exports = mongoose.model('Session', SessionSchema);
