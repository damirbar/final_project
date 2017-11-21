var mongoose = require('mongoose');
var DepartmentSchema = new mongoose.Schema({
    uni_id: String,
    name: String,
    head_id: String,
    courses: Array,
    teachers: Array
});

module.exports = mongoose.model('Department', DepartmentSchema);
