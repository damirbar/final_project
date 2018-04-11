var mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs');

var StudentSchema = new mongoose.Schema({
    user_id: String,
    grades: Array,
    friends: Array,
    events: Array,
    department: String
});

module.exports = mongoose.model('Student', StudentSchema);
