var mongoose = require('mongoose');

var UserSchema = new mongoose.Schema({
    id_num: String,
    role: String,
    first_name: String,
    last_name: String,
    display_name: String,
    email: {type: String, unique: true, require: true},
    password: String,
    about_me: String,
    country: String,
    address: String,
    age: Number,
    gender: String,
    photos: Array,
    profile_img: String,
    cred: Number,
    fame: Number,
    register_date: Date,
    last_modified: Date,
    accessToken:String,
    temp_password: String,
    temp_password_time: String
});

module.exports = mongoose.model('User', UserSchema);
