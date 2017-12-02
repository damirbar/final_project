var mongoose = require('mongoose');

var fileSchema = new mongoose.Schema({
   external_id:{type: String,required:true},
    course_id:{type: String,required:true},
   url: {type: String,required: true},
   type: String,
   name: String,
    date_uploaded: Date,
    date_modified: Date,
    hidden: Number
});

module.exports = mongoose.model('File',fileSchema);