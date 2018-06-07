var mongoose = require('mongoose');

var UserSchema = new mongoose.Schema({
    id_num: {type: String, default: ""},
    role: {type: String, default: ""},
    first_name: {type: String, default: ""},
    last_name: {type: String, default: ""},
    display_name: {type: String, default: ""},
    email: {type: String, unique: true, require: true},
    password: {type: String, default: ""},
    about_me: {type: String, default: ""},
    country: {type: String, default: ""},
    address: {type: String, default: ""},
    birthday: Date,
    gender: {type: String, default: ""},
    // photos: Array,
    profile_img: {type: String, default: ""},
    profile_file_id: {type: String, default: ""},
    // cred: Number,
    // fame: Number,
    register_date: {type: Date, default: Date.now()},
    last_modified: {type: Date, default: Date.now()},
    accessToken: {type: String, default: ""},
    temp_password: {type: String, default: ""},
    temp_password_time: {type: String, default: ""},
    // courses: {type: Array, default: [] },
    events: [{
        type:  {type: String, default: "general"},
        event: {type: String, default: ""},
        date:  {type: Date, default: Date.now()}
            }]
}, { usePushEach: true });

module.exports = mongoose.model('User', UserSchema);
