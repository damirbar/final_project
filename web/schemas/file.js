var mongoose = require('mongoose');

var fileSchema = new mongoose.Schema({
    originalName: {type: String, default: ""},
    uploaderid: {type: String, default: ""},
    url: {type: String, default: ""},
    type: {type: String, default: ""},
    creation_date:{type: Date, default: Date.now()},
    size: {type: String, default: ""},
    hidden: Boolean,
    publicid: String
});

module.exports = mongoose.model('File', fileSchema);