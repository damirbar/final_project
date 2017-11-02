var mongoose = require('mongoose');
var DepartmentSchema = new mongoose.Schema({
    Name: String,
    head:{},
    courses: Array,
    teachers: Array
});
module.exports = mongoose.model('Department', DepartmentSchema);
