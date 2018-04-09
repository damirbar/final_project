var mongoose = require('mongoose');

var fileSchema = new mongoose.Schema({
    course_id: String,
    url: {type: String, required: true},
    name: String,
    creation_date: Date,
    last_modified: Date,
    hidden: Boolean
});

module.exports = mongoose.model('File', fileSchema);