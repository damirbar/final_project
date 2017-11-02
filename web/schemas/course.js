var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    course_no:Number,
    name: String,
    teacher: {},
    students: Array,
    fs: {},
    location: {},
    qa: {},
    quiz: {},
    test: {},
    ex: {},
    naz: Number,
    msg: {},
    register_date: Date,
    last_update: Date,
    notifications: Array
});
module.exports = mongoose.model('Course', CourseSchema);
