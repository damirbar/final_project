var mongoose = require('mongoose');
var FacultySchema = new mongoose.Schema({
    university_id: String,
    name: String,
    head_id: String, //Teacher or higher
});

module.exports = mongoose.model('Faculty', FacultySchema);
