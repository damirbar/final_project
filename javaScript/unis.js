var mongoose = require('mongoose');
var UniSchema = new mongoose.Schema({
    Name: String,
    teachers:Array,
    departments: Array,
    phones: Array
});
module.exports = mongoose.model('User', UserSchema);
