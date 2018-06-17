var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    cid: {type: String, default: ""},
    name:{type: String, required: true},
    department: {type: String, required: true},
    teacher_fname:{type: String, required: true},
    teacher_lname:{type: String, required: true},
    teacher_email: String,
    location: {type: String, required: true},
    points: {type: Number, required: true},
    creation_date: {type: Date, default: Date.now()},
    last_modified: {type: Date, default: Date.now()},
    students:[String],
    files:[String],
    messages: Array,
    sessions: Array


}, { usePushEach: true });
module.exports = mongoose.model('Course', CourseSchema);
