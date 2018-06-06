var mongoose = require('mongoose');
var CourseSchema = new mongoose.Schema({
    cid: {type: Number, unique: true, required: true},
    name: {type: String, default: ""},
    department: {type: String, default: ""},
    teacher:{type: String, default: ""},
    location: {type: String, default: ""},
    points: Number,
    creation_date: {type: Date, default: Date.now()},
    last_modified: {type: Date, default: Date.now()},
    students:[String],
    files:[{
            originalName: {type: String, default: ""},
            url: {type: String, default: ""},
            creation_date: Date,
            }]

}, { usePushEach: true });
module.exports = mongoose.model('Course', CourseSchema);
