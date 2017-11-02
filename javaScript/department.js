var mongoose = require('mongoose');
var DepartmentSchema = new mongoose.Schema({
    Name: String,
    head:String,
    courses: Array,
    teachers: Array
});
module.exports = mongoose.model('User', UserSchema);
