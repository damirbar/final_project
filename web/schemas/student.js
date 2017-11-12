var mongoose = require('mongoose');
var StudentSchema = new mongoose.Schema({

    first_name: String,
    last_name: String,
    display_name: String,
    mail: { type: String, unique: true },
    about_me: String,
    facebook_id: String,
    country: String,
    city: String,
    age: Number,
    uni: {},
    gender: String,
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

module.exports = mongoose.model('Student', StudentSchema);
