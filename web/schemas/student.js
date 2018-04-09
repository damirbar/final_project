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

// module.exports.createStudent = function(newStudent, callback) {
//     bcrypt.genSalt(10, function(err, salt) {
//         bcrypt.hash(newStudent.password, salt, null, function(err, hash) {
//             newStudent.password = hash;
//             newStudent.save(callback);
//         });
//     });
// };

