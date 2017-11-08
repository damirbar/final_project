var mongoose = require('mongoose');

var StudentSchema = new mongoose.Schema({

    first_name: { type: String, required: true},
    last_name: { type: String, required: true},
    display_name: { type: String, default: this.first_name },
    mail: { type: String, required: true, unique: true },
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
    register_date: { type: Date, default: Date.now() },
    last_update: { type: Date, default: Date.now() },
    notifications: Array


});

module.exports = mongoose.model('Student', StudentSchema);
