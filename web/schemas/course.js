var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    internal_id: Number,
    name: String,
    department_id: String,
    teacher_id:String,
    location: String,
    naz: Number,
    creation_date: Date,
    last_update: Date,
    file_system:Array,
    students:Array,
    notifications: Array,
    msg: Array,
    hidden: Number
});
module.exports = mongoose.model('Course', CourseSchema);
