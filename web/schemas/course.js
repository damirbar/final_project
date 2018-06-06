var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    cid: {type: Number, unique: true, required: true},
    name: String,
    department: String,
    teacher:String,
    location: String,
    points: Number,
    creation_date: {type: Date, default: Date.now()},
    last_modified: Date,
    students:[String],
    files:[{
            originalName: String,
            url: String,
            }]

}, { usePushEach: true });
module.exports = mongoose.model('Course', CourseSchema);
