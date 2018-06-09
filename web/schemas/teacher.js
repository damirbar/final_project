var mongoose = require('mongoose');
var TeacherSchema = new mongoose.Schema({
    user_id: {type: String, default: ""},
    degree: {type: String, default: ""},
    friends: Array,
    events: Array,
    departments: Array
});
module.exports = mongoose.model('Teacher', TeacherSchema);
