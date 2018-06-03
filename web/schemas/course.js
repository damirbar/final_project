var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    name: String,
    course_num: {type: Number, unique: true, required: true},
    department: String,
    teacher:String,
    location: String,
    points: Number,
    creation_date: {type: Date, default: Date.now()},
    last_modified: Date,
    hidden: Boolean,
    students:[String],
    files:[{
            originalName: String,
            url: String,
            }]
},
    { usePushEach: true });
module.exports = mongoose.model('Course', CourseSchema);
