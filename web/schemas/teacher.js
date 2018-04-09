var mongoose = require('mongoose');
var TeacherSchema = new mongoose.Schema({
    user_id: String,
    degree: String,
    friends: Array,
    events: Array,
    departments: Array
});
module.exports = mongoose.model('Teacher', TeacherSchema);
