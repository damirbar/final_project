var mongoose = require('mongoose');
var TestSchema = new mongoose.Schema({
    Name: String,
    teacher:String,
    students: Array,
    place: String,
    naz : Number
});
module.exports = mongoose.model('Test', TestSchema);
