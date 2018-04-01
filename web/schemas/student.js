var mongoose = require('mongoose');
var bcrypt = require('bcrypt-nodejs');

var StudentSchema = new mongoose.Schema({

    id_num: String,
    first_name: String,
    last_name: String,
    display_name: String,
    mail: { type: String, unique: true },
    password: String,
    about_me: String,
    country: String,
    city: String,
    age: Number,
    uni: {},
    department : String,
    gender: String,
    courses: Array,
    photos: Array,
    profile_img: String,
    fs: {},
    grades: Array,
    cred: Number,
    fame: Number,
    msg: Array,
    register_date: Date,
    last_update: Date,
    notifications: Array,
    accessToken:String,
    temp_password	: String,
    temp_password_time: String
});

module.exports = mongoose.model('Student', StudentSchema);

module.exports.createStudent = function(newStudent, callback) {
    bcrypt.genSalt(10, function(err, salt) {
        bcrypt.hash(newStudent.password, salt, null, function(err, hash) {
            newStudent.password = hash;
            newStudent.save(callback);
        });
    });
};

