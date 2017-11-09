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
<<<<<<< HEAD
    mail: { type: String, required: true, unique: true },
=======
<<<<<<< HEAD
    mail: { type: String, required: true, unique: false },
=======
    mail: String,
>>>>>>> origin/SefiWork
>>>>>>> 1a11db5666e5349bd8f38a29e726928ba084ad23
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
