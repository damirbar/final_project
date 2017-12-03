var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    course_no:Number,
    name: String,
    department_id: String,
    teacher_id:String,
    location: String,
    naz: Number,
    register_date: Date,
    last_update: Date,
    file_system:Array,
    students: Array,
    notifications: Array,
    msg: Array,
});
module.exports = mongoose.model('Course', CourseSchema);
