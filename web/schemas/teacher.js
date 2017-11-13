var mongoose = require('mongoose');
var TeacherSchema = new mongoose.Schema({
    first_name: String,
    last_name: String,
    display_name: String,
    age: Number,
    about_me: String,
    facebook_id: String,
    country: String,
    city: String,
    mail: { type: String, required: true, unique: false },

    gender: String,
    unis: Array,
    courses: Array,
    photos: Array,
    fs: {},
    grades: Array,
    cred: Number,
    fame: Number,
    msg: Array,
    register_date: Date,
    last_update: Date,
    notifications: Array
});
module.exports = mongoose.model('Teacher', TeacherSchema);



var shay = "shay";