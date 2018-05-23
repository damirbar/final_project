var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    name: String,
    department_id: String,
    teacher_id:String,
    location: String,
    points: Number,
    creation_date: {type: Date, default: Date.now()},
    last_modified: Date,
    hidden: Boolean,
    students:[String]
});
module.exports = mongoose.model('Course', CourseSchema);
