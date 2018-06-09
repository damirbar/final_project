var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    cid: {type: Number, unique: true, required: true},
    name:{type: String, required: true},
    department: {type: String, required: true},
    teacher_fname:{type: String, required: true},
    teacher_lname:{type: String, required: true},
    location: {type: String, required: true},
    points: {type: Number, required: true},
    creation_date: {type: Date, default: Date.now()},
    last_modified: {type: Date, default: Date.now()},
    students:[String],
    files:[String]

}, { usePushEach: true });
module.exports = mongoose.model('Course', CourseSchema);
