var mongoose = require('mongoose');
var DepartmentSchema = new mongoose.Schema({
    university_id: String,
    faculty_id: String,
    name: String,
    head_id: String, //Teacher or higher
    location: String,
    activity_hours: Array,
});

module.exports = mongoose.model('Department', DepartmentSchema);
