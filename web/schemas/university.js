var mongoose = require('mongoose');
var UniversitySchema = new mongoose.Schema({
    name: String,
    address: String,
    phones: Array,
    display_photo: String,
    photos: Array
});
module.exports = mongoose.model('University', UniversitySchema);
