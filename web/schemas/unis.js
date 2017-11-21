var mongoose = require('mongoose');
var UniSchema = new mongoose.Schema({
    name: String,
    address: String,
    teachers:Array,
    departments: Array,
    phones: Array,
    display_photo: String,
    photos: Array
});
module.exports = mongoose.model('Unis', UniSchema);
