var mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs');

var StudentSchema = new mongoose.Schema({
    user_id: {type: String, default: ""},
    grades: Array,
    friends: Array,
    events: Array,
    department: {type: String, default: ""}
});

module.exports = mongoose.model('Student', StudentSchema);
